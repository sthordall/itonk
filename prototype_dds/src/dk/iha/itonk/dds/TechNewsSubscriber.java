package dk.iha.itonk.dds;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.StringDataReader;
import com.rti.dds.type.builtin.StringTypeSupport;

public class TechNewsSubscriber extends DataReaderAdapter {
	// For clean shutdown sequence
    private static boolean shutdown_flag = false;

    public static final void main(String[] args) {
        // Create the DDS Domain participant on domain ID 0
        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(
                0, // Domain ID = 0
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, 
                null, // listener
                StatusKind.STATUS_MASK_NONE);
        if (participant == null) {
            System.err.println("Unable to create domain participant");
            return;
        }

        // Create the topic "TechNews" for the String type
        Topic topic = participant.create_topic(
                "TechNews", 
                StringTypeSupport.get_type_name(), 
                DomainParticipant.TOPIC_QOS_DEFAULT, 
                null, // listener
                StatusKind.STATUS_MASK_NONE);
        if (topic == null) {
            System.err.println("Unable to create topic.");
            return;
        }

        // Create the data reader using the default publisher
        StringDataReader dataReader =
            (StringDataReader) participant.create_datareader(
                topic, 
                Subscriber.DATAREADER_QOS_DEFAULT,
                new TechNewsSubscriber(),         // Listener
                StatusKind.DATA_AVAILABLE_STATUS);
        if (dataReader == null) {
            System.err.println("Unable to create DDS Data Reader");
            return;
        }

        System.out.println("TechNewsSubscriber up and running.");
        
        for (;;) {
            try {
                Thread.sleep(2000);
                if(shutdown_flag) break;
            } catch (InterruptedException e) {
                // Nothing to do...
            }
        }
	
        System.out.println("Shutting down...");
        participant.delete_contained_entities();
        DomainParticipantFactory.get_instance().delete_participant(participant);
    }

    /*
     * This method gets called back by DDS when one or more data samples have
     * been received.
     */
    public void on_data_available(DataReader reader) {
        StringDataReader stringReader = (StringDataReader) reader;
        SampleInfo info = new SampleInfo();
        for (;;) {
            try {
                String sample = stringReader.take_next_sample(info);
                if (info.valid_data) {
                    System.out.println(sample);
                    if (sample.equals("")) {
                        shutdown_flag = true;
                    }
                }
            } catch (RETCODE_NO_DATA noData) {
                // No more data to read
                break;
            } catch (RETCODE_ERROR e) {
                // An error occurred
                e.printStackTrace();
            }
        }
    }
}
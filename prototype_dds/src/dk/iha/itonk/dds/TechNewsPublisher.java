package dk.iha.itonk.dds;

import java.util.ArrayList;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.StringDataWriter;
import com.rti.dds.type.builtin.StringTypeSupport;

public class TechNewsPublisher {
	
	public static final void main(String args[]) {
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

        // Create the topic "MovieNews" for the String type
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

        // Create the data writer using the default publisher
        StringDataWriter dataWriter =
            (StringDataWriter) participant.create_datawriter(
                topic, 
                Publisher.DATAWRITER_QOS_DEFAULT,
                null, // listener
                StatusKind.STATUS_MASK_NONE);
        if (dataWriter == null) {
            System.err.println("Unable to create data writer\n");
            return;
        }

        System.out.println("TechNewsPublisher up and running.");
        System.out.println("TechNewsPublisher will now start publishing.");
        
        try {
        	ArrayList<String> newsArray = getNewsArray();
	       	int newsCount = newsArray.size();

            while (newsCount != 0) {
            	newsCount--;
            	String toPublish = newsArray.get(newsCount);
            	dataWriter.write(toPublish, InstanceHandle_t.HANDLE_NIL);
           		System.out.println("TechNewsPublisher published: " + toPublish);
           		try {
    	            Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // Mute exception
                }
            }
        } catch (RETCODE_ERROR e) {
            // This exception can be thrown from DDS write operation
            e.printStackTrace();
        }
		
        System.out.println("Shutting down...");
		participant.delete_contained_entities();
        DomainParticipantFactory.get_instance().delete_participant(participant);

	}

	private static ArrayList<String> getNewsArray () {
		ArrayList<String> newsArray = new ArrayList<>();
		
		newsArray.add("Apple just hired one of Nokia's PureView camera experts");
        newsArray.add("Bitcoin is now approved for political donations");
        newsArray.add("3D printing might save lives");
        newsArray.add("Scientists create 'semi-synthetic' living cells with extra DNA letters");
        newsArray.add("California's smartphone kill switch bill passes a second senate vote");
        newsArray.add("New app brings doctors to doorsteps in New York City");
        newsArray.add("Apple is reportedly close to buying Beats for $3.2 billion");
        newsArray.add("​Sprint is ready to throttle its unlimited data plans, but only in 'congested' areas");
        newsArray.add("This smartphone app can detect skin cancer");
        newsArray.add("United States credit card system begins complete overhaul in the next 18 months");
        newsArray.add("Google's camera app once again lets you snap photos while recording video");
        newsArray.add("Remember Unreal Tournament? Epic's making a new one and it's free");
        newsArray.add("Flickr struggles to capture the selfie generation");
        newsArray.add("Snapchat settles with FTC for misrepresenting its ephemeral nature, gathering user data");
        newsArray.add("Lyft's new premium service hauls you around in high-tech style");
        newsArray.add("An avalanche of new Snowden documents will go online next week");

		return newsArray;
	}

}
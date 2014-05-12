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

public class MovieNewsPublisher {
	
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
                "MovieNews", 
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

        System.out.println("MovieNewsPublisher up and running.");
        System.out.println("MovieNewsPublisher will now start publishing.");
        
        try {
        	ArrayList<String> newsArray = getNewsArray();
	       	int newsCount = newsArray.size();

            while (newsCount != 0) {
            	newsCount--;
            	String toPublish = newsArray.get(newsCount);
            	dataWriter.write(toPublish, InstanceHandle_t.HANDLE_NIL);
           		System.out.println("MovieNewsPublisher published: " + toPublish);
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
		
		newsArray.add("Power Rangers coming to big screen");
		newsArray.add("A feature-length The Flintstones animation? Yabba dabba do it!");
		newsArray.add("Where Will George Lucas Build His Museum?");
		newsArray.add("Industry failing women directors");
		newsArray.add("Prometheus actor hits Indiegogo");
		newsArray.add("BBC Films to adapt Alan Bennett’s The Lady In The Van");
		newsArray.add("Beta boards Jim Broadbent drama");
		newsArray.add("Sony snaps up Farrell-Weisz starrer The Lobster");
		newsArray.add("Agents Of S.H.I.E.L.D. Renewed for Season 2 by ABC; Marvel’s Agent Carter Ordered to Series");
		newsArray.add("John Goodman, Ken Watanabe to voice characters in Transformers 4");
		newsArray.add("Jay Roach to Direct Baseball Sex Scandal Movie for Warner Bros.");
		newsArray.add("Cannes: Content Adds Leif Tilden’s ‘Life at These Speeds’ to Slate");
		newsArray.add("WTFilms launches doc strand with La Bare");

		return newsArray;
	}

}
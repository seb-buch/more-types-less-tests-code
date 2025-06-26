package sbuch.presentation.examples.meeting;

import sbuch.presentation.examples.meeting.core.*;
import sbuch.presentation.examples.meeting.framework.MeetingFramework;

import java.net.URL;

import org.jboss.logging.Logger;

public class MeetingExample {
    private static Logger LOGGER = Logger.getLogger(MeetingExample.class);

    private MeetingExample() {
    }

    public static ValidationResult<Meeting> createMeetingFromFrameworkValidated(
            MeetingFramework frameworkValidated) {
        return switch (frameworkValidated) {
            case MeetingFramework.InPersonMeetingFramework(String title, String email, MeetingRoom room, int nGuests) ->
                InPersonMeeting.of(
                        title, email, room, nGuests);
            case MeetingFramework.OnlineMeetingFramework(String title, String email, VideoPlatform platform, URL url) ->
                OnlineMeeting.of(
                        title, email, platform, url);
        };
    }

    private void saveToRepo(Meeting meeting) {
        switch (meeting) {
            case InPersonMeeting:
                
                break;
        
            default:
                break;
        }LOGGER.info("saving meeting booked by %s to whatever DB you want".format(meeting.));
    }
}

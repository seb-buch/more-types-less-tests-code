package sbuch.presentation.examples.meeting;

import sbuch.presentation.examples.meeting.core.*;
import sbuch.presentation.examples.meeting.framework.MeetingFramework;

import java.net.URL;
import java.util.logging.Logger;

public class MeetingExample {
    private static final Logger LOGGER = Logger.getLogger(MeetingExample.class.getName());

    private MeetingExample() {
    }

    public static ValidationResult<Meeting> createMeetingFromFrameworkValidated(MeetingFramework frameworkValidated) {
        return switch (frameworkValidated) {
            case MeetingFramework.InPersonMeetingFramework(String title, String email, MeetingRoom room, int nGuests) ->
                    InPersonMeeting.of(
                            title, email, room, nGuests
                    );
            case MeetingFramework.OnlineMeetingFramework(String title, String email, VideoPlatform platform, URL url) ->
                    OnlineMeeting.of(
                            title, email, platform, url
                    );
        };
    }

    public void saveToRepo(Meeting meeting) {
        switch (meeting) {
            case InPersonMeeting inPersonMeeting: {
                LOGGER.info("InPersonMeeting in room %s saved to DB".formatted(inPersonMeeting.meetingRoom));
                break;
            }
            case OnlineMeeting onlineMeeting: {
                LOGGER.info("OnlineMeeting hosted on %s saved to DB".formatted(onlineMeeting.videoPlatform));
            }
        }
    }

}

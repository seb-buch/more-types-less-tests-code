package sbuch.presentation.examples.meeting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sbuch.presentation.examples.meeting.core.*;
import sbuch.presentation.examples.meeting.framework.MeetingFramework;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

class MeetingExampleTest {

    public static Stream<Arguments> provideValidInput() {
        try {
            return Stream.of(
                    // In-person meetings
                    Arguments.of(
                            new MeetingFramework.InPersonMeetingFramework(
                                    "Team Meeting",
                                    "john.doe@acme.org",
                                    MeetingRoom.ROOM_101,
                                    5
                            )
                    ),
                    Arguments.of(
                            new MeetingFramework.InPersonMeetingFramework(
                                    "Department Meeting",
                                    "jane.smith@acme.org",
                                    MeetingRoom.ROOM_42,
                                    15
                            )
                    ),
                    Arguments.of(
                            new MeetingFramework.InPersonMeetingFramework(
                                    "Exact Room Capacity - Small",
                                    "john.doe@acme.org",
                                    MeetingRoom.ROOM_101,
                                    20
                            )
                    ),
                    Arguments.of(
                            new MeetingFramework.InPersonMeetingFramework(
                                    "Exact Room Capacity - Medium",
                                    "jane.smith@acme.org",
                                    MeetingRoom.ROOM_42,
                                    50
                            )
                    ),
                    Arguments.of(
                            new MeetingFramework.InPersonMeetingFramework(
                                    "Exact Room Capacity - Large",
                                    "ceo.boss@acme.org",
                                    MeetingRoom.AMPHITHEATER,
                                    200
                            )
                    ),
                    Arguments.of(
                            new MeetingFramework.InPersonMeetingFramework(
                                    "Minimum Guests",
                                    "john.doe@acme.org",
                                    MeetingRoom.ROOM_101,
                                    1
                            )
                    ),

                    // Online meetings
                    Arguments.of(
                            new MeetingFramework.OnlineMeetingFramework(
                                    "Weekly Standup",
                                    "team.lead@acme.org",
                                    VideoPlatform.ZOOM,
                                    new URI("https://zoom.us/j/123456789").toURL()
                            )
                    ),
                    Arguments.of(
                            new MeetingFramework.OnlineMeetingFramework(
                                    "Project Review",
                                    "project.manager@acme.org",
                                    VideoPlatform.MEET,
                                    new URI("https://meet.google.com/abc-defg-hij").toURL()
                            )
                    )
            );
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException("Invalid URL in test data", e);
        }
    }

    @ParameterizedTest
    @MethodSource("provideValidInput")
    void shouldCreateMeetingFromValidInput(MeetingFramework frameworkValidated) {
        var result = MeetingExample.createMeetingFromFrameworkValidated(frameworkValidated);

        Assertions.assertFalse(result.hasFailedValidation());

        switch (frameworkValidated) {
            case MeetingFramework.InPersonMeetingFramework expected: {
                Assertions.assertInstanceOf(InPersonMeeting.class, result.getValue());
                InPersonMeeting actual = (InPersonMeeting) result.getValue();

                Assertions.assertEquals(expected.title(), actual.title);
                Assertions.assertEquals(validCorporateContact(expected.contactEmail()), actual.contact);
                Assertions.assertEquals(expected.meetingRoom(), actual.meetingRoom);
                Assertions.assertEquals(expected.numberOfGuests(), actual.nGuests);

                break;

            }
            case MeetingFramework.OnlineMeetingFramework expected: {
                Assertions.assertInstanceOf(OnlineMeeting.class, result.getValue());
                OnlineMeeting actual = (OnlineMeeting) result.getValue();

                Assertions.assertEquals(expected.title(), actual.title);
                Assertions.assertEquals(validCorporateContact(expected.contactEmail()), actual.contact);
                Assertions.assertEquals(expected.videoPlatform(), actual.videoPlatform);
                Assertions.assertEquals(expected.videoLink(), actual.videoLink);

                break;
            }
        }
    }

    public static Stream<Arguments> provideInvalidInput() {
        try {
            return Stream.of(
                    // Invalid email domain (in-person meeting)
                    Arguments.of(
                            new MeetingFramework.InPersonMeetingFramework(
                                    "Invalid Meeting",
                                    "firstname.lastname@gmail.com",
                                    MeetingRoom.ROOM_101,
                                    5
                            ),
                            "Email domain must be 'acme.org', 'gmail.com' is not valid."
                    ),
                    // Invalid email domain (online meeting)
                    Arguments.of(
                            new MeetingFramework.OnlineMeetingFramework(
                                    "Invalid Meeting",
                                    "firstname.lastname@gmail.com",
                                    VideoPlatform.MEET,
                                    new URI("https://meet.google.com/abc-defg-hij").toURL()
                            ),
                            "Email domain must be 'acme.org', 'gmail.com' is not valid."
                    ),
                    // Invalid username format
                    Arguments.of(
                            new MeetingFramework.InPersonMeetingFramework(
                                    "Invalid Meeting",
                                    "johndoe@acme.org",
                                    MeetingRoom.ROOM_101,
                                    5
                            ),
                            "Username must be 'firstname.lastname', 'johndoe' is not valid."
                    ),
                    // Room capacity exceeded for Room-101
                    Arguments.of(
                            new MeetingFramework.InPersonMeetingFramework(
                                    "Room Capacity Exceeded",
                                    "john.doe@acme.org",
                                    MeetingRoom.ROOM_101,
                                    21
                            ),
                            "max capacity for room 101 is 20 (21 guests required)"
                    ),
                    // Room capacity exceeded for Room-42
                    Arguments.of(
                            new MeetingFramework.InPersonMeetingFramework(
                                    "Room Capacity Exceeded",
                                    "jane.smith@acme.org",
                                    MeetingRoom.ROOM_42,
                                    51
                            ),
                            "max capacity for room 42 is 50 (51 guests required)"
                    ),
                    // Room capacity exceeded for Amphitheater
                    Arguments.of(
                            new MeetingFramework.InPersonMeetingFramework(
                                    "Room Capacity Exceeded",
                                    "ceo.boss@acme.org",
                                    MeetingRoom.AMPHITHEATER,
                                    201
                            ),
                            "max capacity for the amphitheater is 200 (201 guests required)"
                    )
            );
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException("Invalid URL in test data", e);
        }
    }

    @ParameterizedTest
    @MethodSource("provideInvalidInput")
    void shouldFailToCreateMeetingFromInvalidInput(MeetingFramework frameworkValidated, String expectedErrorMessage) {
        var result = MeetingExample.createMeetingFromFrameworkValidated(frameworkValidated);

        Assertions.assertTrue(result.hasFailedValidation());
        Assertions.assertEquals(expectedErrorMessage, result.getError().getMessage());
    }


    private CorporateContact validCorporateContact(String email) {
        var contact = CorporateContact.of(email);

        return contact.getValue();
    }

}

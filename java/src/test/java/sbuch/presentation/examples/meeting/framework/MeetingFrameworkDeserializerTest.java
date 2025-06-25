package sbuch.presentation.examples.meeting.framework;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import sbuch.presentation.examples.meeting.core.MeetingRoom;
import sbuch.presentation.examples.meeting.core.ValidationResult;
import sbuch.presentation.examples.meeting.core.VideoPlatform;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.stream.Stream;

class MeetingFrameworkDeserializerTest {

    public static Stream<Arguments> provideValidRawInputs() throws MalformedURLException {
        return Stream.of(
                Arguments.of(
                        """
                                {
                                    "title": "Team Meeting",
                                    "contactEmail": "john.doe@acme.org",
                                    "type": "in_person",
                                    "meetingRoom": "small",
                                    "nGuests": 5
                                }
                                """,
                        new MeetingFramework.InPersonMeetingFramework(
                                "Team Meeting",
                                "john.doe@acme.org",
                                MeetingRoom.ROOM_101,
                                5
                        )
                ),
                Arguments.of(
                        """
                                {
                                    "title": "Department Meeting",
                                    "contactEmail": "jane.smith@acme.org",
                                    "type": "in_person",
                                    "meetingRoom": "medium",
                                    "nGuests": 15
                                }
                                """,
                        new MeetingFramework.InPersonMeetingFramework(
                                "Department Meeting",
                                "jane.smith@acme.org",
                                MeetingRoom.ROOM_42,
                                15
                        )
                ),
                Arguments.of(
                        """
                                {
                                    "title": "Company Meeting",
                                    "contactEmail": "ceo.boss@acme.org",
                                    "type": "in_person",
                                    "meetingRoom": "large",
                                    "nGuests": 100
                                }
                                """,
                        new MeetingFramework.InPersonMeetingFramework(
                                "Company Meeting",
                                "ceo.boss@acme.org",
                                MeetingRoom.AMPHITHEATER,
                                100
                        )
                ),

                Arguments.of(
                        """
                                {
                                    "title": "Weekly Standup",
                                    "contactEmail": "team.lead@acme.org",
                                    "type": "online",
                                    "videoPlatform": "zoom",
                                    "videoLink": "https://zoom.us/j/123456789"
                                }
                                """,
                        new MeetingFramework.OnlineMeetingFramework(
                                "Weekly Standup",
                                "team.lead@acme.org",
                                VideoPlatform.ZOOM,
                                URI.create("https://zoom.us/j/123456789").toURL()
                        )
                ),
                Arguments.of(
                        """
                                {
                                    "title": "Project Review",
                                    "contactEmail": "project.manager@acme.org",
                                    "type": "online",
                                    "videoPlatform": "meet",
                                    "videoLink": "https://meet.google.com/abc-defg-hij"
                                }
                                """,
                        new MeetingFramework.OnlineMeetingFramework(
                                "Project Review",
                                "project.manager@acme.org",
                                VideoPlatform.MEET,
                                URI.create("https://meet.google.com/abc-defg-hij").toURL()
                        )
                ),
                Arguments.of(
                        """
                                {
                                    "title": "Client Presentation",
                                    "contactEmail": "sales.rep@acme.org",
                                    "type": "online",
                                    "videoPlatform": "teams",
                                    "videoLink": "https://teams.microsoft.com/l/meetup-join/123456789"
                                }
                                """,
                        new MeetingFramework.OnlineMeetingFramework(
                                "Client Presentation",
                                "sales.rep@acme.org",
                                VideoPlatform.TEAMS,
                                URI.create("https://teams.microsoft.com/l/meetup-join/123456789").toURL()
                        )
                )
        );
    }


    @ParameterizedTest
    @MethodSource("provideValidRawInputs")
    void shouldCreateMeetingFromValidRaw(String input, MeetingFramework expected) {

        var result = MeetingFrameworkDeserializer.createMeetingFromRaw(input);

        Assertions.assertFalse(result.hasFailedValidation());
        Assertions.assertEquals(expected, result.getValue());
    }

    public static Stream<Arguments> provideInvalidRawInputs() {
        return Stream.of(
                // Missing title
                Arguments.of(
                        """
                                {
                                    "contactEmail": "john.doe@acme.org",
                                    "type": "in_person",
                                    "meetingRoom": "small",
                                    "nGuests": 5
                                }
                                """
                ),
                // Missing contactEmail
                Arguments.of(
                        """
                                {
                                    "title": "Team Meeting",
                                    "type": "in_person",
                                    "meetingRoom": "small",
                                    "nGuests": 5
                                }
                                """
                ),
                // Missing type
                Arguments.of(
                        """
                                {
                                    "title": "Team Meeting",
                                    "contactEmail": "john.doe@acme.org",
                                    "meetingRoom": "small",
                                    "nGuests": 5
                                }
                                """
                ),
                // Direct room name instead of size
                Arguments.of(
                        """
                                {
                                    "title": "Room Size Mapping Test",
                                    "contactEmail": "john.doe@acme.org",
                                    "type": "in_person",
                                    "meetingRoom": "Room-101",
                                    "nGuests": 5
                                }
                                """
                ),
                // Title with just whitespace
                Arguments.of(
                        """
                                {
                                    "title": "   ",
                                    "contactEmail": "john.doe@acme.org",
                                    "type": "in_person",
                                    "meetingRoom": "small",
                                    "nGuests": 5
                                }
                                """
                ),
                // Invalid email format
                Arguments.of(
                        """
                                {
                                    "title": "Team Meeting",
                                    "contactEmail": "invalid-email",
                                    "type": "in_person",
                                    "meetingRoom": "small",
                                    "nGuests": 5
                                }
                                """
                ),
                // Invalid meeting room
                Arguments.of(
                        """
                                {
                                    "title": "Team Meeting",
                                    "contactEmail": "john.doe@acme.org",
                                    "type": "in_person",
                                    "meetingRoom": "invalid-room",
                                    "nGuests": 5
                                }
                                """
                ),
                // Non-positive number of guests
                Arguments.of(
                        """
                                {
                                    "title": "Team Meeting",
                                    "contactEmail": "john.doe@acme.org",
                                    "type": "in_person",
                                    "meetingRoom": "small",
                                    "nGuests": 0
                                }
                                """
                ),
                // Invalid video platform
                Arguments.of(
                        """
                                {
                                    "title": "Weekly Standup",
                                    "contactEmail": "team.lead@acme.org",
                                    "type": "online",
                                    "videoPlatform": "skype",
                                    "videoLink": "https://zoom.us/j/123456789"
                                }
                                """
                ),
                // Invalid URL
                Arguments.of(
                        """
                                {
                                    "title": "Weekly Standup",
                                    "contactEmail": "team.lead@acme.org",
                                    "type": "online",
                                    "videoPlatform": "zoom",
                                    "videoLink": "invalid-url"
                                }
                                """
                ),
                // Invalid meeting type
                Arguments.of(
                        """
                                {
                                    "title": "Invalid Meeting",
                                    "contactEmail": "john.doe@acme.org",
                                    "type": "hybrid"
                                }
                                """
                ),
                // Empty object
                Arguments.of("{}"),
                // Null object (represented as null string in JSON)
                Arguments.of("null")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRawInputs")
    void shouldFailToCreateMeetingFromInvalidRaw(String input) {
        var expected = ValidationResult.failure("Raw object is not valid").getError();

        var result = MeetingFrameworkDeserializer.createMeetingFromRaw(input);

        Assertions.assertTrue(result.hasFailedValidation());
        Assertions.assertEquals(expected, result.getError());
    }
}

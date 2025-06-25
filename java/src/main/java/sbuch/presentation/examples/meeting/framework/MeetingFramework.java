package sbuch.presentation.examples.meeting.framework;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sbuch.presentation.examples.meeting.core.MeetingRoom;
import sbuch.presentation.examples.meeting.core.VideoPlatform;

import java.net.URL;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MeetingFramework.InPersonMeetingFramework.class, name = "in_person"),
        @JsonSubTypes.Type(value = MeetingFramework.OnlineMeetingFramework.class, name = "online")
})
public sealed interface MeetingFramework permits MeetingFramework.InPersonMeetingFramework, MeetingFramework.OnlineMeetingFramework {

    @JsonTypeName("in_person")
    record InPersonMeetingFramework(
            @NotBlank
            String title,

            @NotNull
            @Email
            String contactEmail,

            @NotNull
            @JsonDeserialize(using = MeetingRoomDeserializer.class)
            MeetingRoom meetingRoom,

            @Min(1)
            @JsonProperty("nGuests")
            @NotNull
            int numberOfGuests
    ) implements MeetingFramework {
    }

    @JsonTypeName("online")
    record OnlineMeetingFramework(
            @NotBlank
            String title,

            @NotNull
            @Email
            String contactEmail,

            @NotNull
            @JsonDeserialize(using = VideoPlatformDeserializer.class)
            VideoPlatform videoPlatform,

            @NotNull
            URL videoLink
    ) implements MeetingFramework {
    }
}

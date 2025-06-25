package sbuch.presentation.examples.meeting.framework;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import sbuch.presentation.examples.meeting.core.MeetingRoom;

import java.io.IOException;

class MeetingRoomDeserializer extends JsonDeserializer<MeetingRoom> {
    @Override
    public MeetingRoom deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();

        return switch (value) {
            case "small" -> MeetingRoom.ROOM_101;
            case "medium" -> MeetingRoom.ROOM_42;
            case "large" -> MeetingRoom.AMPHITHEATER;
            default -> throw new IllegalArgumentException("Unknown meeting room size: " + value);
        };
    }
}

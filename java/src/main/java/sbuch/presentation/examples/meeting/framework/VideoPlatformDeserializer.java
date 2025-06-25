package sbuch.presentation.examples.meeting.framework;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import sbuch.presentation.examples.meeting.core.VideoPlatform;

import java.io.IOException;

class VideoPlatformDeserializer extends JsonDeserializer<VideoPlatform> {
    @Override
    public VideoPlatform deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();

        return switch (value) {
            case "zoom" -> VideoPlatform.ZOOM;
            case "meet" -> VideoPlatform.MEET;
            case "teams" -> VideoPlatform.TEAMS;
            default -> throw new IllegalArgumentException("Unknown video platform: " + value);
        };
    }
}

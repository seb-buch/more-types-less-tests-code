package sbuch.presentation.examples;

import java.util.logging.Logger;


public class TrafficLightExample {
    private static final Logger LOGGER = Logger.getLogger(TrafficLightExample.class.getName());

    public static void main(String[] args) {
        logTrafficLightState(TrafficLightColor.RED);
        logTrafficLightState(TrafficLightColor.ORANGE);
        logTrafficLightState(TrafficLightColor.GREEN);
    }

    private static void logTrafficLightState(TrafficLightColor color) {
        switch (color) {
            case RED -> LOGGER.info("Red");
            case ORANGE -> LOGGER.info("Orange");
            case GREEN -> LOGGER.info("Green");
        }
    }
}

// TrafficLight = (Boolean, Boolean, Boolean)
record TrafficLight(Boolean redOn,
                    Boolean orangeOn,
                    Boolean greenOn) {
}

// TrafficLightColor = String
// Use String

// TrafficLightColor = "red" | "orange" | "green"
enum TrafficLightColor {RED, ORANGE, GREEN}

package sbuch.presentation.examples;

import java.util.logging.Logger;


public class DoorExample {
    private static final Logger LOGGER = Logger.getLogger(DoorExample.class.getName());

    public static void main(String[] args) {
        Door door1 = new OpenDoor();
        Door door2 = new ClosedDoor(true);
        Door door3 = new ClosedDoor(false);

        logDoorState(door1);
        logDoorState(door2);
        logDoorState(door3);
    }

    private static void logDoorState(Door door) {
        switch (door) {
            case OpenDoor _ -> LOGGER.info("Open Door");
            case ClosedDoor(boolean isLocked) -> {
                if (isLocked) {
                    LOGGER.info("Closed and locked Door");
                    return;
                }
                LOGGER.info("Closed but unlocked Door");
            }
        }
    }
}

sealed interface Door permits OpenDoor, ClosedDoor {
}

record OpenDoor() implements Door {
}

record ClosedDoor(boolean isLocked) implements Door {
}

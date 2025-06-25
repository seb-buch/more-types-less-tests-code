package sbuch.presentation.examples.meeting.core;

public enum MeetingRoom {
    ROOM_101,
    ROOM_42,
    AMPHITHEATER;

    @Override
    public String toString() {
        return switch (this) {
            case ROOM_101 -> "Room-101";
            case ROOM_42 -> "Room-42";
            case AMPHITHEATER -> "Amphitheater";
        };
    }
}

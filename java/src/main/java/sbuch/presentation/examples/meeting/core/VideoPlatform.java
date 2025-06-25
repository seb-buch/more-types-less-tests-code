package sbuch.presentation.examples.meeting.core;

public enum VideoPlatform {
    ZOOM,
    MEET,
    TEAMS;

    @Override
    public String toString() {
        return switch (this) {
            case ZOOM -> "zoom";
            case MEET -> "meet";
            case TEAMS -> "teams";
        };
    }
}

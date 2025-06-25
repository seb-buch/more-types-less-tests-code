package sbuch.presentation.examples.meeting.core;

public sealed interface Meeting permits InPersonMeeting, OnlineMeeting {
}

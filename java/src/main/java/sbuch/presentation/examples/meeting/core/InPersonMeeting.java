package sbuch.presentation.examples.meeting.core;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public final class InPersonMeeting implements Meeting {
    public final String title;
    public final CorporateContact contact;

    public final MeetingRoom meetingRoom;
    public final int nGuests;

    private InPersonMeeting(String title, CorporateContact contact, MeetingRoom meetingRoom, int nGuests) {
        this.title = title;
        this.contact = contact;
        this.meetingRoom = meetingRoom;
        this.nGuests = nGuests;
    }

    public static ValidationResult<Meeting> of(String title, String email, MeetingRoom meetingRoom, int nGuests) {

        ValidationResult<CorporateContact> validationResult = CorporateContact.of(email);
        if (validationResult.hasFailedValidation()) {
            return ValidationResult.failure(validationResult.getError().getMessage());
        }
        CorporateContact contact = validationResult.getValue();

        ValidationResult<Meeting> badGuestNumber = switch (meetingRoom) {
            case ROOM_101 -> (nGuests > 20) ?
                    ValidationResult.failure("max capacity for room 101 is 20 (%s guests required)".formatted(nGuests))
                    : null;
            case ROOM_42 -> (nGuests > 50) ?
                    ValidationResult.failure("max capacity for room 42 is 50 (%s guests required)".formatted(nGuests))
                    : null;
            case AMPHITHEATER -> (nGuests > 200) ?
                    ValidationResult.failure("max capacity for the amphitheater is 200 (%s guests required)".formatted(nGuests))
                    : null;
        };
        if (badGuestNumber != null) {
            return badGuestNumber;
        }

        return ValidationResult.success(new InPersonMeeting(title, contact, meetingRoom, nGuests));
    }
}

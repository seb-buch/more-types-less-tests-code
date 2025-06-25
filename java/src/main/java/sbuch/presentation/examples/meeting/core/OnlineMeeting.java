package sbuch.presentation.examples.meeting.core;

import lombok.EqualsAndHashCode;

import java.net.URL;

@EqualsAndHashCode(callSuper = false)
public final class OnlineMeeting implements Meeting {
    public final String title;
    public final CorporateContact contact;

    public final VideoPlatform videoPlatform;
    public final URL videoLink;

    private OnlineMeeting(String title, CorporateContact contact, VideoPlatform videoPlatform, URL videoLink) {
        this.title = title;
        this.contact = contact;
        this.videoPlatform = videoPlatform;
        this.videoLink = videoLink;
    }

    public static ValidationResult<Meeting> of(String title, String email, VideoPlatform videoPlatform, URL videoLink) {
        ValidationResult<CorporateContact> validationResult = CorporateContact.of(email);
        if (validationResult.hasFailedValidation()) {
            return ValidationResult.failure(validationResult.getError().getMessage());
        }
        CorporateContact contact = validationResult.getValue();

        return ValidationResult.success(new OnlineMeeting(title, contact, videoPlatform, videoLink));
    }

}

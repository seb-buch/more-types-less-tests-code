package sbuch.presentation.examples.meeting.core;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class CorporateContact {
    public final String firstName;
    public final String lastName;
    public final String email;

    private CorporateContact(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public static ValidationResult<CorporateContact> of(String email) {
        var splittedEmail = email.split("@");

        var domain = splittedEmail[1];
        if (!domain.equals("acme.org"))
            return ValidationResult.failure("Email domain must be 'acme.org', '%s' is not valid.".formatted(domain));

        var splittedUsername = splittedEmail[0].split("\\.");
        if (splittedUsername.length != 2)
            return ValidationResult.failure("Username must be 'firstname.lastname', '%s' is not valid.".formatted(splittedEmail[0]));

        var firstName = splittedUsername[0].substring(0, 1).toUpperCase() + splittedUsername[0].substring(1);
        var lastName = splittedUsername[1].toUpperCase();
        return ValidationResult.success(new CorporateContact(firstName, lastName, email));
    }
}

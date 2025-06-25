package sbuch.presentation.examples.meeting.framework;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import sbuch.presentation.examples.meeting.core.ValidationResult;

import java.io.IOException;
import java.util.Set;

public class MeetingFrameworkDeserializer {
    private MeetingFrameworkDeserializer() {
    }

    public static ValidationResult<MeetingFramework> createMeetingFromRaw(String rawJson) {
        var mapper = new ObjectMapper();

        try {
            // Deserialize the JSON
            MeetingFramework deserialized = mapper.readValue(rawJson, MeetingFramework.class);
            if (deserialized == null)
                return ValidationResult.failure("Raw object is not valid");

            // Create a validator
            Validator validator;
            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                validator = factory.getValidator();
            }

            // Validate the deserialized object
            Set<ConstraintViolation<MeetingFramework>> violations = validator.validate(deserialized);

            // If there are validation errors, return a failure result
            if (!violations.isEmpty()) {
                return validationError();
            }

            // If validation passes, return a success result
            return ValidationResult.success(deserialized);
        } catch (IOException _) {
            return validationError();
        }
    }

    private static ValidationResult<MeetingFramework> validationError() {
        return ValidationResult.failure("Raw object is not valid");
    }
}

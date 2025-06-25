package sbuch.presentation.examples.meeting.core;

import lombok.EqualsAndHashCode;

public final class ValidationResult<T> {
    private final T value;
    private final ValidationError error;

    private ValidationResult(T value, ValidationError error) {
        this.value = value;
        this.error = error;
    }

    public boolean hasFailedValidation() {
        return value == null;
    }

    public T getValue() {
        if (value == null)
            throw new IllegalStateException("getValidated() called on a failed validation.");
        return value;
    }

    public ValidationError getError() {
        if (error == null)
            throw new IllegalStateException("getError() called on a validated object.");
        return error;
    }

    public static <T> ValidationResult<T> success(T value) {
        return new ValidationResult<>(value, null);
    }


    public static <T> ValidationResult<T> failure(String message) {
        return new ValidationResult<>(null, new ValidationError(message));
    }

    @EqualsAndHashCode(callSuper = false)
    public static final class ValidationError extends RuntimeException {
        public ValidationError(final String message) {
            super(message);
        }
    }
}

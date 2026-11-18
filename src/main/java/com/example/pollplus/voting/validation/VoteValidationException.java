package com.example.pollplus.voting.validation;

import java.util.Objects;

/**
 * Thrown by a validation handler when a ballot must be rejected.
 *
 * <p>Kept framework-free on purpose: the voting facade translates it into the
 * shared HTTP exception types, so this package stays unit-testable without a
 * Spring context.
 */
public class VoteValidationException extends RuntimeException {

    private final VoteValidationError error;

    public VoteValidationException(VoteValidationError error) {
        super(Objects.requireNonNull(error, "error").message());
        this.error = error;
    }

    public VoteValidationError error() {
        return error;
    }

    public int suggestedStatus() {
        return error.suggestedStatus();
    }
}

package com.example.pollplus.voting.validation;

import java.util.Objects;

/**
 * Base class of the Chain of Responsibility used to validate a ballot.
 *
 * <p>Each handler performs exactly one check and delegates to the next handler
 * only when its own check passes. A rejected ballot never reaches the handlers
 * behind the failing one, which keeps each rule independently testable.
 */
public abstract class VoteValidationHandler {

    private VoteValidationHandler next;

    /**
     * Appends the next handler.
     *
     * @return the handler passed in, so calls can be chained
     */
    public VoteValidationHandler setNext(VoteValidationHandler next) {
        this.next = Objects.requireNonNull(next, "next");
        return next;
    }

    public final void validate(VoteValidationContext context) {
        Objects.requireNonNull(context, "context");
        check(context);
        if (next != null) {
            next.validate(context);
        }
    }

    protected abstract void check(VoteValidationContext context);
}

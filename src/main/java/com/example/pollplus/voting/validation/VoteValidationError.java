package com.example.pollplus.voting.validation;

/**
 * Rejection reasons produced by the validation chain.
 *
 * <p>The suggested status is a hint for the facade, which maps it onto the
 * shared {@code AppException} family once Member 1 publishes it (401, 403,
 * 409 as listed in the project plan).
 */
public enum VoteValidationError {

    ELECTION_NOT_OPEN(409, "Election is not open for voting"),
    VOTER_NOT_ELIGIBLE(403, "Voter is not eligible for this election"),
    INVALID_TOKEN(401, "Ballot token is invalid"),
    TOKEN_EXPIRED(401, "Ballot token has expired"),
    TOKEN_ALREADY_USED(409, "Ballot token has already been used"),
    ALREADY_VOTED(409, "Voter has already cast a vote in this election");

    private final int suggestedStatus;
    private final String message;

    VoteValidationError(int suggestedStatus, String message) {
        this.suggestedStatus = suggestedStatus;
        this.message = message;
    }

    public int suggestedStatus() {
        return suggestedStatus;
    }

    public String message() {
        return message;
    }
}

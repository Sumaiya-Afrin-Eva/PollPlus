package com.example.pollplus.voting.validation;

import com.example.pollplus.voting.port.VoterEligibilityPort;

import java.util.Objects;

/**
 * Second link: the voter must appear in the election's voter registry.
 */
public final class VoterEligibleHandler extends VoteValidationHandler {

    private final VoterEligibilityPort voterEligibility;

    public VoterEligibleHandler(VoterEligibilityPort voterEligibility) {
        this.voterEligibility = Objects.requireNonNull(voterEligibility, "voterEligibility");
    }

    @Override
    protected void check(VoteValidationContext context) {
        if (!voterEligibility.isEligible(context.electionId(), context.voterId())) {
            throw new VoteValidationException(VoteValidationError.VOTER_NOT_ELIGIBLE);
        }
    }
}

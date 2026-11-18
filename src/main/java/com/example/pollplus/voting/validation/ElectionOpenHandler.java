package com.example.pollplus.voting.validation;

import com.example.pollplus.voting.port.ElectionStatusPort;

import java.util.Objects;

/**
 * First link: the election must currently accept votes.
 */
public final class ElectionOpenHandler extends VoteValidationHandler {

    private final ElectionStatusPort electionStatus;

    public ElectionOpenHandler(ElectionStatusPort electionStatus) {
        this.electionStatus = Objects.requireNonNull(electionStatus, "electionStatus");
    }

    @Override
    protected void check(VoteValidationContext context) {
        if (!electionStatus.isOpen(context.electionId())) {
            throw new VoteValidationException(VoteValidationError.ELECTION_NOT_OPEN);
        }
    }
}

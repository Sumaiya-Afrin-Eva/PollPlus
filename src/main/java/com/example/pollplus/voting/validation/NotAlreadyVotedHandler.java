package com.example.pollplus.voting.validation;

import com.example.pollplus.voting.port.VoterRegistryPort;

import java.util.Objects;

/**
 * Fourth link: the voter must not already be marked as having voted.
 */
public final class NotAlreadyVotedHandler extends VoteValidationHandler {

    private final VoterRegistryPort voterRegistry;

    public NotAlreadyVotedHandler(VoterRegistryPort voterRegistry) {
        this.voterRegistry = Objects.requireNonNull(voterRegistry, "voterRegistry");
    }

    @Override
    protected void check(VoteValidationContext context) {
        if (voterRegistry.hasVoted(context.electionId(), context.voterId())) {
            throw new VoteValidationException(VoteValidationError.ALREADY_VOTED);
        }
    }
}

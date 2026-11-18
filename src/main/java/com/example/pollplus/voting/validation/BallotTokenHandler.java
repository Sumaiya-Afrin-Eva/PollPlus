package com.example.pollplus.voting.validation;

import com.example.pollplus.voting.port.BallotTokenPort;

import java.util.Objects;

/**
 * Third link: the one-time ballot token must exist, be unused and be unexpired.
 */
public final class BallotTokenHandler extends VoteValidationHandler {

    private final BallotTokenPort ballotTokenPort;

    public BallotTokenHandler(BallotTokenPort ballotTokenPort) {
        this.ballotTokenPort = Objects.requireNonNull(ballotTokenPort, "ballotTokenPort");
    }

    @Override
    protected void check(VoteValidationContext context) {
        BallotTokenPort.TokenStatus status = ballotTokenPort.verify(
                context.electionId(), context.voterId(), context.rawToken());

        switch (status) {
            case VALID -> {
                // continue down the chain
            }
            case INVALID -> throw new VoteValidationException(VoteValidationError.INVALID_TOKEN);
            case EXPIRED -> throw new VoteValidationException(VoteValidationError.TOKEN_EXPIRED);
            case ALREADY_USED -> throw new VoteValidationException(VoteValidationError.TOKEN_ALREADY_USED);
        }
    }
}

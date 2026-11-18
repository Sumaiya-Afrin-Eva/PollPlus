package com.example.pollplus.voting.port;

/**
 * Verifies a one-time ballot token without exposing the stored hash.
 */
public interface BallotTokenPort {

    enum TokenStatus {
        VALID,
        INVALID,
        EXPIRED,
        ALREADY_USED
    }

    /**
     * @param electionId election the token was issued for
     * @param voterId    voter presenting the token
     * @param rawToken   plain token supplied by the client; never stored as-is
     */
    TokenStatus verify(long electionId, long voterId, String rawToken);
}

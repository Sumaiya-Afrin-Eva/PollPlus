package com.example.pollplus.voting.port;

/**
 * Answers whether a voter may vote in an election.
 *
 * <p>In PollPulse eligibility comes from the imported voter registry, so the
 * production adapter checks that a registry row exists for
 * {@code (electionId, voterId)}.
 */
public interface VoterEligibilityPort {

    boolean isEligible(long electionId, long voterId);
}

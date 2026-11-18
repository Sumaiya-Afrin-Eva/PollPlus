package com.example.pollplus.voting.port;

/**
 * Read side of the voter registry, used to block double voting.
 */
public interface VoterRegistryPort {

    boolean hasVoted(long electionId, long voterId);
}

package com.example.pollplus.voting.port;

/**
 * Narrow read contract over the election module.
 *
 * <p>Implemented later by an adapter around Member 2's
 * {@code ElectionQueryService}, so the voting module can be built and tested
 * before that service exists.
 */
public interface ElectionStatusPort {

    /** @return {@code true} when the election currently accepts votes */
    boolean isOpen(long electionId);
}

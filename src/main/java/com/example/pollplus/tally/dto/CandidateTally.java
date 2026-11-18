package com.example.pollplus.tally.dto;

/**
 * Final vote count for one candidate in one position.
 */
public record CandidateTally(long candidateId, long votes) {
}

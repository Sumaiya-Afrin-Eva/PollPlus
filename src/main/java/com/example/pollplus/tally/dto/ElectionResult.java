package com.example.pollplus.tally.dto;

import java.util.List;

/**
 * Complete tally for an election, one entry per position.
 *
 * @param electionId the tallied election
 * @param positions  per-position results
 * @param published  {@code true} once results may be shown to voters
 */
public record ElectionResult(long electionId, List<PositionResult> positions, boolean published) {

    public ElectionResult {
        positions = List.copyOf(positions);
    }
}

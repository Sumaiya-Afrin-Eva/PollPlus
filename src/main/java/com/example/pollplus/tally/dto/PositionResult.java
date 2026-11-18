package com.example.pollplus.tally.dto;

import java.util.List;

/**
 * Tally outcome for a single position.
 *
 * @param positionId         the tallied position
 * @param winnerCandidateIds winners in vote order; may hold more ids than
 *                           {@code maxWinners} when the cutoff is tied
 * @param tie                {@code true} when more candidates share the cutoff
 *                           than there are available winner slots
 * @param tallies            every candidate with their vote count, highest first
 * @param totalVotes         number of ballots counted for this position
 */
public record PositionResult(
        long positionId,
        List<Long> winnerCandidateIds,
        boolean tie,
        List<CandidateTally> tallies,
        long totalVotes) {

    public PositionResult {
        winnerCandidateIds = List.copyOf(winnerCandidateIds);
        tallies = List.copyOf(tallies);
    }
}

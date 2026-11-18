package com.example.pollplus.tally.strategy;

import com.example.pollplus.tally.dto.PositionResult;

import java.util.List;

/**
 * Strategy contract for counting votes.
 *
 * <p>Swapping the counting rule (first-past-the-post, ranked choice, ...) must
 * not require changes to the service that stores or publishes results.
 */
public interface TallyStrategy {

    /**
     * @param positionId       position being tallied
     * @param maxWinners       number of winner slots for the position
     * @param candidateIds     candidates contesting the position
     * @param votedCandidateIds one entry per counted vote (duplicates allowed)
     */
    PositionResult tally(long positionId,
                         int maxWinners,
                         List<Long> candidateIds,
                         List<Long> votedCandidateIds);
}

package com.example.pollplus.tally.strategy;

import com.example.pollplus.tally.dto.CandidateTally;
import com.example.pollplus.tally.dto.PositionResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * First-past-the-post tally with multi-winner and tie support.
 *
 * <p>Winners are the candidates whose vote count is at or above the cutoff for
 * the last available slot. If more candidates share that cutoff than there are
 * slots, every tied candidate is returned and {@link PositionResult#tie()} is
 * {@code true}, so the caller can escalate instead of silently picking one.
 */
public final class FirstPastThePostStrategy implements TallyStrategy {

    @Override
    public PositionResult tally(long positionId,
                                int maxWinners,
                                List<Long> candidateIds,
                                List<Long> votedCandidateIds) {
        Objects.requireNonNull(candidateIds, "candidateIds");
        Objects.requireNonNull(votedCandidateIds, "votedCandidateIds");

        if (maxWinners < 1) {
            throw new IllegalArgumentException("maxWinners must be at least 1");
        }
        if (candidateIds.isEmpty()) {
            throw new IllegalArgumentException("candidateIds must not be empty");
        }

        Set<Long> uniqueCandidates = new LinkedHashSet<>(candidateIds);
        if (uniqueCandidates.size() != candidateIds.size() || uniqueCandidates.contains(null)) {
            throw new IllegalArgumentException("candidateIds must be non-null and unique");
        }

        Map<Long, Long> counts = new LinkedHashMap<>();
        for (Long candidateId : uniqueCandidates) {
            counts.put(candidateId, 0L);
        }

        long totalVotes = 0;
        for (Long votedCandidateId : votedCandidateIds) {
            if (votedCandidateId == null || !counts.containsKey(votedCandidateId)) {
                throw new IllegalArgumentException(
                        "Vote references candidate " + votedCandidateId
                                + " who is not contesting position " + positionId);
            }
            counts.merge(votedCandidateId, 1L, Long::sum);
            totalVotes++;
        }

        List<CandidateTally> tallies = counts.entrySet().stream()
                .map(entry -> new CandidateTally(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(CandidateTally::votes)
                        .reversed()
                        .thenComparingLong(CandidateTally::candidateId))
                .toList();

        if (totalVotes == 0) {
            return new PositionResult(positionId, List.of(), false, tallies, 0);
        }

        int slots = Math.min(maxWinners, tallies.size());
        long cutoff = tallies.get(slots - 1).votes();

        List<Long> winners = new ArrayList<>();
        for (CandidateTally candidateTally : tallies) {
            if (candidateTally.votes() >= cutoff) {
                winners.add(candidateTally.candidateId());
            }
        }

        boolean tie = winners.size() > maxWinners;
        return new PositionResult(positionId, winners, tie, tallies, totalVotes);
    }
}

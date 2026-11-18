package com.example.pollplus.tally.strategy;

import com.example.pollplus.tally.dto.CandidateTally;
import com.example.pollplus.tally.dto.PositionResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FirstPastThePostStrategyTest {

    private final TallyStrategy strategy = new FirstPastThePostStrategy();

    @Test
    void shouldElectCandidateWithMostVotes() {
        PositionResult result = strategy.tally(
                10L, 1, List.of(1L, 2L, 3L), List.of(1L, 2L, 1L));

        assertEquals(10L, result.positionId());
        assertEquals(List.of(1L), result.winnerCandidateIds());
        assertFalse(result.tie());
        assertEquals(3, result.totalVotes());
        assertEquals(new CandidateTally(1L, 2L), result.tallies().get(0));
    }

    @Test
    void shouldDetectTie_whenTopCandidatesHaveEqualVotes() {
        PositionResult result = strategy.tally(
                10L, 1, List.of(1L, 2L), List.of(1L, 2L));

        assertEquals(List.of(1L, 2L), result.winnerCandidateIds());
        assertTrue(result.tie());
    }

    @Test
    void shouldDetectTieAtCutoff_whenMultipleWinners() {
        PositionResult result = strategy.tally(
                10L, 2, List.of(1L, 2L, 3L), List.of(1L, 1L, 2L, 2L, 3L, 3L));

        assertEquals(List.of(1L, 2L, 3L), result.winnerCandidateIds());
        assertTrue(result.tie());
    }

    @Test
    void shouldElectMultipleWinners_whenNoTie() {
        PositionResult result = strategy.tally(
                10L, 2, List.of(1L, 2L, 3L), List.of(1L, 2L, 3L, 2L, 3L));

        assertEquals(List.of(2L, 3L), result.winnerCandidateIds());
        assertFalse(result.tie());
        assertEquals(5, result.totalVotes());
    }

    @Test
    void shouldReturnNoWinner_whenNoVotesCast() {
        PositionResult result = strategy.tally(
                10L, 1, List.of(1L, 2L), List.of());

        assertTrue(result.winnerCandidateIds().isEmpty());
        assertFalse(result.tie());
        assertEquals(0, result.totalVotes());
        assertEquals(2, result.tallies().size());
    }

    @Test
    void shouldRejectVoteForUnknownCandidate() {
        assertThrows(IllegalArgumentException.class,
                () -> strategy.tally(10L, 1, List.of(1L), List.of(9L)));
    }

    @Test
    void shouldRejectInvalidMaxWinners() {
        assertThrows(IllegalArgumentException.class,
                () -> strategy.tally(10L, 0, List.of(1L), List.of(1L)));
    }

    @Test
    void shouldRejectDuplicateCandidates() {
        assertThrows(IllegalArgumentException.class,
                () -> strategy.tally(10L, 1, List.of(1L, 1L), List.of(1L)));
    }
}

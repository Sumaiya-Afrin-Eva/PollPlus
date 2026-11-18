package com.example.pollplus.tally.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TurnoutStatsTest {

    @Test
    void shouldRoundPercentageToTwoDecimals() {
        TurnoutStats stats = TurnoutStats.of(300, 100);

        assertEquals(33.33, stats.percentage());
        assertEquals(300, stats.eligibleVoters());
        assertEquals(100, stats.votesCast());
    }

    @Test
    void shouldReturnZeroPercentage_whenNobodyEligible() {
        assertEquals(0.0, TurnoutStats.of(0, 0).percentage());
    }

    @Test
    void shouldRejectMoreVotesThanEligibleVoters() {
        assertThrows(IllegalArgumentException.class, () -> TurnoutStats.of(10, 11));
    }
}

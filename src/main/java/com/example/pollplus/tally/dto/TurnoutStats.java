package com.example.pollplus.tally.dto;

/**
 * Turnout figures used by the live turnout counter and the final report.
 *
 * @param eligibleVoters registered voters for the election
 * @param votesCast      voters who have already voted
 * @param percentage     {@code votesCast / eligibleVoters}, rounded to 2 decimals
 */
public record TurnoutStats(long eligibleVoters, long votesCast, double percentage) {

    public TurnoutStats {
        if (eligibleVoters < 0) {
            throw new IllegalArgumentException("eligibleVoters must not be negative");
        }
        if (votesCast < 0 || votesCast > eligibleVoters) {
            throw new IllegalArgumentException("votesCast must be between 0 and eligibleVoters");
        }
    }

    public static TurnoutStats of(long eligibleVoters, long votesCast) {
        double percentage = eligibleVoters == 0
                ? 0.0
                : Math.round(votesCast * 10_000.0 / eligibleVoters) / 100.0;
        return new TurnoutStats(eligibleVoters, votesCast, percentage);
    }
}

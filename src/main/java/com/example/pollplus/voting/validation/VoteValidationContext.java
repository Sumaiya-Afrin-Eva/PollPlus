package com.example.pollplus.voting.validation;

import java.util.Map;
import java.util.Objects;

/**
 * Everything the validation chain needs to accept or reject one ballot.
 *
 * <p>A ballot is submitted once per election and carries a selection for every
 * position, because the registry allows only one vote per voter per election
 * ({@code has_voted} is a single flag). Position id maps to candidate id.
 */
public record VoteValidationContext(
        long electionId,
        long voterId,
        String rawToken,
        Map<Long, Long> selections) {

    public VoteValidationContext {
        if (electionId <= 0) {
            throw new IllegalArgumentException("electionId must be positive");
        }
        if (voterId <= 0) {
            throw new IllegalArgumentException("voterId must be positive");
        }
        rawToken = Objects.requireNonNull(rawToken, "rawToken");
        if (rawToken.isBlank()) {
            throw new IllegalArgumentException("rawToken must not be blank");
        }
        selections = Map.copyOf(Objects.requireNonNull(selections, "selections"));
        if (selections.isEmpty()) {
            throw new IllegalArgumentException("selections must contain at least one position");
        }
    }
}

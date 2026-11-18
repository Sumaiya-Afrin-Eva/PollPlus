package com.example.pollplus.voting.validation;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VoteValidationContextTest {

    @Test
    void shouldCopySelectionsDefensively() {
        Map<Long, Long> selections = new HashMap<>();
        selections.put(10L, 100L);

        VoteValidationContext context = new VoteValidationContext(1L, 2L, "token", selections);
        selections.put(11L, 101L);

        assertEquals(Map.of(10L, 100L), context.selections());
    }

    @Test
    void shouldRejectEmptySelections() {
        assertThrows(IllegalArgumentException.class,
                () -> new VoteValidationContext(1L, 2L, "token", Map.of()));
    }

    @Test
    void shouldRejectBlankToken() {
        assertThrows(IllegalArgumentException.class,
                () -> new VoteValidationContext(1L, 2L, "  ", Map.of(10L, 100L)));
    }
}

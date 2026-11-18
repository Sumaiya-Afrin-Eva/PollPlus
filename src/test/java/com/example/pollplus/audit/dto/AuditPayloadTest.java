package com.example.pollplus.audit.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditPayloadTest {

    @Test
    void shouldCanonicalizeInFrozenFieldOrder() {
        AuditPayload payload = new AuditPayload(
                7L, "LOGIN", "User", 7L, "login success", Instant.ofEpochMilli(1700000000000L));

        assertEquals("7|LOGIN|User|7|login success|1700000000000", payload.canonical());
    }

    @Test
    void shouldRenderMissingValuesAsEmptySegments() {
        AuditPayload payload = new AuditPayload(null, "SYSTEM", null, null, null, Instant.EPOCH);

        assertEquals("|SYSTEM||||0", payload.canonical());
    }

    @Test
    void shouldEscapeDelimiterCharacters() {
        AuditPayload payload = new AuditPayload(null, "A|B", "Election", 1L, "C:\\votes", Instant.EPOCH);

        assertEquals("|A\\|B|Election|1|C:\\\\votes|0", payload.canonical());
    }
}

package com.example.pollplus.audit.dto;

import java.time.Instant;
import java.util.Objects;

/**
 * The exact data that gets hashed into an audit entry.
 *
 * <p>Canonical form (frozen team contract):
 * <pre>
 *   actorId|action|entityType|entityId|details|epochMillis
 * </pre>
 * Missing values become empty segments, and the pipe/backslash characters are
 * escaped so a caller cannot forge a different logical payload. Timestamps are
 * reduced to epoch milliseconds so that a database round-trip cannot change the
 * payload (PostgreSQL keeps microsecond precision, Java keeps nanoseconds).
 */
public record AuditPayload(
        Long actorId,
        String action,
        String entityType,
        Long entityId,
        String details,
        Instant createdAt) {

    public AuditPayload {
        Objects.requireNonNull(createdAt, "createdAt");
    }

    public String canonical() {
        return String.join("|",
                actorId == null ? "" : actorId.toString(),
                escape(action),
                escape(entityType),
                entityId == null ? "" : entityId.toString(),
                escape(details),
                Long.toString(createdAt.toEpochMilli()));
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }
}

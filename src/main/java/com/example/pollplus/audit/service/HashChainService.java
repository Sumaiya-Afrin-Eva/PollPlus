package com.example.pollplus.audit.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Builds SHA-256 hash links for the tamper-evident audit log.
 *
 * <p>Canonical link format (frozen team contract):
 * <pre>
 *   hash = SHA-256(previousHash + "|" + canonicalPayload)
 *   genesis previousHash = 64 zero characters
 * </pre>
 * Any change to this format invalidates previously stored chains, so it must
 * not be modified without informing the whole team.
 */
public class HashChainService {

    /** Previous-hash value used by the first entry of a chain. */
    public static final String GENESIS_HASH = "0".repeat(64);

    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * Computes the hash for one audit entry.
     *
     * @param previousHash    hash of the previous entry, or {@link #GENESIS_HASH}
     * @param canonicalPayload payload produced by
     *                        {@link com.example.pollplus.audit.dto.AuditPayload#canonical()}
     * @return lowercase hexadecimal SHA-256 digest
     */
    public String computeHash(String previousHash, String canonicalPayload) {
        return sha256(requireText(previousHash, "previousHash") + "|"
                + requireText(canonicalPayload, "canonicalPayload"));
    }

    /** Returns the lowercase hexadecimal SHA-256 digest of {@code value}. */
    public String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashed = digest.digest(requireText(value, "value").getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available in this JVM", ex);
        }
    }

    /** Checks a single stored link against its recomputed hash. */
    public boolean isValidLink(String previousHash, String canonicalPayload, String storedHash) {
        if (storedHash == null) {
            return false;
        }
        return computeHash(previousHash, canonicalPayload).equalsIgnoreCase(storedHash);
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}

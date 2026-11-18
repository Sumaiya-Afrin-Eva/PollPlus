package com.example.pollplus.audit.dto;

/**
 * Outcome of verifying a hash chain.
 *
 * @param valid          {@code true} when every entry matched
 * @param entriesChecked number of entries verified before stopping
 * @param brokenEntryId  id of the first entry that failed, or {@code null} when valid
 */
public record VerificationResult(boolean valid, int entriesChecked, Long brokenEntryId) {

    public static VerificationResult valid(int entriesChecked) {
        return new VerificationResult(true, entriesChecked, null);
    }

    public static VerificationResult broken(int entriesChecked, long brokenEntryId) {
        return new VerificationResult(false, entriesChecked, brokenEntryId);
    }
}

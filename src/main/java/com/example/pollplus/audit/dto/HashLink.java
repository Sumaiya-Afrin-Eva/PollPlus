package com.example.pollplus.audit.dto;

import java.util.Objects;

/**
 * One entry of a hash chain, reduced to the fields needed to verify it.
 *
 * @param id           database id of the entry (reported when the chain breaks)
 * @param payload      canonical payload that was hashed
 * @param previousHash hash of the previous entry, or the genesis hash
 * @param hash         stored hash of this entry
 */
public record HashLink(long id, String payload, String previousHash, String hash) {

    public HashLink {
        Objects.requireNonNull(payload, "payload");
        Objects.requireNonNull(previousHash, "previousHash");
        Objects.requireNonNull(hash, "hash");
    }
}

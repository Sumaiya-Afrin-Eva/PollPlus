package com.example.pollplus.audit.service;

import com.example.pollplus.audit.dto.HashLink;
import com.example.pollplus.audit.dto.VerificationResult;

import java.util.List;
import java.util.Objects;

/**
 * Walks a hash chain in ascending id order and reports the first broken link.
 *
 * <p>A link breaks when its stored {@code previousHash} does not match the
 * previous entry, or when its own payload no longer hashes to the stored value.
 */
public class HashChainVerifier {

    private final HashChainService hashChainService;

    public HashChainVerifier(HashChainService hashChainService) {
        this.hashChainService = Objects.requireNonNull(hashChainService, "hashChainService");
    }

    public VerificationResult verify(List<HashLink> orderedLinks) {
        Objects.requireNonNull(orderedLinks, "orderedLinks");

        String expectedPrevious = HashChainService.GENESIS_HASH;
        int checked = 0;

        for (HashLink link : orderedLinks) {
            boolean previousMatches = expectedPrevious.equalsIgnoreCase(link.previousHash());
            boolean hashMatches = hashChainService.isValidLink(
                    link.previousHash(), link.payload(), link.hash());

            if (!previousMatches || !hashMatches) {
                return VerificationResult.broken(checked, link.id());
            }

            expectedPrevious = link.hash();
            checked++;
        }

        return VerificationResult.valid(checked);
    }
}

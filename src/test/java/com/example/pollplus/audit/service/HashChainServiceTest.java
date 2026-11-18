package com.example.pollplus.audit.service;

import com.example.pollplus.audit.dto.HashLink;
import com.example.pollplus.audit.dto.VerificationResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashChainServiceTest {

    private static final String PAYLOAD_ONE = "1|CREATE_USER|User|1|created user|1700000000000";
    private static final String PAYLOAD_TWO = "1|LOGIN|User|1|login success|1700000001000";

    private final HashChainService hashChainService = new HashChainService();
    private final HashChainVerifier verifier = new HashChainVerifier(hashChainService);

    @Test
    void shouldProduceStableHashForSameInput() {
        String first = hashChainService.computeHash(HashChainService.GENESIS_HASH, PAYLOAD_ONE);
        String second = hashChainService.computeHash(HashChainService.GENESIS_HASH, PAYLOAD_ONE);

        assertEquals(first, second);
    }

    @Test
    void shouldProduceDifferentHash_whenPayloadChanges() {
        String original = hashChainService.computeHash(HashChainService.GENESIS_HASH, PAYLOAD_ONE);
        String tampered = hashChainService.computeHash(HashChainService.GENESIS_HASH, PAYLOAD_ONE + "!");

        assertNotEquals(original, tampered);
    }

    @Test
    void shouldProduceLowercaseSixtyFourCharacterHex() {
        String hash = hashChainService.computeHash(HashChainService.GENESIS_HASH, PAYLOAD_ONE);

        assertEquals(64, hash.length());
        assertEquals(hash.toLowerCase(), hash);
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    void shouldVerifyValidChain() {
        List<HashLink> chain = validChain();

        VerificationResult result = verifier.verify(chain);

        assertTrue(result.valid());
        assertEquals(2, result.entriesChecked());
        assertNull(result.brokenEntryId());
    }

    @Test
    void shouldDetectTamperedPayload() {
        List<HashLink> chain = validChain();
        HashLink tampered = new HashLink(
                2L,
                PAYLOAD_TWO + " (edited)",
                chain.get(1).previousHash(),
                chain.get(1).hash());

        VerificationResult result = verifier.verify(List.of(chain.get(0), tampered));

        assertFalse(result.valid());
        assertEquals(1, result.entriesChecked());
        assertEquals(2L, result.brokenEntryId());
    }

    @Test
    void shouldDetectBrokenPreviousHash() {
        HashLink orphan = new HashLink(
                2L,
                PAYLOAD_TWO,
                HashChainService.GENESIS_HASH,
                hashChainService.computeHash(HashChainService.GENESIS_HASH, PAYLOAD_TWO));

        VerificationResult result = verifier.verify(List.of(validChain().get(0), orphan));

        assertFalse(result.valid());
        assertEquals(1, result.entriesChecked());
        assertEquals(2L, result.brokenEntryId());
    }

    @Test
    void shouldTreatEmptyChainAsValid() {
        VerificationResult result = verifier.verify(List.of());

        assertTrue(result.valid());
        assertEquals(0, result.entriesChecked());
    }

    private List<HashLink> validChain() {
        String firstHash = hashChainService.computeHash(HashChainService.GENESIS_HASH, PAYLOAD_ONE);
        String secondHash = hashChainService.computeHash(firstHash, PAYLOAD_TWO);
        return List.of(
                new HashLink(1L, PAYLOAD_ONE, HashChainService.GENESIS_HASH, firstHash),
                new HashLink(2L, PAYLOAD_TWO, firstHash, secondHash));
    }
}

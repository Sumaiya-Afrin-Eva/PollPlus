package com.example.pollplus.voting.validation;

import com.example.pollplus.voting.port.BallotTokenPort;
import com.example.pollplus.voting.port.ElectionStatusPort;
import com.example.pollplus.voting.port.VoterEligibilityPort;
import com.example.pollplus.voting.port.VoterRegistryPort;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VoteValidationChainTest {

    private static final long ELECTION_ID = 1L;
    private static final long VOTER_ID = 2L;
    private static final String RAW_TOKEN = "raw-ballot-token";

    private final StubElectionStatus electionStatus = new StubElectionStatus(true);
    private final StubVoterEligibility voterEligibility = new StubVoterEligibility(true);
    private final StubBallotToken ballotToken = new StubBallotToken(BallotTokenPort.TokenStatus.VALID);
    private final StubVoterRegistry voterRegistry = new StubVoterRegistry(false);

    @Test
    void shouldAcceptBallot_whenEveryCheckPasses() {
        assertDoesNotThrow(() -> chain().validate(context()));

        assertTrue(electionStatus.called);
        assertTrue(voterEligibility.called);
        assertTrue(ballotToken.called);
        assertTrue(voterRegistry.called);
    }

    @Test
    void shouldReject_whenElectionNotOpen() {
        electionStatus.open = false;

        VoteValidationException exception = assertThrows(
                VoteValidationException.class, () -> chain().validate(context()));

        assertEquals(VoteValidationError.ELECTION_NOT_OPEN, exception.error());
        assertEquals(409, exception.suggestedStatus());
        assertFalse(voterEligibility.called, "later handlers must not run");
    }

    @Test
    void shouldReject_whenVoterNotEligible() {
        voterEligibility.eligible = false;

        VoteValidationException exception = assertThrows(
                VoteValidationException.class, () -> chain().validate(context()));

        assertEquals(VoteValidationError.VOTER_NOT_ELIGIBLE, exception.error());
        assertEquals(403, exception.suggestedStatus());
        assertFalse(ballotToken.called, "later handlers must not run");
    }

    @Test
    void shouldReject_whenTokenInvalid() {
        ballotToken.status = BallotTokenPort.TokenStatus.INVALID;

        VoteValidationException exception = assertThrows(
                VoteValidationException.class, () -> chain().validate(context()));

        assertEquals(VoteValidationError.INVALID_TOKEN, exception.error());
        assertFalse(voterRegistry.called, "later handlers must not run");
    }

    @Test
    void shouldReject_whenTokenExpired() {
        ballotToken.status = BallotTokenPort.TokenStatus.EXPIRED;

        VoteValidationException exception = assertThrows(
                VoteValidationException.class, () -> chain().validate(context()));

        assertEquals(VoteValidationError.TOKEN_EXPIRED, exception.error());
        assertEquals(401, exception.suggestedStatus());
    }

    @Test
    void shouldReject_whenTokenAlreadyUsed() {
        ballotToken.status = BallotTokenPort.TokenStatus.ALREADY_USED;

        VoteValidationException exception = assertThrows(
                VoteValidationException.class, () -> chain().validate(context()));

        assertEquals(VoteValidationError.TOKEN_ALREADY_USED, exception.error());
    }

    @Test
    void shouldReject_whenVoterAlreadyVoted() {
        voterRegistry.hasVoted = true;

        VoteValidationException exception = assertThrows(
                VoteValidationException.class, () -> chain().validate(context()));

        assertEquals(VoteValidationError.ALREADY_VOTED, exception.error());
        assertEquals(409, exception.suggestedStatus());
    }

    private VoteValidationChain chain() {
        return new VoteValidationChain(electionStatus, voterEligibility, ballotToken, voterRegistry);
    }

    private static VoteValidationContext context() {
        return new VoteValidationContext(ELECTION_ID, VOTER_ID, RAW_TOKEN, Map.of(10L, 100L, 11L, 101L));
    }

    private static final class StubElectionStatus implements ElectionStatusPort {

        private boolean open;
        private boolean called;

        private StubElectionStatus(boolean open) {
            this.open = open;
        }

        @Override
        public boolean isOpen(long electionId) {
            called = true;
            return open;
        }
    }

    private static final class StubVoterEligibility implements VoterEligibilityPort {

        private boolean eligible;
        private boolean called;

        private StubVoterEligibility(boolean eligible) {
            this.eligible = eligible;
        }

        @Override
        public boolean isEligible(long electionId, long voterId) {
            called = true;
            return eligible;
        }
    }

    private static final class StubBallotToken implements BallotTokenPort {

        private TokenStatus status;
        private boolean called;

        private StubBallotToken(TokenStatus status) {
            this.status = status;
        }

        @Override
        public TokenStatus verify(long electionId, long voterId, String rawToken) {
            called = true;
            return status;
        }
    }

    private static final class StubVoterRegistry implements VoterRegistryPort {

        private boolean hasVoted;
        private boolean called;

        private StubVoterRegistry(boolean hasVoted) {
            this.hasVoted = hasVoted;
        }

        @Override
        public boolean hasVoted(long electionId, long voterId) {
            called = true;
            return hasVoted;
        }
    }
}

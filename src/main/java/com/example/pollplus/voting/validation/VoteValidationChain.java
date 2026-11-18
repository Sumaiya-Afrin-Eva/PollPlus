package com.example.pollplus.voting.validation;

import com.example.pollplus.voting.port.BallotTokenPort;
import com.example.pollplus.voting.port.ElectionStatusPort;
import com.example.pollplus.voting.port.VoterEligibilityPort;
import com.example.pollplus.voting.port.VoterRegistryPort;

import java.util.Objects;

/**
 * Wires and runs the ballot validation chain:
 * <pre>
 *   election open -&gt; voter eligible -&gt; token valid -&gt; not already voted
 * </pre>
 *
 * <p>Deliberately not a Spring bean yet: the production adapters for the four
 * ports arrive with the voting facade, and creating the bean now would prevent
 * the existing {@code contextLoads} test from starting.
 */
public final class VoteValidationChain {

    private final VoteValidationHandler head;

    public VoteValidationChain(ElectionStatusPort electionStatus,
                               VoterEligibilityPort voterEligibility,
                               BallotTokenPort ballotTokenPort,
                               VoterRegistryPort voterRegistry) {
        this.head = new ElectionOpenHandler(Objects.requireNonNull(electionStatus, "electionStatus"));
        this.head.setNext(new VoterEligibleHandler(
                        Objects.requireNonNull(voterEligibility, "voterEligibility")))
                .setNext(new BallotTokenHandler(
                        Objects.requireNonNull(ballotTokenPort, "ballotTokenPort")))
                .setNext(new NotAlreadyVotedHandler(
                        Objects.requireNonNull(voterRegistry, "voterRegistry")));
    }

    public void validate(VoteValidationContext context) {
        head.validate(context);
    }
}

package eu.rstular.exclusions.detector;

import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.engine.MutationDetails;

public record ExclusionCandidate(String mutator, String filename, int lineNumber) {
    public ExclusionCandidate(MutationDetails details) {
        this(details.getMutator(), details.getFilename(), details.getLineNumber());
    }

    public ExclusionCandidate(MutationResult mutationResult) {
        this(mutationResult.getDetails());
    }
}

package eu.rstular.exclusions.detector;

import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;
import org.pitest.plugin.Feature;

import java.util.Properties;

public class ExclusionCandidateDetectorFactory implements MutationResultListenerFactory {
    @Override
    public MutationResultListener getListener(Properties properties, ListenerArguments listenerArguments) {
        return new ExclusionCandidateDetector(listenerArguments.data().getReportDir());
    }

    @Override
    public String name() {
        return "ExclusionCandidateDetector";
    }

    @Override
    public Feature provides() {
        return Feature.named("EXCLUSION_DETECTOR").withOnByDefault(true);
    }

    @Override
    public String description() {
        return "Detect potential mutation exclusion candidates.";
    }
}

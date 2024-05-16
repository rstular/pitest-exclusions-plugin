package eu.rstular.exclusions;

import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureParameter;

import java.util.Optional;

public class ExclusionFilterMutationInterceptorFactory implements MutationInterceptorFactory {
    public static final String FEATURE_NAME = "EXCLUSIONS";
    public static final String PAYLOAD_NAME = "payload";
    private final FeatureParameter exclusionsParam = FeatureParameter.named(PAYLOAD_NAME);

    @Override
    public MutationInterceptor createInterceptor(InterceptorParameters interceptorParameters) {
        Optional<String> exclusions = interceptorParameters.getString(exclusionsParam);

        if (exclusions.isEmpty()) {
            return new ExclusionFilterMutationInterceptor(new PitestExclusions());
        }

        PitestExclusions exclObj = PitestExclusions.fromFeatureString(exclusions.orElseThrow());
        return new ExclusionFilterMutationInterceptor(exclObj);
    }

    @Override
    public Feature provides() {
        return Feature.named(FEATURE_NAME).withParameter(exclusionsParam);
    }

    @Override
    public String description() {
        return "Exclude mutations based on inline code comments.";
    }
}

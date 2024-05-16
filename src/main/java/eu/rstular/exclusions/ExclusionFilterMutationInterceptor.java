package eu.rstular.exclusions;

import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.util.Log;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.logging.Logger;

class ExclusionFilterMutationInterceptor implements MutationInterceptor {
    private final static Logger LOG = Log.getLogger();

    private final PitestExclusions exclusions;

    public ExclusionFilterMutationInterceptor(PitestExclusions exclusions) {
        this.exclusions = exclusions;
    }

    @Override
    public void begin(ClassTree classTree) {
    }

    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }

    @Override
    public Collection<MutationDetails> intercept(Collection<MutationDetails> collection, Mutater mutater) {
        Collection<MutationDetails> filteredMutations = collection.stream()
                .filter(mutation -> !exclusions.isExcluded(mutation.getMutator(), mutation.getLineNumber())).toList();

        LOG.info(MessageFormat.format("Started with {0} mutations, filtered list contains {1}",
                collection.size(),
                filteredMutations.size()));

        return filteredMutations;
    }

    @Override
    public void end() {
    }
}

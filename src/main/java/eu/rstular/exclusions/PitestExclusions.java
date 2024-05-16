package eu.rstular.exclusions;

import com.google.gson.Gson;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.config.Mutator;

import java.util.*;

public class PitestExclusions {
    /**
     * The list of exclusions. Maps from the (internal) mutator name to a list of excluded lines.
     */
    private final Map<String, Set<Integer>> mutatorExclusions = new HashMap<>();

    /**
     * Convert the name of a mutator from the "friendly" name (e.g. TRUE_RETURNS)
     * to the internal representation (class name).
     *
     * @param mutatorName friendly name of the mutator.
     * @return Internal name (class name) of the mutator.
     */
    static Optional<String> convertMutationKey(String mutatorName) {
        Optional<MethodMutatorFactory> mutator = Mutator.byName(mutatorName).stream().findFirst();
        return mutator.map(methodMutatorFactory -> methodMutatorFactory.getClass().getName());
    }

    /**
     * Convert the exclusions into a serialized representation that can be used with Pitest features.
     *
     * @see org.pitest.plugin.Feature
     * @see #fromFeatureString(String)
     * @return Serialized representation of the exclusion
     */
    public String toFeatureString() {
        Gson gson = new Gson();
        return Base64.getEncoder().encodeToString(gson.toJson(this).getBytes());
    }

    /**
     * Create a new exclusion object from a serialized representation.
     *
     * @see org.pitest.plugin.Feature
     * @see #toFeatureString()
     * @param featureString Feature string to deserialize
     * @return Constructed {@link PitestExclusions} object
     */
    public static PitestExclusions fromFeatureString(String featureString) {
        Gson gson = new Gson();
        byte[] decodedBytes = Base64.getDecoder().decode(featureString);
        return gson.fromJson(new String(decodedBytes), PitestExclusions.class);
    }

    /**
     * Check if the given mutation satisfies the exclusion criteria.
     *
     * @param mutator Mutator that has mutated the line
     * @param line Line the mutation is present on
     * @return A boolean indicating whether the mutation should be excluded or not.
     */
    public boolean isExcluded(String mutator, int line) {
        Set<Integer> exclusions = mutatorExclusions.get(mutator);
        return exclusions != null && exclusions.contains(line);
    }

    /**
     * Exclude a specific mutator from a specific line number.
     *
     * @param line Line number that should exclude the specific mutator
     * @param mutator Mutator that should not execute
     */
    public void addLineExclusion(int line, String mutator) {
        if (line <= 0) {
            throw new IllegalArgumentException("Line number must be greater than 0");
        }

        String internalMutatorKey = convertMutationKey(mutator).orElseThrow(() -> new IllegalArgumentException("Invalid mutator: " + mutator));
        Set<Integer> mutatorLineExclusions = mutatorExclusions.getOrDefault(internalMutatorKey, new HashSet<>());
        mutatorLineExclusions.add(line);
        mutatorExclusions.put(internalMutatorKey, mutatorLineExclusions);
    }

    /**
     * Exclude a specific mutator from a collection of line numbers.
     *
     * @param mutation The mutation to exclude
     * @param lines The lines to exclude the mutation from
     */
    public void addMutationExclusions(String mutation, Collection<Integer> lines) {
        String internalMutatorKey = convertMutationKey(mutation).orElseThrow(() -> new IllegalArgumentException("Invalid mutator: " + mutation));
        Set<Integer> mutatorLineExclusions = mutatorExclusions.getOrDefault(internalMutatorKey, new HashSet<>());
        mutatorLineExclusions.addAll(lines);
        mutatorExclusions.put(internalMutatorKey, mutatorLineExclusions);
    }

    /**
     * Get the number of excluded mutators.
     *
     * @return The number of excluded mutators.
     */
    public int getNumberOfExcludedMutators() {
        return this.mutatorExclusions.size();
    }
}

package eu.rstular.exclusions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PitestExclusionsTest {
    private final static String TRUE_RETURNS = "TRUE_RETURNS";
    private final static String NONEXISTENT_MUTATOR = "NONEXISTENT_MUTATOR";

    @Test
    void convertMutationKeyKnown() {
        assertTrue(PitestExclusions.convertMutationKey(TRUE_RETURNS).isPresent());
    }

    @Test
    void convertMutationKeyUnknown() {
        assertTrue(PitestExclusions.convertMutationKey(NONEXISTENT_MUTATOR).isEmpty());
    }

    @Test
    void addLineExclusionUnknownMutator() {
        PitestExclusions exclusions = new PitestExclusions();
        assertThrows(IllegalArgumentException.class, () -> exclusions.addLineExclusion(1, NONEXISTENT_MUTATOR));
        assertEquals(0, exclusions.getNumberOfExcludedMutators());
    }

    @Test
    void addLineExclusionLineZero() {
        PitestExclusions exclusions = new PitestExclusions();
        assertThrows(IllegalArgumentException.class, () -> exclusions.addLineExclusion(0, TRUE_RETURNS));
        assertEquals(0, exclusions.getNumberOfExcludedMutators());
    }

    @Test
    void addLineExclusion() {
        PitestExclusions exclusions = new PitestExclusions();
        exclusions.addLineExclusion(1, TRUE_RETURNS);
        assertEquals(1, exclusions.getNumberOfExcludedMutators());
    }

    @Test
    void testSerialization() {
        PitestExclusions exclusions = new PitestExclusions();
        exclusions.addLineExclusion(1, TRUE_RETURNS);

        PitestExclusions newExclusions = PitestExclusions.fromFeatureString(exclusions.toFeatureString());
        assertNotSame(exclusions, newExclusions);

        assertEquals(1, newExclusions.getNumberOfExcludedMutators());
    }

    @Test
    void matchingMutationIsExcluded() {
        PitestExclusions exclusions = new PitestExclusions();
        exclusions.addLineExclusion(1, TRUE_RETURNS);

        String convertedMutationKey = PitestExclusions.convertMutationKey(TRUE_RETURNS).orElseThrow();

        assertTrue(exclusions.isExcluded(convertedMutationKey, 1));
    }

    @Test
    void nonMatchingMutationLineIsNotExcluded() {
        PitestExclusions exclusions = new PitestExclusions();
        exclusions.addLineExclusion(1, TRUE_RETURNS);

        String convertedMutationKey = PitestExclusions.convertMutationKey(TRUE_RETURNS).orElseThrow();

        assertFalse(exclusions.isExcluded(convertedMutationKey, 2));
    }

    @Test
    void nonMatchingMutationIsNotExcluded() {
        PitestExclusions exclusions = new PitestExclusions();
        exclusions.addLineExclusion(1, TRUE_RETURNS);

        assertFalse(exclusions.isExcluded(NONEXISTENT_MUTATOR, 1));
    }

    @Test
    void matchingMutationsIsExcluded() {
        PitestExclusions exclusions = new PitestExclusions();
        exclusions.addMutationExclusions(TRUE_RETURNS, List.of(2, 3));

        String convertedMutationKey = PitestExclusions.convertMutationKey(TRUE_RETURNS).orElseThrow();

        assertTrue(exclusions.isExcluded(convertedMutationKey, 2));
        assertTrue(exclusions.isExcluded(convertedMutationKey, 3));

        assertFalse(exclusions.isExcluded(convertedMutationKey, 1));
        assertFalse(exclusions.isExcluded(convertedMutationKey, 4));
    }
}
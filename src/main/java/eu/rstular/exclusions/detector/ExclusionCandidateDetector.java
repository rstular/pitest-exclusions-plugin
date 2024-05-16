package eu.rstular.exclusions.detector;

import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.util.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

class ExclusionCandidateDetector implements MutationResultListener {
    private final static Logger LOG = Log.getLogger();
    private final String reportDirectory;
    private final List<ExclusionCandidate> exclusionCandidates = new ArrayList<>();

    ExclusionCandidateDetector(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    @Override
    public void runStart() {
    }

    @Override
    public void handleMutationResult(ClassMutationResults classMutationResults) {
        exclusionCandidates.addAll(classMutationResults.getMutations().stream()
                .filter(mutation -> mutation.getStatus() == DetectionStatus.MEMORY_ERROR || mutation.getStatus() == DetectionStatus.TIMED_OUT)
                .map(ExclusionCandidate::new).toList());
    }

    @Override
    public void runEnd() {
        LOG.info("Exclusion candidates: " + exclusionCandidates.size());

        StringBuilder outReport = new StringBuilder("file,number,mutator\n");
        for (ExclusionCandidate candidate : exclusionCandidates) {
            outReport.append(candidate.filename());
            outReport.append(',');
            outReport.append(candidate.lineNumber());
            outReport.append(',');
            outReport.append(candidate.mutator());
            outReport.append('\n');
        }

        Path exclusionReportPath = Paths.get(reportDirectory, "exclusion_candidates.csv");
        try {
            Files.write(exclusionReportPath, outReport.toString().getBytes());
        } catch (IOException e) {
            LOG.severe("Could not write exclusion candidate report file: " + e.toString());
            throw new RuntimeException(e);
        }
    }
}

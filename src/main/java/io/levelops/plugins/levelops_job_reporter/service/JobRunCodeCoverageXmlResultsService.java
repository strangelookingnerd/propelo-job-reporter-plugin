package io.levelops.plugins.levelops_job_reporter.service;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobRunCodeCoverageXmlResultsService {
    private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    public static final String LEVELOPS_CODE_COVERAGE_XML_ZIP = "levelops_code_coverage_xml.zip";
    private static final String BULLSEYE_XML_RESULTS_PATH = "**/*.cov,**/*.cqv";

    public void gatherJobRunCodeCoverageXmlResults(Run run, String bullseyeXmlResultPaths, File completeDataDirectory){
        if(StringUtils.isBlank(bullseyeXmlResultPaths)) {
            LOGGER.log(Level.FINE, "Cannot gather job run code coverage xml results, bullseyeXmlResultPaths is blank");
            return;
        }
        if (!(run instanceof AbstractBuild)) {
            LOGGER.log(Level.FINE, "Cannot gather job run code coverage xml results, run is NOT instanceof AbstractBuild");
            return;
        }

        LOGGER.log(Level.FINEST, "run is instanceof AbstractBuild");
        AbstractBuild build = (AbstractBuild) run;
        FilePath workspace = build.getWorkspace();
        if (workspace == null) {
            LOGGER.log(Level.FINE, "Cannot gather job run code coverage xml results, no workspace in " + build.toString());
            return;
        }
        try {
            LOGGER.log(Level.FINEST, "Propelo code coverage xml post build publisher called");
            final List<Byte> bytes = workspace.act(new ArchiveJobRunResultFiles(bullseyeXmlResultPaths));
            final File codeCoverageResults = new File(completeDataDirectory, LEVELOPS_CODE_COVERAGE_XML_ZIP);
            LOGGER.log(Level.FINEST, "codeCoverageResults = {0}", codeCoverageResults);
            FileUtils.writeByteArrayToFile(codeCoverageResults, ArrayUtils.toPrimitive(bytes.toArray(new Byte[0])));
            LOGGER.log(Level.FINEST, "Stored code coverage xml result zip");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in Propelo code coverage xml PostBuildPublisher perform!", e);
        }
    }
}

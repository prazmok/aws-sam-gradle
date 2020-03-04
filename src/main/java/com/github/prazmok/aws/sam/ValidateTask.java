package com.github.prazmok.aws.sam;

import com.github.prazmok.aws.sam.command.SamCommandBuilder;
import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

public class ValidateTask extends SamCliTask {
    private final Config config;
    private final SamCommandBuilder samCommandBuilder;

    @Inject
    public ValidateTask(Config config) {
        this.config = config;
        this.samCommandBuilder = new SamCommandBuilder(logger, config.isDryRunOption());
    }

    @TaskAction
    protected void validateSam() {
        ExecResult result = getProject().exec((action) -> {
            action.commandLine(buildCommand());
        });

        if (result.getExitValue() == 0) {
            logger.lifecycle("AWS SAM template " + config.getSamTemplate().getPath() + " is valid");
        }

        result.rethrowFailure();
    }

    public Set<String> buildCommand() {
        try {
            samCommandBuilder.task("validate")
                .option("--debug", config.debug())
                .argument("--template-file", getSamTemplatePath().getAbsolutePath())
                .argument("--profile", config.getAwsProfile())
                .argument("--region", config.getAwsRegion());

            return samCommandBuilder.build();
        } catch (MissingConfigurationException | FileNotFoundException e) {
            logger.error(e.toString());
        }

        return returnCodeCommand(1);
    }

    @InputFile
    File getSamTemplatePath() throws FileNotFoundException {
        File template = config.getSamTemplate();

        if (!template.exists() || !template.isFile()) {
            throw new FileNotFoundException("AWS SAM template couldn't been found in "
                + template.getAbsolutePath() + " location!");
        }

        return template;
    }

    @OutputFile
    File getOutputFile() throws IOException {
        String taskName = AwsSamPlugin.VALIDATE_TASK;
        File tmpDir = new File(getProject().getBuildDir() + "/tmp/" + taskName);

        if (!tmpDir.exists() && !tmpDir.mkdirs()) {
            logger.error("Couldn't create tmp directory for " + AwsSamPlugin.VALIDATE_TASK);
        }

        return Files.createTempFile(tmpDir.toPath(), taskName, Long.toString(System.currentTimeMillis()))
            .toFile();
    }
}

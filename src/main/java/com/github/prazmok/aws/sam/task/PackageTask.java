package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;
import com.github.prazmok.aws.sam.task.command.SamCommandBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

public class PackageTask extends DefaultTask implements CommandBuilderAwareInterface {
    private final Config config;
    private final Logger logger = getProject().getLogger();
    private final SamCommandBuilder samCommandBuilder = new SamCommandBuilder();

    @Inject
    public PackageTask(Config config) {
        this.config = config;
    }

    @TaskAction
    protected void packageSam() throws Exception {
        ExecResult result = getProject().exec((action) -> {
            action.commandLine(buildCommand());
        });

        if (result.getExitValue() == 0) {
            if (!config.getPackagedTemplate().exists()) {
                throw new Exception("Couldn't generate output SAM template!");
            }

            logger.lifecycle("Successfully created output SAM template: " + config.getPackagedTemplate().getPath());
        }

        result.rethrowFailure();
    }

    public Set<String> buildCommand() {
        try {
            samCommandBuilder.task("package")
                .option("--force-upload", config.forceUpload())
                .option("--use-json", config.useJson())
                .option("--debug", config.debug())
                .argument("--template-file", getSamTemplatePath())
                .argument("--output-template-file", getSamPackagedTemplate())
                .argument("--s3-bucket", config.getS3Bucket())
                .argument("--s3-prefix", config.getS3Prefix())
                .argument("--profile", config.getAwsProfile())
                .argument("--region", config.getAwsRegion())
                .argument("--kms-key-id", config.getKmsKeyId());

            Set<String> command = samCommandBuilder.build();

            if (config.isDryRunOption()) {
                logger.info("Dry run execution of command: " + String.join(" ", command));
                command = returnCodeCommand(0);
            }

            return command;
        } catch (MissingConfigurationException | FileNotFoundException e) {
            logger.error(e.toString());
        }

        return returnCodeCommand(1);
    }

    String getSamTemplatePath() throws FileNotFoundException {
        File template = config.getSamTemplate();

        if (!template.exists() || !template.isFile()) {
            throw new FileNotFoundException("AWS SAM template couldn't been found under "
                + template.getAbsolutePath() + " location!");
        }

        return template.getAbsolutePath();
    }

    String getSamPackagedTemplate() throws FileNotFoundException {
        File packaged = config.getPackagedTemplate();

        if (!packaged.getParentFile().exists() || !packaged.getParentFile().isDirectory()) {
            throw new FileNotFoundException("Provided packaged template directory ("
                + packaged.getParentFile().getAbsolutePath() + ") is invalid!");
        }

        return packaged.getAbsolutePath();
    }
}

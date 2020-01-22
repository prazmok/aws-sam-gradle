package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;
import com.github.prazmok.aws.sam.task.command.SamCommandBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;

import javax.inject.Inject;
import java.util.LinkedHashSet;

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
            if (!config.getOutputSamTemplate().exists()) {
                throw new Exception("Couldn't generate output SAM template!");
            }

            logger.lifecycle("Successfully created output SAM template: " + config.getOutputSamTemplate().getPath());
        }

        result.rethrowFailure();
    }

    public LinkedHashSet<String> buildCommand() {
        try {
            samCommandBuilder.task("package")
                .option("--force-upload", config.forceUpload())
                .option("--use-json", config.useJson())
                .option("--debug", config.debug())
                .argument("--template-file", config.getSamTemplate())
                .argument("--output-template-file", config.getOutputSamTemplate())
                .argument("--s3-bucket", config.getS3Bucket())
                .argument("--s3-prefix", config.getS3Prefix())
                .argument("--profile", config.getAwsProfile())
                .argument("--region", config.getAwsRegion())
                .argument("--kms-key-id", config.getKmsKeyId());

            return samCommandBuilder.build();
        } catch (MissingConfigurationException e) {
            logger.error(e.toString());
        }

        return errorCodeCommand(1);
    }
}

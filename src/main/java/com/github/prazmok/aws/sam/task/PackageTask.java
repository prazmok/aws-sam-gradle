package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.task.command.SamCommandBuilder;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.util.LinkedHashSet;

public class PackageTask extends Exec implements CommandBuilderAwareInterface {
    private final Config config;
    private final Logger logger;
    private final SamCommandBuilder samCommandBuilder = new SamCommandBuilder();

    @Inject
    public PackageTask(Config config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    @TaskAction
    @Override
    protected void exec() {
        try {
            commandLine(buildCommand());
            super.exec();

            if (!config.getOutputSamTemplate().exists()) {
                throw new Exception("Couldn't generate output SAM template!");
            }

            logger.lifecycle("Successfully generated output SAM template: " + config.getOutputSamTemplate().getAbsolutePath());
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public LinkedHashSet<String> buildCommand() throws Exception {
        samCommandBuilder.task("package")
            .option("--force-upload", config.forceUpload())
            .option("--use-json", config.useJson())
            .option("--debug", config.debug())
            .argument("--template-file", getGeneratedSamTemplate())
            .argument("--output-template-file", config.getOutputSamTemplate())
            .argument("--s3-bucket", config.getS3Bucket())
            .argument("--s3-prefix", config.getS3Prefix())
            .argument("--profile", config.getAwsProfile())
            .argument("--region", config.getAwsRegion())
            .argument("--kms-key-id", config.getKmsKeyId());

        return samCommandBuilder.build();
    }

    private String getGeneratedSamTemplate() throws Exception {
        if (!config.getGeneratedSamTemplate().exists()) {
            throw new Exception("Couldn't find generated SAM template file " + config.getGeneratedSamTemplate().getAbsolutePath() + "!");
        }

        return config.getGeneratedSamTemplate().getAbsolutePath();
    }
}

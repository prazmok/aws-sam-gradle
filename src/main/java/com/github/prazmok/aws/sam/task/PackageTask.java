package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.task.exec.SamCommandBuilder;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class PackageTask extends Exec {
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
            if (!config.getGeneratedSamTemplate().exists()) {
                throw new Exception("Couldn't find source SAM template! Please make sure \"generateSamTemplate\" task" +
                    " has been executed!");
            }

            samCommandBuilder.task("package")
                .option("--force-upload", config.forceUpload())
                .option("--use-json", config.useJson())
                .option("--debug", config.debug())
                .argument("--template-file", config.getGeneratedSamTemplate())
                .argument("--output-template-file", config.getOutputSamTemplate())
                .argument("--s3-bucket", config.getS3Bucket())
                .argument("--s3-prefix", config.getS3Prefix())
                .argument("--profile", config.getAwsProfile())
                .argument("--region", config.getAwsRegion())
                .argument("--kms-key-id", config.getKmsKeyId());

            commandLine(samCommandBuilder.build());

            super.exec();
        } catch (Exception e) {
            this.logger.error(e.toString());
        }
    }
}

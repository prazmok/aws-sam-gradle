package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.task.command.SamCommandBuilder;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskOutputs;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

public class DeployTask extends Exec implements CommandBuilderAwareInterface {
    private final Config config;
    private final Task packageSamTask;
    private final Logger logger;
    private final SamCommandBuilder samCommandBuilder = new SamCommandBuilder();

    @Inject
    public DeployTask(Config config, Task packageSamTask, Logger logger) {
        this.config = config;
        this.packageSamTask = packageSamTask;
        this.logger = logger;
    }

    @TaskAction
    @Override
    protected void exec() {
        try {
            commandLine(buildCommand());
            super.exec();

            this.logger.lifecycle("Deploy finished successfully");
        } catch (Exception e) {
            this.logger.error(e.toString());
        }
    }

    public LinkedHashSet<String> buildCommand() throws Exception {
        samCommandBuilder.task("deploy")
            .option("--force-upload", config.forceUpload())
            .option("--use-json", config.useJson())
            .option("--fail-on-empty-changeset", config.failOnEmptyChangeset())
            .option("--no-fail-on-empty-changeset", config.noFailOnEmptyChangeset())
            .option("--confirm-changeset", config.confirmChangeset())
            .option("--debug", config.debug())
            .argument("--template-file", getOutputSamTemplate())
            .argument("--stack-name", config.getStackName())
            .argument("--s3-bucket", config.getS3Bucket())
            .argument("--s3-prefix", config.getS3Prefix())
            .argument("--profile", config.getAwsProfile())
            .argument("--region", config.getAwsRegion())
            .argument("--kms-key-id", config.getKmsKeyId())
            .argument("--capabilities", listToArgValue(config.getCapabilities()))
            .argument("--notification-arns", listToArgValue(config.getNotificationArns()))
            .argument("--tags", listToArgValue(config.getTags()))
            .argument("--parameter-overrides", mapToArgValue(config.getParameterOverrides()));

        return samCommandBuilder.build();
    }

    private String getOutputSamTemplate() throws Exception {
        TaskOutputs outputs = Objects.requireNonNull(packageSamTask).getOutputs();
        File packageSamFile = outputs.getFiles().getSingleFile();

        if (!packageSamFile.exists()) {
            throw new Exception("Couldn't find source SAM template file " + packageSamFile.getAbsolutePath() + "!");
        }

        return packageSamFile.getAbsolutePath();
    }

    private String listToArgValue(List<String> input) {
        return String.join(",", input);
    }

    private String mapToArgValue(Map<String, Object> map) {
        List<String> list = new LinkedList<>();
        map.forEach((k, v) -> list.add(k + "=" + v));

        return String.join(" ", list);
    }
}

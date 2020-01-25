package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.task.command.SamCommandBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeployTask extends DefaultTask implements CommandBuilderAwareInterface {
    private final Config config;
    private final Logger logger = getProject().getLogger();
    private final SamCommandBuilder samCommandBuilder = new SamCommandBuilder();

    @Inject
    public DeployTask(Config config) {
        this.config = config;
    }

    @TaskAction
    protected void deploySam() {
        ExecResult result = getProject().exec((action) -> {
            action.commandLine(buildCommand());
        });

        if (result.getExitValue() == 0) {
            this.logger.lifecycle("SAM deploy finished");
        }

        result.rethrowFailure();
    }

    public Set<String> buildCommand() {
        try {
            samCommandBuilder.task("deploy")
                .option("--force-upload", config.forceUpload())
                .option("--use-json", config.useJson())
                .option("--fail-on-empty-changeset", config.failOnEmptyChangeset())
                .option("--no-fail-on-empty-changeset", config.noFailOnEmptyChangeset())
                .option("--confirm-changeset", config.confirmChangeset())
                .option("--debug", config.debug())
                .argument("--template-file", getSamPackagedTemplate())
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

            Set<String> command = samCommandBuilder.build();

            if (config.isDryRunOption()) {
                logger.info("Dry run execution of command: " + String.join(" ", command));
                command = returnCodeCommand(0);
            }

            return command;
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return returnCodeCommand(1);
    }

    String getSamPackagedTemplate() throws Exception {
        File packaged = config.getPackagedTemplate();

        if (!packaged.exists() || !packaged.isFile()) {
            throw new FileNotFoundException("Couldn't find packaged SAM template file "
                + packaged.getAbsolutePath() + "!");
        }

        return packaged.getAbsolutePath();
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

package com.github.prazmok.aws.sam;

import com.github.prazmok.aws.sam.command.SamCommandBuilder;
import com.github.prazmok.aws.sam.config.Config;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeployTask extends SamCliTask {
    private final Config config;
    private final SamCommandBuilder samCommandBuilder;

    @Inject
    public DeployTask(Config config) {
        this.config = config;
        this.samCommandBuilder = new SamCommandBuilder(logger, config.isDryRunOption());
    }

    @TaskAction
    protected void deploySam() {
        ExecResult result = getProject().exec((action) -> {
            action.commandLine(buildCommand());
        });

        if (result.getExitValue() == 0) {
            this.logger.lifecycle("Successfully finished AWS SAM deployment!");
        }

        result.rethrowFailure();
    }

    public Set<String> buildCommand() {
        try {
            samCommandBuilder.task("deploy")
                .option("--force-upload", config.forceUpload())
                .option("--use-json", config.useJson())
                .option("--no-execute-changeset", config.noExecuteChangeset())
                .option("--fail-on-empty-changeset", config.failOnEmptyChangeset())
                .option("--no-fail-on-empty-changeset", config.noFailOnEmptyChangeset())
                .option("--debug", config.debug())
                .argument("--template-file", samPackagedTemplate())
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
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return returnCodeCommand(1);
    }

    String samPackagedTemplate() throws Exception {
        File packaged = config.getPackagedTemplate();

        if (!config.isDryRunOption() && (!packaged.exists() || !packaged.isFile())) {
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

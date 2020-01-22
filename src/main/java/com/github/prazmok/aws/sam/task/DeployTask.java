package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.task.command.SamCommandBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;

import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    public LinkedHashSet<String> buildCommand() {
        try {
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
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return errorCodeCommand(1);
    }

    private String getOutputSamTemplate() throws Exception {
        if (!config.getOutputSamTemplate().exists()) {
            throw new Exception("Couldn't find source SAM template file " + config.getOutputSamTemplate().getAbsolutePath() + "!");
        }

        return config.getOutputSamTemplate().getAbsolutePath();
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

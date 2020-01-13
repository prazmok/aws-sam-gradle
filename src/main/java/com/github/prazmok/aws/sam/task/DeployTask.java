package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;
import com.github.prazmok.aws.sam.task.command.SamCommandBuilder;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DeployTask extends Exec implements CommandBuilderAwareInterface {
    private final Config config;
    private final Logger logger;
    private final SamCommandBuilder samCommandBuilder = new SamCommandBuilder();

    @Inject
    public DeployTask(Config config, Logger logger) {
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

            commandLine(buildCommand());
            super.exec();
        } catch (Exception e) {
            this.logger.error(e.toString());
        }
    }

    public LinkedHashSet<String> buildCommand() throws MissingConfigurationException {
        samCommandBuilder.task("deploy")
            .option("--force-upload", config.forceUpload())
            .option("--use-json", config.useJson())
            .option("--fail-on-empty-changeset", config.failOnEmptyChangeset())
            .option("--no-fail-on-empty-changeset", config.noFailOnEmptyChangeset())
            .option("--confirm-changeset", config.confirmChangeset())
            .option("--debug", config.debug())
            .argument("--template-file", config.getOutputSamTemplate())
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

    private String listToArgValue(List<String> input) {
        return String.join(",", input);
    }

    private String mapToArgValue(Map<String, Object> map) {
        List<String> list = new LinkedList<>();
        map.forEach((k, v) -> list.add(k + "=" + v));

        return String.join(" ", list);
    }
}

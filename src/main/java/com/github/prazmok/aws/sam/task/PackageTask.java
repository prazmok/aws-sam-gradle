package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;
import com.github.prazmok.aws.sam.task.command.SamCommandBuilder;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskOutputs;

import javax.inject.Inject;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Objects;

public class PackageTask extends Exec implements CommandBuilderAwareInterface {
    private final Config config;
    private final Task generateTemplateTask;
    private final Logger logger;
    private final SamCommandBuilder samCommandBuilder = new SamCommandBuilder();

    @Inject
    public PackageTask(Config config, Task generateTemplateTask, Logger logger) {
        this.config = config;
        this.generateTemplateTask = generateTemplateTask;
        this.logger = logger;
    }

    @OutputFile
    public File getOutputSamTemplate() throws MissingConfigurationException {
        return config.getOutputSamTemplate().getAbsoluteFile();
    }

    @TaskAction
    @Override
    protected void exec() {
        try {
            commandLine(buildCommand());
            super.exec();

            if (!getOutputSamTemplate().exists()) {
                throw new Exception("Couldn't generate output SAM template!");
            }

            this.logger.lifecycle("Successfully generated output SAM template: " + getOutputSamTemplate());
        } catch (Exception e) {
            this.logger.error(e.toString());
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
        TaskOutputs outputs = Objects.requireNonNull(generateTemplateTask).getOutputs();
        File generatedSamTemplateFile = outputs.getFiles().getSingleFile();

        if (!generatedSamTemplateFile.exists()) {
            throw new Exception("Couldn't find generated SAM template file " + generatedSamTemplateFile.getAbsolutePath() + "!");
        }

        return generatedSamTemplateFile.getAbsolutePath();
    }
}

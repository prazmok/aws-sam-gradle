package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

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
            if (!prepareSourceSamTemplate()) {
                throw new Exception("Couldn't generate source SAM template!");
            }

            samCommandBuilder.task("package")
                .option("--force-upload", config.forceUpload())
                .option("--use-json", config.useJson())
                .option("--debug", config.debug())
                .argument("--template-file", config.getGeneratedSamTemplatePath().toString())
                .argument("--output-template-file", config.getOutputSamTemplatePath().toString())
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

    // todo extract it to separate task perhaps???s
    private boolean prepareSourceSamTemplate() throws Exception {
        File tmpDir = config.getSamTmpDir();

        if (!tmpDir.exists() && !tmpDir.mkdirs()) {
            throw new Exception("Couldn't create temporary directory for SAM templates!");
        }

        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(config.getSamTemplate().toPath()));
        content = replaceCodeUriParam(content);

        Path generatedFile = Files.write(config.getGeneratedSamTemplatePath(), content.getBytes(charset));

        return generatedFile.toFile().exists();
    }

    private String replaceCodeUriParam(String content) {
        return content.replaceAll(
            "([\"'])?" + Pattern.quote("${CodeUri}") + "([\"'])?",
            '"' + config.getShadowJarFile().getAbsolutePath() + '"'
        );
    }
}

package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskOutputs;

import javax.inject.Inject;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.regex.Pattern;

public class GenerateTemplateTask extends DefaultTask {
    private final Config config;
    private final Task shadowJarTask;
    private final Logger logger;

    @Inject
    public GenerateTemplateTask(Config config, Task shadowJarTask, Logger logger) {
        this.config = config;
        this.shadowJarTask = shadowJarTask;
        this.logger = logger;
    }

    @TaskAction
    public void generateTemplate() throws Exception {
        File tmpDir = config.getTmpDir();

        if (!tmpDir.exists() && !tmpDir.mkdirs()) {
            throw new Exception("Couldn't create temporary directory for SAM templates!");
        }

        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(config.getSamTemplate().toPath()));
        content = replaceCodeUriParam(content);
        Files.write(config.getGeneratedSamTemplate().toPath(), content.getBytes(charset));

        if (!config.getGeneratedSamTemplate().exists()) {
            throw new Exception("Couldn't generate source SAM template!");
        }

        this.logger.lifecycle("Successfully generated SAM template: " + config.getGeneratedSamTemplate());
    }

    private String replaceCodeUriParam(String content) throws Exception {
        return content.replaceAll(
            "([\"'])?" + Pattern.quote("${CodeUri}") + "([\"'])?",
            '"' + getShadowJarPath() + '"'
        );
    }

    private String getShadowJarPath() throws Exception {
        TaskOutputs outputs = Objects.requireNonNull(shadowJarTask).getOutputs();
        File shadowJarFile = outputs.getFiles().getSingleFile();

        if (!shadowJarFile.exists()) {
            throw new Exception("Couldn't find JAR file " + shadowJarFile.getAbsolutePath() + "!");
        }

        return shadowJarFile.getAbsolutePath();
    }
}

package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class GenerateTemplateTask extends DefaultTask {
    private final Config config;

    @Inject
    public GenerateTemplateTask(Config config) {
        this.config = config;
    }

    @OutputFile
    File getGeneratedSamTemplate() throws MissingConfigurationException {
        return config.getGeneratedSamTemplate();
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
        Path generatedFile = Files.write(getGeneratedSamTemplate().toPath(), content.getBytes(charset));

        if (!generatedFile.toFile().exists()) {
            throw new Exception("Couldn't generate source SAM template!");
        }
    }

    private String replaceCodeUriParam(String content) {
        return content.replaceAll(
            "([\"'])?" + Pattern.quote("${CodeUri}") + "([\"'])?",
            '"' + config.getShadowJarFile().getAbsolutePath() + '"'
        );
    }
}

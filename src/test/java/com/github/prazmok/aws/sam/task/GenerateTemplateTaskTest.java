package com.github.prazmok.aws.sam.task;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import com.github.prazmok.aws.sam.AwsSamPlugin;
import com.github.prazmok.aws.sam.config.Config;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.TaskOutputsInternal;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GenerateTemplateTaskTest {
    @Test
    void generateTemplate() throws Exception {
        File tmpDir = new File("build/tmp");
        assertTrue(tmpDir.exists());

        File generatedTemplate = new File(tmpDir + "/generated.template.yml");
        generatedTemplate.deleteOnExit();
        assertFalse(generatedTemplate.exists());

        File shadowJar = new File(tmpDir + "/aws-sam-gradle-example-0.0.1-all.jar");

        if (!shadowJar.exists()) {
            assertTrue(shadowJar.createNewFile(), "Assert file has been created");
        }

        assertTrue(shadowJar.exists());

        Config config = Mockito.mock(Config.class);
        when(config.getSamTemplateFile()).thenReturn("template.yml");
        when(config.getSamTemplatePath()).thenReturn(new File("./src/test/resources"));
        when(config.getSamTemplate()).thenReturn(new File("./src/test/resources/template.yml"));
        when(config.getTmpDir()).thenReturn(tmpDir);
        when(config.getGeneratedSamTemplate()).thenReturn(generatedTemplate);

        GenerateTemplateTask task = (GenerateTemplateTask) buildTask(config);
        task.generateTemplate();

        TaskOutputsInternal outputs = Objects.requireNonNull(task).getOutputs();
        assertEquals(generatedTemplate.getAbsoluteFile(), outputs.getFiles().getSingleFile());
        assertTrue(tmpDir.exists());
        assertTrue(generatedTemplate.exists());

        String content = new String(Files.readAllBytes(generatedTemplate.toPath()));
        assertFalse(content.isEmpty());

        Matcher matcher = Pattern.compile("CodeUri: \"(.*)\"").matcher(content);

        while (matcher.find()) {
            assertEquals("CodeUri: \"" + shadowJar.getAbsoluteFile().toString() + "\"", matcher.group());
        }
    }

    private Task buildTask(Config config) {
        FileCollection files = Mockito.mock(FileCollection.class);
        when(files.getSingleFile()).thenReturn(new File("build/tmp/aws-sam-gradle-example-0.0.1-all.jar"));
        TaskOutputsInternal outputs = Mockito.mock(TaskOutputsInternal.class);
        when(outputs.getFiles()).thenReturn(files);
        Task shadowJarTask = Mockito.mock(ShadowJar.class);
        when(shadowJarTask.getOutputs()).thenReturn(outputs);
        Project project = ProjectBuilder.builder().build();
        Object[] constructorArgs = {config, shadowJarTask, project.getLogger()};
        Map<String, Object> taskParams = new HashMap<String, Object>() {{
            put("type", GenerateTemplateTask.class);
            put("constructorArgs", constructorArgs);
        }};

        return project.task(taskParams, AwsSamPlugin.GENERATE_TEMPLATE_TASK_NAME);
    }
}

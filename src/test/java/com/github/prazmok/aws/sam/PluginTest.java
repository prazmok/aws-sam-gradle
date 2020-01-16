package com.github.prazmok.aws.sam;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PluginTest {
    private static final String ENV = "test";
    private static final File EXAMPLE_PROJECT_DIR = new File("example");

    private BuildResult buildResult;

    @Test
    public void testGenerateSamTemplate() {
        gradleExecute(EXAMPLE_PROJECT_DIR, "generateSamTemplate");
        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":build")).getOutcome());
        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":shadowJar")).getOutcome());
        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":generateSamTemplate")).getOutcome());

        File generatedTemplateFile = new File(EXAMPLE_PROJECT_DIR + "/build/tmp/generated.template.yml");
        assertTrue(generatedTemplateFile.exists());
    }

    @Test
    public void testPackageSam() {
        // todo
    }

    @Test
    public void testDeploySam() {
        // todo
    }

    private void gradleExecute(File projectDir, String... arguments) {
        final List<String> argsList = new ArrayList<>();
        argsList.addAll(asList(arguments));
        argsList.addAll(asList("-Penvironment=" + ENV, "--info", "--stacktrace", "--max-workers", "1"));

        buildResult = GradleRunner.create().withProjectDir(projectDir.getAbsoluteFile())
            .withPluginClasspath()
            .withArguments(argsList)
            .forwardOutput()
            .build();
    }
}

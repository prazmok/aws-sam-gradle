package com.github.prazmok.aws.sam;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class PluginTest {
    private static final String ENV = "test";
    private static final File EXAMPLE_PROJECT_DIR = new File("example");

    private BuildResult buildResult;

    @Test
    public void testPackageSamTask() {
//        gradleExecute(EXAMPLE_PROJECT_DIR, "packageSam");
//
//        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":clean")).getOutcome());
//        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":build")).getOutcome());
//        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":shadowJar")).getOutcome());
//        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":generateSamTemplate")).getOutcome());
//        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":packageSam")).getOutcome());
//
//        File file = new File(EXAMPLE_PROJECT_DIR + "/build/tmp/sam/generated.template.yml");
//        assertTrue(file.exists());
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

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

public class AwsSamPluginTest {
    private static final String ENV = "test";
    private static final File EXAMPLE_PROJECT_DIR = new File("example");

    private BuildResult buildResult;

    @Test
    public void testGenerateSamTemplate() {
        gradleExecute("packageSam");

        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":clean")).getOutcome());
        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":build")).getOutcome());
        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":shadowJar")).getOutcome());
        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":packageSam")).getOutcome());
    }

    private void gradleExecute(String... arguments) {
        final List<String> argsList = new ArrayList<>();
        argsList.addAll(asList(arguments));
        argsList.addAll(asList("-Penvironment=" + ENV, "--stacktrace"));

        buildResult = GradleRunner.create().withProjectDir(AwsSamPluginTest.EXAMPLE_PROJECT_DIR.getAbsoluteFile())
            .withPluginClasspath()
            .withArguments(argsList)
            .forwardOutput()
            .build();
    }
}

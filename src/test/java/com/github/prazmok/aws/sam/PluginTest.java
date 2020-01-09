package com.github.prazmok.aws.sam;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class PluginTest {
    private static final String ENV = "test";
    private static final File EXAMPLE_PROJECT_DIR = new File("example");

    private BuildResult buildResult;

    @Test
    public void testDeployMinimalApp() {
//        gradleExecute(EXAMPLE_PROJECT_DIR, "clean", "hello");
//        assertEquals(TaskOutcome.SUCCESS, Objects.requireNonNull(buildResult.task(":hello")).getOutcome());
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

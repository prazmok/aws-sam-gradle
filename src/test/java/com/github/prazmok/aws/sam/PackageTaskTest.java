package com.github.prazmok.aws.sam;

import com.github.prazmok.aws.sam.config.AwsSamExtension;
import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.Environment;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PackageTaskTest {
    private Project project;
    private NamedDomainObjectContainer<Environment> envs;
    private File samTemplate = new File("/tmp/template.yml");

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() throws IOException {
        if (!samTemplate.exists() || !samTemplate.isFile()) {
            assertTrue(samTemplate.createNewFile(), "Assert create temporary SAM template file");
        }
        project = ProjectBuilder.builder()
            .withProjectDir(samTemplate.getParentFile())
            .build();
        envs = Mockito.mock(NamedDomainObjectContainer.class);
        when(envs.getByName("default")).thenReturn(new Environment("default"));
    }

    @AfterEach
    void tearDown() {
        samTemplate.deleteOnExit();
    }

    @Test
    void testNotExistingTemplateFile() {
        AwsSamExtension extension = new AwsSamExtension(envs);
        extension.samTemplate = new File("/non/existing/sam/template.yml");
        Config config = new Config(project, extension);
        PackageTask task = (PackageTask) buildTask(config);
        assertThrows(FileNotFoundException.class, task::getSamTemplatePath);
        extension.samTemplate = new File("/wrong/path/to/directory");
        assertThrows(FileNotFoundException.class, task::getSamTemplatePath);
    }

    @Test
    void testNotExistingOutputDirectory() {
        AwsSamExtension extension = new AwsSamExtension(envs);
        extension.samTemplate = samTemplate;
        extension.samPackagedTemplate = new File("/not/existing/directory/packaged.yml");
        Config config = new Config(project, extension);
        PackageTask task = (PackageTask) buildTask(config);
        assertThrows(FileNotFoundException.class, task::getSamPackagedTemplate);
    }

    @Test
    void testBuildCommand() {
        Config config = new Config(project, getExtension());
        PackageTask task = (PackageTask) buildTask(config);
        String expected = "sam package --force-upload --use-json --debug --template-file /tmp/template.yml " +
            "--output-template-file /tmp/packaged.yml --s3-bucket example-s3-bucket --s3-prefix " +
            "example-s3-prefix --profile default --region eu-west-1 --kms-key-id example-kms-key-id";
        assertEquals(expected, String.join(" ", task.buildCommand()));
    }

    @Test
    void testReturnCodeOnError() {
        AwsSamExtension extension = new AwsSamExtension(envs);
        Config config = new Config(project, extension);
        PackageTask task = (PackageTask) buildTask(config);
        assertEquals("return 1", String.join(" ", task.buildCommand()));
    }

    @Test
    void testDryRunExecution() {
        Config config = new Config(project, getExtension());
        Config configMock = Mockito.spy(config);
        when(configMock.isDryRunOption()).thenReturn(true);
        PackageTask task = (PackageTask) buildTask(configMock);
        assertEquals("echo", String.join(" ", task.buildCommand()));
    }

    private Task buildTask(Config config) {
        Object[] constructorArgs = {config};
        Map<String, Object> taskParams = new HashMap<String, Object>() {{
            put("type", PackageTask.class);
            put("constructorArgs", constructorArgs);
        }};

        return project.task(taskParams, AwsSamPlugin.PACKAGE_TASK + "Test");
    }

    private AwsSamExtension getExtension() {
        AwsSamExtension extension = new AwsSamExtension(envs);
        extension.samTemplate = samTemplate;
        extension.awsRegion = "eu-west-1";
        extension.awsProfile = "default";
        extension.kmsKeyId = "example-kms-key-id";
        extension.s3Bucket = "example-s3-bucket";
        extension.s3Prefix = "example-s3-prefix";
        extension.forceUpload = true;
        extension.useJson = true;
        extension.debug = true;

        return extension;
    }
}

package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.AwsSamPlugin;
import com.github.prazmok.aws.sam.config.AwsSamExtension;
import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.Environment;
import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PackageTaskTest {
    private Project project;
    private NamedDomainObjectContainer<Environment> envs;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        project = ProjectBuilder.builder()
            .withProjectDir(new File("/tmp"))
            .build();
        envs = Mockito.mock(NamedDomainObjectContainer.class);
        when(envs.getByName("test")).thenReturn(new Environment("test"));
    }

    @Test
    void buildCommand() throws MissingConfigurationException {
        Config config = new Config(project, getFullExtension(), "test");
        PackageTask task = (PackageTask) buildTask(config);
        String expected = "sam package --force-upload --use-json --debug --template-file /tmp/sam/generated.template.yml --output-template-file /tmp/sam/packaged.template.yml --s3-bucket example-s3-bucket --s3-prefix example-s3-prefix --profile default --region eu-west-1 --kms-key-id example-kms-key-id";
        assertEquals(expected, String.join(" ", task.buildCommand()));
    }

    private Task buildTask(Config config) {
        Object[] constructorArgs = {config, project.getLogger()};
        Map<String, Object> taskParams = new HashMap<String, Object>() {{
            put("type", PackageTask.class);
            put("constructorArgs", constructorArgs);
        }};
        return project.task(taskParams, AwsSamPlugin.SAM_PACKAGE_TASK_NAME);
    }

    private AwsSamExtension getFullExtension() {
        AwsSamExtension extension = new AwsSamExtension(envs);
        extension.tmpDir = new File("/tmp");
        extension.samTemplatePath = new File("/tmp");
        extension.samTemplateFile = "template.yml";
        extension.awsRegion = "eu-west-1";
        extension.awsProfile = "default";
        extension.kmsKeyId = "example-kms-key-id";
        extension.s3Bucket = "example-s3-bucket";
        extension.s3Prefix = "example-s3-prefix";
//        extension.stackName = "example-cloud-formation-stack";
//        extension.roleArn = "example-cf-role-arn-assumed-when-executing-the-change-set";

        extension.forceUpload = true;
        extension.useJson = true;
        extension.debug = true;
//        extension.noExecuteChangeset = true;
//        extension.failOnEmptyChangeset = true;
//        extension.noFailOnEmptyChangeset = true;
//        extension.confirmChangeset = true;

//        extension.capabilities = new LinkedList<>();
//        extension.capabilities.add("CAPABILITY_IAM");
//        extension.capabilities.add("CAPABILITY_NAMED_IAM");
//
//        extension.tags = new LinkedList<>();
//        extension.tags.add("example-tag1");
//        extension.tags.add("example-tag2");
//        extension.tags.add("example-tag3");
//
//        extension.notificationArns = new LinkedList<>();
//        extension.notificationArns.add("example-notification-arn1");
//        extension.notificationArns.add("example-notification-arn2");
//        extension.notificationArns.add("example-notification-arn3");
//        extension.notificationArns.add("example-notification-arn4");
//
//        extension.parameterOverrides = new LinkedHashMap<>();
//        extension.parameterOverrides.put("SomeParam1", "ParamValue1");
//        extension.parameterOverrides.put("SomeParam2", "ParamValue2");
//        extension.parameterOverrides.put("SomeParam3", "ParamValue3");
//        extension.parameterOverrides.put("SomeParam4", "ParamValue4");
//        extension.parameterOverrides.put("SomeParam5", "ParamValue5");

        return extension;
    }
}

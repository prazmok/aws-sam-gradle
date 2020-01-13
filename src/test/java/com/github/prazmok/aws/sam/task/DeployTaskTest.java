package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.AwsSamPlugin;
import com.github.prazmok.aws.sam.config.AwsSamExtension;
import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.Environment;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.TaskOutputsInternal;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class DeployTaskTest {
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
    void buildCommand() throws Exception {
        Config config = new Config(project, getFullExtension(), "test");
        DeployTask task = (DeployTask) buildTask(config);
        String expected = "sam deploy --force-upload --use-json --fail-on-empty-changeset --confirm-changeset --debug" +
            " --template-file /tmp/packaged.template.yml --stack-name example-cloud-formation-stack --s3-bucket " +
            "example-s3-bucket --s3-prefix example-s3-prefix --profile default --region eu-west-1 --kms-key-id " +
            "example-kms-key-id --capabilities CAPABILITY_IAM,CAPABILITY_NAMED_IAM --notification-arns " +
            "example-notification-arn1,example-notification-arn2 --tags example-tag1,example-tag2,example-tag3 " +
            "--parameter-overrides SomeParam1=ParamValue1 SomeParam2=ParamValue2";
        assertEquals(expected, String.join(" ", task.buildCommand()));
    }

    private Task buildTask(Config config) throws Exception {
        File templateFile = new File("/tmp/packaged.template.yml");

        if (!templateFile.exists()) {
            assertTrue(templateFile.createNewFile(), "Assert file has been created");
        }

        FileCollection files = Mockito.mock(FileCollection.class);
        when(files.getSingleFile()).thenReturn(templateFile);
        TaskOutputsInternal outputs = Mockito.mock(TaskOutputsInternal.class);
        when(outputs.getFiles()).thenReturn(files);
        Task packageTask = Mockito.mock(PackageTask.class);
        when(packageTask.getOutputs()).thenReturn(outputs);
        Object[] constructorArgs = {config, packageTask, project.getLogger()};
        Map<String, Object> taskParams = new HashMap<String, Object>() {{
            put("type", DeployTask.class);
            put("constructorArgs", constructorArgs);
        }};
        return project.task(taskParams, AwsSamPlugin.SAM_DEPLOY_TASK_NAME);
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
        extension.stackName = "example-cloud-formation-stack";
        extension.roleArn = "example-cf-role-arn-assumed-when-executing-the-change-set";

        extension.forceUpload = true;
        extension.useJson = true;
        extension.noExecuteChangeset = true;
        extension.failOnEmptyChangeset = true;
        extension.noFailOnEmptyChangeset = true;
        extension.confirmChangeset = true;
        extension.debug = true;

        extension.capabilities = new LinkedList<>();
        extension.capabilities.add("CAPABILITY_IAM");
        extension.capabilities.add("CAPABILITY_NAMED_IAM");

        extension.tags = new LinkedList<>();
        extension.tags.add("example-tag1");
        extension.tags.add("example-tag2");
        extension.tags.add("example-tag3");

        extension.notificationArns = new LinkedList<>();
        extension.notificationArns.add("example-notification-arn1");
        extension.notificationArns.add("example-notification-arn2");

        extension.parameterOverrides = new LinkedHashMap<>();
        extension.parameterOverrides.put("SomeParam1", "ParamValue1");
        extension.parameterOverrides.put("SomeParam2", "ParamValue2");

        return extension;
    }
}

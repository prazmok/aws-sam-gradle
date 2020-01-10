package com.github.prazmok.aws.sam.config;

import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ConfigTest {
    private Project project;
    private NamedDomainObjectContainer<Environment> envs;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        project = Mockito.mock(Project.class);
        envs = Mockito.mock(NamedDomainObjectContainer.class);
        when(project.getRootDir()).thenReturn(new File("./"));
        when(project.getBuildDir()).thenReturn(new File("./build"));
    }

    @Test
    public void testEmptyConfig() {
        when(envs.getByName("test")).thenReturn(new Environment("test"));
        AwsSamExtension extension = new AwsSamExtension(envs);
        Config config = new Config(project, extension, "test");

        assertEquals("test", config.getEnvironment().name);
        assertEquals(new File("./"), config.getSamTemplatePath());
        assertEquals(new LinkedHashMap<>(), config.getParameterOverrides());
        assertEquals(new LinkedList<>(), config.getTags());
        assertEquals(new LinkedList<>(), config.getNotificationArns());

        assertNull(config.getAwsProfile());
        assertNull(config.getKmsKeyId());
        assertNull(config.getRoleArn());

        assertTrue(config.forceUpload());
        assertTrue(config.failOnEmptyChangeset());

        assertFalse(config.useJson());
        assertFalse(config.debug());
        assertFalse(config.noExecuteChangeset());
        assertFalse(config.noFailOnEmptyChangeset());
        assertFalse(config.confirmChangeset());

        // Assert exceptions when missing required configuration properties
        assertThrows(MissingConfigurationException.class, config::getSamTemplateFile);
        assertThrows(MissingConfigurationException.class, config::getAwsRegion);
        assertThrows(MissingConfigurationException.class, config::getS3Bucket);
        assertThrows(MissingConfigurationException.class, config::getS3Prefix);
        assertThrows(MissingConfigurationException.class, config::getStackName);
    }

    @Test
    public void testExtensionConfig() throws MissingConfigurationException {
        when(envs.getByName("test")).thenReturn(new Environment("test"));
        Config config = new Config(project, getBaseExtension(), "test");

        assertEquals(new File("./src/test"), config.getSamTemplatePath());
        assertEquals("template.yml", config.getSamTemplateFile());
        assertEquals("eu-west-1", config.getAwsRegion());
        assertEquals("default", config.getAwsProfile());
        assertEquals("kms-key-id", config.getKmsKeyId());
        assertEquals("bucket-name", config.getS3Bucket());
        assertEquals("bucket-prefix", config.getS3Prefix());
        assertEquals("cf-stack-name", config.getStackName());
        assertEquals("role-arn", config.getRoleArn());
        assertEquals(1, config.getTags().size());
        assertEquals("TAG", config.getTags().get(0));
        assertEquals(1, config.getNotificationArns().size());
        assertEquals("NotificationArn", config.getNotificationArns().get(0));
        assertEquals(1, config.getParameterOverrides().size());
        assertEquals("ParamValue", config.getParameterOverrides().get("SomeParam"));

        assertFalse(config.forceUpload());
        assertFalse(config.failOnEmptyChangeset());

        assertTrue(config.useJson());
        assertTrue(config.debug());
        assertTrue(config.noExecuteChangeset());
        assertTrue(config.noFailOnEmptyChangeset());
        assertTrue(config.confirmChangeset());

        assertEquals(new File("./src/test/template.yml"), config.getSamTemplate());
        assertEquals(new File("./build/tmp/sam"), config.getSamTmpDir());
        assertEquals(Paths.get("./build/tmp/sam/generated.template.yml"), config.getGeneratedSamTemplatePath());
        assertEquals(Paths.get("./build/tmp/sam/packaged.template.yml"), config.getOutputSamTemplatePath());
    }

    @Test
    public void testExtendedEnvironmentConfig() throws MissingConfigurationException {
        when(envs.getByName("test")).thenReturn(getReachEnvironment());
        Config config = new Config(project, getBaseExtension(), "test");

        assertEquals(config.getSamTemplatePath(), new File("./src/test/resources"));
        assertEquals(config.getSamTemplateFile(), "env_template.yml");
        assertEquals(config.getAwsRegion(), "env_eu-west-1");
        assertEquals(config.getAwsProfile(), "env_default");
        assertEquals(config.getKmsKeyId(), "env_kms-key-id");
        assertEquals(config.getS3Bucket(), "env_bucket-name");
        assertEquals(config.getS3Prefix(), "env_bucket-prefix");
        assertEquals(config.getStackName(), "env_cf-stack-name");
        assertEquals(config.getRoleArn(), "env_role-arn");
        assertEquals(config.getTags().size(), 1);
        assertEquals(config.getTags().get(0), "EXTENDED_ENV_TAG");
        assertEquals(config.getNotificationArns().size(), 1);
        assertEquals(config.getNotificationArns().get(0), "ExtendedEnvNotificationArn");
        assertEquals(config.getParameterOverrides().size(), 1);
        assertEquals(config.getParameterOverrides().get("SomeExtendedEnvParam"), "ExtendedEnvParamValue");

        assertTrue(config.forceUpload());
        assertTrue(config.failOnEmptyChangeset());

        assertFalse(config.useJson());
        assertFalse(config.debug());
        assertFalse(config.noExecuteChangeset());
        assertFalse(config.noFailOnEmptyChangeset());
        assertFalse(config.confirmChangeset());
    }

    @Test
    public void testConflictingConfigParameters() {
        Environment env = new Environment("test");
        env.failOnEmptyChangeset = true;
        when(envs.getByName("test")).thenReturn(env);
        AwsSamExtension ext = new AwsSamExtension(envs);
        ext.noFailOnEmptyChangeset = true;
        Config config = new Config(project, ext, "test");

        assertTrue(config.failOnEmptyChangeset());
        assertFalse(config.noFailOnEmptyChangeset()); // always false when failOnEmptyChangeset = true
    }

    private AwsSamExtension getBaseExtension() {
        AwsSamExtension extension = new AwsSamExtension(envs);
        extension.samTemplatePath = new File("./src/test");
        extension.samTemplateFile = "template.yml";
        extension.awsRegion = "eu-west-1";
        extension.awsProfile = "default";
        extension.kmsKeyId = "kms-key-id";
        extension.s3Bucket = "bucket-name";
        extension.s3Prefix = "bucket-prefix";
        extension.stackName = "cf-stack-name";
        extension.roleArn = "role-arn";
        extension.forceUpload = false;
        extension.useJson = true;
        extension.debug = true;
        extension.noExecuteChangeset = true;
        extension.failOnEmptyChangeset = false;
        extension.noFailOnEmptyChangeset = true;
        extension.confirmChangeset = true;
        extension.tags = new LinkedList<>(Collections.singletonList("TAG"));
        extension.notificationArns = new LinkedList<>(Collections.singletonList("NotificationArn"));
        extension.parameterOverrides = new LinkedHashMap<>();
        extension.parameterOverrides.put("SomeParam", "ParamValue");

        return extension;
    }

    private Environment getReachEnvironment() {
        Environment env = new Environment("test");
        env.samTemplatePath = new File("./src/test/resources");
        env.samTemplateFile = "env_template.yml";
        env.awsRegion = "env_eu-west-1";
        env.awsProfile = "env_default";
        env.kmsKeyId = "env_kms-key-id";
        env.s3Bucket = "env_bucket-name";
        env.s3Prefix = "env_bucket-prefix";
        env.stackName = "env_cf-stack-name";
        env.roleArn = "env_role-arn";
        env.forceUpload = true;
        env.useJson = false;
        env.debug = false;
        env.noExecuteChangeset = false;
        env.failOnEmptyChangeset = true;
        env.noFailOnEmptyChangeset = false;
        env.confirmChangeset = false;
        env.notificationArns = new LinkedList<>(Collections.singletonList("ExtendedEnvNotificationArn"));
        env.tags = new LinkedList<>(Collections.singletonList("EXTENDED_ENV_TAG"));
        env.parameterOverrides = new LinkedHashMap<>();
        env.parameterOverrides.put("SomeExtendedEnvParam", "ExtendedEnvParamValue");

        return env;
    }
}

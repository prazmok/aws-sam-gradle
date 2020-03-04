package com.github.prazmok.aws.sam.config;

import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
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
        when(envs.findByName("test")).thenReturn(new Environment(null));
        AwsSamExtension extension = new AwsSamExtension(envs);
        Config config = new Config(project, extension);

        assertEquals(null, config.getEnvironment().name);
        assertEquals(new File("./template.yml"), config.getSamTemplate());
        assertEquals(new File("./packaged.yml"), config.getPackagedTemplate());
        assertEquals(new LinkedHashMap<>(), config.getParameterOverrides());
        assertEquals(new LinkedList<>(), config.getTags());
        assertEquals(new LinkedList<>(), config.getNotificationArns());
        assertEquals(new LinkedList<String>() {{
            add("CAPABILITY_IAM");
        }}, config.getCapabilities());

        assertNull(config.getAwsProfile());
        assertNull(config.getS3Prefix());
        assertNull(config.getKmsKeyId());
        assertNull(config.getRoleArn());

        assertTrue(config.forceUpload());
        assertTrue(config.failOnEmptyChangeset());

        assertFalse(config.useJson());
        assertFalse(config.debug());
        assertFalse(config.noExecuteChangeset());
        assertFalse(config.noFailOnEmptyChangeset());

        // Assert exceptions when missing required configuration properties
        assertThrows(MissingConfigurationException.class, config::getAwsRegion);
        assertThrows(MissingConfigurationException.class, config::getS3Bucket);
        assertThrows(MissingConfigurationException.class, config::getStackName);
    }

    @Test
    public void testDefaultConfig() throws MissingConfigurationException {
        Config config = new Config(project, getBaseProperties());

        assertEquals(new File("./src/test/resources/template.yml"), config.getSamTemplate());
        assertEquals(new File("./src/test/resources/packaged.yml"), config.getPackagedTemplate());
        assertEquals("eu-west-1", config.getAwsRegion());
        assertEquals(null, config.getAwsProfile());
        assertEquals("kms-key-id", config.getKmsKeyId());
        assertEquals("bucket-name", config.getS3Bucket());
        assertEquals("bucket-prefix", config.getS3Prefix());
        assertEquals("cf-stack-name", config.getStackName());
        assertEquals("role-arn", config.getRoleArn());
        assertEquals(1, config.getCapabilities().size());
        assertEquals("CAPABILITY_IAM", config.getCapabilities().get(0));
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
    }

    @Test
    public void testExtendedEnvironmentConfig() throws MissingConfigurationException {
        when(envs.findByName("test")).thenReturn(getExtendedProperties());
        Config config = new Config(project, getBaseProperties());

        assertEquals(new File("./src/test/resources/extended_template.yml"), config.getSamTemplate());
        assertEquals(new File("./src/test/extended_packaged.yml"), config.getPackagedTemplate());
        assertEquals("env_eu-west-1", config.getAwsRegion());
        assertEquals("env_default", config.getAwsProfile());
        assertEquals("env_kms-key-id", config.getKmsKeyId());
        assertEquals("env_bucket-name", config.getS3Bucket());
        assertEquals("env_bucket-prefix", config.getS3Prefix());
        assertEquals("env_cf-stack-name", config.getStackName());
        assertEquals("env_role-arn", config.getRoleArn());
        assertEquals(1, config.getCapabilities().size());
        assertEquals("CAPABILITY_NAMED_IAM", config.getCapabilities().get(0));
        assertEquals(1, config.getTags().size());
        assertEquals("EXTENDED_ENV_TAG", config.getTags().get(0));
        assertEquals(1, config.getNotificationArns().size());
        assertEquals("ExtendedEnvNotificationArn", config.getNotificationArns().get(0));
        assertEquals(1, config.getParameterOverrides().size());
        assertEquals("ExtendedEnvParamValue", config.getParameterOverrides().get("SomeExtendedEnvParam"));

        assertTrue(config.forceUpload());
        assertTrue(config.failOnEmptyChangeset());

        assertFalse(config.useJson());
        assertFalse(config.debug());
        assertFalse(config.noExecuteChangeset());
        assertFalse(config.noFailOnEmptyChangeset());
    }

    @Test
    public void testConflictingConfigParameters() {
        Environment env = new Environment("test");
        env.failOnEmptyChangeset = true;
        when(envs.findByName("test")).thenReturn(env);
        AwsSamExtension ext = new AwsSamExtension(envs);
        ext.noFailOnEmptyChangeset = true;
        Config config = new Config(project, ext);

        assertTrue(config.failOnEmptyChangeset());
        assertFalse(config.noFailOnEmptyChangeset()); // always false when failOnEmptyChangeset = true
    }

    private AwsSamExtension getBaseProperties() {
        AwsSamExtension extension = new AwsSamExtension(envs);
        extension.samTemplate = new File("./src/test/resources/template.yml");
        extension.samPackagedTemplate = new File("./src/test/resources/packaged.yml");
        extension.awsRegion = "eu-west-1";
        extension.awsProfile = null;
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
        extension.capabilities = new LinkedList<>(Collections.singletonList("CAPABILITY_IAM"));
        extension.tags = new LinkedList<>(Collections.singletonList("TAG"));
        extension.notificationArns = new LinkedList<>(Collections.singletonList("NotificationArn"));
        extension.parameterOverrides = new LinkedHashMap<>();
        extension.parameterOverrides.put("SomeParam", "ParamValue");

        return extension;
    }

    private Environment getExtendedProperties() {
        Environment env = new Environment("test");
        env.samTemplate = new File("./src/test/resources/extended_template.yml");
        env.samPackagedTemplate = new File("./src/test/extended_packaged.yml");
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
        env.notificationArns = new LinkedList<>(Collections.singletonList("ExtendedEnvNotificationArn"));
        env.capabilities = new LinkedList<>(Collections.singletonList("CAPABILITY_NAMED_IAM"));
        env.tags = new LinkedList<>(Collections.singletonList("EXTENDED_ENV_TAG"));
        env.parameterOverrides = new LinkedHashMap<>();
        env.parameterOverrides.put("SomeExtendedEnvParam", "ExtendedEnvParamValue");

        return env;
    }
}

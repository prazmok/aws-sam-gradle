package com.github.prazmok.aws.sam.config;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.gradle.internal.impldep.org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ConfigTest {
    private Project project;
    private NamedDomainObjectContainer<Environment> envs;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        project = Mockito.mock(Project.class);
        envs = Mockito.mock(NamedDomainObjectContainer.class);
    }

    @Test //(expected = MissingConfigPropertyException.class)
    public void testDefaultConfig() {
        Config config = getDefaultConfig("test_tdc");
        assertEquals(config.getEnvironment().name, "test_tdc");

        // todo test all default values and exceptions + correct defaults extension by env config
    }

    private Config getDefaultConfig(String env) {
        Environment environment = new Environment(env);
        when(envs.getByName(env)).thenReturn(environment);
        AwsSamExtension extension = new AwsSamExtension(envs);

        return new Config(project, extension, env);
    }
}

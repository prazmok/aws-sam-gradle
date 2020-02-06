package com.github.prazmok.aws.sam;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import com.github.prazmok.aws.sam.config.AwsSamExtension;
import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.Environment;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.ExtensionAware;

import java.util.HashMap;
import java.util.Map;

public class AwsSamPlugin implements Plugin<Project> {
    public static final String EXTENSION = "deployment";
    public static final String EXT_ENVIRONMENTS = "environments";
    public static final String VALIDATE_TASK = "validateSam";
    public static final String PACKAGE_TASK = "packageSam";
    public static final String DEPLOY_TASK = "deploySam";

    private Project project;

    @Override
    public void apply(Project project) {
        this.project = project;
        this.project.getAllprojects().forEach((p) -> {
            p.getPluginManager().apply("java");
            p.getPluginManager().apply("com.github.johnrengelman.shadow");

            Task clean = p.getTasks().getByName("clean");
            Task build = p.getTasks().getByName("build");

            ShadowJar shadowJar = (ShadowJar) p.getTasks().getByName("shadowJar");
            shadowJar.getArchiveVersion().set("");

            build.mustRunAfter(clean)
                .finalizedBy(shadowJar);
        });

        final NamedDomainObjectContainer<Environment> envs = project.container(Environment.class);
        final AwsSamExtension extension = project
            .getExtensions()
            .create(EXTENSION, AwsSamExtension.class, envs);
        ((ExtensionAware) extension).getExtensions().add(EXT_ENVIRONMENTS, envs);

        final Config config = new Config(project, extension);

        validateSamTask(config);
        packageSamTask(config);
        deploySamTask(config);
    }

    private void validateSamTask(Config config) {
        Object[] constructorArgs = {config};
        Map<String, Object> taskParams = new HashMap<String, Object>() {{
            put("type", ValidateTask.class);
            put("group", "AWS SAM");
            put("description", "Validates AWS SAM application.");
            put("constructorArgs", constructorArgs);
        }};

        project.task(taskParams, VALIDATE_TASK);
    }

    private void packageSamTask(Config config) {
        Object[] dependsOn = {VALIDATE_TASK, "clean", "build"};
        Object[] constructorArgs = {config};
        Map<String, Object> taskParams = new HashMap<String, Object>() {{
            put("type", PackageTask.class);
            put("group", "AWS SAM");
            put("description", "Package AWS SAM application.");
            put("dependsOn", dependsOn);
            put("constructorArgs", constructorArgs);
        }};

        project.task(taskParams, PACKAGE_TASK);
    }

    @SuppressWarnings("UnusedReturnValue")
    private void deploySamTask(Config config) {
        Object[] dependsOn = {PACKAGE_TASK};
        Object[] constructorArgs = {config};
        Map<String, Object> taskParams = new HashMap<String, Object>() {{
            put("type", DeployTask.class);
            put("group", "AWS SAM");
            put("description", "Deploys AWS SAM application.");
            put("dependsOn", dependsOn);
            put("constructorArgs", constructorArgs);
        }};

        project.task(taskParams, DEPLOY_TASK);
    }
}

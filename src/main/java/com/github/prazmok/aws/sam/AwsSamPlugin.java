package com.github.prazmok.aws.sam;

import com.github.prazmok.aws.sam.config.AwsSamExtension;
import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.Environment;
import com.github.prazmok.aws.sam.task.DeployTask;
import com.github.prazmok.aws.sam.task.GenerateTemplateTask;
import com.github.prazmok.aws.sam.task.PackageTask;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.ExtensionAware;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AwsSamPlugin implements Plugin<Project> {
    public static final String SAM_DEPLOY_EXTENSION = "deployment";
    public static final String SAM_DEPLOY_ENVIRONMENTS = "environments";
    public static final String GENERATE_TEMPLATE_TASK_NAME = "generateSamTemplate";
    public static final String SAM_PACKAGE_TASK_NAME = "packageSam";
    public static final String SAM_DEPLOY_TASK_NAME = "deploySam";

    private Project project;

    @Override
    public void apply(Project project) {
        this.project = project;
        this.project.getAllprojects().forEach((p) -> {
            p.getPluginManager().apply("java");
            p.getPluginManager().apply("com.github.johnrengelman.shadow");

            Task clean = p.getTasks().getByName("clean");
            Task build = p.getTasks().getByName("build");
            Task shadow = p.getTasks().getByName("shadowJar");

            Set<Object> currentDeps = shadow.getDependsOn();
            Set<Object> dependsOn = new HashSet<Object>() {{
                addAll(currentDeps);

                if (!currentDeps.contains(clean)) {
                    add(clean);
                }

                if (!currentDeps.contains(build)) {
                    add(build);
                }
            }};

            shadow.setDependsOn(dependsOn);
        });

        final NamedDomainObjectContainer<Environment> envs = project.container(Environment.class);
        final AwsSamExtension extension = project
            .getExtensions()
            .create(SAM_DEPLOY_EXTENSION, AwsSamExtension.class, envs);
        ((ExtensionAware) extension).getExtensions().add(SAM_DEPLOY_ENVIRONMENTS, envs);
        final String environment = project.hasProperty("environment")
            ? (String) project.getProperties().get("environment")
            : "dev";

        final Config config = new Config(project, extension, environment);
        Task shadowJarTask = project.getTasks().getByName("shadowJar");

        generateTemplateTask(config, shadowJarTask);
        packageTask(config);
        deployTask(config);
    }

    private void generateTemplateTask(Config config, Task shadowJarTask) {
        Object[] dependsOn = {shadowJarTask};
        Object[] constructorArgs = {config, shadowJarTask, project.getLogger()};
        Map<String, Object> taskParams = new HashMap<String, Object>() {{
            put("type", GenerateTemplateTask.class);
            put("group", "AWS SAM");
            put("description", "Generate source SAM template with correct CodeUri JAR path");
            put("dependsOn", dependsOn);
            put("constructorArgs", constructorArgs);
        }};

        project.task(taskParams, GENERATE_TEMPLATE_TASK_NAME);
    }

    private void packageTask(Config config) {
        Object[] dependsOn = {GENERATE_TEMPLATE_TASK_NAME};
        Object[] constructorArgs = {config, project.getLogger()};
        Map<String, Object> taskParams = new HashMap<String, Object>() {{
            put("type", PackageTask.class);
            put("group", "AWS SAM");
            put("description", "Packages an AWS SAM application.");
            put("dependsOn", dependsOn);
            put("constructorArgs", constructorArgs);
        }};

        project.task(taskParams, SAM_PACKAGE_TASK_NAME);
    }

    @SuppressWarnings("UnusedReturnValue")
    private void deployTask(Config config) {
        Object[] dependsOn = {SAM_PACKAGE_TASK_NAME};
        Object[] constructorArgs = {config, project.getLogger()};
        Map<String, Object> taskParams = new HashMap<String, Object>() {{
            put("type", DeployTask.class);
            put("group", "AWS SAM");
            put("description", "Deploys an AWS SAM application.");
            put("dependsOn", dependsOn);
            put("constructorArgs", constructorArgs);
        }};

        project.task(taskParams, SAM_DEPLOY_TASK_NAME);
    }
}

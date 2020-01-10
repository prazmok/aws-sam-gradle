package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.Config;
import com.github.prazmok.aws.sam.config.exception.MissingConfigPropertyException;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public class DeployTask extends DefaultTask {
    private final Project project;
    private final Config config;

    @Inject
    public DeployTask(Project project, Config config) {
        this.project = project;
        this.config = config;
    }

    @TaskAction
    public void samDeploy() throws MissingConfigPropertyException {
        System.out.println(config.getAwsRegion());

        System.out.println("HELLO FROM DEPLOY TASK!!!");
    }
}

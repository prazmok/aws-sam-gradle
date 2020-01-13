package com.github.prazmok.aws.sam.task;

import com.github.prazmok.aws.sam.config.exception.MissingConfigurationException;

import java.util.LinkedHashSet;

public interface CommandBuilderAwareInterface {
    LinkedHashSet<String> buildCommand() throws MissingConfigurationException;
}

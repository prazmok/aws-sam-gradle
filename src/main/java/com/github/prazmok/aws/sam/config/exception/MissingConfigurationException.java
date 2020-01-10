package com.github.prazmok.aws.sam.config.exception;

public class MissingConfigurationException extends Exception {
    public MissingConfigurationException(String property) {
        super("Missing \"" + property + "\" configuration property!");
    }
}

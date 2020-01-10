package com.github.prazmok.aws.sam.config.exception;

public class MissingConfigPropertyException extends Exception {
    public MissingConfigPropertyException(String property) {
        super("Missing \"" + property + "\" configuration property!");
    }
}

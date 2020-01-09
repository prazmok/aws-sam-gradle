package com.github.prazmok.aws.sam.config;

public class Environment extends ConfigProperties {
    public final String name;

    public Environment(String name) {
        this.name = name;
    }
}

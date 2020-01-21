package com.github.prazmok.aws.sam.config;

import org.gradle.api.NamedDomainObjectContainer;

public class AwsSamExtension extends ConfigProperties {
    public final NamedDomainObjectContainer<Environment> environments;

    public AwsSamExtension(NamedDomainObjectContainer<Environment> environments) {
        this.environments = environments;
    }
}

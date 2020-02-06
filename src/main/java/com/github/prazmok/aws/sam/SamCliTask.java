package com.github.prazmok.aws.sam;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;

abstract class SamCliTask extends DefaultTask implements CommandBuilderAwareInterface {
    protected final Logger logger = getProject().getLogger();
}

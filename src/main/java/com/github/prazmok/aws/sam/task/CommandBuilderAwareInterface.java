package com.github.prazmok.aws.sam.task;

import java.util.LinkedHashSet;

public interface CommandBuilderAwareInterface {
    LinkedHashSet<String> buildCommand() throws Exception;
}

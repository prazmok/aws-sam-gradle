package com.github.prazmok.aws.sam.task;

import java.util.LinkedHashSet;

public interface CommandBuilderAwareInterface {
    LinkedHashSet<String> buildCommand();

    default LinkedHashSet<String> errorCodeCommand(Integer code) {
        return new LinkedHashSet<String>() {{
            add("return");
            add(code.toString());
        }};
    }
}

package com.github.prazmok.aws.sam;

import java.util.LinkedHashSet;
import java.util.Set;

public interface CommandBuilderAwareInterface {
    Set<String> buildCommand();

    default Set<String> returnCodeCommand(Integer code) {
        return new LinkedHashSet<String>() {{
            add("return");
            add(code.toString());
        }};
    }
}

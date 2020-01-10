package com.github.prazmok.aws.sam.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SamCommandBuilderTest {
    @Test
    void testCommandBuilder() {
        SamCommandBuilder builder = new SamCommandBuilder();
        builder.task("task");
        builder.argument("--correctArg", "CorrectVal");
        builder.argument("--emptyArg", "");
        builder.argument("--nullArg", null);
        builder.option("--correctOption", true);
        builder.option("--falseOption", false);
        builder.option("--nullOption", null);
        String expected = "sam task --correctArg CorrectVal --correctOption";
        assertEquals(expected, String.join(" ", builder.build()));
    }
}

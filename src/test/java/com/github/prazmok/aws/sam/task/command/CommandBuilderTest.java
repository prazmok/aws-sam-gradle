package com.github.prazmok.aws.sam.task.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandBuilderTest {
    @Test
    void testMissingCommandException() {
        CommandBuilder builder = new CommandBuilder(ArgSeparator.SPACE);
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testMultipleCommands() {
        CommandBuilder builder = new CommandBuilder(ArgSeparator.SPACE);
        builder.command("java")
            .command("multiple")
            .command("commands")
            .command("test")
            .option("-version");
        assertEquals("java -version", String.join(" ", builder.build()));
    }

    @Test
    void testMultipleTasks() {
        CommandBuilder builder = new CommandBuilder(ArgSeparator.SPACE);
        builder.command("gradle")
            .task("clean")
            .task("build")
            .task("check");
        assertEquals("gradle clean build check", String.join(" ", builder.build()));
    }

    @Test
    void testCommandBuilder() {
        CommandBuilder builder = new CommandBuilder(ArgSeparator.SPACE);
        builder.command("some");
        builder.task("task");
        builder.option("--correctOption", true);
        builder.option("-singleDashOption", true);
        builder.option("--falseOption", false);
        builder.option("--nullOption", null);
        builder.argument("--correctArg", "CorrectVal");
        builder.argument("--emptyArg", "");
        builder.argument("--nullArg", null);
        String expected = "some task --correctOption -singleDashOption --correctArg CorrectVal";
        assertEquals(expected, String.join(" ", builder.build()));
    }

    @Test
    void testArgSeparator() {
        CommandBuilder builder = new CommandBuilder(ArgSeparator.EQUAL_SIGN);
        builder.command("some");
        builder.task("task");
        builder.argument("--arg", "value");
        assertEquals("some task --arg=value", String.join(" ", builder.build()));
    }
}

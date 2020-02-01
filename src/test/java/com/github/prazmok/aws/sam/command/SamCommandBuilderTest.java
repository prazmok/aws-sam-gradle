package com.github.prazmok.aws.sam.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SamCommandBuilderTest {
    @Test
    void testCommandBuilder() {
        SamCommandBuilder builder = new SamCommandBuilder();
        builder.task("package")
            .option("--debug")
            .argument("--arg", "value");
        assertEquals("sam package --debug --arg value", String.join(" ", builder.build()));
    }
}

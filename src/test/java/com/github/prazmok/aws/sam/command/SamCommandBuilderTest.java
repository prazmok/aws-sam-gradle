package com.github.prazmok.aws.sam.command;

import org.gradle.api.logging.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SamCommandBuilderTest {
    @Test
    void testCommandBuilder() {
        Logger logger = Mockito.mock(Logger.class);
        SamCommandBuilder builder = new SamCommandBuilder(logger, false);
        builder.task("package")
            .option("--debug")
            .argument("--arg", "value");
        assertEquals("sam package --debug --arg value", String.join(" ", builder.build()));
    }
}

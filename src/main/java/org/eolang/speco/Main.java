/*
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
 */
package org.eolang.speco;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import picocli.CommandLine;

/**
 * Main entrance.
 *
 * @since 0.0.1
 */
@CommandLine.Command(name = "speco",
    mixinStandardHelpOptions = true,
    version = "Version 0.0.1",
    description = "Specializes objects in EO programs")
public final class Main implements Callable<Integer> {

    /**
     * Relative path to the directory with input files.
     */
    @CommandLine.Option(names = { "--source" },
        description = "Directory with input .xmir files.")
    private Path input;

    /**
     * Relative path to the directory with output files.
     */
    @CommandLine.Option(names = { "--target" },
        description = "Directory for modified .xmir files.")
    private Path output;

    /**
     * Flag indicating whether the input files is EO-program.
     */
    @CommandLine.Option(names = { "--eo" },
        defaultValue = "false",
        description = "If the input program is in EO")
    private boolean eolang;

    /**
     * Flag indicating whether the temporary tags should be deleted.
     */
    @CommandLine.Option(names = { "--clear-xmir" },
        defaultValue = "false",
        description = "If delete temporary tags")
    private boolean clearxmir;

    @Override
    public Integer call() throws IOException {
        Speco speco = new DefaultSpeco();
        if (this.clearxmir) {
            speco = new ClearXmirSpeco(speco);
        }
        final Walk walk;
        if (this.eolang) {
            walk = new EoWalk(this.input, this.output, speco);
        } else {
            walk = new XmirWalk(this.input, this.output, speco);
        }
        walk.exec();
        return 0;
    }

    /**
     * Main entrance for Java command line.
     * @param args The args from the command line.
     */
    public static void main(final String[] args) {
        new CommandLine(new Main()).execute(args);
    }
}

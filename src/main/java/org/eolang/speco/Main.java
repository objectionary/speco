/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Objectionary
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.speco;

import java.io.IOException;
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
    @CommandLine.Option(names = { "--dir" },
        description = "Directory with input .xmir files")
    private String input;

    /**
     * Relative path to the directory with output files.
     */
    @CommandLine.Option(names = { "--target" },
        description = "Directory for modified .xmir files")
    private String output;

    @Override
    public Integer call() throws IOException {
        new Speco(this.input, this.output).exec();
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

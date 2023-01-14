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
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test to test the operation of the command line tool.
 *
 * @since 0.0.1
 */
public final class MainTest {

    /**
     * Relative path to the directory with tests.
     */
    private final Path tests = Path.of("src", "test", "resources");

    /**
     * Relative path to the directory with .xmir files.
     */
    private final Path xmirs = this.tests.resolve("xmir");

    /**
     * Relative path to the directory with .eo files.
     */
    private final Path eos = this.tests.resolve("eo");

    @Test
    public void convertsFromXmir(@TempDir final Path temp) throws IOException {
        final Path output = this.runSpeco(false, temp);
        for (final Path prog : Files.newDirectoryStream(output)) {
            this.compare(temp, prog);
        }
    }

    @Test
    public void convertsFromEo(@TempDir final Path temp) throws IOException {
        final Path output = this.runSpeco(true, temp);
        for (final Path prog : Files.newDirectoryStream(output.resolve("prog"))) {
            final Path program = temp.resolve(prog.getFileName());
            this.compare(temp, output.resolve("speco").resolve(prog.getFileName()));
            final List<String> expected = Files.readAllLines(prog.resolve("app"));
            final List<String> produced = this.exec(program.toString());
            Assertions.assertEquals(expected, produced);
        }
    }

    /**
     * Compares two directpries.
     *
     * @param first Directory
     * @param second Directory
     * @throws IOException Iff IO error
     */
    private void compare(final Path first, final Path second) throws IOException {
        for (final Path path : Files.newDirectoryStream(second)) {
            final List<String> expected = Files.readAllLines(path);
            final Path out = first.resolve(second.getFileName()).resolve(path.getFileName());
            final List<String> produced = Files.readAllLines(out);
            Assertions.assertEquals(expected, produced);
        }
    }

    /**
     * Runs Speco.
     *
     * @param eolang Iff input is EO
     * @param temp Path to the temporary output dir
     * @return Path to the reference output dir
     * @throws IOException Iff IO error
     */
    private Path runSpeco(final boolean eolang, final Path temp) throws IOException {
        final Path base;
        if (eolang) {
            base = this.eos;
        } else {
            base = this.xmirs;
        }
        final Path input = base.resolve("in");
        final Path output = base.resolve("out");
        for (final Path shift : Files.newDirectoryStream(input)) {
            final Path abs = shift.getFileName();
            new Speco(input.resolve(abs), temp.resolve(abs), eolang).exec();
        }
        return output;
    }

    /**
     * Compiles EO program.
     *
     * @param target Path to the dir with target EO program
     * @return List of lines in output
     * @throws IOException
     */
    private List<String> exec(final String target) throws IOException {
        final String pattern = "eoc link -s %s && eoc --alone dataize app && eoc clean";
        final String command = String.format(pattern, target);
        final String executor;
        final String flag;
        if (SystemUtils.IS_OS_WINDOWS) {
            executor = "cmd";
            flag = "/c";
        } else {
            executor = "bash";
            flag = "-c";
        }
        final Process process = new ProcessBuilder(executor, flag, command).start();
        final StringWriter writer = new StringWriter();
        IOUtils.copy(process.getInputStream(), writer);
        final String[] output = writer.toString().split("\\r?\\n");
        final String[] result = Arrays.copyOfRange(output, 11, output.length - 1);
        return Arrays.asList(result);
    }
}

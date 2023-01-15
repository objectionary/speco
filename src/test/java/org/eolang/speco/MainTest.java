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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(strings = {"simple"})
    public void convertsFromXmir(final String name, @TempDir final Path temp) throws IOException {
        final Path output = this.runSpeco(this.xmirs.resolve(name), temp, false);
        this.compare(temp, output);
    }

    @ParameterizedTest
    @ValueSource(strings = {"booms", "pets"})
    public void convertsFromEo(final String name, @TempDir final Path temp) throws IOException {
        final Path base = this.eos.resolve(name);
        final Path output = this.runSpeco(base, temp, true);
        this.compare(temp, output);
        final List<String> expected = Files.readAllLines(base.resolve("result.txt"));
        final List<String> produced = this.exec(temp.toString());
        Assertions.assertEquals(expected, produced);
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
            final Path out = first.resolve(path.getFileName());
            final List<String> produced = Files.readAllLines(out);
            Assertions.assertEquals(expected, produced);
        }
    }

    /**
     * Runs Speco.
     *
     * @param base Path to the base input dir
     * @param temp Path to the temporary output dir
     * @param iseo Iff input is EO
     * @return Path to the reference output dir
     * @throws IOException Iff IO error
     */
    private Path runSpeco(final Path base, final Path temp, final boolean iseo) throws IOException {
        final Path input = base.resolve("in");
        final Path output = base.resolve("out");
        new Speco(input, temp, iseo).exec();
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

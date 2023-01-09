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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test to test the operation of the command line tool.
 *
 * @since 0.0.1
 */
public final class MainTest {

    /**
     * Relative path to the directory with tests.
     */
    private final Path tests = Path.of("./src/test/resources");

    /**
     * Relative path to the directory with .xmir files.
     */
    private final Path xmirs = this.tests.resolve("xmir");

    /**
     * Relative path to the directory with .eo files.
     */
    private final Path eos = this.tests.resolve("eo");

    @Test
    public void testXmir() throws IOException {
        final Path input = this.xmirs.resolve("in");
        final Path output = this.xmirs.resolve("out");
        final Path temp = this.xmirs.resolve("temp");
        temp.toFile().mkdir();
        new Speco(input.toString(), temp.toString(), false).exec();
        final File[] reference = output.toFile().listFiles();
        Arrays.sort(reference);
        final File[] target = temp.toFile().listFiles();
        Arrays.sort(target);
        for (int index = 0; index < reference.length; index = index + 1) {
            final List<String> expected = FileUtils.readLines(reference[index]);
            final List<String> produced = FileUtils.readLines(target[index]);
            Assertions.assertEquals(expected, produced);
        }
        FileUtils.cleanDirectory(temp.toFile());
    }

    @Test
    public void testEo() throws IOException {
        final Path input = this.eos.resolve("in");
        final Path output = this.eos.resolve("out");
        final Path temp = this.eos.resolve("temp");
        temp.toFile().mkdir();
        new Speco(input.toString(), temp.toString(), true).exec();
        final File[] reference = output.toFile().listFiles();
        Arrays.sort(reference);
        final File[] target = temp.toFile().listFiles();
        Arrays.sort(target);
        for (int index = 0; index < reference.length; index = index + 1) {
            final List<String> expected = FileUtils.readLines(reference[index]);
            final List<String> produced = this.exec(target[index].getParent());
            Assertions.assertEquals(expected, produced);
        }
        FileUtils.cleanDirectory(temp.toFile());
    }

    private List<String> exec(final String target) throws IOException {
        final String command;
        if (SystemUtils.IS_OS_WINDOWS) {
            command = "cmd /c eoc link -s %s && eoc --alone dataize app && eoc clean";
        } else {
            command = "eoc link -s %s && eoc --alone dataize app && eoc clean";
        }
        final Process process = Runtime.getRuntime().exec(String.format(command, target));
        final StringWriter writer = new StringWriter();
        IOUtils.copy(process.getInputStream(), writer);
        final String string = writer.toString();
        final String[] full = string.split("\\r?\\n");
        final String[] result = Arrays.copyOfRange(full, 11, full.length - 1);
        return Arrays.asList(result);
    }
}

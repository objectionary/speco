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
import java.util.List;
import org.apache.commons.io.FileUtils;
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
    private final String tests = "./src/test/resources/";

    /**
     * Relative path to the directory with input files.
     */
    private final String input = this.tests.concat("in");

    /**
     * Relative path to the directory with output files.
     */
    private final String output = this.tests.concat("out");

    /**
     * Relative path to the directory with temporary files.
     */
    private final String temp = this.tests.concat("temp");

    @Test
    public void fullRun() throws IOException {
        FileUtils.cleanDirectory(new File(this.temp));
        new Speco(this.input, this.temp).exec();
        final File[] reference = new File(this.output).listFiles();
        final File[] target = new File(this.temp).listFiles();
        for (int index = 0; index < reference.length; index = index + 1) {
            final List<String> expected = FileUtils.readLines(reference[index]);
            final List<String> produced = FileUtils.readLines(target[index]);
            Assertions.assertEquals(expected, produced);
        }
        FileUtils.cleanDirectory(new File(this.temp));
    }
}

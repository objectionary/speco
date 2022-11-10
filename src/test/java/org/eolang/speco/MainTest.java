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
     * Relative path to the directory with input files.
     */
    private final String input = "./tmp/xmir";

    /**
     * Relative path to the directory with output files.
     */
    private final String output = "./tmp/xmir2";

    @Test
    public void fullRun() throws IOException {
        FileUtils.cleanDirectory(new File(this.output));
        new Speco(this.input, this.output).exec();
        final File[] source = new File(this.input).listFiles();
        for (int index = 0; index < source.length; index = index + 1) {
            source[index] = new File(source[index].getName());
        }
        final File[] target = new File(this.output).listFiles();
        for (int index = 0; index < target.length; index = index + 1) {
            target[index] = new File(target[index].getName());
        }
        Assertions.assertArrayEquals(source, target);
        FileUtils.cleanDirectory(new File(this.output));
    }
}

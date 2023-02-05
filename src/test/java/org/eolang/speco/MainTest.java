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
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test to test the operation of the command line tool.
 *
 * @since 0.0.1
 * @todo #22:90min add separate unit-test for each transformation,
 *  which would run step by step and check the intermediate results,
 *  for example, use yml packs as in dejump.
 * @todo #32:90min fix disabled convertsFromEo tests for a group with matrices,
 *  the reason is that only one parameter is processed for specialization,
 *  but it is necessary to process all.
 */
public final class MainTest {

    /**
     * Relative path to the directory with tests.
     */
    private final Path tests = Path.of("src", "test", "resources", "org", "eolang", "speco");

    /**
     * Relative path to the directory with .xmir files.
     */
    private final Path xmirs = this.tests.resolve("xmir");

    @Tag("fast")
    @ParameterizedTest
    @ValueSource(strings = {"simple"})
    public void convertsFromXmir(final String name, @TempDir final Path temp) throws IOException {
        MainTest.compare(temp, MainTest.runSpeco(this.xmirs.resolve(name), temp, false));
    }

    /**
     * Compares two directories according to the RIGHT JOIN sql principle.
     * Checks that in second only files from first and the content of these files match.
     *
     * @param base First directory to be compared (base part of RIGHT JOIN)
     * @param joining First directory to be compared (joining part of RIGHT JOIN)
     * @throws IOException Iff IO error
     */
    private static void compare(final Path base, final Path joining) throws IOException {
        for (final Path path : Files.newDirectoryStream(joining)) {
            Assertions.assertEquals(
                Files.readAllLines(path),
                Files.readAllLines(base.resolve(path.getFileName())),
                String.format("Files in %s and %s are different", base, joining)
            );
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
    private static Path runSpeco(final Path base, final Path temp, final boolean iseo)
        throws IOException {
        new Speco(base.resolve("in"), temp, iseo).exec();
        return base.resolve("out");
    }
}

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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests that check entire Speco algorithm applied directly to parsed XMIR.
 *
 * @since 0.2
 */
class SpecoXmirTest {

    @Tag("fast")
    @ParameterizedTest
    @ValueSource(strings = "simple")
    void convertsFromXmir(final String title, @TempDir final Path out) throws IOException {
        final Path base = Path.of(
            "src", "test", "resources",
            "org", "eolang", "speco",
            "xmir", title
        );
        new Speco(base.resolve("in"), out, false).exec();
        final Path expected = base.resolve("out");
        for (final Path path : Files.newDirectoryStream(expected)) {
            MatcherAssert.assertThat(
                String.format(
                    "Files %s in %s and %s are different",
                    path.getFileName(),
                    out,
                    expected
                ),
                Files.readAllLines(out.resolve(path.getFileName())),
                Matchers.equalTo(
                    Files.readAllLines(path)
                )
            );
        }
    }
}

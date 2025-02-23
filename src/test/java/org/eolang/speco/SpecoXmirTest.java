/*
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
 */
package org.eolang.speco;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
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
        new XmirWalk(
            base.resolve("in"),
            out,
            new DefaultSpeco()
        ).exec();
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

/*
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
 */
package org.eolang.speco;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The class encapsulating applying of specialization to XMIR.
 *
 * @since 0.0.3
 */
public final class XmirWalk implements Walk {
    /**
     * Absolute path to the directory with input files.
     */
    private final Path input;

    /**
     * Absolute path to the directory with output files.
     */
    private final Path output;

    /**
     * Origin speco.
     */
    private final Speco speco;

    /**
     * Ctor.
     *
     * @param input Absolute path to the directory with input files
     * @param output Absolute path to the directory with output files
     * @param speco Origin speco
     */
    public XmirWalk(
        final Path input,
        final Path output,
        final Speco speco
    ) {
        this.input = input;
        this.output = output;
        this.speco = speco;
    }

    @Override
    public void exec() throws IOException {
        Files.createDirectories(this.output);
        for (final Path path : Files.newDirectoryStream(this.input)) {
            Files.write(
                this.output.resolve(path.getFileName()),
                this.speco.transform(Walk.toXml(path)).toString().getBytes()
            );
        }
    }
}

/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
 */
package org.eolang.speco;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The interface encapsulating applying of specialization.
 *
 * @since 0.0.3
 * @todo #39:60min refactor this interface and classes EoWalk and XmirWalk implementing it,
 *  to avoid code duplication. In particular, in the fields (input, output and speco),
 *  constructor and exec method.
 */
public interface Walk {
    /**
     * Starts the specialization process.
     *
     * @throws IOException In case of errors when working with files or parsing a document
     */
    void exec() throws IOException;

    /**
     * Read XML from file.
     *
     * @param path Path to input file.
     * @return Read XML
     * @throws IOException In case of errors when reading from file
     */
    static XML toXml(final Path path) throws IOException {
        return new XMLDocument(Files.readString(path));
    }
}

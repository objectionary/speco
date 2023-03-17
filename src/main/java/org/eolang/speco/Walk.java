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

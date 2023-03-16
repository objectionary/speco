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
 * The interface encapsulating applying of specialization to XMIR.
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
                this.speco.transform(XmirWalk.toXml(path)).toString().getBytes()
            );
        }
    }

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

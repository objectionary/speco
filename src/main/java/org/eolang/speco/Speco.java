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
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.StClasspath;
import com.yegor256.xsline.StEndless;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.Train;
import com.yegor256.xsline.Xsline;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * The class encapsulating specialization logic.
 *
 * @since 0.0.1
 */
public final class Speco {

    /**
     * Absolute path to the directory with input files.
     */
    private final String input;

    /**
     * Absolute path to the directory with output files.
     */
    private final String output;

    /**
     * Ctor.
     *
     * @param input Path to the directory with input files
     * @param output Path to the directory with output files
     */
    public Speco(final String input, final String output) {
        this.input = new File(input).getAbsolutePath();
        this.output = new File(output).getAbsolutePath();
    }

    /**
     * Applies train of XSL-transformations.
     *
     * @param xml XML
     * @return XML
     */
    public static XML applyTrain(final XML xml) {
        final Train<Shift> train = new TrDefault<Shift>()
            .with(new StEndless(new StClasspath("/org/eolang/speco/simple-transformation.xsl")));
        return new Xsline(train).pass(xml);
    }

    /**
     * Will do specialization.
     *
     * @throws IOException Always before implemantation
     */
    public void exec() throws IOException {
        final File[] dir = new File(this.input).listFiles();
        for (final File file : dir) {
            final XML document = new XMLDocument(Files.readString(file.toPath()));
            final XML before = Speco.getParsedXml(document);
            final XML after = Speco.applyTrain(before);
            final File target = new File(this.output, file.getName());
            target.createNewFile();
            try (FileWriter out = new FileWriter(target.getPath())) {
                out.write(after.toString());
                out.flush();
            }
        }
    }

    /**
     * Parses EO-xmir documents.
     *
     * @param input XML input
     * @return XML
     * @throws IOException When Parsing EO fails
     */
    public static XML getParsedXml(final XML input) throws IOException {
        return new Xsline(
            new TrDefault<Shift>().with(new StClasspath("/org/eolang/parser/wrap-method-calls.xsl"))
        ).pass(input);
    }
}

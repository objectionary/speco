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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.cactoos.io.InputOf;
import org.cactoos.io.OutputTo;
import org.eolang.parser.Syntax;
import org.eolang.parser.XMIR;
import org.objectionary.aoi.launch.LauncherKt;

/**
 * The class encapsulating specialization logic.
 *
 * @since 0.0.1
 */
final class Speco {

    /**
     * Absolute path to the directory with input files.
     */
    private final Path input;

    /**
     * Absolute path to the directory with output files.
     */
    private final Path output;

    /**
     * Flag indicating whether the input files is EO-program.
     */
    private final boolean eolang;

    /**
     * Ctor.
     *
     * @param input Path to the directory with input files
     * @param output Path to the directory with output files
     * @param eolang Iff the input program is in EO
     */
    Speco(final Path input, final Path output, final boolean eolang) {
        this.input = input.toAbsolutePath();
        this.output = output.toAbsolutePath();
        this.eolang = eolang;
    }

    /**
     * Starts the specialization process.
     *
     * @throws IOException In case of errors when working with files or parsing a document
     */
    public void exec() throws IOException {
        final Path source;
        if (this.eolang) {
            source = parse(this.input);
        } else {
            source = this.input;
        }
        for (final Path path : Files.newDirectoryStream(source)) {
            final XML before;
            before = Speco.getParsedXml(new XMLDocument(Files.readString(path)));
            final String after;
            if (this.eolang) {
                after = new XMIR(
                    Speco.applyTrain(before).toString()
                ).toEO();
            } else {
                after = Speco.applyTrain(before).toString();
            }
            Files.createDirectories(this.output);
            Files.write(this.output.resolve(path.getFileName()), after.getBytes());
        }
        if (this.eolang) {
            try {
                FileUtils.deleteDirectory(source.toFile());
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Applies train of XSL-transformations.
     *
     * @param xml XML
     * @return XML
     */
    private static XML applyTrain(final XML xml) {
        final Train<Shift> train = new TrDefault<Shift>()
            .with(new StEndless(new StClasspath("/org/eolang/speco/coping.xsl")))
            .with(new StEndless(new StClasspath("/org/eolang/speco/preparation.xsl")))
            .with(new StEndless(new StClasspath("/org/eolang/speco/simple-transformation.xsl")))
            .with(new StEndless(new StClasspath("/org/eolang/speco/formatting.xsl")));
        return new Xsline(train).pass(xml);
    }

    /**
     * Parses EO-xmir documents.
     *
     * @param input XML input
     * @return XML
     */
    private static XML getParsedXml(final XML input) {
        return new Xsline(
            new TrDefault<Shift>().with(new StClasspath("/org/eolang/parser/wrap-method-calls.xsl"))
        ).pass(input);
    }

    /**
     * Takes source codes on EO, converts to xmir and applies the AOI tool.
     *
     * @param input Path of the source directory
     * @return Path to the directory with the parsed files
     * @throws IOException When Parsing EO fails
     */
    private static Path parse(final Path input) throws IOException {
        final StringBuilder name = new StringBuilder(input.toString());
        final Path source = Path.of(name.append("_prs").toString());
        FileUtils.copyDirectory(input.toFile(), source.toFile());
        for (final Path path : Files.newDirectoryStream(source)) {
            new Syntax(
                "scenario",
                new InputOf(String.format("%s\n", Files.readString(path))),
                new OutputTo(new FileOutputStream(path.toFile()))
            ).parse();
        }
        LauncherKt.launch(source.toString());
        FileUtils.deleteDirectory(source.toFile());
        return Path.of(name.append("_aoi").toString());
    }
}

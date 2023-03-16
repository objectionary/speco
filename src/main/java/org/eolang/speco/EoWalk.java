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
 * The interface encapsulating applying of specialization to EO.
 *
 * @since 0.0.3
 */
public final class EoWalk implements Walk {
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
    public EoWalk(
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
        for (final Path path : Files.newDirectoryStream(EoWalk.parse(this.input))) {
            Files.write(
                this.output.resolve(path.getFileName()),
                new XMIR(this.speco.transform(EoWalk.toXml(path))).toEO().getBytes()
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
                new InputOf(String.format("%s%n", Files.readString(path))),
                new OutputTo(new FileOutputStream(path.toFile()))
            ).parse();
        }
        LauncherKt.launch(source.toString());
        return Path.of(name.append("_aoi").toString());
    }
}

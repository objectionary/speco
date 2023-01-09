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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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
     * Flag indicating whether the input files is EO-program.
     */
    private final Boolean eolang;

    /**
     * Ctor.
     *
     * @param input Path to the directory with input files
     * @param output Path to the directory with output files
     * @param eolang Iff the input program is in EO
     */
    public Speco(final String input, final String output, final Boolean eolang) {
        this.input = new File(input).getAbsolutePath();
        this.output = new File(output).getAbsolutePath();
        this.eolang = eolang;
    }

    /**
     * Applies train of XSL-transformations.
     *
     * @param xml XML
     * @return XML
     */
    public static XML applyTrain(final XML xml) {
        final Train<Shift> train = new TrDefault<Shift>()
            .with(new StEndless(new StClasspath("/org/eolang/speco/coping.xsl")))
            .with(new StEndless(new StClasspath("/org/eolang/speco/preparation.xsl")))
            .with(new StEndless(new StClasspath("/org/eolang/speco/simple-transformation.xsl")))
            .with(new StEndless(new StClasspath("/org/eolang/speco/formatting.xsl")));
        return new Xsline(train).pass(xml);
    }

    /**
     * Starts the specialization process.
     *
     * @throws IOException In case of errors when working with files or parsing a document
     */
    public void exec() throws IOException {
        final String source;
        if (this.eolang) {
            source = parse(this.input);
        } else {
            source = this.input;
        }
        final File[] dir = new File(source).listFiles();
        for (final File file : dir) {
            final XML before;
            final XML document = new XMLDocument(Files.readString(file.toPath()));
            before = Speco.getParsedXml(document);
            final String after;
            if (this.eolang) {
                after = new XMIR(
                    Speco.applyTrain(before).toString()
                ).toEO();
            } else {
                after = Speco.applyTrain(before).toString();
            }
            final File target = new File(this.output, file.getName());
            target.createNewFile();
            try (FileWriter out = new FileWriter(target.getPath())) {
                out.write(after);
                out.flush();
            }
        }
    }

    /**
     * Parses EO-xmir documents.
     *
     * @param input XML input
     * @return XML
     */
    public static XML getParsedXml(final XML input) {
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
    public static String parse(final String input) throws IOException {
        String source = input.concat("_prs");
        FileUtils.copyDirectory(new File(input), new File(source));
        final File[] dir = new File(source).listFiles();
        for (final File file : dir) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final String content = Files.readString(file.toPath());
            new Syntax(
                "scenario",
                new InputOf(String.format("%s\n", content)),
                new OutputTo(baos)
            ).parse();
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(baos.toByteArray());
                out.flush();
            }
            baos.close();
        }
        LauncherKt.launch(source.toString());
        source = source.concat("_aoi");
        return source.toString();
    }
}

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

import com.yegor256.xsline.Shift;
import com.yegor256.xsline.Train;
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
 * The entity encapsulating specialization logic.
 *
 * @since 0.0.3
 */
final class EolangSpeco implements Speco {

    /**
     * Encapsulated speco.
     */
    private final Speco origin;

    /**
     * Ctor.
     *
     * @param origin Origin encapsulated speco.
     */
    EolangSpeco(final Speco origin) {
        this.origin = origin;
    }

    @Override
    public void exec() throws IOException {
        Files.createDirectories(this.output());
        for (final Path path : Files.newDirectoryStream(this.input())) {
            Files.write(
                this.output().resolve(path.getFileName()),
                new XMIR(this.transform(path)).toEO().getBytes()
            );
        }
    }

    @Override
    public String transform(final Path path) throws IOException {
        return this.origin.transform(path);
    }

    @Override
    public Train<Shift> train() {
        return this.origin.train();
    }

    @Override
    public Path input() throws IOException {
        return parse(this.origin.input());
    }

    @Override
    public Path output() {
        return this.origin.output();
    }

    @Override
    public String format(final String content) {
        return new XMIR(this.origin.format(content)).toEO();
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

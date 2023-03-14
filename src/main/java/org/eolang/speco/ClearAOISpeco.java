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

import com.jcabi.xml.XMLDocument;
import com.yegor256.xsline.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The entity encapsulating specialization logic.
 *
 * @since 0.0.3
 */
public class ClearAOISpeco implements Speco {

    /**
     * Encapsulated speco.
     */
    private final Speco origin;

    /**
     * Ctor.
     *
     * @param origin Origin encapsulated speco.
     */
    ClearAOISpeco(final Speco origin) {
        this.origin = origin;
    }

    @Override
    public final void exec() throws IOException {
        Files.createDirectories(this.output());
        for (final Path path : Files.newDirectoryStream(this.input())) {
            Files.write(
                this.output().resolve(path.getFileName()),
                this.format(this.transform(path)).getBytes()
            );
        }
    }

    @Override
    public final String transform(final Path path) throws IOException {
        return new Xsline(train()).pass(
            new Xsline(
                new TrDefault<Shift>().with(
                    new StClasspath("/org/eolang/parser/wrap-method-calls.xsl")
                )
            ).pass(new XMLDocument(Files.readString(path)))
        ).toString();
    }

    @Override
    public final Train<Shift> train() {
        return this.origin.train().with(new StClasspath("/org/eolang/speco/clear.xsl"));
    }

    @Override
    public final Path input() throws IOException {
        return this.origin.input();
    }

    @Override
    public final Path output() {
        return this.origin.output();
    }

    @Override
    public final String format(String content) {
        return this.origin.format(content);
    }
}

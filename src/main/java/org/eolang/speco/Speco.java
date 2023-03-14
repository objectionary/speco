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
import java.io.IOException;
import java.nio.file.Path;

/**
 * The entity encapsulating specialization logic.
 *
 * @since 0.0.3
 */
public interface Speco {
    /**
     * Starts the specialization process.
     *
     * @throws IOException In case of errors when working with files or parsing a document
     */
    void exec() throws IOException;

    /**
     * Transforms.
     *
     * @param path Path
     * @return String
     * @throws IOException In case of errors when working with files or parsing a document
     */
    String transform(Path path) throws IOException;

    /**
     * Applies train of XSL-transformations.
     *
     * @return XML
     */
    Train<Shift> train();

    /**
     * Absolute path to the directory with input files.
     *
     * @param content String
     * @return Path
     */
    String format(String content);

    /**
     * Absolute path to the directory with input files.
     *
     * @return Path
     * @throws IOException In case of errors when working with files or parsing a document
     */
    Path input() throws IOException;

    /**
     * Absolute path to the directory with output files.
     *
     * @return Path
     */
    Path output();
}

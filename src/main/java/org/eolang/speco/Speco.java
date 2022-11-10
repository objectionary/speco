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

/**
 * The class encapsulating specialization logic.
 *
 * @since 0.0.1
 */
public final class Speco {

    /**
     * Relative path to the directory with input files.
     */
    private final String input;

    /**
     * Relative path to the directory with output files.
     */
    private final String output;

    /**
     * Ctor.
     *
     * @param input Path to the directory with input files
     * @param output Path to the directory with output files
     */
    public Speco(final String input, final String output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Will do specialization.
     *
     * @throws UnsupportedOperationException Always before implemantation
     */
    public void exec() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Speco is not implemented yet");
    }
}

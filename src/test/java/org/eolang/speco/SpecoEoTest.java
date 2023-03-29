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

import com.jcabi.log.Logger;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eolang.jucs.ClasspathSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.yaml.snakeyaml.Yaml;

/**
 * Tests that check entire Speco algorithm applied directly to raw EO programs.
 *
 * @since 0.0.1
 */
class SpecoEoTest {

    /**
     * The number of lines that the command "eoc link" outputs.
     */
    private static final int INTENT = 11;

    /**
     * The number of lines from the beginning of the test to identify the context.
     */
    private static final int HEAD = 20;

    /**
     * Integration test for conversation from EO.
     * @param pack Pack this test data
     * @param temp Temporary test dir
     * @throws IOException Iff IO error
     */
    @Tag("fast")
    @DisabledOnOs(OS.WINDOWS)
    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/speco/packs", glob = "**.yaml")
    void convertsFromEo(final String pack, @TempDir final Path temp) throws IOException {
        final Map<String, Object> script = new Yaml().load(pack);
        MatcherAssert.assertThat(
            "Unexpected transformation result",
            Files.readString(SpecoEoTest.run(script, temp).resolve("app.eo")),
            Matchers.equalTo(script.get("after").toString())
        );
    }

    /**
     * Integration test for compilation and result checking program converted from EO.
     * @todo #32:30min investigate problem with @DisableOnOs(OS.Windows),
     *  this is an issue of junit -- https://github.com/junit-team/junit5/issues/2811,
     *  maybe try to manually delete each created file or use a pat
     *  or get rid of the @TempDir.
     * @param pack Pack this test data
     * @param temp Temporary test dir
     * @throws java.io.IOException Iff IO error
     */
    @Tag("slow")
    @DisabledOnOs(OS.WINDOWS)
    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/speco/packs", glob = "**.yaml")
    void compilesFromEo(final String pack, @TempDir final Path temp)
        throws IOException, InterruptedException {
        final Map<String, Object> script = new Yaml().load(pack);
        MatcherAssert.assertThat(
            "Unexpected execution result",
            this.dataize(
                SpecoEoTest.run(script, temp).toString(),
                pack.substring(0, Math.min(pack.length(), SpecoEoTest.HEAD))
            ),
            Matchers.equalTo(
                script.get("result").toString().split("\\r?\\n")
            )
        );
    }

    /**
     * Runs Speco.
     *
     * @param script Yaml data object
     * @param temp Path to the temporary dir
     * @return Path to the output dir
     * @throws IOException Iff IO error
     */
    private static Path run(final Map<String, Object> script, final Path temp)
        throws IOException {
        final Path input = temp.resolve("input");
        final Path output = temp.resolve("output");
        Files.createDirectories(input);
        Files.writeString(
            input.resolve("app.eo"),
            script.get("before").toString(),
            StandardOpenOption.CREATE
        );
        new Speco(input, output, true).exec();
        return output;
    }

    /**
     * Compiles and dataize EO program.
     *
     * @param target Path to the dir with target EO program
     * @param context Information about the test being run, the head of the input data
     * @return List of lines in output
     * @throws IOException Iff IO error
     */
    private String[] dataize(final String target, final String context)
        throws IOException, InterruptedException {
        final String executor;
        final String flag;
        if (SystemUtils.IS_OS_WINDOWS) {
            executor = "cmd";
            flag = "/c";
        } else {
            executor = "bash";
            flag = "-c";
        }
        Logger.debug(
            this,
            String.format("Started compilation for %s with test head: %s.", target, context)
        );
        final Process process = new ProcessBuilder(
            executor,
            flag,
            String.format("eoc link -s %s && eoc --alone dataize app && eoc clean", target)
        ).start();
        process.waitFor();
        final StringWriter writer = new StringWriter();
        IOUtils.copy(process.getInputStream(), writer, Charset.defaultCharset());
        process.getInputStream().close();
        process.destroy();
        final String[] output = writer.toString().split("\\r?\\n");
        writer.close();
        Logger.debug(
            this,
            String.format(
                "Finished compilation for %s and got %d lines of output",
                target,
                output.length
            )
        );
        return Arrays.copyOfRange(output, SpecoEoTest.INTENT, output.length - 1);
    }
}

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
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.StClasspath;
import com.yegor256.xsline.StEndless;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.Train;
import com.yegor256.xsline.Xsline;
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
import org.cactoos.scalar.Ternary;
import org.cactoos.scalar.Unchecked;
import org.eolang.jucs.ClasspathSource;
import org.eolang.xax.XaxStory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.yaml.snakeyaml.Yaml;

/**
 * Packs tests.
 *
 * @since 0.0.1
 */
class SpecoTest {

    /**
     * The number of lines that the command "eoc link" outputs.
     */
    private static final int INTENT = 11;

    @Tag("fast")
    @ParameterizedTest
    @ValueSource(strings = {"simple"})
    public void convertsFromXmir(final String title, @TempDir final Path temp) throws IOException {
        final Path base = Path.of(
            "src", "test", "resources",
            "org", "eolang", "speco",
            "xmir", title
        );
        new Speco(base.resolve("in"), temp, false).exec();
        final Path reference = base.resolve("out");
        for (final Path path : Files.newDirectoryStream(reference)) {
            MatcherAssert.assertThat(
                String.format(
                    "Files %s in %s and %s are different",
                    path.getFileName(),
                    temp,
                    reference
                ),
                Files.readAllLines(temp.resolve(path.getFileName())),
                Matchers.equalTo(
                    Files.readAllLines(path)
                )
            );
        }
    }

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
    public void convertsFromEo(final String pack, @TempDir final Path temp) throws IOException {
        final Map<String, Object> script = new Yaml().load(pack);
        MatcherAssert.assertThat(
            "Unexpected transformation result",
            Files.readString(SpecoTest.run(script, temp).resolve("app.eo")),
            Matchers.equalTo(
                script.get("after").toString()
            )
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
     * @throws IOException Iff IO error
     */
    @Tag("slow")
    @DisabledOnOs(OS.WINDOWS)
    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/speco/packs", glob = "**.yaml")
    public void compilesFromEo(final String pack, @TempDir final Path temp)
        throws IOException, InterruptedException {
        final Map<String, Object> script = new Yaml().load(pack);
        MatcherAssert.assertThat(
            "Unexpected execution result",
            SpecoTest.dataize(SpecoTest.run(script, temp).toString()),
            Matchers.equalTo(
                script.get("result").toString().split("\\r?\\n")
            )
        );
    }

    /**
     * Unit tests for transformations.
     * @param pack Pack with test data
     * @throws IOException Iff IO error
     */
    @Tag("fast")
    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/speco/transformations", glob = "**.yaml")
    public void applyTransToXmir(final String pack) throws IOException {
        MatcherAssert.assertThat(
            new XaxStory(pack),
            Matchers.is(true)
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
        Files.write(
            input.resolve("app.eo"),
            script.get("before").toString().getBytes(),
            StandardOpenOption.CREATE
        );
        new Speco(input, output, true).exec();
        return output;
    }

    /**
    * Compiles and dataize EO program.
    *
    * @param target Path to the dir with target EO program
    * @return List of lines in output
    * @throws IOException Iff IO error
    */
    private static String[] dataize(final String target) throws IOException, InterruptedException {
        final String executor;
        final String flag;
        if (SystemUtils.IS_OS_WINDOWS) {
            executor = "cmd";
            flag = "/c";
        } else {
            executor = "bash";
            flag = "-c";
        }
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
        return Arrays.copyOfRange(output, SpecoTest.INTENT, output.length - 1);
    }
}

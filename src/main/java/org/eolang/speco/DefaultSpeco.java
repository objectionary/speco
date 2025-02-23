/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
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
 * The class encapsulating specialization logic for xmir programs.
 *
 * @since 0.0.3
 */
final class DefaultSpeco implements Speco {

    @Override
    public XML transform(final XML xml) throws IOException {
        return new Xsline(new TrDefault<Shift>()
            .with(new StClasspath("/org/eolang/speco/1-1-coping.xsl"))
            .with(new StEndless(new StClasspath("/org/eolang/speco/1-2-specialization.xsl")))
            .with(new StClasspath("/org/eolang/speco/1-3-extension.xsl"))
            .with(new StClasspath("/org/eolang/speco/2-1-substitute-applications.xsl"))
            .with(new StClasspath("/org/eolang/speco/3-1-add-with.xsl"))
            .with(new StClasspath("/org/eolang/speco/4-1-fence-tuples.xsl"))
            .with(new StClasspath("/org/eolang/speco/5-1-substitute-fence.xsl"))
            .with(new StClasspath("/org/eolang/speco/6-1-substitute-dominant.xsl"))
            .with(new StClasspath("/org/eolang/speco/7-1-substitute-returned.xsl"))
        ).pass(
            new Xsline(
                new TrDefault<Shift>().with(
                    new StClasspath("/org/eolang/parser/wrap-method-calls.xsl")
                )
            ).pass(xml)
        );
    }
}

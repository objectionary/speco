/*
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
 */
package org.eolang.speco;

import com.jcabi.xml.XML;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.StClasspath;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.Xsline;
import java.io.IOException;

/**
 * The class encapsulating specialization logic with the clearing of the resulting xmir.
 *
 * @since 0.0.3
 */
final class ClearXmirSpeco implements Speco {

    /**
     * Encapsulated speco.
     */
    private final Speco origin;

    /**
     * Ctor.
     *
     * @param origin Encapsulated speco.
     */
    ClearXmirSpeco(final Speco origin) {
        this.origin = origin;
    }

    @Override
    public XML transform(final XML xml) throws IOException {
        return new Xsline(
            new TrDefault<>(new StClasspath("/org/eolang/speco/clear.xsl"))
            ).pass(
                new Xsline(
                    new TrDefault<Shift>().with(
                        new StClasspath("/org/eolang/parser/wrap-method-calls.xsl")
                    )
                ).pass(this.origin.transform(xml))
            );
    }
}

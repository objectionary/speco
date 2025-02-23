/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
 */
package org.eolang.speco;

import com.jcabi.log.Logger;
import org.eolang.jucs.ClasspathSource;
import org.eolang.xax.XaxStory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * The tests check that the transformations are performed correctly on XMIR.
 * Each test check separate transformation of the Speco algorithm.
 *
 * @since 0.2
 */
@Tag("fast")
class AllTransformationStepsTest {

    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/speco/transformations", glob = "**.yaml")
    void appliesTransformationsToXmir(final String pack) {
        MatcherAssert.assertThat(
            new XaxStory(pack),
            Matchers.is(true)
        );
    }
}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
 */
package org.eolang.speco;

import com.jcabi.xml.XML;
import java.io.IOException;

/**
 * The interface encapsulating specialization logic.
 *
 * @since 0.0.3
 */
public interface Speco {
    /**
     * Applies XSL-transformations to XML.
     *
     * @param path Path to source xml file
     * @return String Representation of transfromed xml
     * @throws IOException In case of errors when reading from file
     */
    XML transform(XML path) throws IOException;
}

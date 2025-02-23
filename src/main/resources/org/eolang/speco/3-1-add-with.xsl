<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="SG" version="2.0">
  <!--
    Rule #3: extend the program by adding new with-* attributes to all synthetic objects.
  -->
  <xsl:output indent="yes" method="xml"/>
  <xsl:strip-space elements="*"/>
  <!--
    Iterates over <objects/> and adds with-* attributes
    for all specialized versions of current object.
  -->
  <xsl:template match="/program/objects/o">
    <xsl:variable name="name" select="@name"/>
    <xsl:variable name="spec" select="@spec"/>
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:for-each select="/program/speco/version[@name=$name or @name=$spec]/o">
        <xsl:element name="o">
          <xsl:attribute name="abstract"/>
          <xsl:attribute name="name">
            <xsl:value-of select="concat('with_', translate(../@spec, '.', '_'))"/>
          </xsl:attribute>
          <xsl:for-each select="o[not(@base) and not(@abstract)]">
            <xsl:element name="o">
              <xsl:attribute name="name">
                <xsl:value-of select="@name"/>
              </xsl:attribute>
            </xsl:element>
          </xsl:for-each>
          <xsl:element name="o">
            <xsl:attribute name="base">
              <xsl:value-of select="@name"/>
            </xsl:attribute>
            <xsl:attribute name="name">
              <xsl:value-of select="'@'"/>
            </xsl:attribute>
            <xsl:for-each select="o[not(@base) and not(@abstract)]">
              <xsl:element name="o">
                <xsl:attribute name="base">
                  <xsl:value-of select="@name"/>
                </xsl:attribute>
              </xsl:element>
            </xsl:for-each>
          </xsl:element>
        </xsl:element>
      </xsl:for-each>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

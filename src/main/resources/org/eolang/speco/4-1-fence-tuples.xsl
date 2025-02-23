<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="SG" version="2.0">
  <!--
    Rule #4: extend the program by creating duplicates of each fence attribute.
  -->
  <xsl:output indent="yes" method="xml"/>
  <xsl:strip-space elements="*"/>
  <!--
    Finds fence attributes and creates duplicates of them, which return a two-elements tuple
    with the object it returned before and the "parent" object.
  -->
  <xsl:template match="/program/objects//o">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:for-each select="o[@abstract and .//@base=concat('.', ../o[@base='memory']/@name)]">
        <xsl:copy>
          <xsl:attribute name="abstract"/>
          <xsl:attribute name="name">
            <xsl:value-of select="concat(@name, '_as_tuple')"/>
          </xsl:attribute>
          <xsl:attribute name="fence">
            <xsl:value-of select="@name"/>
          </xsl:attribute>
          <xsl:for-each select="o">
            <xsl:choose>
              <xsl:when test=".[@name='@']">
                <xsl:copy>
                  <xsl:apply-templates select="@*"/>
                  <xsl:for-each select="o[position() != last()]">
                    <xsl:copy-of select="."/>
                  </xsl:for-each>
                  <xsl:for-each select="o[last()]">
                    <xsl:element name="o">
                      <xsl:attribute name="base">
                        <xsl:value-of select="'tuple'"/>
                      </xsl:attribute>
                      <xsl:attribute name="data">
                        <xsl:value-of select="'tuple'"/>
                      </xsl:attribute>
                      <xsl:copy-of select="."/>
                      <xsl:element name="o">
                        <xsl:attribute name="base">
                          <xsl:value-of select="'^'"/>
                        </xsl:attribute>
                      </xsl:element>
                    </xsl:element>
                  </xsl:for-each>
                </xsl:copy>
              </xsl:when>
              <xsl:otherwise>
                <xsl:copy-of select="."/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </xsl:copy>
      </xsl:for-each>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

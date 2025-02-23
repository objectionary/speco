<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="SG" version="2.0">
  <!--
    Rule #2: substitute all applications of native objects with
    applications of their synthetic counterparts.
  -->
  <xsl:output indent="yes" method="xml"/>
  <xsl:strip-space elements="*"/>
  <!--
    Checks the appropriate specialized version in the lines with the application.
  -->
  <xsl:template match="/program/objects//o">
    <xsl:variable name="name" select="@base"/>
    <xsl:variable name="spec">
      <xsl:for-each select="o">
        <xsl:copy>
          <xsl:value-of select="concat(@base, '_')"/>
        </xsl:copy>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy>
      <xsl:if test="@base">
        <xsl:attribute name="base">
          <xsl:value-of select="$name"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:for-each select="/program/speco/version[@name=$name and concat(@spec, '_')=$spec]/o">
        <xsl:attribute name="base">
          <xsl:value-of select="@name"/>
        </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="@* except @base |node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

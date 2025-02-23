<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="SG" version="2.0">
  <!--
    Rule #1: extend the program by creating specialized object
    based on AOI.
  -->
  <xsl:output indent="yes" method="xml"/>
  <xsl:strip-space elements="*"/>
  <!--
     Recursively copies the <aoi/> node to the new <speco/> node.
  -->
  <xsl:template match="program">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:element name="speco">
        <xsl:copy-of select="/program/aoi/node()"/>
      </xsl:element>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="SG" version="2.0">
  <!--
    Rule #7: Substitute all "x.cage.write y" with "TRUE" (the result of write dataization);
    substitute the first reference to "x" dominated by write with "x.with y" labeled as "tmp";
    substitute all further dominated references to "x" with "tmp"
  -->
  <xsl:output indent="yes" method="xml"/>
  <xsl:strip-space elements="*"/>
  <!--
    Replaces the first fence tuple element with call with an attribute with an argument
    hat was previously the "write" argument.
  -->
  <xsl:template match="/program/objects//o[../../@fence and @base='tuple']/o[1]">
    <xsl:for-each select="../../o[@base='.write']/o[1]">
      <xsl:variable name="name" select="../../../../@name"/>
      <xsl:copy>
        <xsl:attribute name="base">
          <xsl:value-of select="concat(substring-after(@base, '.'), '.')"/>
        </xsl:attribute>
        <xsl:apply-templates select="@* except @base"/>
        <xsl:element name="o">
          <xsl:attribute name="base">
            <xsl:value-of select="concat('.', ../../../../o[starts-with(@name, 'with_') and o/@base=$name or @name=concat('with_', $name)]/@name)"/>
          </xsl:attribute>
          <xsl:attribute name="name">
            <xsl:value-of select="'tmp'"/>
          </xsl:attribute>
          <xsl:attribute name="method"/>
          <xsl:copy-of select="node()"/>
          <xsl:copy>
            <xsl:for-each select="../../o[@base='.write']/o[2]">
              <xsl:apply-templates select="@*|node()"/>
            </xsl:for-each>
          </xsl:copy>
        </xsl:element>
      </xsl:copy>
    </xsl:for-each>
  </xsl:template>
  <!--
     Replaces the second fence tuple element with a temporary object.
  -->
  <xsl:template match="/program/objects//o[../../@fence and @base='tuple']/o[2]">
    <o base="tmp"/>
  </xsl:template>
  <!--
     Replaces write with the result of its dataization.
  -->
  <xsl:template match="/program/objects//o[../../@fence and @base='.write']">
    <o base="bool" data="bytes">01</o>
  </xsl:template>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

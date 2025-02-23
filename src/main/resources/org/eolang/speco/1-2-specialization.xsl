<?xml version="1.0"?>
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
    Iterates through the objects that need to be specialized.
  -->
  <xsl:template match="/program/speco/obj[1]">
    <xsl:variable name="fqn" select="/program/speco/obj[1]/@fqn"/>
    <xsl:variable name="curname" select="substring-before($fqn, '.')"/>
    <xsl:variable name="curvar" select="substring-after($fqn, '.')"/>
    <xsl:choose>
      <xsl:when test="count(/program/speco/version[@name = $curname]) = 0">
        <xsl:for-each select="inferred/obj">
          <xsl:call-template name="specialize">
            <xsl:with-param name="name" select="$curname"/>
            <xsl:with-param name="var" select="$curvar"/>
            <xsl:with-param name="spec" select="@fqn"/>
            <xsl:with-param name="objname" select="concat($curname, '_spec_', $curvar, '_', translate(@fqn, '.', '_'))"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each select="/program/speco/version[@name = $curname]">
          <xsl:variable name="var" select="@var"/>
          <xsl:variable name="spec" select="@spec"/>
          <xsl:variable name="objname" select="o[1]/@name"/>
          <xsl:for-each select="/program/speco/obj[1]/inferred/obj">
            <xsl:call-template name="specialize">
              <xsl:with-param name="name" select="$curname"/>
              <xsl:with-param name="var" select="concat($var, '_', $curvar)"/>
              <xsl:with-param name="spec" select="concat($spec, '_', @fqn)"/>
              <xsl:with-param name="objname" select="concat($objname, '_spec_', $curvar, '_', @fqn)"/>
            </xsl:call-template>
          </xsl:for-each>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <!--
    Copies the contents of the corresponding objects from the <objects/> node.
  -->
  <xsl:template match="@*|node()" name="specialize" mode="call-template">
    <xsl:param name="name"/>
    <xsl:param name="var"/>
    <xsl:param name="spec"/>
    <xsl:param name="objname"/>
    <xsl:element name="version">
      <xsl:attribute name="name">
        <xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:attribute name="var">
        <xsl:value-of select="$var"/>
      </xsl:attribute>
      <xsl:attribute name="spec">
        <xsl:value-of select="$spec"/>
      </xsl:attribute>
      <xsl:for-each select="/program/objects/o[@name=$name]">
        <xsl:call-template name="format">
          <xsl:with-param name="name" select="$objname"/>
          <xsl:with-param name="spec" select="$name"/>
        </xsl:call-template>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>
  <!--
    Modifies the name of the specialized version.
  -->
  <xsl:template match="@*|node()" name="format" mode="call-template">
    <xsl:param name="name"/>
    <xsl:param name="spec"/>
    <xsl:copy>
      <xsl:attribute name="name">
        <xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:attribute name="spec">
        <xsl:value-of select="$spec"/>
      </xsl:attribute>
      <xsl:apply-templates select="@* except @name |node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node() except version[@name=substring-before(/program/speco/obj[1]/@fqn, '.')]"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

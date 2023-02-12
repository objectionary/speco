<?xml version="1.0" encoding="UTF-8"?>
<!--
The MIT License (MIT)

Copyright (c) 2022 Objectionary

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="SG" version="2.0">
  <!--
    Simple specialization.
    -->
  <xsl:output indent="yes" method="xml"/>
  <xsl:strip-space elements="*"/>
  <xsl:template match="@*|node()" name="specialize">
    <xsl:param name="name"/>
    <xsl:param name="var"/>
    <xsl:param name="spec"/>
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
            <xsl:with-param name="name" select="concat($name, '_spec_', $var, '_', $spec)"/>
          </xsl:call-template>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>
  <xsl:template match="aoi/obj/inferred">
    <xsl:for-each select="obj">
      <xsl:call-template name="specialize">
        <xsl:with-param name="name" select="substring-before(../../@fqn, '.')"/>
        <xsl:with-param name="var" select="substring-after(../../@fqn, '.')"/>
        <xsl:with-param name="spec" select="@fqn"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>
  <!--

  -->
  <xsl:template match="@*|node()" name="format">
    <xsl:param name="name"/>
    <xsl:copy>
      <xsl:attribute name="name">
        <xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:apply-templates select="@* except @name |node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="version/o/o">
    <xsl:copy>
      <xsl:attribute name="spec">
        <xsl:value-of select="../../@spec"/>
      </xsl:attribute>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <!--

  -->
  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* |node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

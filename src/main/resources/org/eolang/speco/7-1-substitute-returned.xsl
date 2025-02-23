<?xml version="1.0"?>
<!--
 * Copyright (c) 2022 Objectionary
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

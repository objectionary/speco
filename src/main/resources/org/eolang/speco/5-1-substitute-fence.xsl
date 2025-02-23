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
    Rule #5: substitute each reference to a fence attribute with a reference to its duplicate.
  -->
  <xsl:output indent="yes" method="xml"/>
  <xsl:strip-space elements="*"/>
  <!--
    Replaces fence attribute calls with duplicate calls via a first tuple element.
  -->
  <xsl:template match="/program/objects//o">
    <xsl:variable name="fence" select="substring-after(@base, '.')"/>
    <xsl:variable name="name" select="@name"/>
    <xsl:copy>
      <xsl:choose>
        <xsl:when test="/program/objects//o[not(../@spec) and @fence=$fence]">
          <xsl:for-each select="/program/objects//o[not(../@spec) and @fence=$fence]">
            <xsl:attribute name="base">
              <xsl:value-of select="concat('.', @name)"/>
            </xsl:attribute>
            <xsl:attribute name="name">
              <xsl:value-of select="concat($name, '_tuple')"/>
            </xsl:attribute>
            <xsl:attribute name="fence_tuple"/>
          </xsl:for-each>
          <xsl:apply-templates select="@* except @base except @name|node()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="@*|node()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
    <xsl:for-each select="/program/objects//o[not(../@spec) and @fence=$fence]">
      <xsl:element name="o">
        <xsl:attribute name="base">
          <xsl:value-of select="'.at'"/>
        </xsl:attribute>
        <xsl:attribute name="method"/>
        <xsl:attribute name="name">
          <xsl:value-of select="$name"/>
        </xsl:attribute>
        <xsl:element name="o">
          <xsl:attribute name="base">
            <xsl:value-of select="concat($name, '_tuple')"/>
          </xsl:attribute>
        </xsl:element>
        <o base="int" data="bytes">00 00 00 00 00 00 00 00</o>
      </xsl:element>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

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
    Rule #6: Substitute each reference to an object with a reference
    to the parent object returned by the fluent attribute,
    when such a reference to a fluent attribute of the object is deemed dominant.
    Due to the absence of EOG, the dominance relation is determined by the order of applications.
  -->
  <xsl:output indent="yes" method="xml"/>
  <xsl:strip-space elements="*"/>
  <!--
    Iterated over objects with the fence_tuple attribute (marker from the previous transformation)
    and replaces the parent object with the second element of the tuple of the previous fence attribute.
    The first fence object is not replaced.
  -->
  <xsl:template match="/program/objects//o[@fence_tuple]">
    <xsl:choose>
      <xsl:when test="preceding::o[@fence_tuple]">
        <xsl:element name="o">
          <xsl:attribute name="base">
            <xsl:value-of select="'.at'"/>
          </xsl:attribute>
          <xsl:attribute name="method"/>
          <xsl:element name="o">
            <xsl:attribute name="base">
              <xsl:value-of select="(preceding::o[@fence_tuple])[last()]/@name"/>
            </xsl:attribute>
          </xsl:element>
          <o base="int" data="bytes">00 00 00 00 00 00 00 01</o>
        </xsl:element>
        <xsl:copy>
          <xsl:apply-templates select="@*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

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
                        <xsl:value-of select="'array'"/>
                      </xsl:attribute>
                      <xsl:attribute name="data">
                        <xsl:value-of select="'array'"/>
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

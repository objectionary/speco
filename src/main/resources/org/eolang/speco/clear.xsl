<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2022 Objectionary
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" id="SG" version="2.0">
  <!--
     Removes <speco/> and <aoi/> nodes.
   -->
  <xsl:output indent="yes" method="xml"/>
  <xsl:strip-space elements="*"/>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()[generate-id() != generate-id(/program/speco) and generate-id() != generate-id(/program/aoi)]"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

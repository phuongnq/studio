<?xml version="1.0" encoding="UTF-8"?>
        <!--
  ~ Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License version 3 as published by
  ~ the Free Software Foundation.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

<!-- to keep the right formatting -->
<xsl:output method="xml" indent="yes" />
<xsl:strip-space elements="*"/>

<!-- copy all elements -->
<xsl:template match="node() | @*">
    <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates select="node() | @*"/>
    </xsl:copy>
</xsl:template>

<!-- insert line breaks before and after top level comments -->
<xsl:template match="/comment()">
    <xsl:text>&#10;</xsl:text><xsl:copy-of select="."/><xsl:text>&#10;</xsl:text>
</xsl:template>


<xsl:template match="permissions/role[@name='admin']/rule[@regex='/.*' or @regex='~DASHBOARD~']/allowed-permissions">
    <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates/>
        <xsl:if test="not(permission = 'site_status')">
            <xsl:element name="permission"><xsl:text>site_status</xsl:text></xsl:element><xsl:text>&#10;</xsl:text>
        </xsl:if>
        <xsl:if test="not(permission = 'resolve_conflict')">
            <xsl:element name="permission"><xsl:text>resolve_conflict</xsl:text></xsl:element><xsl:text>&#10;</xsl:text>
        </xsl:if>
        <xsl:if test="not(permission = 'site_diff_conflicted_file')">
            <xsl:element name="permission"><xsl:text>site_diff_conflicted_file</xsl:text></xsl:element><xsl:text>&#10;</xsl:text>
        </xsl:if>
        <xsl:if test="not(permission = 'commit_resolution')">
            <xsl:element name="permission"><xsl:text>commit_resolution</xsl:text></xsl:element><xsl:text>&#10;</xsl:text>
        </xsl:if>
        <xsl:if test="not(permission = 'cancel_failed_pull')">
            <xsl:element name="permission"><xsl:text>cancel_failed_pull</xsl:text></xsl:element><xsl:text>&#10;</xsl:text>
        </xsl:if>
    </xsl:copy>
</xsl:template>

</xsl:stylesheet>
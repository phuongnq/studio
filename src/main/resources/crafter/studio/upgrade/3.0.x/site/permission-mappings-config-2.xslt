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
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <!-- insert line breaks before and after top level comments -->
    <xsl:template match="/comment()">
        <xsl:text>&#10;</xsl:text><xsl:copy-of select="."/><xsl:text>&#10;</xsl:text>
    </xsl:template>

    <!--  Fix format used in older versions, remove the site element  -->
    <xsl:template match="permissions/site">
        <xsl:apply-templates select="node()"/>
    </xsl:template>

    <xsl:template match="*/role[@name='author']|*/role[@name='publisher']|*/role[@name='developer']|*/role[@name='admin']">
        <role>
            <!-- copy all existing rules -->
            <xsl:apply-templates select="node() | @*"/>

            <!-- add new ones if missing -->
            <xsl:if test="not(rule/allowed-permissions/permission/text() = 'S3 Read')">
                <rule regex=".*">
                    <allowed-permissions>
                        <permission>S3 Read</permission>
                        <permission>S3 Write</permission>
                    </allowed-permissions>
                </rule>
            </xsl:if>
        </role>
    </xsl:template>

    <xsl:template match="*/role[@name='reviewer']|*/role[@name='*']">
        <role>
            <!-- copy all existing rules -->
            <xsl:apply-templates select="node() | @*"/>

            <!-- add new ones if missing -->
            <xsl:if test="not(rule/allowed-permissions/permission/text() = 'S3 Read')">
                <rule regex=".*">
                    <allowed-permissions>
                        <permission>S3 Read</permission>
                    </allowed-permissions>
                </rule>
            </xsl:if>
        </role>
    </xsl:template>

</xsl:stylesheet>
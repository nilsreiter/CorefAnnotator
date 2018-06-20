<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:rs="http://uima.apache.org/resourceSpecifier" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" indent="no"/>
	<xsl:template match="/rs:typeSystemDescription">
		<xsl:for-each select="rs:types/rs:typeDescription">
#### <xsl:value-of select="rs:name" /> 
Inherits from: `<xsl:value-of select="rs:supertypeName" />`

<xsl:if test="rs:description">
Description: <xsl:value-of select="rs:description" />
</xsl:if>
<xsl:if test="rs:features/rs:featureDescription">

##### Features
| Name | Description | Range |
| ---- | ----------- | ----- |
<xsl:for-each select="rs:features/rs:featureDescription">
| <xsl:value-of select="rs:name" /> | <xsl:value-of select="rs:description" /> | <xsl:value-of select="rs:rangeTypeName" /> |
</xsl:for-each>
</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml">
    
    <xsl:output method="xml"
                doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
                encoding="UTF-8"
                indent="yes"
    />
	
    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <meta charset="UTF-8"/>
                <link rel="stylesheet" type="text/css" href="payment.css" />
                <title>Account statement</title>
            </head>
            <body>
                <xsl:apply-templates select="payments"/>
            </body>
        </html>
    </xsl:template>	
	
	<xsl:template match="payments">
		<h1>Výpis z účtu<br/>
                Account statements</h1>
		<table>
                    <div class="header">
			<tr>
                            <th><b>Datum</b></th>
			    <th><b>Popis</b></th>
                            <th><b>Nazev účtu</b></th>
                            <th><b>Nazev protiúčtu</b></th>
                            <th><b>Kategorie</b></th>
                            <th><b>Částka</b></th>
			</tr>
                    </div>    
			<xsl:for-each select="payment">
				<tr>
					<td><xsl:value-of select="date"/></td>
					<td><xsl:value-of select="description"/></td>
					<td><xsl:value-of select="account-name"/></td>
					<td><xsl:value-of select="subject-name"/></td>
					<td><xsl:value-of select="category-name"/></td>
					<td><xsl:value-of select="amount"/></td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	 

</xsl:stylesheet>
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
                <link rel="stylesheet" type="text/css" href="style.css" />
                <title>Account statement</title>
            </head>
            <body>
                <xsl:apply-templates select="payments"/>
            </body>
        </html>
    </xsl:template>	
	
	<xsl:template match="payments">
		<h1>Výpis z účtu</h1>
                <h1>Account statements</h1>
                <h4>Automaticky vygenerováno aplikací Expense-Manager</h4>
                    <div class="pageColumnLeft">
                        <h3>Bilance</h3>
                            <h4>Počet plateb:<xsl:text> </xsl:text><xsl:value-of select="count"/></h4>                                
                            <h4>Objem plateb:<xsl:text> </xsl:text><xsl:value-of select="sum"/></h4>                                   
                            <h4>Počet příchozích plateb:<xsl:text> </xsl:text><xsl:value-of select="incomingCount"/></h4>                                
                            <h4>Počet odchozích plateb:<xsl:text> </xsl:text><xsl:value-of select="outgoingCount"/></h4>                              
                            <h4>Objem příchozích plateb:<xsl:text> </xsl:text><xsl:value-of select="incomingSum"/></h4>                                
                            <h4>Objem odchozích plateb:<xsl:text> </xsl:text><xsl:value-of select="outgoingSum"/></h4>
                            <h4>Nejvyšší příchozí platba:<xsl:text> </xsl:text><xsl:value-of select="highestIncoming"/></h4>
                            <h4>Nejvyšší odchozí platba:<xsl:text> </xsl:text><xsl:value-of select="highestOutgoing"/></h4>
                    </div>
                    <table>                    
			<tr>
                            <th>
                                <b>Datum</b>
                            </th>
			    <th><b>Popis</b></th>
                            <th><b>Název účtu</b></th>
                            <th><b>Název protiúčtu</b></th>
                            <th><b>Kategorie</b></th>
                            <th><b>Částka</b></th>
			</tr>
                        
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
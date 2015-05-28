/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.io.File;
import java.io.StringReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author charlliz
 */
public class AccountStatementsTransformation {
    public static void makeAccountStatement(String document) 
            throws TransformerConfigurationException, TransformerException {
        
        TransformerFactory tf = TransformerFactory.newInstance();
        
        Transformer xsltProc = tf.newTransformer(
                new StreamSource(new File("xmlschema/payments.xsl")));
        
        xsltProc.transform(
                new StreamSource(new StringReader(document)), 
                new StreamResult(new File("xmlschema/payments.html")));
    }
}

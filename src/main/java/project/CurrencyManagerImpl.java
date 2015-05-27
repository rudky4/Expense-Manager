/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.CompiledExpression;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.modules.XQueryService;

/**
 *
 * @author marek
 */
public class CurrencyManagerImpl implements CurrencyManager {

    private static final Logger logger = Logger.getLogger(CurrencyManagerImpl.class.getName());

    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/";
    private XPathQueryService xpqs;
    private Collection col = null;
    private XMLResource res = null;

    public CurrencyManagerImpl(String URI) {
        this.URI = URI;
        createDatabase();
    }

    public CurrencyManagerImpl() {
        createDatabase();
    }

    private void createDatabase() {
        try {
            Class c = Class.forName(DRIVER);
            Database database = (Database) c.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
            Collection parent = DatabaseManager.getCollection(URI, "admin", "admin");
            CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");
            mgt.createCollection("expenseManager");
            parent.close();
            col = DatabaseManager.getCollection(URI + "expenseManager", "admin", "admin");
            Resource x = col.getResource("currencies.xml");
            if (x == null) {
                res = (XMLResource) col.createResource("currencies.xml", "XMLResource");
                res.setContent("<currencies></currencies>");
                col.storeResource(res);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating databse", ex);
        }
    }

    @Override
    public void createCurrency(Currency currency) {
        if(currency==null){
            throw new IllegalArgumentException("currency cannot be null");
        }
        
        if(currency.getCcy()==null){
            throw new IllegalArgumentException("ccy cannot be null");
        }
        
        if(currency.getCcyName()==null){
            throw new IllegalArgumentException("currency name cannot be null");
        }
        
        try {
            if(!(isOk(currency.getCcy()))){
                throw new IllegalArgumentException("ccy is now exist");
            }
            String node = getNode(currency);
            String query = "update insert " + node + " into doc(\"currencies.xml\")//currencies";
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/currencies.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
        } catch (NumberFormatException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating payment", ex);
        }
    }

    private String getNode(Currency currency) {

        String node = "<currency cyy=\"" + currency.getCcy() + "\">"
                + "<ccyName>" + currency.getCcyName() + "</ccyName>"
                + "</currency>";
        return node;
    }

    private boolean isOk(String ccy) {
        try {
            String query = "for $currency in doc(\"currencies.xml\")//currency where @ccy=\"" + ccy + "\"" + " return $currency";
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/currencies.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            ResourceSet result = service.execute(compiled);
            ResourceIterator it = result.getIterator();
            while (it.hasMoreResources()) {
                return false;
            }
        } catch (XMLDBException ex) {
            Logger.getLogger(CurrencyManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    @Override
    public void updateCurrency(Currency currency) {
        if(currency==null){
            throw new IllegalArgumentException("currency cannot be null");
        }
        
        if(currency.getCcy()==null){
            throw new IllegalArgumentException("ccy cannot be null");
        }
        
        if(currency.getCcyName()==null){
            throw new IllegalArgumentException("currency name cannot be null");
        }
        try {
            String node = getNode(currency);
            String query = "update replace doc(\"currencies.xml\")//currency[@ccy=" + currency.getCcy() + "] with " + node;
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/currencies.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
        } catch (XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when updating currency", ex);
        }
    }
    
    @Override
    public List<Currency> findAllCurrency() {
        String where = "";
        return findCurrencyBy(where);
    }

    private List<Currency> findCurrencyBy(String where) {
        List<Currency> resultList = new ArrayList<>();
        try {
            String queryStart = "for $currency in doc(\"currencies.xml\")//currency ";
            String queryEnd = " return $currency";
            String query = queryStart + where + queryEnd;
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/currencies.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);

            ResourceSet result = service.execute(compiled);
            ResourceIterator it = result.getIterator();
            while (it.hasMoreResources()) {
                Resource resource = it.nextResource();
                resultList.add(parseCurrencyFromXML(resource.getContent().toString()));
            }
        } catch (XMLDBException ex) {

        }
        return resultList;
    }

    private Currency parseCurrencyFromXML(String xml) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Currency currency = new Currency();
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            try {

                Document doc = db.parse(is);
                NodeList a = doc.getElementsByTagName("currency");
                Element parent = (Element) a.item(0);
                currency.setCcy(parent.getAttribute("ccy"));

                a = parent.getElementsByTagName("ccyName");
                if (a.getLength() != 1) {
                    // throw new Exception
                }
                Element el = (Element) a.item(0);
                currency.setCcyName(el.getTextContent());
            } catch (SAXException | IOException ex) {
                Logger.getLogger(SubjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SubjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return currency;
    }

    @Override
    public void deleteCurrency(String ccy) {
        try {
            String query = "for $currency in doc(\"currencies.xml\")//currency[@ccy=\"" + ccy + "\"] return update delete currency";
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/currencies.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
        } catch (XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when delete currency", ex);
        }
    }

}

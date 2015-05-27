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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marek
 */
public class PaymentManagerImpl implements PaymentManager {

    private static final Logger logger = Logger.getLogger(SubjectManagerImpl.class.getName());

    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/";
    private XPathQueryService xpqs;
    private Collection col = null;
    private XMLResource res = null;

    public PaymentManagerImpl(String URI) {
        this.URI = URI;
        createDatabase();
    }

    public PaymentManagerImpl() {
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
            Resource x = col.getResource("payments.xml");
            if (x == null) {
                res = (XMLResource) col.createResource("payments.xml", "XMLResource");
                res.setContent("<payments></payments>");
                col.storeResource(res);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating databse", ex);
        }
    }

    @Override
    public void createPayment(Payment payment) {
        
        if(payment == null){
            throw new IllegalArgumentException("payment cannot be null");
        }
        
        if (payment.getId() != null) {
            throw new IllegalArgumentException("id must be null");
        }
        if (payment.getDescription() == null) {
            throw new IllegalArgumentException("description cannot be null");
        }
        if (payment.getDate() == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        if (payment.getAmount() == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }
        try {
            Long id = null;
            id = getId();
            payment.setId(id);
            String node = payment.toXML();
            String query = "update insert " + node + " into doc(\"payments.xml\")//payments";
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/payments.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
           // XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            // service.setProperty("indent", "yes");
            // service.query(query);
        } catch (NumberFormatException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating payment", ex);
        }

    }

    private Long getId() {
        Long id = (long) 1;
        String idQuery = "max(for $payment in doc(\"payments.xml\")//payment return $payment/@id)";
        try {
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/payments.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(idQuery);
            ResourceSet rS = service.execute(compiled);
            ResourceIterator it = rS.getIterator();
            if (it.hasMoreResources()) {
                Resource resource = it.nextResource();
                id = Long.parseLong(resource.getContent().toString());
                id++;
                if (it.hasMoreResources()) {
                    throw new IllegalArgumentException("");
                }
            }
        } catch (XMLDBException | IllegalArgumentException ex) {
            logger.log(Level.SEVERE, "Error when geting id", ex);
        }
        return id;
    }

    @Override
    public void updatePayment(Payment payment) {
        
        if(payment == null){
            throw new IllegalArgumentException("payment cannot be null");
        }
        
        if (payment.getId() == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (payment.getDescription() == null) {
            throw new IllegalArgumentException("description cannot be null");
        }
        if (payment.getDate() == null) {
            throw new IllegalArgumentException("date cannot be null");
        }
        if (payment.getAmount() == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }
        try {
            String node = payment.toXML();
            String query = "update replace doc(\"payments.xml\")//payment[@id=" + payment.getId() + "] with " + node;
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/payments.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
        } catch (XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when updating payment", ex);
        }
    }

    @Override
    public void deletePayment(Long id) {
        if(id == null){
            throw new IllegalArgumentException("ID cannot be null!");
        }
        try {
            String query = "for $payment in doc(\"payments.xml\")//payment[@id=\"" + id + "\"] return update delete $payment";
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/payments.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
        } catch (XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when delete payment", ex);
        }
    }

    @Override
    public Payment getPaymentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("");
        }
        String where = "where @id=" + Long.toString(id);
        List<Payment> result = findPaymentsBy(where);
        if (result.size() != 1) {
            return null;
        }
        return result.get(0);
    }

    @Override
    public List<Payment> findAllPayments() {
        String where = "";
        return findPaymentsBy(where);
    }

    private List<Payment> findPaymentsBy(String where) {
        List<Payment> resultList = new ArrayList<>();
        try {
            String queryStart = "for $payment in doc(\"payments.xml\")//payment ";
            String queryEnd = " return $payment";
            String query = queryStart + where + queryEnd;
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/payments.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);

            ResourceSet result = service.execute(compiled);
            ResourceIterator it = result.getIterator();
            while (it.hasMoreResources()) {
                Resource resource = it.nextResource();
                resultList.add(parsePaymentFromXML(resource.getContent().toString()));
            }
        } catch (XMLDBException ex) {

        }
        return resultList;
    }

    private Payment parsePaymentFromXML(String xml) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Payment payment = new Payment();
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            try {

                Document doc = db.parse(is);
                NodeList a = doc.getElementsByTagName("payment");
                Element parent = (Element) a.item(0);
                payment.setId(Long.parseLong(parent.getAttribute("id")));

                a = parent.getElementsByTagName("description");
                if (a.getLength() != 1) {
                    // throw new Exception
                }
                Element el = (Element) a.item(0);
                payment.setDescription(el.getTextContent());

                a = parent.getElementsByTagName("date");
                if (a.getLength() != 1) {
                    // throw new Exception
                }
                el = (Element) a.item(0);
                DateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
                payment.setDate(dateF.parse(el.getTextContent()));

                a = parent.getElementsByTagName("amount");
                if (a.getLength() != 1) {
                    // throw new Exception
                }
                el = (Element) a.item(0);
                payment.setAmount(new BigDecimal(el.getTextContent()));

                a = parent.getElementsByTagName("account-id");
                if (a.getLength() != 1) {
                    // throw new Exception
                }
                el = (Element) a.item(0);
                payment.setAcountId(Long.parseLong(el.getTextContent()));

                a = parent.getElementsByTagName("subject-id");
                if (a.getLength() != 1) {
                    // throw new Exception
                }
                el = (Element) a.item(0);
                payment.setSubjectId(Long.parseLong(el.getTextContent()));

                a = parent.getElementsByTagName("category-id");
                if (a.getLength() != 1) {
                    // throw new Exception
                }
                el = (Element) a.item(0);
                payment.setCategoryId(Long.parseLong(el.getTextContent()));

            } catch (SAXException | IOException | ParseException ex) {
                Logger.getLogger(SubjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SubjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return payment;
    }

}

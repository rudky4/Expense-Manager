/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
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
public class ExpenseManagerImpl implements ExpenseManager {

    private static final Logger logger = Logger.getLogger(SubjectManagerImpl.class.getName());

    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/";
    private XPathQueryService xpqs;
    private Collection col = null;
    private XMLResource res = null;
    private Document doc = null;

    public ExpenseManagerImpl(String URI) {
        this.URI = URI;
        createDatabase();
    }

    public ExpenseManagerImpl() {
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
            Resource x = col.getResource("results.xml");
            if (x == null) {
                res = (XMLResource) col.createResource("results.xml", "XMLResource");
                res.setContent("<results></results>");
                col.storeResource(res);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating databse", ex);
        }
    }

//    @Override
//    public void addPayment(Account account, Payment payment, Subject subject) {
//        try {
//            Long id = null;
//            id = getId();
//            String node = getNode(id,account.getId(),payment.getId(),subject.getId());
//            String query = "update insert " + node + " into doc(\"results.xml\")//results";
//            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
//            service.declareVariable("document", "/db/results.xml");
//            service.setProperty("indent", "yes");
//            CompiledExpression compiled = service.compile(query);
//            service.execute(compiled);
//
//        } catch (NumberFormatException | XMLDBException ex) {
//            logger.log(Level.SEVERE, "Error when creating result", ex);
//        }
//
//    }
    @Override
    public void addPayment(Account account, Payment payment, Subject subject) {
        String node = null;
        try {
            String query = "for $payment in doc(\"payments.xml\")//payment where @id=" + payment.getId() + " return $payment";
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/payments.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);

            ResourceSet result = service.execute(compiled);
            ResourceIterator it = result.getIterator();
            while (it.hasMoreResources()) {
                Resource resource = it.nextResource();
                node = resource.getContent().toString();
                updateNode(node, account.getId(), subject.getId());
                
            }

        } catch (XMLDBException ex) {

        }
    }

    private void updateNode(String node, Long aid, Long sid) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(node));
            Document doc = db.parse(is);
            NodeList a = doc.getElementsByTagName("payment");
            Element parent = (Element) a.item(0);
            Element newNote = doc.createElement("account-id");
            Text createTextNode = doc.createTextNode(aid.toString());
            newNote.appendChild(createTextNode);
            parent.appendChild(newNote);
            //String idaccount = aid.toString();
           // newNote.setTextContent(idaccount);
           // parent.appendChild(newNote);
           /* a = parent.getElementsByTagName("account-id");
            if (a.getLength() != 1) {
                // throw new Exception
            }
            Element el = (Element) a.item(0);   

          
            el.setTextContent(aid.toString());
            parent.appendChild(el);*/
      /*      a = parent.getElementsByTagName("subject-id");
            if (a.getLength() != 1) {
                // throw new Exception
            }
            Element el = (Element) a.item(0);
            el.setTextContent(sid.toString());
            parent.appendChild(el);*/
        } catch (Exception ex) {

        }
    }

    private Long getId() {
        Long id = (long) 1;
        String idQuery = "max(for $result in doc(\"results.xml\")//result return $result/@id)";
        try {
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/results.xml");
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

    private String getNode(Long id, Long aid, Long pid, Long sid) {
        String node = "<result id=\"" + id + "\">"
                + "<account-id>" + aid + "</account-id>"
                + "<payment-id>" + pid + "</payment-id>"
                + "<subject-id>" + sid + "</subject-id>"
                + "</result>";
        return node;
    }

    @Override
    public void removePayment(Long accountId, Long paymentId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal getAccountBalance(Long accountId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal getAccountBalance(Long accountId, Date startDate, Date endDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Account> getAllPayments(Long accountId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Account> getAllPayments(Long accountId, Date startDate, Date endDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Account> getAllPaymentsBySubject(Long accountId, Long subjectId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Account> getAllPaymentsBySubject(Long accountId, Long subjectId, Date startDate, Date endDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

package project;

import java.io.IOException;
import java.io.StringReader;
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
public class SubjectManagerImpl implements SubjectManager {

    private static final Logger logger = Logger.getLogger(SubjectManagerImpl.class.getName());

    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/";
    private XPathQueryService xpqs;
    private Collection col = null;
    private XMLResource res = null;

    public SubjectManagerImpl(String URI){
        this.URI = URI;
        createDatabase();
    }
    
    public SubjectManagerImpl() {
        createDatabase();
    }
    private void createDatabase(){
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
            Resource x = col.getResource("subjects.xml");
            if (x == null) {
                res = (XMLResource) col.createResource("subjects.xml", "XMLResource");
                res.setContent("<subjects></subjects>");
                col.storeResource(res);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating databse", ex);
        }
    }

    @Override
    public void createSubject(Subject subject) {
        if (subject.getId() != null) {
            throw new IllegalArgumentException("id must be null");
        }
        if (subject.getName() == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        try {
            Long id = null;
            id = getId();
            subject.setId(id);
            String node = getNode(subject);
            String query = "update insert " + node + " into doc(\"subjects.xml\")//subjects";
//            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
//            service.declareVariable("document", "/db/subjects.xml");
//            service.setProperty("indent", "yes");
//            CompiledExpression compiled = service.compile(query);
//            service.execute(compiled);
            XPathQueryService service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            service.setProperty("indent", "yes");
            service.query(query);
        } catch (NumberFormatException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating subject", ex);
        }

    }

    private Long getId() {
        Long id = (long) 1;
        String idQuery = "max(for $x in doc(\"subjects.xml\")//subject return $x/@id)";
        try {
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/subjects.xml");
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

    private String getNode(Subject subject) {
        String node = "<subject id=\"" + subject.getId() + "\">"
                + "<name>" + subject.getName() + "</name>"
                + "</subject>";
        return node;
    }

    @Override
    public void updateSubject(Subject subject) {
        if (subject.getId() == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (subject.getName() == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        try {
            String node = getNode(subject);
            String query = "update replace doc(\"subjects.xml\")//subject[@id=" + subject.getId() + "] with " + node;
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/subjects.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
        } catch (XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when updating subject", ex);
        }
    }

    @Override
    public void deleteSubject(Long id) {
        try {
            String query = "for $subject in doc(\"subjects.xml\")//subject[@id=\"" + id + "\"] return update delete $subject";
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/subjects.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
        } catch (XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when delete subject", ex);
        }
    }

    @Override
    public Subject getSubjectById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("");
        }
        String where = "where @id=" + Long.toString(id);
        List<Subject> result = findSubjectsBy(where);
        if (result.size() != 1) {
            throw new IllegalArgumentException();
        }
        return result.get(0);
    }

    @Override
    public Subject getSubjectByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("");
        }
        String where = "where name=" + "\"" + name + "\"";
        List<Subject> result = findSubjectsBy(where);
        if (result.size() != 1) {
            throw new IllegalArgumentException();
        }
        return result.get(0);
    }

    @Override
    public List<Subject> findAllSubjects() {
        String where = "";
        return findSubjectsBy(where);
    }

    private List<Subject> findSubjectsBy(String where) {
        List<Subject> resultList = new ArrayList<>();
        try {
            String queryStart = "for $subject in doc(\"subjects.xml\")//subject ";
            String queryEnd = " return $subject";
            String query = queryStart + where + queryEnd;
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/subjects.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);

            ResourceSet result = service.execute(compiled);
            ResourceIterator it = result.getIterator();
            while (it.hasMoreResources()) {
                Resource resource = it.nextResource();
                resultList.add(parseSubjectFromXML(resource.getContent().toString()));
            }
        } catch (XMLDBException ex) {

        }
        return resultList;
    }

    private Subject parseSubjectFromXML(String xml) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Subject subject = new Subject();
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            try {

                Document doc = db.parse(is);
                NodeList a = doc.getElementsByTagName("subject");
                Element parent = (Element) a.item(0);
                subject.setId(Long.parseLong(parent.getAttribute("id")));

                a = parent.getElementsByTagName("name");
                if (a.getLength() != 1) {

                }
                Element el = (Element) a.item(0);
                subject.setName(el.getTextContent());
            } catch (SAXException | IOException ex) {
                Logger.getLogger(SubjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SubjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return subject;
    }
}

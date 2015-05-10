package project;



import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/";
    private XPathQueryService xpqs;
    private Collection col = null;
    private XMLResource res = null;

    public SubjectManagerImpl() {
        try {
            Class c = Class.forName(DRIVER);
            Database database = (Database) c.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
            Collection parent = DatabaseManager.getCollection(URI, "admin", "admin");
            CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");
            mgt.createCollection("subjects");
            parent.close();
            col = DatabaseManager.getCollection(URI + "subjects", "admin", "admin");
            res = (XMLResource) col.createResource("subjects.xml", "XMLResource");
            res.setContent("<subjects></subjects>");
            col.storeResource(res);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating databse",ex);
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
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/subjects.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
        } catch (NumberFormatException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating subject",ex);
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
            logger.log(Level.SEVERE, "Error when geting id",ex);
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
            logger.log(Level.SEVERE,"Error when updating subject",ex);
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
            logger.log(Level.SEVERE,"Error when delete subject",ex);
        }
    }

    @Override
    public Subject getSubjectById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("");
        }
        String where = "@id=" + Long.toString(id);
        return getSubject(where);
    }

    @Override
    public Subject getSubjectByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("");
        }
        String where = "name=" + "\"" + name + "\"";
        return getSubject(where);
    }

    private Subject getSubject(String where) {
        Subject subject = new Subject();
        try {
            String queryStart = "for $subject in doc(\"subjects.xml\")//subject where ";
            String queryEnd = " return data($subject/";

            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/subjects.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(queryStart + where + queryEnd + "@id)");
            ResourceSet resultId = service.execute(compiled);
            ResourceIterator riId = resultId.getIterator();
            if (riId.hasMoreResources()) {
                Resource resource = riId.nextResource();
                Long rId = Long.parseLong(resource.getContent().toString());
                subject.setId(rId);
                if (riId.hasMoreResources()) {
                    throw new IllegalArgumentException("");
                }
            }

            compiled = service.compile(queryStart + where + queryEnd + "name)");
            ResourceSet resultName = service.execute(compiled);
            ResourceIterator riName = resultName.getIterator();
            if (riName.hasMoreResources()) {
                Resource resource = riName.nextResource();
                String rName = resource.getContent().toString();
                subject.setName(rName);
                if (riName.hasMoreResources()) {
                    throw new IllegalArgumentException("");
                }
            }
        } catch (XMLDBException ex) {
            logger.log(Level.SEVERE,"Error when geting subject",ex);
        }
        return subject;
    }
    
    @Override
    public List<Subject> findAllSubjects() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

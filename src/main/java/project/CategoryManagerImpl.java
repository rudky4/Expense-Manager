    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author marek
 */
public class CategoryManagerImpl implements CategoryManager {

    private static final Logger logger = Logger.getLogger(SubjectManagerImpl.class.getName());

    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/";
    private XPathQueryService xpqs;
    private Collection col = null;
    private XMLResource res = null;

    public CategoryManagerImpl(String URI) {
        this.URI = URI;
        createDatabase();
    }

    public CategoryManagerImpl() {
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
            Resource x = col.getResource("categories.xml");
            if (x == null) {
                res = (XMLResource) col.createResource("categories.xml", "XMLResource");
                res.setContent("<categories></categories>");
                col.storeResource(res);
                logger.log(Level.INFO, "category xml was created");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating databse", ex);
        }
    }

    @Override
    public void createCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }

        if (category.getId() != null) {
            throw new IllegalArgumentException("id must be null");
        }

        if (category.getName() == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        try {
            Long id = null;
            id = getId();
            category.setId(id);
            String node = getNode(category);
            String query = "update insert " + node + " into doc(\"categories.xml\")//categories";
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/categories.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
            logger.log(Level.INFO, "create category");
        } catch (NumberFormatException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating category", ex);
        }
    }

    private Long getId() {
        Long id = (long) 1;
        String idQuery = "max(for $category in doc(\"categories.xml\")//category return $category/@id)";
        try {
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/categories.xml");
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

    private String getNode(Category category) {

        String node = "<category id=\"" + category.getId() + "\">"
                + "<name>" + category.getName() + "</name>"
                + "</category>";
        return node;
    }

    @Override
    public void deleteCategory(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        try {
            String query = "for $category in doc(\"categories.xml\")//category[@id=\"" + id + "\"] return update delete $category";
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/categories.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
            logger.log(Level.INFO, "delete category");
        } catch (XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when delete category", ex);
        }
    }

    @Override
    public void updateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }

        if (category.getId() == null) {
            throw new IllegalArgumentException("id must be null");
        }

        if (category.getName() == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        try {
            String node = getNode(category);
            String query = "update replace doc(\"categories.xml\")//category[@id=" + category.getId() + "] with " + node;
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/categories.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
            logger.log(Level.INFO, "update category");
        } catch (XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when updating category", ex);
        }
    }

    @Override
    public List<Category> findAllCategory() {
        String where = "";
        return findCategoryBy(where);
    }

    private List<Category> findCategoryBy(String where) {
        List<Category> resultList = new ArrayList<>();
        try {
            String queryStart = "for $category in doc(\"categories.xml\")//category ";
            String queryEnd = " return $category";
            String query = queryStart + where + queryEnd;
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/categories.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);

            ResourceSet result = service.execute(compiled);
            ResourceIterator it = result.getIterator();
            while (it.hasMoreResources()) {
                Resource resource = it.nextResource();
                resultList.add(parseCategoryFromXML(resource.getContent().toString()));
            }
        } catch (XMLDBException ex) {

        }
        return resultList;
    }

    private Category parseCategoryFromXML(String xml) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Category category = new Category();
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            try {

                Document doc = db.parse(is);
                NodeList a = doc.getElementsByTagName("category");
                Element parent = (Element) a.item(0);
                category.setId(Long.parseLong(parent.getAttribute("id")));

                a = parent.getElementsByTagName("name");
                if (a.getLength() != 1) {
                    // throw new Exception
                }
                Element el = (Element) a.item(0);
                category.setName(el.getTextContent());
            } catch (SAXException | IOException ex) {
                Logger.getLogger(SubjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SubjectManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return category;
    }

    @Override
    public Category getCategoryById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("");
        }
        String where = "where @id=" + Long.toString(id);
        List<Category> result = findCategoryBy(where);
        if (result.size() != 1) {
            throw new IllegalArgumentException();
        }
        return result.get(0);
    }

}

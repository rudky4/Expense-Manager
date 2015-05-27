/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

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

/**
 *
 * @author marek
 */
public class CategoryManagerImpl implements CategoryManager{

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
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating databse", ex);
        }
    }

    
    @Override
    public void createCategory(Category category) {
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
        } catch (NumberFormatException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating payment", ex);
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
    
}

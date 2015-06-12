package project;

import java.io.IOException;
import java.io.StringReader;
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
public class AccountManagerImpl implements AccountManager {

    private static final Logger logger = Logger.getLogger(AccountManagerImpl.class.getName());

    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/";
    private XPathQueryService xpqs;
    private Collection col = null;
    private XMLResource res = null;

    /**
     * Constructor
     * @param URI
     */
    public AccountManagerImpl(String URI) {
        this.URI = URI;
        createDatabase();
    }

    /**
     * Constructor without parameter
     */
    public AccountManagerImpl() {   
        createDatabase();
    }
    
    /**
     * function detects whether the xml file already created, if not, it creates
     */
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
            Resource x = col.getResource("accounts.xml");
            if (x == null) {
                res = (XMLResource) col.createResource("accounts.xml", "XMLResource");
                res.setContent("<accounts></accounts>");
                col.storeResource(res);
                logger.log(Level.INFO,"account xml was created");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating databse", ex);
        }
    }

    /**
     * function checks all parameters and create account
     * @param account 
     */
    @Override
    public void createAccount(Account account) {
        if (account.getId() != null) {
            throw new IllegalArgumentException("id must be null");
        }
        if (account.getName() == null) {
            throw new IllegalArgumentException("name cannot be null");
        }

        if (account.getDescription() == null) {
            throw new IllegalArgumentException("description cannot be null");
        }

        if (account.getCreationDate() == null) {
            throw new IllegalArgumentException("Creation date cannot be null");
        }

        try {
            Long id = null;
            id = getId();
            account.setId(id);
            String node = getNode(account);
            String query = "update insert " + node + " into doc(\"accounts.xml\")//accounts";
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/accounts.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
            logger.log(Level.INFO,"create account");
        } catch (NumberFormatException | XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when creating account", ex);
        }
    }
    
    /**
     * function checks last id in xml file and increments it
     * @return id 
     */
    private Long getId() {
        Long id = (long) 1;
        String idQuery = "max(for $account in doc(\"accounts.xml\")//account return $account/@id)";
        try {
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/accounts.xml");
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
    
    /**
     * function creates new node for xml file
     * @param account
     * @return node
     */
    private String getNode(Account account) {
        DateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");

        String node = "<account id=\"" + account.getId() + "\">"
                + "<name>" + account.getName() + "</name>"
                + "<description>" + account.getDescription() + "</description>"
                + "<creationDate>" + dateF.format(account.getCreationDate()) + "</creationDate>"
                + "<currency>" + account.getCurrency() + "</currency>"
                + "</account>";
        return node;
    }

    /**
     * function checks all parameter and updates account
     * @param account 
     */
    @Override
    public void updateAccount(Account account) {
        if (account.getId() == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (account.getName() == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (account.getDescription() == null) {
            throw new IllegalArgumentException("description cannot be null");
        }

        if (account.getCreationDate() == null) {
            throw new IllegalArgumentException("Creation date cannot be null");
        }
        try {
            String node = getNode(account);
            String query = "update replace doc(\"accounts.xml\")//account[@id=" + account.getId() + "] with " + node;
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/accounts.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
            logger.log(Level.INFO,"update account");
        } catch (XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when updating account", ex);
        }
    }
    
    /**
     * function deletes account 
     * @param id 
     */
    @Override
    public void deleteAccount(Long id) {
        if(id==null){
            throw new IllegalArgumentException("id cannot be null");
        }
        try {
            String query = "for $account in doc(\"accounts.xml\")//account[@id=\"" + id + "\"] return update delete $account";
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/accounts.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);
            service.execute(compiled);
            logger.log(Level.INFO,"delete account");
        } catch (XMLDBException ex) {
            logger.log(Level.SEVERE, "Error when delete account", ex);
        }
    }

    /**
     * function finds accout by id
     * @param id
     * @return account
     */
    @Override
    public Account getAccountById(Long id){
        if (id == null) {
            throw new IllegalArgumentException("");
        }
        String where = "where @id=" + Long.toString(id);
        List<Account> result = findAccountsBy(where);
        if (result.size() != 1) {
       //     throw new DatabaseException();
        }
        return result.get(0);
    }
    /**
     * function finds accout by name
     * @param name
     * @return account
     */
    @Override
    public Account getAccountByName(String name){
        if (name == null) {
            throw new IllegalArgumentException("");
        }
        String where = "where name=" + "\"" + name + "\"";
        List<Account> result = findAccountsBy(where);
        if (result.size() != 1) {
            //throw new DatabaseException();
        }
        return result.get(0);
    }
    
    /**
     * function finds all accounts
     * @return List of account
     */
    @Override
    public List<Account> findAllAccounts() {
        String where = "";
        return findAccountsBy(where);
    }

    private List<Account> findAccountsBy(String where) {
        List<Account> resultList = new ArrayList<>();
        try {
            String queryStart = "for $account in doc(\"accounts.xml\")//account ";
            String queryEnd = " return $account";
            String query = queryStart + where + queryEnd;
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.declareVariable("document", "/db/accounts.xml");
            service.setProperty("indent", "yes");
            CompiledExpression compiled = service.compile(query);

            ResourceSet result = service.execute(compiled);
            ResourceIterator it = result.getIterator();
            while (it.hasMoreResources()) {
                Resource resource = it.nextResource();
                try {
                    resultList.add(parseAccountFromXML(resource.getContent().toString()));
                } catch (DatabaseException ex) {
                    logger.log(Level.SEVERE,"Error when parse xml",ex);
                }
            }
        } catch (XMLDBException ex) {

        }
        return resultList;
    }

    private Account parseAccountFromXML(String xml) throws DatabaseException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Account account = new Account();
        try {
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            try {

                Document doc = db.parse(is);
                NodeList a = doc.getElementsByTagName("account");
                Element parent = (Element) a.item(0);
                account.setId(Long.parseLong(parent.getAttribute("id")));

                a = parent.getElementsByTagName("name");
                if (a.getLength() != 1) {
                     throw new DatabaseException();
                }
                Element el = (Element) a.item(0);
                account.setName(el.getTextContent());

                a = parent.getElementsByTagName("description");
                if (a.getLength() != 1) {
                    throw new DatabaseException();
                }
                el = (Element) a.item(0);
                account.setDescription(el.getTextContent());

                a = parent.getElementsByTagName("creationDate");
                if (a.getLength() != 1) {
                    throw new DatabaseException();
                }
                el = (Element) a.item(0);
                DateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
                account.setCreationDate(dateF.parse(el.getTextContent()));
                
                a = parent.getElementsByTagName("currency");
                if (a.getLength() != 1) {
                    throw new DatabaseException();
                }
                el = (Element) a.item(0);
                account.setCurrency(el.getTextContent());
            } catch (SAXException | IOException | ParseException ex) {
                logger.log(Level.SEVERE,"parse account",ex);
            }
        } catch (ParserConfigurationException ex) {
           logger.log(Level.SEVERE,"parse account",ex);
        }
        return account;
    }

}

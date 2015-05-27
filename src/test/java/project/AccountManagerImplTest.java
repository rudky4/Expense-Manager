/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author Ondra
 */
public class AccountManagerImplTest {
    
    private AccountManagerImpl manager = new AccountManagerImpl();
    
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Date date(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public AccountManagerImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createAccount method, of class AccountManagerImpl.
     */
    @Test
    public void testCreateAccount() {
        System.out.println("createAccount");
        
        Account account = newAccount("Test Account","This is a test account",date("2015-05-05"));        
        manager.createAccount(account);
        
        Long accountID = account.getId();
        assertNotNull(accountID);
        
        manager.deleteAccount(account.getId());
        
    }

    /**
     * Test of updateAccount method, of class AccountManagerImpl.
     */
    @Test
    public void testUpdateAccount() {
        System.out.println("updateAccount");
        
        Account account1 = newAccount("Test Account 1","This is a test account 1",date("2015-05-05"));  
        Account account2 = newAccount("Test Account 2","This is a test account 2",date("2015-05-06"));  
        manager.createAccount(account1);
        manager.createAccount(account2);
        Long accountId = account1.getId();
        
        account1 = manager.getAccountById(accountId);
        account1.setName("Bank");
        account1.setDescription("Description");
        manager.updateAccount(account1);
        assertEquals("Bank", account1.getName());
        assertEquals("Description", account1.getDescription());
        
        assertDeepEquals(account2, manager.getAccountById(account2.getId()));
        
        manager.deleteAccount(account1.getId());
        manager.deleteAccount(account2.getId());
    }

    /**
     * Test of deleteAccount method, of class AccountManagerImpl.
     */
    @Test
    public void testDeleteAccount() {
        System.out.println("deleteAccount");
        
        Account account1 = newAccount("Test Account 1","This is a test account 1",date("2015-05-05"));
        Account account2 = newAccount("Test Account 2","This is a test account 2",date("2015-05-06"));
        Account account3 = newAccount("Test Account 3","This is a test account 3",date("2015-05-07"));
        manager.createAccount(account1);
        manager.createAccount(account2);
        manager.createAccount(account3);
        
        Long accountId = account1.getId();
        manager.deleteAccount(accountId);
        
        List<Account> result = manager.findAllAccounts();
        List<Account> expectedResult = new ArrayList<>();
        expectedResult.add(account2);
        expectedResult.add(account3);
        
        assertEquals(expectedResult.size(), result.size());
        
        manager.deleteAccount(account2.getId());
        manager.deleteAccount(account3.getId());
    }

    /**
     * Test of getAccount method, of class AccountManagerImpl.
     */
    @Test
    public void testGetAccountById() {
        System.out.println("getAccountById");
       
        Long id = new Long(1);
        Account account = newAccount("Test Account","This is a test account",date("2015-05-05"));
        manager.createAccount(account);
        List<Account> list = new ArrayList<>();
        list.add(account);
        Account result = list.get(0);
        Account accountById = manager.getAccountById(id);
        assertEquals(accountById.getId(), result.getId());
        assertEquals(accountById.getName(), result.getName());
        
        manager.deleteAccount(account.getId());
    }
    
    @Test
    public void testGetAccountByName() {
        System.out.println("getAccountByName");       
        
        Account account = newAccount("Test Account","This is a test account",date("2015-05-05"));
        manager.createAccount(account);
        List<Account> list = new ArrayList<>();
        list.add(account);
        Account result = list.get(0);
        Account accountByName = manager.getAccountByName("Test Account");
        assertEquals(accountByName.getId(), result.getId());
        assertEquals(accountByName.getName(), result.getName());
        
        manager.deleteAccount(account.getId());
    }
    
    private static Account newAccount(String name, String description, Date creationDate) {
        Account account = new Account();
        account.setName(name);   
        account.setDescription(description);
        account.setCreationDate(creationDate);
        return account;
    }
    
    private void assertDeepEquals(Account expected, Account actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());        
    }
}

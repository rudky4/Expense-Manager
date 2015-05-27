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

/**
 *
 * @author Ondra
 */
public class CurrencyManagerImplTest {
    
    private CurrencyManagerImpl manager = new CurrencyManagerImpl();
    
    public CurrencyManagerImplTest() {
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
     * Test of createCurrency method, of class CurrencyManagerImpl.
     */
    @Test
    public void testCreateCurrency() {
        System.out.println("createCurrency");
        
        Currency currency = newCurrency("CZK","Česká koruna");        
        manager.createCurrency(currency);
        
        String ccy = currency.getCcy();
        assertNotNull(ccy);
        
        manager.deleteCurrency(ccy);
        
    }

    /**
     * Test of updateCurrency method, of class CurrencyManagerImpl.
     */
    @Test
    public void testUpdateCurrency() {
        System.out.println("updateCurrency");
        
        Currency currency1 = newCurrency("CZK","Česká koruna");   
        Currency currency2 = newCurrency("USD","American dollar");  
        manager.createCurrency(currency1);
        manager.createCurrency(currency2);
        String ccy = currency1.getCcy();
        
        currency1 = manager.getCurrencyByCcy(ccy);
        currency1.setCcy("EUR");        
        currency1.setCcyName("Euro");
        manager.updateCurrency(currency1);
        assertEquals("EUR", currency1.getCcy());
        assertEquals("Euro", currency1.getCcyName());
        
        assertDeepEquals(currency2, manager.getCurrencyByCcy(currency2.getCcy()));
        
        manager.deleteCurrency(currency1.getCcy());
        manager.deleteCurrency(currency2.getCcy());
    }

    /**
     * Test of findAllCurrency method, of class CurrencyManagerImpl.
     */
    @Test
    public void testFindAllCurrency() {
        System.out.println("findAllCurrency");
        
        Currency currency1 = newCurrency("CZK","Česká koruna");   
        Currency currency2 = newCurrency("USD","American dollar"); 
        Currency currency3 = newCurrency("EUR","Euro"); 
        
        manager.createCurrency(currency1);
        manager.createCurrency(currency2);
        manager.createCurrency(currency3);
        List<Currency> expResult = new ArrayList<>();
        List<Currency> result = new ArrayList<>();
        expResult.add(currency1);
        expResult.add(currency2);
        expResult.add(currency3);
        result = manager.findAllCurrency();
        assertEquals(expResult.size(), result.size());
        assertDeepEquals(expResult, result);
        
    }
    
    private static Currency newCurrency(String ccy, String ccyName) {
        Currency currency = new Currency();
        currency.setCcy(ccy);
        currency.setCcyName(ccyName);        
        return currency;
    }
    
    private void assertDeepEquals(Currency expected, Currency actual) {
        assertEquals(expected.getCcy(), actual.getCcy());
        assertEquals(expected.getCcyName(), actual.getCcyName());        
    }
    
    private void assertDeepEquals(List<Currency> expectedList, List<Currency> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Currency expected = expectedList.get(i);
            Currency actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }
}

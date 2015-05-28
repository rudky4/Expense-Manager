/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class ExpenseManagerImplTest {
    
    private ExpenseManagerImpl expenseManager = new ExpenseManagerImpl();
    private PaymentManagerImpl paymentManager = new PaymentManagerImpl();
    
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Date date(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public ExpenseManagerImplTest() {
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
     * Test of getAccountBalance method, of class ExpenseManagerImpl.
     */
    @Test
    public void testGetAccountBalance_Long() {
        System.out.println("getAccountBalance");
        
        Long accountId = new Long(5);
        Long subjectId = new Long(6);
        Long categoryId = new Long(7);
        BigDecimal price1 = new BigDecimal(130.00);
        Payment payment1 = newPayment("Running shoes Nike",date("2015-05-05"),price1,accountId,subjectId,categoryId);
        paymentManager.createPayment(payment1);
        
        BigDecimal price2 = new BigDecimal(-100.00);
        Payment payment2 = newPayment("Running shoes Adidas",date("2015-05-06"),price2,accountId,subjectId,categoryId);
        paymentManager.createPayment(payment2);
                
        
        BigDecimal expResult = new BigDecimal(30.00);
        BigDecimal result = expenseManager.getAccountBalance(accountId);
        assertEquals(expResult, result);
        
        paymentManager.deletePayment(payment1.getId());
        paymentManager.deletePayment(payment2.getId());
    }

    /**
     * Test of getAccountBalance method, of class ExpenseManagerImpl.
     */
    @Test
    public void testGetAccountBalance_3args() {
        System.out.println("getAccountBalanceDates");
        
        Long accountId = new Long(5);
        Long subjectId = new Long(6);
        Long categoryId = new Long(7);
        Date startDate = date("2015-05-15");
        Date endDate = date("2015-05-25");
        BigDecimal price1 = new BigDecimal(130.00);
        Payment payment1 = newPayment("Running shoes Nike",date("2015-05-20"),price1,accountId,subjectId,categoryId);
        paymentManager.createPayment(payment1);
        
        BigDecimal price2 = new BigDecimal(100.00);
        Payment payment2 = newPayment("Running shoes Adidas",date("2015-05-21"),price2,accountId,subjectId,categoryId);
        paymentManager.createPayment(payment2);
        
        BigDecimal price3 = new BigDecimal(1000.00);
        Payment payment3 = newPayment("Running shoes Puma",date("2015-05-14"),price3,accountId,subjectId,categoryId);
        paymentManager.createPayment(payment3);
                
        
        BigDecimal expResult = new BigDecimal(230.00);
        BigDecimal result = expenseManager.getAccountBalance(accountId,startDate,endDate);
        assertEquals(expResult, result);
        
        paymentManager.deletePayment(payment1.getId());
        paymentManager.deletePayment(payment2.getId());
        paymentManager.deletePayment(payment3.getId());
    }

    /**
     * Test of getAllPayments method, of class ExpenseManagerImpl.
     */
    /*@Test
    public void testGetAllPayments_Long() {
        System.out.println("getAllPayments");
        Long accountId = null;
        ExpenseManagerImpl instance = new ExpenseManagerImpl();
        List<Account> expResult = null;
        List<Account> result = instance.getAllPayments(accountId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllPayments method, of class ExpenseManagerImpl.
     */
    /*@Test
    public void testGetAllPayments_3args() {
        System.out.println("getAllPayments");
        Long accountId = null;
        Date startDate = null;
        Date endDate = null;
        ExpenseManagerImpl instance = new ExpenseManagerImpl();
        List<Account> expResult = null;
        List<Account> result = instance.getAllPayments(accountId, startDate, endDate);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getAllPaymentsBySubject method, of class ExpenseManagerImpl.
     */
   /* @Test
    public void testGetAllPaymentsBySubject_Long_Long() {
        System.out.println("getAllPaymentsBySubject");
        Long accountId = null;
        Long subjectId = null;
        ExpenseManagerImpl instance = new ExpenseManagerImpl();
        List<Account> expResult = null;
        List<Account> result = instance.getAllPaymentsBySubject(accountId, subjectId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

    /**
     * Test of getAllPaymentsBySubject method, of class ExpenseManagerImpl.
     */
    /*@Test
    public void testGetAllPaymentsBySubject_4args() {
        System.out.println("getAllPaymentsBySubject");
        Long accountId = null;
        Long subjectId = null;
        Date startDate = null;
        Date endDate = null;
        ExpenseManagerImpl instance = new ExpenseManagerImpl();
        List<Account> expResult = null;
        List<Account> result = instance.getAllPaymentsBySubject(accountId, subjectId, startDate, endDate);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/

       
    private static Payment newPayment(String description, Date date, BigDecimal amount, Long accountId, Long subjectId, Long categoryId) {
        Payment payment = new Payment();
        payment.setDescription(description);
        payment.setDate(date);
        payment.setAmount(amount);
        payment.setAccountId(accountId);
        payment.setCategoryId(categoryId);
        payment.setSubjectId(subjectId);
        return payment;
    }
}

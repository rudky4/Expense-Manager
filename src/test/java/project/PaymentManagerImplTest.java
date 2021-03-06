/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author charlliz
 */
public class PaymentManagerImplTest {
    
   
    PaymentManagerImpl manager = new PaymentManagerImpl();
    AccountManagerImpl accountManager = new AccountManagerImpl();
    Account account;
    SubjectManagerImpl subManager = new SubjectManagerImpl();
    Subject subject;
    CategoryManagerImpl catManager= new CategoryManagerImpl();
    Category category;
    
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Date date(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Before
    public void setUp() throws SQLException{
        account = newAccount("Test Account","This is a test account",date("2015-05-05"));        
        accountManager.createAccount(account);
        subject = newSubject("Test subject");
        subManager.createSubject(subject);
        category = newCategory("Test category");
        catManager.createCategory(category);
    }
    
    /**
     * Test of createPayment method, of class PaymentManagerImpl.
     */
    @Test
    public void testCreatePayment() {
        Payment payment = newPayment("Running shoes Nike",date("2015-05-05"),new BigDecimal("130.00"),account,subject,category);
        manager.createPayment(payment);
        
        Long paymentId = payment.getId();
        assertNotNull(paymentId);
        assertNotNull(payment.getAccountId());
        assertNotNull(payment.getCategoryId());
        assertNotNull(payment.getCategoryId());
        Payment temp = manager.getPaymentById(paymentId);
        assertEquals(payment,temp);
        assertDeepEquals(payment,temp);
        assertNotSame(payment,temp);
        
        manager.deletePayment(paymentId);
    }

    @Test
    public void testCreatePaymentWithWrongArguments(){
        try {
            manager.createPayment(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        Payment payment = newPayment("Running shoes Nike",date("2015-05-05"),new BigDecimal("130.00"),account,subject,category);
        payment.setId(1l);
        try {
            manager.createPayment(payment);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        payment = newPayment("Running shoes Nike",date("2015-05-05"),null,account,subject,category);
        payment.setId(1l);
        try {
            manager.createPayment(payment);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        payment = newPayment(null,date("2015-05-05"),new BigDecimal("130.00"),account,subject,category);
        payment.setId(1l);
        try {
            manager.createPayment(payment);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        payment = newPayment("Running shoes Nike",null,new BigDecimal("130.00"),account,subject,category);
        payment.setId(1l);
        try {
            manager.createPayment(payment);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }
    
    /**
     * Test of updatePayment method, of class PaymentManagerImpl.
     */
    @Test
    public void testUpdatePayment() {
        Payment p1 = newPayment("Flowers",date("2015-05-05"),new BigDecimal("5.60"),account,subject,category);
        manager.createPayment(p1);
        
        Long paymentId = p1.getId();

        p1 = manager.getPaymentById(paymentId);
        p1.setAmount(new BigDecimal("6.50"));
        manager.updatePayment(p1);        
        assertEquals(new BigDecimal("6.50"), p1.getAmount());
        assertEquals("Flowers", p1.getDescription());
        assertEquals(date("2015-05-05"), p1.getDate());

        p1 = manager.getPaymentById(paymentId);
        p1.setDate(date("2015-05-06"));
        manager.updatePayment(p1);        
        assertEquals(new BigDecimal("6.50"), p1.getAmount());
        assertEquals("Flowers", p1.getDescription());
        assertEquals(date("2015-05-06"), p1.getDate());

        p1 = manager.getPaymentById(paymentId);
        p1.setDescription("Magazines");
        manager.updatePayment(p1);        
        assertEquals(new BigDecimal("6.50"), p1.getAmount());
        assertEquals("Magazines", p1.getDescription());
        assertEquals(date("2015-05-06"), p1.getDate());
        
        manager.deletePayment(paymentId);
    }

    
    @Test
    public void testUpdatePaymentWithWrongArguments(){
        Payment p1 = newPayment("Running shoes Nike",date("2015-05-05"),new BigDecimal("130.00"),account,subject,category);
        manager.createPayment(p1);
        
        Long paymentId = p1.getId();
        
        try {
            manager.updatePayment(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        try {
            p1 = manager.getPaymentById(paymentId);
            p1.setId(null);
            manager.updatePayment(p1);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        try {
            p1 = manager.getPaymentById(paymentId);
            p1.setAmount(null);
            manager.updatePayment(p1);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        try {
            p1 = manager.getPaymentById(paymentId);
            p1.setDescription(null);
            manager.updatePayment(p1);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        try {
            p1 = manager.getPaymentById(paymentId);
            p1.setDate(null);
            manager.updatePayment(p1);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
    }
    
    /**
     * Test of deletePayment method, of class PaymentManagerImpl.
     */
    @Test
    public void testDeletePayment() {
        Payment p1 = newPayment("Wine",date("2015-05-05"),new BigDecimal("3.20"),account,subject,category);
        Payment p2 = newPayment("Milk",date("2015-05-06"),new BigDecimal("0.80"),account,subject,category);
        manager.createPayment(p1);
        manager.createPayment(p2);
        
        assertNotNull(manager.getPaymentById(p1.getId()));
        assertNotNull(manager.getPaymentById(p2.getId()));
        
        manager.deletePayment(p1.getId());
        
        assertNull(manager.getPaymentById(p1.getId()));
        assertNotNull(manager.getPaymentById(p2.getId()));  
        
        try {
            manager.deletePayment(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        manager.deletePayment(p2.getId());
    }

    /**
     * Test of getPaymentById method, of class PaymentManagerImpl.
     */
    @Test
    public void testGetPaymentById() {
        
        Payment payment = newPayment("Running shirt Adidas",date("2015-05-05"),new BigDecimal("25.00"),account,subject,category);
        manager.createPayment(payment);
        Long paymentId = payment.getId();
        
        Payment result = manager.getPaymentById(paymentId);
        assertEquals(payment,result);
        assertDeepEquals(payment,result);
    }

    /**
     * Test of findAllPayments method, of class PaymentManagerImpl.
     */
    @Test
    public void testFindAllPayments() {
        List<Payment> list = manager.findAllPayments();
        int list_size = list.size();
        Payment payment = newPayment("Running shoes Nike",date("2015-05-05"),new BigDecimal("130.00"),account,subject,category);
        manager.createPayment(payment);
        list = manager.findAllPayments();
        if((++list_size) != list.size()) fail();
        
        manager.deletePayment(payment.getId());
        
    }
    
    
    private static Payment newPayment(String description, Date date, BigDecimal amount, Account account, Subject subject, Category category) {
        Payment payment = new Payment();
        payment.setDescription(description);
        payment.setDate(date);
        payment.setAmount(amount);
        payment.setAccountId(account.getId());
        payment.setCategoryId(category.getId());
        payment.setSubjectId(subject.getId());
        return payment;
    }
    
    private static Account newAccount(String name, String description, Date creationDate) {
        Account account = new Account();
        account.setName(name);   
        account.setDescription(description);
        account.setCreationDate(creationDate);
        return account;
    }
    
    private static Subject newSubject(String name) {
        Subject subject = new Subject();
        subject.setName(name);        
        return subject;
    }
    
    private static Category newCategory(String name) {
        Category category = new Category();
        category.setName(name);        
        return category;
    }
     
    private void assertDeepEquals(Payment p1, Payment p2) {
        assertEquals(p1.getId(), p2.getId());
        assertEquals(p1.getDescription(), p2.getDescription());
        assertEquals(p1.getDate(),p2.getDate());
        assertEquals(p1.getAmount(),p2.getAmount());
        assertEquals(p1.getAccountId(),p2.getAccountId());
        assertEquals(p1.getSubjectId(),p2.getSubjectId());
        assertEquals(p1.getCategoryId(),p2.getCategoryId());
    }
    
    private void assertEqualsList(List<Payment> list1,List<Payment> list2){
        if(list1.size() != list2.size()) fail();
        
        for(int i=0;i<list1.size();i++){
            assertDeepEquals(list1.get(i),list2.get(i));
        }
    }
}

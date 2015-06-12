/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author marek
 */
public class ExpenseManagerImpl implements ExpenseManager {

private PaymentManager paymentManager = new PaymentManagerImpl();
private SubjectManager subjectManager = new SubjectManagerImpl();
private AccountManager accountManager = new AccountManagerImpl();

    @Override
    public BigDecimal getAccountBalance(Long accountId) {
        List<Payment> paymnentsList = paymentManager.findAllPayments();
        BigDecimal balance = new BigDecimal(0);
        for (Payment p : paymnentsList) {
            if (p.getAccountId().equals(accountId)) {
                balance = balance.add(p.getAmount());
            }
        }
        return balance;
    }

    @Override
    public BigDecimal getAccountBalance(Long accountId, Date startDate, Date endDate) {
        List<Payment> paymnentsList = paymentManager.findAllPayments();
        BigDecimal balance = new BigDecimal(0);
        for (Payment p : paymnentsList) {
            if (p.getAccountId().equals(accountId) && ((startDate.compareTo(p.getDate())) <= 0) && (endDate.compareTo(p.getDate()) >= 0)) {
                balance = balance.add(p.getAmount());
            }
        }
        return balance;
    }

    @Override
    public List<Payment> getAllPaymentsByAccount(Long accountId) {
        List<Payment> paymentList = paymentManager.findAllPayments();
        List<Payment> result = new ArrayList<>();
        for (Payment p : paymentList) {
            if (p.getAccountId().equals(accountId)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<Payment> getAllPaymentsByAccount(Long accountId, Date startDate, Date endDate) {
        List<Payment> paymentList = paymentManager.findAllPayments();
        List<Payment> result = new ArrayList<>();
        for (Payment p : paymentList) {
            if (p.getAccountId().equals(accountId) && ((startDate.compareTo(p.getDate())) <= 0) && (endDate.compareTo(p.getDate()) >= 0)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<Payment> getAllPaymentsBySubjectAndAccount(Long accountId, Long subjectId) {
        List<Payment> paymentList = paymentManager.findAllPayments();
        List<Payment> result = new ArrayList<>();
        for (Payment p : paymentList) {
            if (p.getAccountId().equals(accountId) && p.getSubjectId().equals(subjectId)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<Payment> getAllPaymentsBySubjectAndAccount(Long accountId, Long subjectId, Date startDate, Date endDate) {
        List<Payment> paymentList = paymentManager.findAllPayments();
        List<Payment> result = new ArrayList<>();
        for (Payment p : paymentList) {
            if (p.getAccountId().equals(accountId) && p.getSubjectId().equals(subjectId) && ((startDate.compareTo(p.getDate())) <= 0) && (endDate.compareTo(p.getDate()) >= 0)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<Payment> getAllPaymentsBySubject(Long subjectId) {
        List<Payment> paymentList = paymentManager.findAllPayments();
        List<Payment> result = new ArrayList<>();
        for (Payment p : paymentList) {
            if (p.getSubjectId().equals(subjectId)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<Payment> getAllPaymentsBySubject(Long subjectId, Date startDate, Date endDate) {
        List<Payment> paymentList = paymentManager.findAllPayments();
        List<Payment> result = new ArrayList<>();
        for (Payment p : paymentList) {
            if (p.getSubjectId().equals(subjectId) && ((startDate.compareTo(p.getDate())) <= 0) && (endDate.compareTo(p.getDate()) >= 0)) {
                result.add(p);
            }
        }
        return result;
    }

    /*public String createXML(List<Payment> list) {
        DateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
        String result = "<payments>";
        AccountManagerImpl accManager = new AccountManagerImpl();
        SubjectManagerImpl subManager = new SubjectManagerImpl();
        CategoryManagerImpl catManager = new CategoryManagerImpl();

        for (int i = 0; i < list.size(); i++) {
            result += "<payment id=\"" + list.get(i).getId() + "\">"
                    + "<description>" + list.get(i).getDescription() + "</description>"
                    + "<date>" + dateF.format(list.get(i).getDate()) + "</date>"
                    + "<amount>" + list.get(i).getAmount() + "</amount>"
                    + "<account-name>" + accManager.getAccountById(list.get(i).getAccountId()).getName() + "</account-name>"
                    + "<subject-name>" + subManager.getSubjectById(list.get(i).getSubjectId()).getName() + "</subject-name>"
                    + "<category-name>" + catManager.getCategoryById(list.get(i).getCategoryId()).getName() + "</category-name>"
                    + "</payment>";
        }

        result += "</payments>";
        return result;
    }*/
    
    @Override
    public String createXML(List<Payment> list) {
        DateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
        String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<payments>\n";
        AccountManagerImpl accManager = new AccountManagerImpl();
        SubjectManagerImpl subManager = new SubjectManagerImpl();
        CategoryManagerImpl catManager = new CategoryManagerImpl();
        BigDecimal sum = new BigDecimal (0);
        BigDecimal incomingSum = new BigDecimal (0);
        BigDecimal outgoingSum = new BigDecimal (0);
        BigDecimal highestIncoming = new BigDecimal (0);
        BigDecimal highestOutgoing = new BigDecimal (0);
        int count = 0;
        int incomingCount = 0;
        int outgoingCount = 0;

        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getAmount().compareTo(new BigDecimal(0)) == 1){
                incomingCount++;
                incomingSum = incomingSum.add(list.get(i).getAmount());
            }
            if(list.get(i).getAmount().compareTo(new BigDecimal(0)) == -1){
                outgoingCount++;
                outgoingSum = outgoingSum.add(list.get(i).getAmount());
            }
            if(list.get(i).getAmount().compareTo(highestIncoming) == 1){
                highestIncoming = list.get(i).getAmount();
            }
            if(list.get(i).getAmount().compareTo(highestOutgoing) == -1){
                highestOutgoing = list.get(i).getAmount();
            }
            sum = sum.add(list.get(i).getAmount());
            count++;
            result += "\t<payment id=\"" + list.get(i).getId() + "\">\n"
                    + "\t\t<description>" + list.get(i).getDescription() + "</description>\n"
                    + "\t\t<date>" + dateF.format(list.get(i).getDate()) + "</date>\n"
                    + "\t\t<amount>" + list.get(i).getAmount() + "</amount>\n"
                    + "\t\t<account-name>" + accManager.getAccountById(list.get(i).getAccountId()).getName() + "</account-name>\n"
                    + "\t\t<subject-name>" + subManager.getSubjectById(list.get(i).getSubjectId()).getName() + "</subject-name>\n"
                    + "\t\t<category-name>" + catManager.getCategoryById(list.get(i).getCategoryId()).getName() + "</category-name>\n"
                    + "\t\t</payment>";
        }        
        result += "<count>" + count + "</count>\n";
        result += "<sum>" + sum + "</sum>\n";
        result += "<incomingCount>" + incomingCount + "</incomingCount>\n";
        result += "<outgoingCount>" + outgoingCount + "</outgoingCount>\n";
        result += "<incomingSum>" + incomingSum + "</incomingSum>\n";
        result += "<outgoingSum>" + outgoingSum + "</outgoingSum>\n";
        result += "<highestIncoming>" + highestIncoming + "</highestIncoming>\n";
        result += "<highestOutgoing>" + highestOutgoing + "</highestOutgoing>\n";
        result += "</payments>";
        
        return result;
    }
    

    public static void createHTML(String document, String output) throws TransformerConfigurationException, TransformerException {
        
        TransformerFactory tf = TransformerFactory.newInstance();
        
        Transformer xsltProc = tf.newTransformer(
                new StreamSource(new File("xmlschema/payments.xsl")));
        
        xsltProc.transform(
                new StreamSource(new StringReader(document)), 
                new StreamResult(new File(output)));
    }
}

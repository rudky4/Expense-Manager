/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author marek
 */
public interface ExpenseManager {
  
    /**
     * Returns account balance for selected account
     * 
     * @param accountId
     * @return account balance
     */
    public BigDecimal getAccountBalance(Long accountId);
    
    /**
     * Returns account balance for selected account during selected period of time
     * 
     * @param accountId
     * @param startDate
     * @param endDate
     * @return account balance
     */
    public BigDecimal getAccountBalance(Long accountId, Date startDate, Date endDate);
    
     /**
     * Returns all payments for selected account
     * 
     * @param accountId
     * @return list of payments
     */
    public List<Payment> getAllPaymentsByAccount(Long accountId);

    /**
     * Returns all payments for selected account from selected period of time
     * 
     * @param accountId
     * @param startDate
     * @param endDate
     * @return list of payments
     */
    public List<Payment> getAllPaymentsByAccount(Long accountId, Date startDate, Date endDate);

     /**
     * Returns all payments for selected account and selected subject
     * 
     * @param accountId
     * @param subjectId
     * @return list of payments
     */
    public List<Payment> getAllPaymentsBySubjectAndAccount(Long accountId, Long subjectId);

    /**
     * Returns all payments for selected account and selected subject from selected period of time
     * 
     * @param accountId
     * @param subjectId
     * @param startDate
     * @param endDate
     * @return list of payments
     */
    public List<Payment> getAllPaymentsBySubjectAndAccount(Long accountId, Long subjectId, Date startDate, Date endDate);
    
    /**
     * Returns all payments for selected subject
     * 
     * @param subjectId
     * @return list of payments
     */
    public List<Payment> getAllPaymentsBySubject(Long accountId);
    
    /**
     * Returns all payments for selected subject from selected period of time
     * 
     * @param subjectId
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Payment> getAllPaymentsBySubject(Long accountId, Date startDate, Date endDate);
    
    /**
     * Create xml from list of payments
     * 
     * @param list
     * @return xml as String
     */
    public String createXML(List<Payment> list);
    
}

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

    public void addPayment(Account account, Payment payment, Subject subject);

    public void removePayment(Long accountId, Long paymentId);

    public BigDecimal getAccountBalance(Long accountId);

    public BigDecimal getAccountBalance(Long accountId, Date startDate, Date endDate);

    public List<Account> getAllPayments(Long accountId);

    public List<Account> getAllPayments(Long accountId, Date startDate, Date endDate);

    public List<Account> getAllPaymentsBySubject(Long accountId, Long subjectId);

    public List<Account> getAllPaymentsBySubject(Long accountId, Long subjectId, Date startDate, Date endDate);
}

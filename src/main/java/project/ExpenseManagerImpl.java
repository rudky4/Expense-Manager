/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public List<Payment> getAllPaymentsByAcoount(Long accountId, Date startDate, Date endDate) {
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

    public String createXML(List<Payment> list) {
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
    }
}

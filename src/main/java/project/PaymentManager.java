package project;


import java.util.Date;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author marek
 */
public interface PaymentManager {

    /**
     * Add ceartin payment to database
     * @param payment  payment to add to database
     */
    public void createPayment(Payment payment);

    /**
     * Edit payment
     * @param payment payment in database to be updated
     */
    public void updatePayment(Payment payment);

    /**
     * Delete payment in database
     * @param id id of payment
     */
    public void deletePayment(Long id);

    /**
     * Return payment by id
     * @param id payment id
     * @return return payment 
     */
    public Payment getPaymentById(Long id);
    
    /**
     * Return all payments in database
     * @return list od payments
     */
    public List<Payment> findAllPayments();
    
    /**
     * Return all payments in time interval
     * @param startDate from date
     * @param endDate to date
     * @return list of payments
     */
    public List<Payment>findAllPayments(Date startDate, Date endDate);
}

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

    public void createPayment(Payment payment);

    public void updatePayment(Payment payment);

    public void deletePayment(Long id);

    public Payment getPaymentById(Long id);
    
    public List<Payment> findAllPayments();
    
    public List<Payment>findAllPayments(Date startDate, Date endDate);
}

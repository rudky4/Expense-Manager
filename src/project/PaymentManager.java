
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

    public void upradtePayment(Payment payment);

    public void deletePayment(Long id);

    public List<Payment> getPayment(Long id);
}

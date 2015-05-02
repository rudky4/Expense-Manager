
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
public interface AccountManager {

    public void createAccount(Account account);

    public void updateAccount(Account account);

    public void deleteAccount(Long id);

    public List<Account> getAccount(Long id);
}

package project;


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

    /**
     * function checks all parameters and create account
     * @param account 
     */
    public void createAccount(Account account);

    /**
     * function checks all parameter and updates account
     * @param account 
     */
    public void updateAccount(Account account);

     /**
     * function deletes account 
     * @param id 
     */
    public void deleteAccount(Long id);

    /**
     * function finds accout by id
     * @param id
     * @return account
     */
    public Account getAccountById(Long id);
    
    /**
     * function finds accout by name
     * @param name
     * @return account
     */
    public Account getAccountByName(String name);
    
    /**
     * function finds all accounts
     * @return List of account
     */
    public List<Account> findAllAccounts();
}

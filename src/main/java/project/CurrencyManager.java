/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.util.List;

/**
 *
 * @author marek
 */
public interface CurrencyManager {
    
    /**
     * function checks all parameters and create account 
     * @param currency
     */
    public void createCurrency(Currency currency);
    
    /**
     * function deletes account 
     * @param ccy 
     */
    public void deleteCurrency(String ccy);
    
    /**
     * function finds all accounts
     * @return List of currency
     */
    public List<Currency> findAllCurrency();
    
    /**
     * function finds accout by id
     * @param ccy
     * @return currency
     */
    public Currency getCurrencyByCcy(String ccy);
}

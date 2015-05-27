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
    
    public void updateCurrency(Currency currency);
    
    public void createCurrency(Currency currency);
    
    public void deleteCurrency(String ccy);
    
    public List<Currency> findAllCurrency();
}

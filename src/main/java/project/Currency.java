/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

/**
 *
 * @author marek
 */
public class Currency {
    private String ccy;
    private String ccyName;

    /**
     * @return the ccy
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * @param ccy the ccy to set
     */
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * @return the ccyName
     */
    public String getCcyName() {
        return ccyName;
    }

    /**
     * @param ccyName the ccyName to set
     */
    public void setCcyName(String ccyName) {
        this.ccyName = ccyName;
    }
    
    public String toString(){
        return ccy;
    }
}

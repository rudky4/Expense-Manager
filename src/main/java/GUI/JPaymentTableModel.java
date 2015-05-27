package GUI;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import project.AccountManagerImpl;
import project.CurrencyManagerImpl;
import project.Payment;
import project.SubjectManagerImpl;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rk
 */
public class JPaymentTableModel extends AbstractTableModel {
   
    private static final int COLUMN_COUNT = 6;
    private static final int COLUMN_DESCRIPTION = 0;
    private static final int COLUMN_DATE = 1;
    private static final int COLUMN_ACCNAME = 2;
    private static final int COLUMN_SUBNAME = 3;
    private static final int COLUMN_AMOUNT = 4;
    private static final int COLUMN_CURRENCY = 5;
    
    private List<Payment> table = new ArrayList<>();
    
    private ResourceBundle localization; 
    
    
    
    public JPaymentTableModel(ResourceBundle localization){ 
        this.localization = localization;
    }
    
    public JPaymentTableModel(List<Payment> data, ResourceBundle localization){
        table.addAll(data);
        this.localization = localization;
    }
    
    @Override
    public int getRowCount() {        
        return table.size();
    }
 
    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }
 
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(rowIndex >= table.size()){
            throw new IllegalArgumentException("Row index out of bounds.");            
        }
        
        AccountManagerImpl account = new AccountManagerImpl(); 
        SubjectManagerImpl subject = new SubjectManagerImpl(); 
        CurrencyManagerImpl currency = new CurrencyManagerImpl();
        
        Payment payment = table.get(rowIndex);
        
        switch (columnIndex) {
            case COLUMN_DESCRIPTION: return payment.getDescription();
            case COLUMN_DATE: return payment.getDate().toString().substring(0, 10);
            case COLUMN_ACCNAME: return account.getAccountById(payment.getAcountId()).getName();
            case COLUMN_SUBNAME: return subject.getSubjectById(payment.getCategoryId()).getName();
            case COLUMN_AMOUNT: return payment.getAmount();
            case COLUMN_CURRENCY: return account.getAccountById(payment.getAcountId()).getCurrency();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }    
    
    @Override
    public String getColumnName(int columnIndex){
        switch(columnIndex){
            case COLUMN_DESCRIPTION: return localization.getString("global.description");
            case COLUMN_DATE: return localization.getString("global.date");
            case COLUMN_ACCNAME: return localization.getString("global.accountName");
            case COLUMN_SUBNAME: return localization.getString("global.subjectName");
            case COLUMN_AMOUNT: return localization.getString("global.amount");
            case COLUMN_CURRENCY: return localization.getString("global.currency");
        }
        
        throw new IllegalArgumentException("Column index out of bounds.");
    }
        
    @Override
    public Class<Payment> getColumnClass(int columnIndex){
        return Payment.class;
    }
    
    public Payment getRow(int rowIndex){
        return table.get(rowIndex);
    }
    
    public void refresh(Collection<Payment> data){
        table.clear();
        table.addAll(data);
        fireTableDataChanged();
    }
    
    
}
  
    


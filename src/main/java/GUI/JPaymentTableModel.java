package GUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import project.AccountManagerImpl;
import project.CurrencyManagerImpl;
import project.Payment;
import project.PaymentManagerImpl;
import project.SubjectManagerImpl;


/**
 * Table model for listing payments
 * @author rudky4
 */
public class JPaymentTableModel extends AbstractTableModel {
   
    private static final int COLUMN_COUNT = 6;
    private static final int COLUMN_DESCRIPTION = 0;
    private static final int COLUMN_DATE = 1;
    private static final int COLUMN_ACCNAME = 2;
    private static final int COLUMN_SUBNAME = 3;
    private static final int COLUMN_AMOUNT = 4;
    private static final int COLUMN_CURRENCY = 5;
    
    private final AccountManagerImpl account = new AccountManagerImpl();
    private final SubjectManagerImpl subject = new SubjectManagerImpl(); 
    private final CurrencyManagerImpl currency = new CurrencyManagerImpl();
    private final PaymentManagerImpl manager = new PaymentManagerImpl();
    private final List<Payment> table = new ArrayList<>();    
    private final ResourceBundle localization; 
    
    
    /**
     * Constructor using localization bundle
     * @param localization 
     */
    public JPaymentTableModel(ResourceBundle localization){
        this.localization = localization;        
        List<Payment> list = manager.findAllPayments();
        this.refresh(list);
    }
    
    /**
     * Constructor using localization bundle and specific data.
     * @param data - to be add to table
     * @param localization 
     */
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
        
        Payment payment = table.get(rowIndex);
        Date date = payment.getDate();
        
        switch (columnIndex) {
            case COLUMN_DESCRIPTION: return payment.getDescription();
            case COLUMN_DATE: return date.toString().substring(8,10)+". "+date.toString().substring(4,7)+" "+date.toString().substring(24,date.toString().length());
            case COLUMN_ACCNAME: return account.getAccountById(payment.getAccountId()).getName();
            case COLUMN_SUBNAME: return subject.getSubjectById(payment.getSubjectId()).getName();
            case COLUMN_AMOUNT: return payment.getAmount();
            case COLUMN_CURRENCY: return account.getAccountById(payment.getAccountId()).getCurrency();
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
    
    /**
     * Returt specific payment, selected by position in table
     * @param rowIndex 
     * @return specific payment
     */
    public Payment getRow(int rowIndex){
        return table.get(rowIndex);
    }
    
    /**
     * Return all payment in table
     * @return list of payments
     */
    public List<Payment> getPayments(){
        return this.table;
    }
    
    /**
     * Refresh data in table, using data collection
     * @param data - new data
     */
    public void refresh(Collection<Payment> data){
        table.clear();
        table.addAll(data);
        fireTableDataChanged();
    }
    
    
}
  
    


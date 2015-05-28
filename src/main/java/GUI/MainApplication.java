package GUI;


import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import net.sourceforge.jdatepicker.DateModel;
import net.sourceforge.jdatepicker.JDatePicker;
import project.Account;
import project.AccountManagerImpl;
import project.CurrencyManagerImpl;
import project.ExpenseManagerImpl;
import project.Payment;
import project.PaymentManagerImpl;
import project.SubjectManagerImpl;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rk
 */
public class MainApplication extends javax.swing.JFrame{
    private final Action quitAction = new QuitAction();
    private static final BasicDataSource dataSource = new BasicDataSource();
    private final ResourceBundle localization = ResourceBundle.getBundle("localization",new Locale("SK")); 
    //private final ResourceBundle localization = ResourceBundle.getBundle("localization",Locale.US); 
    private static AccountManagerImpl accountManager = new AccountManagerImpl();
    private static PaymentManagerImpl paymentManager = new PaymentManagerImpl();
    private static ExpenseManagerImpl expenseManager = new ExpenseManagerImpl();
    private static CurrencyManagerImpl currencyManager = new CurrencyManagerImpl();
    private static SubjectManagerImpl subjectManager = new SubjectManagerImpl();  
    
    //private JPaymentTableModel paymentTableModel;
    
    private enum AccountActions {

        ADD_ACCOUNT, EDIT_ACCOUNT, REMOVE_ACCOUNT, GET_ALL_ACCOUNTS, SEARCH_ACCOUNT
    };

    private enum PaymentActions {

        ADD_PAYMENT, EDIT_PAYMENT, REMOVE_PAYMENT, GET_ALL_PAYMENTS, SEARCH_PAYMENT
    };
    
    private enum ExpenseActions {

        ADD_PAYMENT_TO_ACCOUNT
    };
    
  /*   private static void initalize(String file) throws SQLException, FileNotFoundException, IOException {

        String [] info;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
                
        info = lines.get(0).split(";");
        dataSource.setUrl(info[1]);
        info = lines.get(1).trim().split(";"); 
        dataSource.setUsername(info[1]);
        info = lines.get(2).trim().split(";");
        dataSource.setPassword(info[1]);
 
        expenseManager = new ExpenseManagerImpl();
        expenseManager.setDataSource(dataSource);  
        accountManager = new AccountManagerImpl(dataSource);
        paymentManager = new PaymentManagerImpl(dataSource); 
        
        info = lines.get(3).trim().split(";");
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(info[1], true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

        accountManager.setLogger(fs);
        paymentManager.setLogger(fs);
        expenseManager.setLogger(fs);     
    }*/
    
    
     private class AccountSwingWorker extends SwingWorker<List<Account>, Void>{
     
        private AccountActions accountAction;
        private Account account;
         
        
        public AccountSwingWorker(AccountActions action, Account account) {
            this.accountAction = action;
            this.account = account;
        }


        @Override
        protected List<Account> doInBackground() throws Exception {
            switch (accountAction) {
                case ADD_ACCOUNT:{
                    try {
                        accountManager.createAccount(account);

                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_add_account"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    return accountManager.findAllAccounts();
                    }
                
                case EDIT_ACCOUNT:
                    try {
                        accountManager.updateAccount(account);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_update_account"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    return accountManager.findAllAccounts();
                
                
                case REMOVE_ACCOUNT:
                    try {
                        accountManager.deleteAccount(account.getId());
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_remove_account"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                    }
                    return accountManager.findAllAccounts();

                case GET_ALL_ACCOUNTS:
                    try {
                        return accountManager.findAllAccounts();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane, localization.getString("db_error"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    } catch (RuntimeException ex) {
                        JOptionPane.showMessageDialog(rootPane, localization.getString("db_error"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                
                default:
                    throw new IllegalStateException("Default reached in doInBackground() AccountSwingWorker");
            }
        }
        
        @Override
        protected void done() {
            try {
                if (null == get()) {
                    accountAction = null;
                    return;
                }
            } catch (ExecutionException ex) {
                accountAction = null;
                JOptionPane.showMessageDialog(rootPane,
                        ex.getCause().getMessage(), ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InterruptedException ex) {
                accountAction = null;
                throw new IllegalStateException(localization.getString("interrupted"), ex);
            }
            switch (accountAction) {
                case REMOVE_ACCOUNT:
                    accountList.removeItem(accountList.getSelectedItem());
                    ((JPaymentTableModel)jPaymentTableModel.getModel()).fireTableDataChanged();
                    jDialog1.setVisible(true);
                    break;
                case ADD_ACCOUNT:
                    accountList.addItem(account);
                    ((JPaymentTableModel)jPaymentTableModel.getModel()).fireTableDataChanged();
                    jFrame1.dispose();
                    break;
                case EDIT_ACCOUNT:
                    accountList.removeItem(accountList.getSelectedItem());
                    accountList.addItem(account);
                    accountList.setSelectedItem(account);
                    ((JPaymentTableModel)jPaymentTableModel.getModel()).fireTableDataChanged();
                    jFrame2.dispose();
                    break;
            }        
            accountAction = null;
        }
         
     }
     
    
     private class PaymentSwingWorker extends SwingWorker<List<Payment>, Void>{
     
        private PaymentActions paymentAction;
        private Payment payment;
         
        
        public PaymentSwingWorker(PaymentActions action, Payment payment) {
            this.paymentAction = action;
            this.payment = payment;
        }


        @Override
        protected List<Payment> doInBackground() throws Exception {
            switch (paymentAction) {
                case ADD_PAYMENT:{
                    try {
                        paymentManager.createPayment(payment);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_add_payment"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    List<Payment> result = new ArrayList<>();
                    result.add(payment);
                    return result;
                    }
                
                case EDIT_PAYMENT:
                    try {
                        paymentManager.updatePayment(payment);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_update_payment"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    return paymentManager.findAllPayments();
                
                
                case REMOVE_PAYMENT:
                    try {
                        paymentManager.deletePayment(payment.getId());
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_remove_payment"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                    }
                    return paymentManager.findAllPayments();

                case GET_ALL_PAYMENTS:
                    try {
                        return paymentManager.findAllPayments();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,localization.getString("db_error"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    } catch (RuntimeException ex) {
                        JOptionPane.showMessageDialog(rootPane,localization.getString("db_error"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                
                default:
                    throw new IllegalStateException("Default reached in doInBackground() PaymentSwingWorker");
            }
        }
        
        @Override
        protected void done() {
            try {
                if (null == get()) {
                    paymentAction = null;
                    return;
                }
            } catch (ExecutionException ex) {
                paymentAction = null;
                JOptionPane.showMessageDialog(rootPane,
                        ex.getCause().getMessage(), ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InterruptedException ex) {
                paymentAction = null;
                throw new IllegalStateException(localization.getString("interrupted"), ex);
            }
            switch (paymentAction) {
                case REMOVE_PAYMENT:
                    ((JPaymentTableModel)jPaymentTableModel.getModel()).fireTableDataChanged();
                    jDialog2.setVisible(true);
                    break;
                case ADD_PAYMENT:
                    new ExpenseSwingWorker(ExpenseActions.ADD_PAYMENT_TO_ACCOUNT,(Account)accountList.getSelectedItem() ,payment).execute();
                    break;
                case EDIT_PAYMENT:
                    ((JPaymentTableModel)jPaymentTableModel.getModel()).fireTableDataChanged();
                    jFrame4.dispose();
                    break;
            }
            paymentAction = null;
        }
         
     }
     
    
     
     private class ExpenseSwingWorker extends SwingWorker<Integer, Void>{
     
        private ExpenseActions expenseAction;
        private Account account;
        private Payment payment;
         
        
        public ExpenseSwingWorker(ExpenseActions action, Account account,Payment payment) {
            this.expenseAction = action;
            this.account = account;
            this.payment = payment;
        }


        @Override
        protected Integer doInBackground() throws Exception {
            switch (expenseAction) {
                case ADD_PAYMENT_TO_ACCOUNT:{
                    try {
                       // expenseManager.addPaymentToAccount(payment,account);
                        return 1;
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane, localization.getString("db_error"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return 0;
                    } catch (RuntimeException ex) {
                        JOptionPane.showMessageDialog(rootPane, localization.getString("db_error"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                    return 0;
                    }
                
                }
                default:
                    throw new IllegalStateException("Default reached in doInBackground() ExpenseSwingWorker");
            }
        }
        
        @Override
        protected void done() {
            try {
                if (0 == get()) {
                    expenseAction = null;
                    return;
                }
            } catch (ExecutionException ex) {
                expenseAction = null;
                JOptionPane.showMessageDialog(rootPane,
                        ex.getCause().getMessage(), ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InterruptedException ex) {
                expenseAction = null;
                throw new IllegalStateException(localization.getString("error"), ex);
            }
            switch (expenseAction) {
                case ADD_PAYMENT_TO_ACCOUNT:
                    ((JPaymentTableModel)jPaymentTableModel.getModel()).fireTableDataChanged();
                    jFrame3.dispose();
                    break;
            }
            expenseAction = null;
        }
         
     }
     
     
     
    /**
     * Creates new form application
     */
        public MainApplication() {
        initComponents();
        jButton7.setText(localization.getString("new_account"));
        jLabel2.setText(localization.getString("account_name"));
        jLabel4.setText(localization.getString("description"));
        jLabel5.setText(localization.getString("creation_date"));
        jButton8.setText(localization.getString("update_account"));
        jLabel6.setText(localization.getString("account_name"));
        jLabel7.setText(localization.getString("description"));
        jLabel8.setText(localization.getString("creation_date"));
        jLabel9.setText(localization.getString("description"));
        jLabel10.setText(localization.getString("amount"));
        jLabel11.setText(localization.getString("creation_date"));
        jLabel12.setText(localization.getString("successful_deleted_account"));
        jButton2.setText(localization.getString("close"));
        jLabel13.setText(localization.getString("successful_deleted_payment"));
        jButton6.setText(localization.getString("close"));
        jButton12.setText(localization.getString("edit_payment"));
        jLabel14.setText(localization.getString("description"));
        jLabel15.setText(localization.getString("amount"));
        jLabel16.setText(localization.getString("creation_date"));
        jLabel17.setText(localization.getString("choose_payment"));
        jButton13.setText(localization.getString("close"));
        jButton14.setText(localization.getString("close"));
        jLabel19.setText(localization.getString("non_valid_account"));
        jButton15.setText(localization.getString("close"));
        jButton16.setText(localization.getString("find_payments"));
        jLabel22.setText(localization.getString("from"));
        jLabel23.setText(localization.getString("to"));
        jLabel1.setText(localization.getString("actual_account"));
       
        jLabel3.setText(localization.getString("payments_in_account"));
        
        jButton5.setText(localization.getString("remove_payment"));

        jButton10.setText(localization.getString("edit_payment"));
        jMenu1.setText(localization.getString("file"));
        jButton11.setText(localization.getString("new_payment"));
        jButton9.setText(localization.getString("add_payment"));
        jMenuItem5.setText(localization.getString("exit"));
        jFrame3.setTitle(localization.getString("new_payment"));
        jFrame1.setTitle(localization.getString("new_account"));
        jFrame2.setTitle(localization.getString("edit_account"));
        jFrame4.setTitle(localization.getString("edit_payment"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame("Přidat účet");
        jButton7 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jFrame2 = new javax.swing.JFrame("Aktualizovat účet");
        jButton8 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jFrame3 = new javax.swing.JFrame("Přidání platby");
        jButton9 = new javax.swing.JButton();
        jTextField5 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jDialog1 = new javax.swing.JDialog();
        jLabel12 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jDialog2 = new javax.swing.JDialog();
        jLabel13 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jFrame4 = new javax.swing.JFrame("Aktualizace platby");
        jButton12 = new javax.swing.JButton();
        jTextField7 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jDialog3 = new javax.swing.JDialog();
        jLabel17 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jDialog4 = new javax.swing.JDialog();
        jLabel18 = new javax.swing.JLabel();
        jButton14 = new javax.swing.JButton();
        jDialog5 = new javax.swing.JDialog();
        jLabel19 = new javax.swing.JLabel();
        jButton15 = new javax.swing.JButton();
        jFrame5 = new javax.swing.JFrame("Vyhľadávanie podľa dátumu");
        jButton16 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        accountList = new javax.swing.JComboBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        jButton5 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPaymentTableModel = new javax.swing.JTable();
        jButton10 = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        accountList1 = new javax.swing.JComboBox();
        jButton17 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();

        jFrame1.setMinimumSize(new java.awt.Dimension(380, 240));

        jButton7.setText("Pridať účet");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel2.setText("Název účtu:");

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel4.setText("Popis:");

        jLabel5.setText("Dátum vytvoření: ");

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(jFrame1Layout.createSequentialGroup()
                        .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(45, 45, 45)
                        .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrame1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jButton7)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jFrame2.setMinimumSize(new java.awt.Dimension(380, 240));

        jButton8.setText("Aktualizovat účet");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jLabel6.setText("Název účtu:");

        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jLabel7.setText("Popis:");

        jLabel8.setText("Dátum vytvoření: ");

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(jFrame2Layout.createSequentialGroup()
                        .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(45, 45, 45)
                        .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton8))))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrame2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jButton8)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jFrame3.setMinimumSize(new java.awt.Dimension(380, 240));

        jButton9.setText("Přidat platbu");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jLabel9.setText("Popis platby:");

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel10.setText("Částka:");

        jLabel11.setText("Dátum vytvoření: ");

        javax.swing.GroupLayout jFrame3Layout = new javax.swing.GroupLayout(jFrame3.getContentPane());
        jFrame3.getContentPane().setLayout(jFrame3Layout);
        jFrame3Layout.setHorizontalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(jFrame3Layout.createSequentialGroup()
                        .addGroup(jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGroup(jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jFrame3Layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jFrame3Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        jFrame3Layout.setVerticalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrame3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(jButton9)
                .addContainerGap(86, Short.MAX_VALUE))
        );

        jDialog1.setMinimumSize(new java.awt.Dimension(350, 140));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Účet bol úspešne zmazaný");

        jButton2.setText("Zavrieť");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jLabel12))
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addComponent(jButton2)))
                .addContainerGap(97, Short.MAX_VALUE))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jDialog2.setMinimumSize(new java.awt.Dimension(350, 140));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("Platba bola úspešne zmazaná");

        jButton6.setText("Zavrieť");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog2Layout.createSequentialGroup()
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog2Layout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addComponent(jButton6))
                    .addGroup(jDialog2Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(jLabel13)))
                .addContainerGap(89, Short.MAX_VALUE))
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jFrame4.setMinimumSize(new java.awt.Dimension(380, 240));

        jButton12.setText("Upravit platbu");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jLabel14.setText("Popis platby:");

        jTextField8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField8ActionPerformed(evt);
            }
        });

        jLabel15.setText("Částka:");

        jLabel16.setText("Dátum vytvoření: ");

        javax.swing.GroupLayout jFrame4Layout = new javax.swing.GroupLayout(jFrame4.getContentPane());
        jFrame4.getContentPane().setLayout(jFrame4Layout);
        jFrame4Layout.setHorizontalGroup(
            jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addGroup(jFrame4Layout.createSequentialGroup()
                        .addGroup(jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15))
                        .addGroup(jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jFrame4Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jFrame4Layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        jFrame4Layout.setVerticalGroup(
            jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrame4Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addGap(18, 18, 18)
                .addComponent(jButton12)
                .addContainerGap(86, Short.MAX_VALUE))
        );

        jDialog3.setMinimumSize(new java.awt.Dimension(350, 140));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setText("Prosím vyberte platbu v tabuľke");

        jButton13.setText("Zavrieť");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog3Layout = new javax.swing.GroupLayout(jDialog3.getContentPane());
        jDialog3.getContentPane().setLayout(jDialog3Layout);
        jDialog3Layout.setHorizontalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog3Layout.createSequentialGroup()
                .addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog3Layout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addComponent(jButton13))
                    .addGroup(jDialog3Layout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addComponent(jLabel17)))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jDialog3Layout.setVerticalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton13)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jDialog4.setMinimumSize(new java.awt.Dimension(350, 140));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setText("Popis alebo hodnota platby nie je validná");

        jButton14.setText("Zavrieť");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog4Layout = new javax.swing.GroupLayout(jDialog4.getContentPane());
        jDialog4.getContentPane().setLayout(jDialog4Layout);
        jDialog4Layout.setHorizontalGroup(
            jDialog4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog4Layout.createSequentialGroup()
                .addGap(138, 138, 138)
                .addComponent(jButton14)
                .addContainerGap(145, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addGap(44, 44, 44))
        );
        jDialog4Layout.setVerticalGroup(
            jDialog4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton14)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jDialog5.setMinimumSize(new java.awt.Dimension(350, 140));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setText("Názov alebo popis účtu nie je validný");

        jButton15.setText("Zavrieť");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog5Layout = new javax.swing.GroupLayout(jDialog5.getContentPane());
        jDialog5.getContentPane().setLayout(jDialog5Layout);
        jDialog5Layout.setHorizontalGroup(
            jDialog5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog5Layout.createSequentialGroup()
                .addGap(138, 138, 138)
                .addComponent(jButton15)
                .addContainerGap(145, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel19)
                .addGap(58, 58, 58))
        );
        jDialog5Layout.setVerticalGroup(
            jDialog5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton15)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jFrame5.setMinimumSize(new java.awt.Dimension(350, 200));
        jFrame5.setResizable(false);

        jButton16.setText("Vyhľadať platby");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jLabel22.setText("Do:");

        jLabel23.setText("Od:");

        javax.swing.GroupLayout jFrame5Layout = new javax.swing.GroupLayout(jFrame5.getContentPane());
        jFrame5.getContentPane().setLayout(jFrame5Layout);
        jFrame5Layout.setHorizontalGroup(
            jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame5Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addGap(74, 74, 74)
                .addComponent(jButton16)
                .addContainerGap(114, Short.MAX_VALUE))
        );
        jFrame5Layout.setVerticalGroup(
            jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame5Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFrame5Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel22))
                    .addGroup(jFrame5Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(88, 88, 88)
                        .addComponent(jButton16)))
                .addContainerGap(63, Short.MAX_VALUE))
        );

        jMenuItem14.setText("jMenuItem14");

        jMenuItem16.setText("jMenuItem16");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ExpenseUlimate Manager");
        setMaximumSize(new java.awt.Dimension(795, 520));
        setMinimumSize(new java.awt.Dimension(795, 520));
        setPreferredSize(new java.awt.Dimension(795, 520));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(233, 233, 233));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextArea1.setRows(2);
        jTextArea1.setText("INFO");
        jTextArea1.setAlignmentX(1.0F);
        jTextArea1.setAlignmentY(1.0F);
        jTextArea1.setAutoscrolls(false);
        jScrollPane3.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 400, 760, 50));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(806, 396, 1, 8));

        jLabel1.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jLabel1.setText("Subject:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 20, 80, 30));

        jLabel3.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jLabel3.setText("Payments:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 70, 20));

        jButton11.setBackground(new java.awt.Color(255, 255, 255));
        jButton11.setText("generate HTML report");
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton11MouseClicked(evt);
            }
        });
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 360, 170, 30));

        List<Account> temp;
        temp = accountManager.findAllAccounts();
        String[] accountNames = new String[temp.size()];
        for(int i=0; i <temp.size(); i++){
            accountNames[i] = temp.get(i).getName();
        }
        accountList.setModel(new javax.swing.DefaultComboBoxModel(new Vector<Account>(accountManager.findAllAccounts())));
        accountList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                accountListItemStateChanged(evt);
            }
        });
        accountList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accountListActionPerformed(evt);
            }
        });
        getContentPane().add(accountList, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 20, 300, 30));
        getContentPane().add(filler1, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 20, 10, 30));

        jButton5.setText("odstraniť platbu");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 360, 180, 30));

        jPaymentTableModel.setModel(new JPaymentTableModel(localization));
        jPaymentTableModel.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jPaymentTableModel);
        jPaymentTableModel.getColumnModel().getColumn(0).setPreferredWidth(1);
        jPaymentTableModel.getColumnModel().getColumn(1).setPreferredWidth(500);
        jPaymentTableModel.getColumnModel().getColumn(2).setPreferredWidth(100);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 760, 270));

        jButton10.setText("upravit platbu");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 180, 30));

        jLabel21.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jLabel21.setText("Account:");
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 80, 30));

        List<Account> temp2;
        temp2 = accountManager.findAllAccounts();
        String[] accountNames2 = new String[temp2.size()];
        for(int i=0; i <temp2.size(); i++){
            accountNames2[i] = temp2.get(i).getName();
        }
        accountList1.setModel(new javax.swing.DefaultComboBoxModel(new Vector<Account>(accountManager.findAllAccounts())));
        accountList1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                accountList1ItemStateChanged(evt);
            }
        });
        accountList1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accountList1ActionPerformed(evt);
            }
        });
        getContentPane().add(accountList1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, 300, 30));

        jButton17.setBackground(new java.awt.Color(255, 255, 255));
        jButton17.setText("přidat novou platbu");
        jButton17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton17MouseClicked(evt);
            }
        });
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton17, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 360, 180, 30));

        jMenu1.setText("File");

        jMenuItem5.setAction(quitAction);
        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Exit");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        jMenu4.setText("Accounts");

        jMenuItem8.setText("Add");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem8);

        jMenuItem9.setText("Delete");
        jMenu4.add(jMenuItem9);

        jMenuItem10.setText("Edit");
        jMenu4.add(jMenuItem10);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("Subjects");

        jMenuItem11.setText("Add");
        jMenu5.add(jMenuItem11);

        jMenuItem12.setText("Delete");
        jMenu5.add(jMenuItem12);

        jMenuItem13.setText("Edit");
        jMenu5.add(jMenuItem13);

        jMenuBar1.add(jMenu5);

        jMenu2.setText("Currencies");

        jMenuItem1.setText("Add ");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem2.setText("Delete");
        jMenu2.add(jMenuItem2);

        jMenuItem3.setText("Edit");
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Categories");

        jMenuItem4.setText("Add");
        jMenu3.add(jMenuItem4);

        jMenuItem6.setText("Delete");
        jMenu3.add(jMenuItem6);

        jMenuItem7.setText("Edit");
        jMenu3.add(jMenuItem7);

        jMenuBar1.add(jMenu3);

        jMenu6.setText("XML");

        jMenuItem15.setText("Create report");
        jMenu6.add(jMenuItem15);

        jMenuBar1.add(jMenu6);

        jMenu7.setText("Help");

        jMenuItem18.setText("Help");
        jMenu7.add(jMenuItem18);

        jMenuItem17.setText("About");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem17);

        jMenuBar1.add(jMenu7);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

        
    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
       // datePicker_3.getModel().setDate(now.getYear(), now.getMonthValue()-1, now.getDayOfMonth());
        jTextField5.setText("");
        jTextField6.setText("0.00");
        jFrame3.setVisible(true);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void accountListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accountListActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_accountListActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        Long id = (Long)jPaymentTableModel.getModel().getValueAt(jPaymentTableModel.getSelectedRow(), 0);
        new PaymentSwingWorker(PaymentActions.REMOVE_PAYMENT, (Payment)paymentManager.getPaymentById(id)).execute();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void accountListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_accountListItemStateChanged
        changeAccount();     
    }//GEN-LAST:event_accountListItemStateChanged

    private void jButton11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton11MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11MouseClicked

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed
    
    public static LocalDate getLocalDateInPicker(JDatePicker picker) {
    DateModel model = picker.getModel();
    if (!model.isSelected()) {
        return null;
    }
    model.setMonth(model.getMonth()+1);
    if (model.getMonth() == 0){
       return LocalDate.of(model.getYear(), Month.of(12), model.getDay()); 
    }
    return LocalDate.of(model.getYear(), Month.of(model.getMonth()), model.getDay());
    }
    
    public void changeAccount(){
        
        accountList.repaint();
        //jPaymentTableModel.setModel(new PaymentTableModel());
        jPaymentTableModel.getColumnModel().getColumn(0).setPreferredWidth(10);
        jPaymentTableModel.getColumnModel().getColumn(1).setPreferredWidth(500);
        jPaymentTableModel.getColumnModel().getColumn(2).setPreferredWidth(100);
        jPaymentTableModel.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        ((JPaymentTableModel)jPaymentTableModel.getModel()).fireTableDataChanged();
    
        jPaymentTableModel.repaint();

        String output = "";
       // output += "ID: " + ((Account)accountList.getSelectedItem()).getId() + " | ";
        output += localization.getString("description") + ": " + ((Account)accountList.getSelectedItem()).getDescription() + " \n";
        output += localization.getString("creation_date") + ": " + ((Account)accountList.getSelectedItem()).getCreationDate() + "  ";
        Account acc = (Account)accountList.getSelectedItem();
      /*  
        try {
            output += "Aktuální stav: " + expenseManager.getAccountBalance(acc);
        } catch (ServiceFailureException | SQLException ex) {
            //Logger.getLogger(MainApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
      */
        jTextArea1.setText(output);
        jTextArea1.repaint();
    }
    
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
       // LocalDate date = getLocalDateInPicker(datePicker);
        String name = jTextField1.getText();
        String desc = jTextField2.getText();        
        if((name.equals("")) || (desc.equals(""))){
           jDialog5.setVisible(true);
        } else { 
        Account account = new Account();
       // account.setCreationDate(date);
        account.setName(name);
        account.setDescription(desc);
       // System.out.println(date);
        new AccountSwingWorker(AccountActions.ADD_ACCOUNT, account).execute();
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        //LocalDate date = getLocalDateInPicker(datePicker_2);
        String name = jTextField3.getText();
        String desc = jTextField4.getText();        
        if((name.equals("")) || (desc.equals(""))){
           jDialog5.setVisible(true);
        } else { 
        Account account = new Account();
       // account.setCreationDate(date);
        account.setName(name);
        account.setDescription(desc);
        account.setId(((Account)accountList.getSelectedItem()).getId());
        new AccountSwingWorker(AccountActions.EDIT_ACCOUNT, account).execute();
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
    
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
      //  LocalDate date = getLocalDateInPicker(datePicker_3);
        String desc = jTextField5.getText();
        BigDecimal amount = null;
        try{
         amount = new BigDecimal(jTextField6.getText().replaceAll(",","."));
        }
        catch(NumberFormatException ex){
            jDialog4.setVisible(true);  
        }
        
        if (desc.equals("") || (amount == null)){
            jDialog4.setVisible(true);  
        } else {
        Payment payment = new Payment();
      //  payment.setDate(date);
        payment.setDescription(desc);
        payment.setAmount(amount);
        new PaymentSwingWorker(PaymentActions.ADD_PAYMENT, payment).execute();
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
       // datePicker_4.getModel().setDate(now.getYear(), now.getMonthValue()-1, now.getDayOfMonth());

        Long id = -1L;
        try{
            id = (Long)jPaymentTableModel.getModel().getValueAt(jPaymentTableModel.getSelectedRow(), 0);
        }
        catch (RuntimeException ex){
            jDialog3.setVisible(true);            
        }
        
        if (id == -1L){            
        } else {
        Payment payment = paymentManager.getPaymentById(id);
        String name = payment.getDescription();
        jTextField7.setText(name);
        name = payment.getAmount().toString();
        jTextField8.setText(name);
        jFrame4.setVisible(true);
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jDialog1.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        jDialog2.dispose();  // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        Long id = (Long)jPaymentTableModel.getModel().getValueAt(jPaymentTableModel.getSelectedRow(), 0);
    //    LocalDate date = getLocalDateInPicker(datePicker_4);
        String desc = jTextField7.getText();      
        
        BigDecimal amount = null;
        try{
         amount = new BigDecimal(jTextField8.getText().replaceAll(",","."));
        }
        catch(NumberFormatException ex){
            jDialog4.setVisible(true);  
        }
        
        if (desc.equals("") || (amount == null)){
            jDialog4.setVisible(true);  
        } else {
        Payment payment = new Payment();
        payment.setId(id);
    //    payment.setDate(date);
        payment.setDescription(desc);
        payment.setAmount(amount);
        new PaymentSwingWorker(PaymentActions.EDIT_PAYMENT, payment).execute();
        }       
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField8ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        jDialog3.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        jDialog4.dispose();         // TODO add your handling code here:
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        jDialog5.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
 /*    //   datePicker_2.getModel().setDate(now.getYear(), now.getMonthValue()-1, now.getDayOfMonth());
     //   datePicker_3.getModel().setDate(now.getYear(), now.getMonthValue()-1, now.getDayOfMonth());
      //  LocalDate from = getLocalDateInPicker(datePicker_2);
      //  LocalDate to = getLocalDateInPicker(datePicker_3);
    //    List<Payment> payments = expenseManager.getAllPaymentsForPeriod(((Account)accountList.getSelectedItem()),from,to);
        jPaymentTableModel.removeAll();
        System.out.println(localization.getString("number") + ": "+payments.size());
        for(int i=0; i<payments.size(); i++){
            jPaymentTableModel.getModel().setValueAt(payments.get(i).getId(), i, 0);
        }
        for(int i=0; i<payments.size(); i++){
            jPaymentTableModel.getModel().setValueAt(payments.get(i).getDescription(), i, 1);
        }
        for(int i=0; i<payments.size(); i++){
            jPaymentTableModel.getModel().setValueAt(payments.get(i).getDate(), i, 2);
        }
        for(int i=0; i<payments.size(); i++){
            jPaymentTableModel.getModel().setValueAt(payments.get(i).getAmount(), i, 3);
        }        
        ((JPaymentTableModel)jPaymentTableModel.getModel()).fireTableDataChanged();
        jFrame5.dispose();      */  
    }//GEN-LAST:event_jButton16ActionPerformed

    private void accountList1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_accountList1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_accountList1ItemStateChanged

    private void accountList1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accountList1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_accountList1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jButton17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton17MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton17MouseClicked

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton17ActionPerformed

    private class QuitAction extends AbstractAction {

    public void actionPerformed(ActionEvent e) {
            if (jButton11.isEnabled()) {
                processWindowEvent(new WindowEvent(MAIN_APPLICATION, WindowEvent.WINDOW_CLOSING));
            }
        }
    }
    
    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     */
    public static void main(String args[]) throws SQLException, IOException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */

     //   initalize("config.txt");  
        
        try {                   
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {            
            public void run() {                
                MAIN_APPLICATION = new MainApplication();
                MAIN_APPLICATION.setVisible(true);
                MAIN_APPLICATION.changeAccount();
            }
        });
    }
    private static MainApplication MAIN_APPLICATION;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JComboBox accountList;
    public javax.swing.JComboBox accountList1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JDialog jDialog3;
    private javax.swing.JDialog jDialog4;
    private javax.swing.JDialog jDialog5;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JFrame jFrame3;
    private javax.swing.JFrame jFrame4;
    private javax.swing.JFrame jFrame5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    public javax.swing.JTable jPaymentTableModel;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    public javax.swing.JTextField jTextField1;
    public javax.swing.JTextField jTextField2;
    public javax.swing.JTextField jTextField3;
    public javax.swing.JTextField jTextField4;
    public javax.swing.JTextField jTextField5;
    public javax.swing.JTextField jTextField6;
    public javax.swing.JTextField jTextField7;
    public javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Desktop;
import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import project.Account;
import project.AccountManagerImpl;
import project.Category;
import project.CategoryManagerImpl;
import project.Currency;
import project.CurrencyManagerImpl;
import project.ExpenseManagerImpl;
import project.Payment;
import project.PaymentManagerImpl;
import project.Subject;
import project.SubjectManagerImpl;

/**
 *
 * @author rk
 */
public class MainFrame extends javax.swing.JFrame {

    
   // private final ResourceBundle localization = ResourceBundle.getBundle("localization",new Locale("SK")); 
    private final ResourceBundle localization = ResourceBundle.getBundle("localization",Locale.US); 
    private static final AccountManagerImpl accountManager = new AccountManagerImpl();
    private static final PaymentManagerImpl paymentManager = new PaymentManagerImpl();
    private static final ExpenseManagerImpl expenseManager = new ExpenseManagerImpl();
    private static final CurrencyManagerImpl currencyManager = new CurrencyManagerImpl();
    private static final SubjectManagerImpl subjectManager = new SubjectManagerImpl();
    private static final CategoryManagerImpl categoryManager = new CategoryManagerImpl(); 
    private List<Payment> toTable = new ArrayList<>();
    private BufferedWriter output;
    
    

    //private JPaymentTableModel paymentTableModel;
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        
    }
       
    private enum AccountActions {
        ADD_ACCOUNT, EDIT_ACCOUNT, REMOVE_ACCOUNT
    };
    
    private class AccountSwingWorker extends SwingWorker<List<Account>, Void>{
     
        private AccountActions accountAction;
        private final Account account;
         
        
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
                        accountList.addItem(account);
                        jComboBox1.addItem(account);
                        jComboBox4.addItem(account);
                        jComboBox9.addItem(account);

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
                    updateAccountLists();
                    jWantDeleteAccount.dispose();
                    jDeleteAccount.setVisible(false);
                    break;
                case ADD_ACCOUNT:
                    jAddAccount.dispose(); 
                    break;
                case EDIT_ACCOUNT:
                    updateAccountLists();
                    update();
                    jUpdateAccount.setVisible(false);
                    break;
            }        
            accountAction = null;
        }
         
     }
    
    private enum CategoryActions{
        ADD_CATEGORY, EDIT_CATEGORY, REMOVE_CATEGORY
    };
    
    private class CategorySwingWorker extends SwingWorker<List<Category>, Void>{
     
        private CategoryActions categoryAction;
        private final Category category;
         
        
        public CategorySwingWorker(CategoryActions action, Category category) {
            this.categoryAction = action;
            this.category = category;
        }


        @Override
        protected List<Category> doInBackground() throws Exception {
            switch (categoryAction) {
                case ADD_CATEGORY:{
                    try {
                        categoryManager.createCategory(category);
                        jComboBox3.addItem(category);
                        jComboBox6.addItem(category);
                        jComboBox15.addItem(category);        
                        jComboBox16.addItem(category);

                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_add_category"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    return categoryManager.findAllCategory();
                    }
                
                case EDIT_CATEGORY:
                    try {
                        categoryManager.updateCategory(category);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_update_category"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    return categoryManager.findAllCategory();
                
                
                case REMOVE_CATEGORY:
                    try {
                        categoryManager.deleteCategory(category.getId());
                        jComboBox3.removeItem(category);
                        jComboBox6.removeItem(category);
                        jComboBox15.removeItem(category);
                        jComboBox16.removeItem(category);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_remove_category"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                    }
                    return categoryManager.findAllCategory();
                
                default:
                    throw new IllegalStateException("Default reached in doInBackground() CategorySwingWorker");
            }
        }
        
        @Override
        protected void done() {
            try {
                if (null == get()) {
                    categoryAction = null;
                    return;
                }
            } catch (ExecutionException ex) {
                categoryAction = null;
                JOptionPane.showMessageDialog(rootPane,
                        ex.getCause().getMessage(), ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InterruptedException ex) {
                categoryAction = null;
                throw new IllegalStateException(localization.getString("interrupted"), ex);
            }
            switch (categoryAction) {
                case REMOVE_CATEGORY:
                    jDeleteCategory.setVisible(false);
                    break;
                case ADD_CATEGORY:
                    jAddCategory.setVisible(false); 
                    break;
                case EDIT_CATEGORY:
                    Vector vec = new Vector<>(categoryManager.findAllCategory());
                    jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(vec));
                    jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(vec));
                    jComboBox15.setModel(new javax.swing.DefaultComboBoxModel(vec));
                    jComboBox16.setModel(new javax.swing.DefaultComboBoxModel(vec));
                    jUpdateCategory.setVisible(false);
                    break;
            }        
            categoryAction = null;
        }
         
     }

    private enum CurrencyActions{
        ADD_CURRENCY, REMOVE_CURRENCY
    };
    
    private class CurrencySwingWorker extends SwingWorker<List<Currency>, Void>{
     
        private CurrencyActions currencyAction;
        private final Currency currency;
         
        
        public CurrencySwingWorker(CurrencyActions action, Currency Currency) {
            this.currencyAction = action;
            this.currency = Currency;
        }


        @Override
        protected List<Currency> doInBackground() throws Exception {
            switch (currencyAction) {
                case ADD_CURRENCY:{
                    try {
                        currencyManager.createCurrency(currency);
                        jComboBox8.addItem(currency);
                        jComboBox11.addItem(currency);
                        jComboBox12.addItem(currency);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_add_currency"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    return currencyManager.findAllCurrency();
                    }
                
                case REMOVE_CURRENCY:
                    try {
                        currencyManager.deleteCurrency(currency.getCcy());
                        jComboBox8.removeItem(currency);
                        jComboBox11.removeItem(currency);
                        jComboBox12.removeItem(currency);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_remove_currency"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                    }
                    return currencyManager.findAllCurrency();
                
                default:
                    throw new IllegalStateException("Default reached in doInBackground() CurrencySwingWorker");
            }
        }
        
        @Override
        protected void done() {
            try {
                if (null == get()) {
                    currencyAction = null;
                    return;
                }
            } catch (ExecutionException ex) {
                currencyAction = null;
                JOptionPane.showMessageDialog(rootPane,
                        ex.getCause().getMessage(), ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InterruptedException ex) {
                currencyAction = null;
                throw new IllegalStateException(localization.getString("interrupted"), ex);
            }
            switch (currencyAction) {
                case REMOVE_CURRENCY:
                    jDeleteCurrency.setVisible(false);
                    break;
                case ADD_CURRENCY:
                    jAddCurrency.dispose(); 
                    break;
            }        
            currencyAction = null;
        }
         
     }
    
    
    private enum PaymentActions{
        ADD_PAYMENT, EDIT_PAYMENT, REMOVE_PAYMENT
    };
    
    private class PaymentSwingWorker extends SwingWorker<List<Payment>, Void>{
     
        private PaymentActions paymentAction;
        private final Payment payment;
         
        
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
                    return paymentManager.findAllPayments();
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
                case ADD_PAYMENT:
                    update();
                    jAddPayment.dispose(); 
                    break;
                case EDIT_PAYMENT:
                    update();
                    jUpdatePayment.dispose();
                    break;
            }        
            paymentAction = null;
        }
         
     }
    
    private enum SubjectActions{
        ADD_SUBJECT, EDIT_SUBJECT, REMOVE_SUBJECT
    };
    
    private class SubjectSwingWorker extends SwingWorker<List<Subject>, Void>{
     
        private SubjectActions subjectAction;
        private final Subject subject;
         
        
        public SubjectSwingWorker(SubjectActions action, Subject subject) {
            this.subjectAction = action;
            this.subject = subject;
        }


        @Override
        protected List<Subject> doInBackground() throws Exception {
            switch (subjectAction) {
                case ADD_SUBJECT:{
                    try {
                        subjectManager.createSubject(subject);

                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_add_subject"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    return subjectManager.findAllSubjects();
                    }
                
                case EDIT_SUBJECT:
                    try {
                        subjectManager.updateSubject(subject);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_update_subject"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    return subjectManager.findAllSubjects();
                
                
                case REMOVE_SUBJECT:
                    try {
                        subjectManager.deleteSubject(subject.getId());
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(rootPane,
                                localization.getString("cannot_remove_subject"),
                                localization.getString("error"), JOptionPane.ERROR_MESSAGE);
                    }
                    return subjectManager.findAllSubjects();
                
                default:
                    throw new IllegalStateException("Default reached in doInBackground() SubjectSwingWorker");
            }
        }
        
        @Override
        protected void done() {
            try {
                if (null == get()) {
                    subjectAction = null;
                    return;
                }
            } catch (ExecutionException ex) {
                subjectAction = null;
                JOptionPane.showMessageDialog(rootPane,
                        ex.getCause().getMessage(), ex.getMessage(), JOptionPane.ERROR_MESSAGE);
                return;
            } catch (InterruptedException ex) {
                subjectAction = null;
                throw new IllegalStateException(localization.getString("interrupted"), ex);
            }
            switch (subjectAction) {
                case REMOVE_SUBJECT:
                    jDeleteSubject.setVisible(false);
                    updateSubjectLists(); 
                    break;
                case ADD_SUBJECT:
                    updateSubjectLists();       
                    jAddSubject.setVisible(false); 
                    break;
                case EDIT_SUBJECT:
                    updateSubjectLists();            
                    jUpdateSubject.setVisible(false);
                    break;
            }        
            subjectAction = null;
        }
         
     }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jAddPayment = new javax.swing.JFrame();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jButton6 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jInvalidPayment = new javax.swing.JDialog();
        jLabel10 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jLabel62 = new javax.swing.JLabel();
        jUpdatePayment = new javax.swing.JFrame();
        jComboBox4 = new javax.swing.JComboBox();
        jComboBox5 = new javax.swing.JComboBox();
        jComboBox6 = new javax.swing.JComboBox();
        jDateChooser4 = new com.toedter.calendar.JDateChooser();
        jButton8 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jAddAccount = new javax.swing.JFrame();
        jComboBox8 = new javax.swing.JComboBox();
        jDateChooser5 = new com.toedter.calendar.JDateChooser();
        jButton9 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jAddCurrency = new javax.swing.JFrame();
        jButton10 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jWantDeleteAccount = new javax.swing.JDialog();
        jLabel21 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jLabel63 = new javax.swing.JLabel();
        jDeleteAccount = new javax.swing.JFrame();
        jComboBox9 = new javax.swing.JComboBox();
        jButton14 = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jUpdateAccount = new javax.swing.JFrame();
        jComboBox10 = new javax.swing.JComboBox();
        jDateChooser6 = new com.toedter.calendar.JDateChooser();
        jButton15 = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jComboBox11 = new javax.swing.JComboBox();
        jLabel51 = new javax.swing.JLabel();
        jDeleteCurrency = new javax.swing.JFrame();
        jComboBox12 = new javax.swing.JComboBox();
        jButton16 = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jUsedCurrency = new javax.swing.JDialog();
        jLabel31 = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        jLabel65 = new javax.swing.JLabel();
        jAddSubject = new javax.swing.JFrame();
        jButton18 = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        jDeleteSubject = new javax.swing.JFrame();
        jComboBox13 = new javax.swing.JComboBox();
        jButton19 = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jUsedSubject = new javax.swing.JDialog();
        jLabel34 = new javax.swing.JLabel();
        jButton20 = new javax.swing.JButton();
        jLabel66 = new javax.swing.JLabel();
        jUpdateSubject = new javax.swing.JFrame();
        jComboBox14 = new javax.swing.JComboBox();
        jButton21 = new javax.swing.JButton();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        jAddCategory = new javax.swing.JFrame();
        jButton22 = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        jUpdateCategory = new javax.swing.JFrame();
        jComboBox15 = new javax.swing.JComboBox();
        jButton23 = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        jDeleteCategory = new javax.swing.JFrame();
        jComboBox16 = new javax.swing.JComboBox();
        jButton24 = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jUsedCategory = new javax.swing.JDialog();
        jLabel41 = new javax.swing.JLabel();
        jButton25 = new javax.swing.JButton();
        jLabel67 = new javax.swing.JLabel();
        jChoosePayment = new javax.swing.JDialog();
        jLabel49 = new javax.swing.JLabel();
        jButton26 = new javax.swing.JButton();
        jLabel68 = new javax.swing.JLabel();
        jAbout = new javax.swing.JFrame();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton27 = new javax.swing.JButton();
        accountList = new javax.swing.JComboBox();
        subjectList = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        paymentTableModel = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jDateChooser3 = new com.toedter.calendar.JDateChooser();
        jButton5 = new javax.swing.JButton();
        try {
            Image img = ImageIO.read(new File("src/main/java/GUI/img/refresh.png"));
            jButton5.setIcon(new ImageIcon(img));
        } catch (IOException ex) {
        }
        jCheckBox1 = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();

        jAddPayment.setTitle(localization.getString("addPayment"));
        jAddPayment.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jAddPayment.setMinimumSize(new java.awt.Dimension(550, 300));
        jAddPayment.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new Vector<Account>(accountManager.findAllAccounts())));
        jAddPayment.getContentPane().add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 50, 320, 30));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new Vector<Subject>(subjectManager.findAllSubjects())));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });
        jAddPayment.getContentPane().add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 90, 320, 30));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new Vector<Category>(categoryManager.findAllCategory())));
        jAddPayment.getContentPane().add(jComboBox3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, 320, 30));
        jAddPayment.getContentPane().add(jDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 170, 170, 30));

        jButton6.setText(localization.getString("addPayment"));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jAddPayment.getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, 200, 30));

        jLabel4.setText(localization.getString("account"));
        jAddPayment.getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, -1, -1));

        jLabel5.setText(localization.getString("subject"));
        jAddPayment.getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, -1, -1));

        jLabel6.setText(localization.getString("category"));
        jAddPayment.getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, -1, -1));

        jLabel7.setText(localization.getString("date"));
        jAddPayment.getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 180, -1, -1));

        jLabel8.setText(localization.getString("amount"));
        jAddPayment.getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 180, -1, -1));

        jTextField1.setText("0.00");
        jAddPayment.getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 170, 90, 30));

        jLabel9.setText(localization.getString("description"));
        jAddPayment.getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));
        jAddPayment.getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 320, 30));

        jLabel42.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/payment.png"));
        jAddPayment.getContentPane().add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 80, 90));

        jInvalidPayment.setMinimumSize(new java.awt.Dimension(400, 150));
        jInvalidPayment.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setText(localization.getString("non_valid_payment"));
        jInvalidPayment.getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 380, 30));

        jButton7.setText(localization.getString("close"));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jInvalidPayment.getContentPane().add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 70, 190, -1));

        jLabel62.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/warning.png"));
        jInvalidPayment.getContentPane().add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 80, 90));

        jUpdatePayment.setTitle(localization.getString("updatePayment"));
        jUpdatePayment.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jUpdatePayment.setMinimumSize(new java.awt.Dimension(550, 300));
        jUpdatePayment.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new Vector<Account>(accountManager.findAllAccounts())));
        jUpdatePayment.getContentPane().add(jComboBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 50, 320, 30));

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new Vector<Subject>(subjectManager.findAllSubjects())));
        jUpdatePayment.getContentPane().add(jComboBox5, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 90, 320, 30));

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new Vector<Category>(categoryManager.findAllCategory())));
        jUpdatePayment.getContentPane().add(jComboBox6, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, 320, 30));
        jUpdatePayment.getContentPane().add(jDateChooser4, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 170, 170, 30));

        jButton8.setText(localization.getString("updatePayment"));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jUpdatePayment.getContentPane().add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, 200, 30));

        jLabel11.setText(localization.getString("account"));
        jUpdatePayment.getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, -1, -1));

        jLabel12.setText(localization.getString("subject"));
        jUpdatePayment.getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, -1, -1));

        jLabel13.setText(localization.getString("category"));
        jUpdatePayment.getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, -1, -1));

        jLabel14.setText(localization.getString("date"));
        jUpdatePayment.getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 180, -1, -1));

        jLabel15.setText(localization.getString("amount"));
        jUpdatePayment.getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 180, -1, -1));

        jTextField3.setText("0.00");
        jUpdatePayment.getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 170, 90, 30));

        jLabel16.setText(localization.getString("description"));
        jUpdatePayment.getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));
        jUpdatePayment.getContentPane().add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 320, 30));

        jLabel44.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/update.png"));
        jUpdatePayment.getContentPane().add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 80, 90));

        jAddAccount.setTitle(localization.getString("createAccount"));
        jAddAccount.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jAddAccount.setMinimumSize(new java.awt.Dimension(550, 270));
        jAddAccount.setResizable(false);
        jAddAccount.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new Vector<Currency>(currencyManager.findAllCurrency())));
        jAddAccount.getContentPane().add(jComboBox8, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 90, 320, 30));
        jAddAccount.getContentPane().add(jDateChooser5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 130, 300, 30));

        jButton9.setText(localization.getString("createAccount"));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jAddAccount.getContentPane().add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 190, 200, 30));

        jLabel17.setText(localization.getString("description"));
        jAddAccount.getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, -1, -1));

        jLabel18.setText(localization.getString("currency"));
        jAddAccount.getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, -1, -1));

        jLabel20.setText(localization.getString("creationDate"));
        jAddAccount.getContentPane().add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, -1, -1));

        jLabel22.setText(localization.getString("name"));
        jAddAccount.getContentPane().add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));
        jAddAccount.getContentPane().add(jTextField6, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 50, 320, 30));
        jAddAccount.getContentPane().add(jTextField7, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 320, 30));

        jLabel45.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/account.png"));
        jAddAccount.getContentPane().add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 80, 90));

        jAddCurrency.setTitle(localization.getString("addCurrency"));
        jAddCurrency.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jAddCurrency.setMinimumSize(new java.awt.Dimension(500, 200));
        jAddCurrency.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton10.setText(localization.getString("addCurrency"));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jAddCurrency.getContentPane().add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 110, 200, 30));

        jLabel19.setText(localization.getString("code"));
        jAddCurrency.getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, -1, -1));

        jLabel24.setText(localization.getString("name"));
        jAddCurrency.getContentPane().add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));
        jAddCurrency.getContentPane().add(jTextField8, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 50, 270, 30));
        jAddCurrency.getContentPane().add(jTextField9, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 270, 30));

        jLabel46.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/currency.png"));
        jAddCurrency.getContentPane().add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 80, 90));

        jWantDeleteAccount.setMinimumSize(new java.awt.Dimension(550, 150));
        jWantDeleteAccount.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel21.setText(localization.getString("wantDeleteAccount"));
        jWantDeleteAccount.getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 380, 30));

        jButton11.setText(localization.getString("close"));
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jWantDeleteAccount.getContentPane().add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 70, 120, -1));

        jButton12.setText(localization.getString("yes"));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jWantDeleteAccount.getContentPane().add(jButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 120, -1));

        jButton13.setText(localization.getString("no"));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        jWantDeleteAccount.getContentPane().add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 70, 120, -1));

        jLabel63.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/warning.png"));
        jWantDeleteAccount.getContentPane().add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 80, 90));

        jDeleteAccount.setTitle(localization.getString("deleteAccount"));
        jDeleteAccount.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jDeleteAccount.setMinimumSize(new java.awt.Dimension(550, 150));
        jDeleteAccount.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new Vector<Account>(accountManager.findAllAccounts())));
        jDeleteAccount.getContentPane().add(jComboBox9, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 320, 30));

        jButton14.setText(localization.getString("deleteAccount"));
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        jDeleteAccount.getContentPane().add(jButton14, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 200, 30));

        jLabel25.setText(localization.getString("account"));
        jDeleteAccount.getContentPane().add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, -1, -1));

        jLabel48.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/remove.png"));
        jDeleteAccount.getContentPane().add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 80, 90));

        jUpdateAccount.setTitle(localization.getString("updateAccount"));
        jUpdateAccount.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jUpdateAccount.setMinimumSize(new java.awt.Dimension(550, 350));
        jUpdateAccount.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel(new Vector<>(accountManager.findAllAccounts())));
        jComboBox10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox10ActionPerformed(evt);
            }
        });
        jUpdateAccount.getContentPane().add(jComboBox10, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 320, 30));
        jUpdateAccount.getContentPane().add(jDateChooser6, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, 300, 30));

        jButton15.setText(localization.getString("updateAccount"));
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });
        jUpdateAccount.getContentPane().add(jButton15, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 250, 200, 30));

        jLabel23.setText(localization.getString("description"));
        jUpdateAccount.getContentPane().add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 120, -1, -1));

        jLabel26.setText(localization.getString("currency"));
        jUpdateAccount.getContentPane().add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 160, -1, -1));

        jLabel27.setText(localization.getString("creationDate"));
        jUpdateAccount.getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 200, -1, -1));

        jLabel28.setText(localization.getString("account"));
        jUpdateAccount.getContentPane().add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, -1, -1));
        jUpdateAccount.getContentPane().add(jTextField10, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, 320, 30));
        jUpdateAccount.getContentPane().add(jTextField11, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 320, 30));

        jLabel29.setText(localization.getString("name"));
        jUpdateAccount.getContentPane().add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, -1, -1));

        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel(new Vector<Currency>(currencyManager.findAllCurrency())));
        jUpdateAccount.getContentPane().add(jComboBox11, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 150, 320, 30));

        jLabel51.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/update.png"));
        jUpdateAccount.getContentPane().add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 80, 90));

        jDeleteCurrency.setTitle(localization.getString("deleteCurrency"));
        jDeleteCurrency.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jDeleteCurrency.setMinimumSize(new java.awt.Dimension(550, 150));
        jDeleteCurrency.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel(new Vector<>(currencyManager.findAllCurrency())));
        jDeleteCurrency.getContentPane().add(jComboBox12, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 320, 30));

        jButton16.setText(localization.getString("deleteCurrency"));
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        jDeleteCurrency.getContentPane().add(jButton16, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 200, 30));

        jLabel30.setText(localization.getString("currency"));
        jDeleteCurrency.getContentPane().add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, -1, -1));

        jLabel53.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/remove.png"));
        jDeleteCurrency.getContentPane().add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 80, 90));

        jUsedCurrency.setMinimumSize(new java.awt.Dimension(600, 150));
        jUsedCurrency.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel31.setText(localization.getString("usedCurrency"));
        jUsedCurrency.getContentPane().add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 510, 30));

        jButton17.setText(localization.getString("close"));
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });
        jUsedCurrency.getContentPane().add(jButton17, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 60, 190, -1));

        jLabel65.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/warning.png"));
        jUsedCurrency.getContentPane().add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 80, 90));

        jAddSubject.setTitle(localization.getString("addSubject"));
        jAddSubject.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jAddSubject.setMinimumSize(new java.awt.Dimension(550, 150));
        jAddSubject.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton18.setText(localization.getString("addSubject"));
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });
        jAddSubject.getContentPane().add(jButton18, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 60, 200, 30));

        jLabel33.setText(localization.getString("name"));
        jAddSubject.getContentPane().add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));
        jAddSubject.getContentPane().add(jTextField13, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 270, 30));

        jLabel54.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/subject.png"));
        jAddSubject.getContentPane().add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 80, 90));

        jDeleteSubject.setTitle(localization.getString("deleteSubject"));
        jDeleteSubject.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jDeleteSubject.setMinimumSize(new java.awt.Dimension(550, 150));
        jDeleteSubject.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel(new Vector<>(subjectManager.findAllSubjects())));
        jDeleteSubject.getContentPane().add(jComboBox13, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 320, 30));

        jButton19.setText(localization.getString("deleteSubject"));
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });
        jDeleteSubject.getContentPane().add(jButton19, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 200, 30));

        jLabel32.setText(localization.getString("subject"));
        jDeleteSubject.getContentPane().add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, -1, -1));

        jLabel55.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/remove.png"));
        jDeleteSubject.getContentPane().add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 80, 90));

        jUsedSubject.setMinimumSize(new java.awt.Dimension(600, 150));
        jUsedSubject.setModal(true);
        jUsedSubject.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel34.setText(localization.getString("usedSubject"));
        jUsedSubject.getContentPane().add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, 510, 30));

        jButton20.setText(localization.getString("close"));
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });
        jUsedSubject.getContentPane().add(jButton20, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 70, 190, -1));

        jLabel66.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/warning.png"));
        jUsedSubject.getContentPane().add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 80, 90));

        jUpdateSubject.setTitle(localization.getString("updateSubject"));
        jUpdateSubject.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jUpdateSubject.setMinimumSize(new java.awt.Dimension(550, 220));
        jUpdateSubject.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox14.setModel(new javax.swing.DefaultComboBoxModel(new Vector<>(subjectManager.findAllSubjects())));
        jUpdateSubject.getContentPane().add(jComboBox14, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 320, 30));

        jButton21.setText(localization.getString("updateSubject"));
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });
        jUpdateSubject.getContentPane().add(jButton21, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 120, 200, 30));

        jLabel35.setText(localization.getString("name"));
        jUpdateSubject.getContentPane().add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, -1, -1));

        jLabel36.setText(localization.getString("subject"));
        jUpdateSubject.getContentPane().add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, -1, -1));
        jUpdateSubject.getContentPane().add(jTextField5, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 320, 30));

        jLabel56.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/update.png"));
        jUpdateSubject.getContentPane().add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 80, 90));

        jAddCategory.setTitle(localization.getString("addCategory"));
        jAddCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jAddCategory.setMinimumSize(new java.awt.Dimension(550, 150));
        jAddCategory.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton22.setText(localization.getString("addCategory"));
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });
        jAddCategory.getContentPane().add(jButton22, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 60, 200, 30));

        jLabel37.setText(localization.getString("category"));
        jAddCategory.getContentPane().add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));

        jTextField14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField14ActionPerformed(evt);
            }
        });
        jAddCategory.getContentPane().add(jTextField14, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 330, 30));

        jLabel57.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/category.png"));
        jAddCategory.getContentPane().add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 80, 90));

        jUpdateCategory.setTitle(localization.getString("updateCategory"));
        jUpdateCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jUpdateCategory.setMinimumSize(new java.awt.Dimension(550, 200));
        jUpdateCategory.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox15.setModel(new javax.swing.DefaultComboBoxModel(new Vector<>(categoryManager.findAllCategory())));
        jUpdateCategory.getContentPane().add(jComboBox15, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 320, 30));

        jButton23.setText(localization.getString("updateSubject"));
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });
        jUpdateCategory.getContentPane().add(jButton23, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 120, 200, 30));

        jLabel38.setText(localization.getString("name"));
        jUpdateCategory.getContentPane().add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 80, -1, -1));

        jLabel39.setText(localization.getString("category"));
        jUpdateCategory.getContentPane().add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, -1, -1));
        jUpdateCategory.getContentPane().add(jTextField12, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 320, 30));

        jLabel60.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/update.png"));
        jUpdateCategory.getContentPane().add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 80, 90));

        jDeleteCategory.setTitle(localization.getString("deleteCategory"));
        jDeleteCategory.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jDeleteCategory.setMinimumSize(new java.awt.Dimension(550, 150));
        jDeleteCategory.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox16.setModel(new javax.swing.DefaultComboBoxModel(new Vector<>(categoryManager.findAllCategory())));
        jDeleteCategory.getContentPane().add(jComboBox16, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, 320, 30));

        jButton24.setText(localization.getString("deleteCategory"));
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });
        jDeleteCategory.getContentPane().add(jButton24, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 200, 30));

        jLabel40.setText(localization.getString("category"));
        jDeleteCategory.getContentPane().add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, -1, -1));

        jLabel61.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/remove.png"));
        jDeleteCategory.getContentPane().add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 80, 90));

        jUsedCategory.setLocationByPlatform(true);
        jUsedCategory.setMinimumSize(new java.awt.Dimension(600, 150));
        jUsedCategory.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel41.setText(localization.getString("usedCategory"));
        jUsedCategory.getContentPane().add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 510, 30));

        jButton25.setText(localization.getString("close"));
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });
        jUsedCategory.getContentPane().add(jButton25, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 80, 190, -1));

        jLabel67.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/warning.png"));
        jUsedCategory.getContentPane().add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 80, 90));

        jChoosePayment.setMinimumSize(new java.awt.Dimension(400, 150));
        jChoosePayment.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel49.setText(localization.getString("choose"));
        jChoosePayment.getContentPane().add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 380, 30));

        jButton26.setText(localization.getString("close"));
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });
        jChoosePayment.getContentPane().add(jButton26, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, 190, -1));

        jLabel68.setIcon(new javax.swing.ImageIcon("src/main/java/GUI/img/warning.png"));
        jChoosePayment.getContentPane().add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 80, 90));

        jAbout.setTitle("About");
        jAbout.setMinimumSize(new java.awt.Dimension(520, 350));

        jTextArea2.setEditable(false);
        jTextArea2.setBackground(new java.awt.Color(240, 240, 240));
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(10);
        jTextArea2.setText("Expense-Manager\n\nThe application manages individual accounts and payments, also records subjects with whom there are ongoing payment transactions. Based on the data obtained allows generation of reports for a certain period, for individual accounts, for each entity etc. The purpose is to increase the visibility and facilitate the work with a large number of payment transactions on different accounts.\n\nCreated by:\nOndrěj Grůza - @levejhak\nRudolf Kvašövský - @rudky4\nAndrea Turiaková - @charlliz\nMarek Zvolánek - @zvolas8\n\nThis project has been develeped for course PB138 Modern Markup Languages. (c) 2015");
        jTextArea2.setToolTipText("");
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setBorder(null);
        jScrollPane3.setViewportView(jTextArea2);

        jButton27.setText("Close");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jAboutLayout = new javax.swing.GroupLayout(jAbout.getContentPane());
        jAbout.getContentPane().setLayout(jAboutLayout);
        jAboutLayout.setHorizontalGroup(
            jAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAboutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
            .addGroup(jAboutLayout.createSequentialGroup()
                .addGap(224, 224, 224)
                .addComponent(jButton27)
                .addContainerGap(237, Short.MAX_VALUE))
        );
        jAboutLayout.setVerticalGroup(
            jAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAboutLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton27)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ExpenseUlimate Manager");
        setMaximumSize(new java.awt.Dimension(780, 530));
        setMinimumSize(new java.awt.Dimension(780, 530));
        setPreferredSize(new java.awt.Dimension(780, 530));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Vector vec = new Vector<Account>(accountManager.findAllAccounts());
        Account tempAcc = new Account();
        tempAcc.setId(0L);
        tempAcc.setName("-- " + localization.getString("allAccounts") + " --");
        vec.add(0, tempAcc);
        accountList.setModel(new javax.swing.DefaultComboBoxModel(vec));
        accountList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                accountListItemStateChanged(evt);
            }
        });
        getContentPane().add(accountList, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 11, 300, 30));

        Vector vec2 = new Vector<Subject>(subjectManager.findAllSubjects());
        Subject tempSub = new Subject();
        tempSub.setId(0L);
        tempSub.setName("-- " + localization.getString("allSubjects") + " --");
        vec2.add(0, tempSub);
        subjectList.setModel(new javax.swing.DefaultComboBoxModel(vec2));
        getContentPane().add(subjectList, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 49, 300, 30));

        jLabel1.setText("Account:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel2.setText("Subject:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(2);
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 404, 750, 50));

        paymentTableModel.setModel(new JPaymentTableModel(localization));
        paymentTableModel.setMaximumSize(null);
        paymentTableModel.getColumnModel().getColumn(0).setPreferredWidth(300);
        paymentTableModel.getColumnModel().getColumn(1).setPreferredWidth(100);
        paymentTableModel.getColumnModel().getColumn(2).setPreferredWidth(150);
        paymentTableModel.getColumnModel().getColumn(3).setPreferredWidth(150);
        paymentTableModel.getColumnModel().getColumn(4).setPreferredWidth(100);
        jScrollPane2.setViewportView(paymentTableModel);
        //PaymentManagerImpl manager = new PaymentManagerImpl();
        //  List<Payment> list = manager.findAllPayments();
        //  ((JPaymentTableModel)paymentTableModel.getModel()).refresh(list);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 750, 250));

        jLabel3.setText("Payments:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        jButton1.setText("Add Payment");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 150, -1));

        jButton2.setText("Delete Payment");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 370, 150, -1));

        jButton3.setText("EditPayment");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 370, 150, -1));

        jButton4.setText("Generate HTML report");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 370, 200, -1));

        jDateChooser2.setDate(new Date());
        getContentPane().add(jDateChooser2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 10, 142, 30));

        jDateChooser3.setDate(new Date());
        getContentPane().add(jDateChooser3, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 50, 142, 30));

        jButton5.setText("Refresh");
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton5MouseClicked(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 10, 128, 70));

        jCheckBox1.setText("Use Date");
        jCheckBox1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        getContentPane().add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 30, -1, -1));

        jMenu2.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Account");

        jMenuItem2.setText("Add");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuItem3.setText("Delete");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuItem4.setText("Update");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenuBar1.add(jMenu3);

        jMenu6.setText("Subject");

        jMenuItem5.setText("Add");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem5);

        jMenuItem6.setText("Delete");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem6);

        jMenuItem7.setText("Edit");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem7);

        jMenuBar1.add(jMenu6);

        jMenu7.setText("Currency");

        jMenuItem8.setText("Add");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem8);

        jMenuItem9.setText("Delete");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem9);

        jMenuBar1.add(jMenu7);

        jMenu8.setText("Category");

        jMenuItem11.setText("Add");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem11);

        jMenuItem12.setText("Delete");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem12);

        jMenuItem13.setText("Edit");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem13);

        jMenuBar1.add(jMenu8);

        jMenu9.setText("XML");

        jMenuItem14.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem14.setText("Generate");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem14);

        jMenuBar1.add(jMenu9);

        jMenu1.setText("Help");

        jMenuItem16.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem16.setText("About");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem16);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void accountListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_accountListItemStateChanged
            // TODO add your handling code here:
    }//GEN-LAST:event_accountListItemStateChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        
       try{
            String path = "xml-out/" + new Date().toString().substring(4, 20).replaceAll(" ","").replaceAll(":","") +".html";
            ExpenseManagerImpl.createHTML(expenseManager.createXML(((JPaymentTableModel)paymentTableModel.getModel()).getPayments()), path);                    
            File file = new File(path);
            
            if(Desktop.isDesktopSupported())
            {
                try {
                    Desktop.getDesktop().browse(new URI("file:///"+file.getAbsolutePath().replaceAll("\\\\", "/")));
                } catch (URISyntaxException | IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }     
            
            }catch(Exception e){
                System.out.println("Could not create HTML file");
            }
       
       
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(paymentTableModel.getSelectedRow() == -1){
            jChoosePayment.setVisible(true);
        } else {
        Payment temp = ((JPaymentTableModel)paymentTableModel.getModel()).getRow(paymentTableModel.getSelectedRow());
        new PaymentSwingWorker(PaymentActions.REMOVE_PAYMENT,temp).execute();
        update();
        }        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jDateChooser1.setDate(new Date());
        jAddPayment.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseClicked
        update();
    }//GEN-LAST:event_jButton5MouseClicked

    public void update(){         
        if((((Account)accountList.getSelectedItem()).getId() == 0) && (((Subject)subjectList.getSelectedItem()).getId() == 0) && (jCheckBox1.isSelected()))
            toTable = paymentManager.findAllPayments(jDateChooser2.getDate(),jDateChooser3.getDate());
        
        if((((Account)accountList.getSelectedItem()).getId() == 0) && (((Subject)subjectList.getSelectedItem()).getId() != 0) && (jCheckBox1.isSelected()))
            toTable = expenseManager.getAllPaymentsBySubject(((Subject)subjectList.getSelectedItem()).getId(), jDateChooser2.getDate(),jDateChooser3.getDate());
            
        if((((Account)accountList.getSelectedItem()).getId() != 0) && (((Subject)subjectList.getSelectedItem()).getId() == 0) && (jCheckBox1.isSelected()))
            toTable = expenseManager.getAllPaymentsByAccount(((Account)accountList.getSelectedItem()).getId(), jDateChooser2.getDate(),jDateChooser3.getDate());
        
        if((((Account)accountList.getSelectedItem()).getId() != 0) && (((Subject)subjectList.getSelectedItem()).getId() != 0) && (jCheckBox1.isSelected()))
            toTable = expenseManager.getAllPaymentsBySubjectAndAccount(((Account)accountList.getSelectedItem()).getId(), ((Subject)subjectList.getSelectedItem()).getId(), jDateChooser2.getDate(),jDateChooser3.getDate());
         
        if((((Account)accountList.getSelectedItem()).getId() == 0) && (((Subject)subjectList.getSelectedItem()).getId() == 0) && !(jCheckBox1.isSelected()))
            toTable = paymentManager.findAllPayments();
        
        if((((Account)accountList.getSelectedItem()).getId() == 0) && (((Subject)subjectList.getSelectedItem()).getId() != 0) && !(jCheckBox1.isSelected()))
            toTable = expenseManager.getAllPaymentsBySubject(((Subject)subjectList.getSelectedItem()).getId());
        
        if((((Account)accountList.getSelectedItem()).getId() != 0) && (((Subject)subjectList.getSelectedItem()).getId() == 0) && !(jCheckBox1.isSelected()) )
            toTable = expenseManager.getAllPaymentsByAccount(((Account)accountList.getSelectedItem()).getId());
        
        if((((Account)accountList.getSelectedItem()).getId() != 0) && (((Subject)subjectList.getSelectedItem()).getId() != 0) && !(jCheckBox1.isSelected()) ){
            toTable = expenseManager.getAllPaymentsBySubjectAndAccount(((Account)accountList.getSelectedItem()).getId(), ((Subject)subjectList.getSelectedItem()).getId());
        }
        
        ((JPaymentTableModel)paymentTableModel.getModel()).refresh(toTable);
        
        if (((Account)accountList.getSelectedItem()).getId() != 0){
            jTextArea1.setText("Account description: " + ((Account)accountList.getSelectedItem()).getDescription() +  "\nNumber of payments: "+toTable.size());
        } else {
        jTextArea1.setText("Number of payments: "+toTable.size());
        }
    }
    
    public void updateAccountLists(){
        Vector vec = new Vector<>(accountManager.findAllAccounts());
       
        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(vec));
        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel(vec));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(vec));
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(vec));
        Account tempAcc = new Account();
        tempAcc.setId(0L);
        tempAcc.setName("-- " + localization.getString("allAccounts") + " --");
        vec.add(0, tempAcc);        
        accountList.setModel(new javax.swing.DefaultComboBoxModel(vec));        
    }
    
    public void updateSubjectLists(){
        Vector vec = new Vector<>(subjectManager.findAllSubjects());
            jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(vec));
            jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(vec));
            jComboBox13.setModel(new javax.swing.DefaultComboBoxModel(vec));
            jComboBox14.setModel(new javax.swing.DefaultComboBoxModel(vec));
            Subject tempSub = new Subject();
            tempSub.setId(0L);
            tempSub.setName("-- " + localization.getString("allSubjects") + " --");
            vec.add(0, tempSub);
            subjectList.setModel(new javax.swing.DefaultComboBoxModel(vec));       
    }
    
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        String desc = jTextField2.getText();
        BigDecimal amount = null;
        Date date = jDateChooser4.getDate();
        
        try{
           amount = new BigDecimal(jTextField1.getText().replaceAll(",","."));
        }
        catch(NumberFormatException ex){
            jInvalidPayment.setVisible(true);
        }
        
        if (desc.equals("") || (amount == null)){
            jInvalidPayment.setVisible(true);  
        } else {            
        Payment payment = new Payment();
        payment.setAccountId(((Account)jComboBox1.getSelectedItem()).getId());
        payment.setSubjectId(((Subject)jComboBox2.getSelectedItem()).getId());
        payment.setCategoryId(((Category)jComboBox3.getSelectedItem()).getId());
        payment.setDescription(desc);
        payment.setAmount(amount);
        payment.setDate(jDateChooser1.getDate());
        new PaymentSwingWorker(PaymentActions.ADD_PAYMENT, payment).execute();
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        jInvalidPayment.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        String desc = jTextField4.getText();
        BigDecimal amount = null;
        Date date = jDateChooser4.getDate();
        try{
           amount = new BigDecimal(jTextField3.getText().replaceAll(",","."));
        }
        catch(NumberFormatException ex){
            jInvalidPayment.setVisible(true);
        }
        
        if (desc.equals("") || (amount == null) || (date == null)){
            jInvalidPayment.setVisible(true);  
        } else {
            
        Payment payment = ((JPaymentTableModel)paymentTableModel.getModel()).getRow(paymentTableModel.getSelectedRow());
        payment.setAccountId(((Account)jComboBox4.getSelectedItem()).getId());
        payment.setSubjectId(((Subject)jComboBox5.getSelectedItem()).getId());
        payment.setCategoryId(((Category)jComboBox6.getSelectedItem()).getId());
        payment.setDescription(desc);
        payment.setAmount(amount);
        payment.setDate(jDateChooser4.getDate());       
        new PaymentSwingWorker(PaymentActions.EDIT_PAYMENT, payment).execute();
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(paymentTableModel.getSelectedRow() == -1){
            jChoosePayment.setVisible(true);
        } else {        
        Payment temp = ((JPaymentTableModel)paymentTableModel.getModel()).getRow(paymentTableModel.getSelectedRow());
        jTextField4.setText(temp.getDescription());
        jTextField3.setText(temp.getAmount().toString());
        jComboBox4.setSelectedItem(accountManager.getAccountById(temp.getAccountId()));
        jComboBox5.setSelectedItem(subjectManager.getSubjectById(temp.getSubjectId()));
        jComboBox6.setSelectedItem(categoryManager.getCategoryById(temp.getCategoryId()));
        jDateChooser4.setDate(temp.getDate());
        jUpdatePayment.setVisible(true);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        jTextField6.setText("");
        jTextField7.setText("");
        jDateChooser5.setDate(new Date());        
        jAddAccount.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
         Account temp = new Account();
         temp.setName(jTextField7.getText());
         temp.setDescription(jTextField6.getText());
         temp.setCreationDate(jDateChooser5.getDate());
         temp.setCurrency((jComboBox8.getSelectedItem().toString()));
         new AccountSwingWorker(AccountActions.ADD_ACCOUNT,temp).execute();      
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
       Currency temp = new Currency();
       temp.setCcy(jTextField8.getText());
       temp.setCcyName(jTextField9.getText());
       new CurrencySwingWorker(CurrencyActions.ADD_CURRENCY,temp).execute();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        jTextField8.setText("");
        jTextField9.setText("");
        jAddCurrency.setVisible(true);   
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        jWantDeleteAccount.dispose();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        List<Payment> temp = paymentManager.findAllPayments();
        Long id = ((Account)jComboBox9.getSelectedItem()).getId();
        for (Payment temp1 : temp) {
            if (temp1.getAccountId().equals(id)) {
                new PaymentSwingWorker(PaymentActions.REMOVE_PAYMENT,temp1).execute();
            }
        }
        new AccountSwingWorker(AccountActions.REMOVE_ACCOUNT,accountManager.getAccountById(id)).execute();
        updateAccountLists();
        update();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        jWantDeleteAccount.dispose();
        jDeleteAccount.setVisible(false);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        jWantDeleteAccount.setVisible(true);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        jDeleteSubject.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
       jDeleteAccount.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
       Account temp = accountManager.getAccountById(((Account)jComboBox10.getSelectedItem()).getId());
       temp.setName(jTextField11.getText());
       temp.setDescription(jTextField10.getText());
       temp.setCreationDate(jDateChooser6.getDate());
       temp.setCurrency(((Currency)jComboBox11.getSelectedItem()).getCcy()); 
       new AccountSwingWorker(AccountActions.EDIT_ACCOUNT,temp).execute();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        Account temp = (Account)jComboBox10.getSelectedItem();
        jTextField11.setText(temp.getName());
        jTextField10.setText(temp.getDescription());
        jDateChooser6.setDate(temp.getCreationDate()); 
        jUpdateAccount.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jComboBox10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox10ActionPerformed
        Account temp = (Account)jComboBox10.getSelectedItem();
        jTextField11.setText(temp.getName());
        jTextField10.setText(temp.getDescription());
        jDateChooser6.setDate(temp.getCreationDate());
    }//GEN-LAST:event_jComboBox10ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        Currency temp = (Currency)jComboBox12.getSelectedItem();
        List acc = accountManager.findAllAccounts();
        boolean check = true; 
        for (Object acc1 : acc) {
            if (((Account) acc1).getCurrency().equals(temp.getCcy())) {
                jUsedCurrency.setVisible(true); 
                check = false;
                break;
            }        
        }
        
        if(check){
            new CurrencySwingWorker(CurrencyActions.REMOVE_CURRENCY,temp).execute();
        }
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        jUsedCurrency.dispose();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        jDeleteCurrency.setVisible(true);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        Subject temp = new Subject();
        temp.setName(jTextField13.getText());
        new SubjectSwingWorker(SubjectActions.ADD_SUBJECT,temp).execute();
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        jTextField13.setText("");
        jAddSubject.setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        Subject temp = (Subject)jComboBox13.getSelectedItem();
        List payments = paymentManager.findAllPayments();
        boolean check = true; 
        for (Object payment : payments) {
            if (((Payment) payment).getSubjectId().equals(temp.getId())) {
                jUsedSubject.setVisible(true); 
                check = false;
                break;
            }        
        }
        
        if(check){
            new SubjectSwingWorker(SubjectActions.REMOVE_SUBJECT,temp).execute(); 
        }
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        jUsedSubject.dispose();
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
            Subject temp = (Subject)jComboBox13.getSelectedItem();
            temp.setName(jTextField5.getText());
            new SubjectSwingWorker(SubjectActions.EDIT_SUBJECT,temp).execute();
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
            jUpdateSubject.setVisible(true);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
           Category temp = new Category();
        temp.setName(jTextField14.getText());
        new CategorySwingWorker(CategoryActions.ADD_CATEGORY,temp).execute();
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jTextField14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField14ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
            Category temp = (Category)jComboBox15.getSelectedItem();
            temp.setName(jTextField12.getText());
            new CategorySwingWorker(CategoryActions.EDIT_CATEGORY,temp).execute();           
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        Category temp = (Category)jComboBox16.getSelectedItem();
        List payments = paymentManager.findAllPayments();
        boolean check = true; 
        for (Object payment : payments) {
            if (((Payment) payment).getCategoryId().equals(temp.getId())) {
                jUsedCategory.setVisible(true); 
                check = false;
                break;
            }        
        }
        
        if(check){
            new CategorySwingWorker(CategoryActions.REMOVE_CATEGORY,temp).execute();            
        }
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
            jUsedCategory.dispose();
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        try{
            String path = new Date().toString().substring(4, 20).replaceAll(" ","").replaceAll(":","");
            
            File file = new File("xml-out/"+path+".xml");
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.write(expenseManager.createXML(((JPaymentTableModel)paymentTableModel.getModel()).getPayments()));
                
                if(Desktop.isDesktopSupported())
                {
                    try {
                        Desktop.getDesktop().browse(new URI("file:///"+file.getAbsolutePath().replaceAll("\\\\", "/")));
                    } catch (URISyntaxException | IOException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            }catch(Exception e){
                System.out.println("Could not create file");
            }                  
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        jAddCategory.setVisible(true);    
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        jDeleteCategory.setVisible(true);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        jUpdateCategory.setVisible(true);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        jChoosePayment.dispose();
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
         jAbout.setVisible(true);
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        jAbout.setVisible(false);
    }//GEN-LAST:event_jButton27ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox accountList;
    private javax.swing.JFrame jAbout;
    private javax.swing.JFrame jAddAccount;
    private javax.swing.JFrame jAddCategory;
    private javax.swing.JFrame jAddCurrency;
    private javax.swing.JFrame jAddPayment;
    private javax.swing.JFrame jAddSubject;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JDialog jChoosePayment;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox10;
    private javax.swing.JComboBox jComboBox11;
    private javax.swing.JComboBox jComboBox12;
    private javax.swing.JComboBox jComboBox13;
    private javax.swing.JComboBox jComboBox14;
    private javax.swing.JComboBox jComboBox15;
    private javax.swing.JComboBox jComboBox16;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JComboBox jComboBox9;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private com.toedter.calendar.JDateChooser jDateChooser3;
    private com.toedter.calendar.JDateChooser jDateChooser4;
    private com.toedter.calendar.JDateChooser jDateChooser5;
    private com.toedter.calendar.JDateChooser jDateChooser6;
    private javax.swing.JFrame jDeleteAccount;
    private javax.swing.JFrame jDeleteCategory;
    private javax.swing.JFrame jDeleteCurrency;
    private javax.swing.JFrame jDeleteSubject;
    private javax.swing.JDialog jInvalidPayment;
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
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JFrame jUpdateAccount;
    private javax.swing.JFrame jUpdateCategory;
    private javax.swing.JFrame jUpdatePayment;
    private javax.swing.JFrame jUpdateSubject;
    private javax.swing.JDialog jUsedCategory;
    private javax.swing.JDialog jUsedCurrency;
    private javax.swing.JDialog jUsedSubject;
    private javax.swing.JDialog jWantDeleteAccount;
    public javax.swing.JTable paymentTableModel;
    private javax.swing.JComboBox subjectList;
    // End of variables declaration//GEN-END:variables
}

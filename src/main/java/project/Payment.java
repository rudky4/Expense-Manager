package project;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author marek
 */
public class Payment {

    private Long id;
    private String description;
    private Date date;
    private BigDecimal amount;
    private Long accountId;
    private Long subjectId;
    private Long categoryId;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the acountId
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * @param acountId the acountId to set
     */
    public void setAccountId(Long acountId) {
        this.accountId = acountId;
    }

    /**
     * @return the subjectId
     */
    public Long getSubjectId() {
        return subjectId;
    }

    /**
     * @param subjectId the subjectId to set
     */
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * @return the categoryId
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

     @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Payment other = (Payment) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
    @Override
    public String toString() {
        return id + ": " + date + " " + amount;
    }
    
    /**
     * 
     * @return converted payment to XML schema
     */
    public String toXML(){
        DateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
        String result = "<payment id=\"" + this.getId() + "\">"
                + "<description>" + this.getDescription() + "</description>"
                + "<date>" + dateF.format(this.getDate()) + "</date>"
                + "<amount>" + this.getAmount() + "</amount>"
                + "<account-id>" + this.getAccountId().toString() + "</account-id>"
                + "<subject-id>" + this.getSubjectId().toString() + "</subject-id>"
                + "<category-id>" + this.getCategoryId().toString() + "</category-id>"
                + "</payment>";
        return result;
    }

}

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
public interface CategoryManager {

    /**
     * function checks all parameters and create account 
     * @param category
     */
    public void createCategory(Category category);

    /**
     * function checks all parameter and updates account 
     * @param category
     */
    public void updateCategory(Category category);

    /**
     * function deletes account 
     * @param id 
     */
    public void deleteCategory(Long id);

    /**
     * function finds all accounts
     * @return List of category
     */
    public List<Category> findAllCategory();
    
    /**
     * function finds accout by id
     * @param id
     * @return Category
     */
    public Category getCategoryById(Long id);
}

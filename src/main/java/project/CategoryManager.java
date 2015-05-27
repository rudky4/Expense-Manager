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

    public void createCategory(Category category);

    public void updateCategory(Currency currency);

    public void deleteCategory(Long id);

    public List<Currency> findAllCategory();
}

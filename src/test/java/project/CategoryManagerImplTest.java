/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author charlliz
 */
public class CategoryManagerImplTest {
    
    CategoryManagerImpl manager = new CategoryManagerImpl();

    /**
     * Test of createCategory method, of class CategoryManagerImpl.
     */
    @Test
    public void testCreateCategory() {
        Category category = newCategory("Books");
        manager.createCategory(category);
        Long categoryId = category.getId();
        assertNotNull(categoryId);
        Category temp = manager.getCategoryById(categoryId);
        assertEquals(category,temp);
        assertDeepEquals(category,temp);
        assertNotSame(category,temp);
    }

    @Test
    public void testCreateWithWrongArguments(){
        Category category = newCategory("Books");
        category.setId(1l);
        try{
        manager.createCategory(category);
        fail();
        }catch (IllegalArgumentException ex){
            //OK
        }
        
        Category category2 = newCategory(null);
        try{
        manager.createCategory(category);
        fail();
        }catch (IllegalArgumentException ex){
            //OK
        }
        
        
    }
    /**
     * Test of deleteCategory method, of class CategoryManagerImpl.
     */
    @Test
    public void testDeleteCategory() {
        Category c1 = newCategory("Food");
        Category c2 = newCategory("Clothes");
        manager.createCategory(c1);
        manager.createCategory(c2);
        
        assertNotNull(manager.getCategoryById(c1.getId()));
        assertNotNull(manager.getCategoryById(c2.getId()));
        
        manager.deleteCategory(c1.getId());
        
        //assertNull(manager.getCategoryById(c1.getId()));
        assertNotNull(manager.getCategoryById(c2.getId()));
        
        
        try {
            manager.deleteCategory(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        manager.deleteCategory(c2.getId());
    }

    
    @Test
    public void testGetCategoryById(){
    
        Category c1 = newCategory("Shoes");
        manager.createCategory(c1);
        Long categoryId = c1.getId();
        
        Category result = manager.getCategoryById(categoryId);
        assertEquals(c1,result);
        assertDeepEquals(c1,result);
        
    }
    
    /**
     * Test of updateCategory method, of class CategoryManagerImpl.
     */
    @Test
    public void testUpdateCategory() {
        Category c1 = newCategory("Traveling");
        manager.createCategory(c1);
        
        Long categoryId = c1.getId();

        c1 = manager.getCategoryById(categoryId);
        c1.setName("Party");
        manager.updateCategory(c1);        
        assertEquals("Party", c1.getName());
  
        manager.deleteCategory(categoryId);
    }

    
    @Test
    public void testUpdateCategoryWithWrongArguments(){
        Category c1 = newCategory("Newspaper");
        manager.createCategory(c1);
        
        Long categoryId = c1.getId();
        try {
            manager.updateCategory(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        try {
            c1 = manager.getCategoryById(categoryId);
            c1.setId(null);
            manager.updateCategory(c1);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        try {
            c1 = manager.getCategoryById(categoryId);
            c1.setName(null);
            manager.updateCategory(c1);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }
    
    /**
     * Test of findAllCategory method, of class CategoryManagerImpl.
     */
    @Test
    public void testFindAllCategory() {
        System.out.println("findAllCategories");
        
        Category c1 = newCategory("Category 1");
        Category c2 = newCategory("Category 2");
        Category c3 = newCategory("Category 3");
        
        manager.createCategory(c1);
        manager.createCategory(c2);
        manager.createCategory(c3);
        
        List<Category> list = manager.findAllCategory();
        
        Category c4 = newCategory("Category 4");
        manager.createCategory(c4);
        
        assertEqualsList(list,manager.findAllCategory());
        
        
    }
       
    private Category newCategory(String name){
        Category category = new Category();
        category.setName(name);
        return category;
    }
    
    private void assertDeepEquals(Category c1, Category c2) {
        assertEquals(c1.getId(), c2.getId());
        assertEquals(c1.getName(), c2.getName());       
    }
    
    private void assertEqualsList(List<Category> list1,List<Category> list2){
        if(list1.size() != list2.size()) fail();
        
        for(int i=0;i<list1.size();i++){
            assertDeepEquals(list1.get(i),list2.get(i));
        }
    }
    
}

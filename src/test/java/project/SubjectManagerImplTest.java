/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ondra
 */
public class SubjectManagerImplTest {
    
    private SubjectManagerImpl manager = new SubjectManagerImpl();
    
    public SubjectManagerImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createSubject method, of class SubjectManagerImpl.
     */
    @Test
    public void testCreateSubject() {
        System.out.println("createSubject");
        Subject subject = newSubject("Test Subject");        
        manager.createSubject(subject);
        
        Long subjectID = subject.getId();
        assertNotNull(subjectID);
        
        manager.deleteSubject(subjectID);
        
    }

    /**
     * Test of updateSubject method, of class SubjectManagerImpl.
     */
    @Test
    public void testUpdateSubject() {
        System.out.println("updateSubject");        
                
        Subject subject1 = newSubject("Test Subject 1");
        Subject subject2 = newSubject("Test Subject 2");
        manager.createSubject(subject1);
        manager.createSubject(subject2);
        Long subjectId = subject1.getId();        
        
        subject1.setName("Bank");
        manager.updateSubject(subject1);
        subject1 = manager.getSubjectById(subjectId);
        assertEquals("Bank", subject1.getName());
        
        assertDeepEquals(subject2, manager.getSubjectById(subject2.getId()));
        
        manager.deleteSubject(subjectId);
        manager.deleteSubject(subject2.getId());
    }

    /**
     * Test of deleteSubject method, of class SubjectManagerImpl.
     */
    @Test
    public void testDeleteSubject() {
        System.out.println("deleteSubject");      
               
        Subject subject1 = newSubject("Test Subject 1");
        Subject subject2 = newSubject("Test Subject 2");
        Subject subject3 = newSubject("Test Subject 3");
        manager.createSubject(subject1);
        manager.createSubject(subject2);
        manager.createSubject(subject3);
        
       Long subjectId = subject1.getId();
       manager.deleteSubject(subjectId);       
        
        List<Subject> result = manager.findAllSubjects();
        List<Subject> expectedResult = new ArrayList<>();
        expectedResult.add(subject2);
        expectedResult.add(subject3);
        
        assertEquals(expectedResult.size(), result.size());
        
        manager.deleteSubject(subject2.getId());
        manager.deleteSubject(subject3.getId());
    }

    /**
     * Test of getSubjectById method, of class SubjectManagerImpl.
     */
    @Test
    public void testGetSubjectById() {
        System.out.println("getSubjectById");
        
        Long id = new Long(1);
        Subject subject = newSubject("Test Subject");
        manager.createSubject(subject);
        List<Subject> list = new ArrayList<>();
        list.add(subject);
        Subject result = list.get(0);
        Subject subjectById = manager.getSubjectById(id);
        assertEquals(subjectById.getId(), result.getId());
        assertEquals(subjectById.getName(), result.getName());        
        
        manager.deleteSubject(subject.getId());
    }

    /**
     * Test of getSubjectByName method, of class SubjectManagerImpl.
     */
    @Test
    public void testGetSubjectByName() {
        System.out.println("getSubjectByName");
        
        String name1 = "Test 1";
        String name2 = "Test 2";
        Subject subject1 = newSubject("Test 1");
        Subject subject2 = newSubject("Test 2");
        Subject subject3 = newSubject("Test 3");
        Subject subject4 = newSubject("Test 4");
        manager.createSubject(subject1);
        manager.createSubject(subject2);
        manager.createSubject(subject3);
        manager.createSubject(subject4);
        
        Subject result1 = new Subject();
        Subject result2 = new Subject();
        result1 = manager.getSubjectByName(name1);        
        result2 = manager.getSubjectByName(name2);              
        assertEquals(subject1, result1);
        assertEquals(subject2, result2);
        
        manager.deleteSubject(subject1.getId());
        manager.deleteSubject(subject2.getId());
        manager.deleteSubject(subject3.getId());
        manager.deleteSubject(subject4.getId());
    }
    
    /**
     * Test of findAllSubjects method, of class SubjectManagerImpl.
     */
    @Test
    public void testFindAllSubjects() {
        System.out.println("findAllSubjects");
        
        Subject subject1 = newSubject("Test 1");
        Subject subject2 = newSubject("Test 2");
        Subject subject3 = newSubject("Test 3");
        Subject subject4 = newSubject("Test 4");
        manager.createSubject(subject1);
        manager.createSubject(subject2);
        manager.createSubject(subject3);
        manager.createSubject(subject4);
        
        List<Subject> expResult = new ArrayList<>();
        List<Subject> result = new ArrayList<>();
        expResult.add(subject1);
        expResult.add(subject2);
        expResult.add(subject3);
        expResult.add(subject4);
        result = manager.findAllSubjects();
        assertEquals(expResult.size(), result.size());
        assertDeepEquals(expResult, result);
        
        manager.deleteSubject(subject1.getId());
        manager.deleteSubject(subject2.getId());
        manager.deleteSubject(subject3.getId());
        manager.deleteSubject(subject4.getId());
    }
    
    private static Subject newSubject(String name) {
        Subject subject = new Subject();
        subject.setName(name);        
        return subject;
    }
    
    private void assertDeepEquals(Subject expected, Subject actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());        
    }
    
    private void assertDeepEquals(List<Subject> expectedList, List<Subject> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Subject expected = expectedList.get(i);
            Subject actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }
}

package project;


import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marek
 */
public interface SubjectManager {

    /**
     * Add certain subject to database
     * @param subject subject to be added
     */
    public void createSubject(Subject subject);

    /**
     * Edit certain subject in database
     * @param subject subject to be edited
     */
    public void updateSubject(Subject subject);

    /**
     * Delete subject in database
     * @param id id of subject
     */
    public void deleteSubject(Long id);

    /**
     * Return certain subject by id
     * @param id subject id
     * @return subject
     */
    public Subject getSubjectById(Long id);
    
    /**
     * Return certain subject by name
     * @param name name of subject
     * @return subject
     */
    public Subject getSubjectByName(String name);
    
    /**
     * Return all subjects in database
     * @return list of subjects
     */
    public List<Subject> findAllSubjects();
}

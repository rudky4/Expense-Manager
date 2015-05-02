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

    public void createSubject(Subject subject);

    public void updateSubject(Subject subject);

    public void deleteSubject(Long id);

    public List<Subject> getSubject(Long id);
}

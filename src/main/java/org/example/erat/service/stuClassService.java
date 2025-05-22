package org.example.erat.service;

import org.example.erat.dao.stuClassDAO;
import org.example.erat.model.stuClass;

import java.util.List;

public class stuClassService {
    private static stuClassDAO classDAO = new stuClassDAO();

    public void addClass(stuClass newClass) {
        classDAO.addClass(newClass);
    }

    public static void editClass(stuClass updatedClass) {
        classDAO.editClass(updatedClass);
    }

    public void deleteClass(String classId) {
        classDAO.deleteClass(classId);
    }

    public static List<stuClass> getClasses() {
        return classDAO.getClasses();
    }
}

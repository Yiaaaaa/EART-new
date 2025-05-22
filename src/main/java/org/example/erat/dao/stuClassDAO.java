package org.example.erat.dao;

import org.example.erat.model.stuClass;

import java.util.ArrayList;
import java.util.List;

public class stuClassDAO {
    private List<stuClass> classes = new ArrayList<>();

    public void addClass(stuClass newClass) {
        classes.add(newClass);
    }

    public void editClass(stuClass updatedClass) {
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).getClassId().equals(updatedClass.getClassId())) {
                classes.set(i, updatedClass);
                break;
            }
        }
    }

    public void deleteClass(String classId) {
        classes.removeIf(c -> c.getClassId().equals(classId));
    }

    public List<stuClass> getClasses() {
        return classes;
    }
}

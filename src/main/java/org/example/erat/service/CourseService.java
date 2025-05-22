package org.example.erat.service;

import org.example.erat.dao.CourseDAO;
import org.example.erat.model.Course;

import java.util.List;

public class CourseService {
    private CourseDAO courseDAO = new CourseDAO();

    public void addCourse(Course newCourse) {
        courseDAO.addCourse(newCourse);
    }

    public void editCourse(Course updatedCourse) {
        courseDAO.editCourse(updatedCourse);
    }

    public void deleteCourse(String courseId) {
        courseDAO.deleteCourse(courseId);
    }

    public List<Course> getCourses() {
        return courseDAO.getCourses();
    }
}

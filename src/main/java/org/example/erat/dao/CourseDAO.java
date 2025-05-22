package org.example.erat.dao;

import org.example.erat.model.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private List<Course> courses = new ArrayList<>();

    public void addCourse(Course newCourse) {
        courses.add(newCourse);
    }

    public void editCourse(Course updatedCourse) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseId().equals(updatedCourse.getCourseId())) {
                courses.set(i, updatedCourse);
                break;
            }
        }
    }

    public void deleteCourse(String courseId) {
        courses.removeIf(c -> c.getCourseId().equals(courseId));
    }

    public List<Course> getCourses() {
        return courses;
    }
}

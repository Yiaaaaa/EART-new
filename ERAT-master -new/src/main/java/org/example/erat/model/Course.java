package org.example.erat.model;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseId;
    private String courseName;
    private List<ExperimentFile> experiments;
    private List<Student> students;

    public Course(String courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.experiments = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<ExperimentFile> getExperiments() {
        return experiments;
    }

    public void addExperiment(ExperimentFile experiment) {
        experiments.add(experiment);
    }

    // 添加学生方法
    public void addStudent(Student student) {
        if (student != null && !students.contains(student)) {
            students.add(student);
        }
    }

    // 获取学生列表
    public List<Student> getStudents() {
        return students;
    }

}

package org.example.erat.model;

import java.util.Collections;
import java.util.List;

public class ExperimentFile extends ReportComponent {
    private String studentId;
    private String experimentId;
    private String studentName;
    private String courseName;
    private String className;
    private String filePath;
    private Course course;

    public ExperimentFile(String studentId, String experimentId, String studentName, String courseName, String className, String filePath) {
        super("实验报告");
        this.studentId = studentId;
        this.experimentId = experimentId;
        this.studentName = studentName;
        this.courseName = courseName;
        this.className = className;
        this.filePath = filePath;
    }

    @Override
    public List<String> getExperiments() {
        return Collections.singletonList(experimentId);
    }

    public String getFullInfo() {
        return String.format("课程: %s | 班级: %s | 学号: %s | 姓名: %s | 实验: %s | 路径: %s", courseName, className, studentId, studentName, experimentId, filePath);
    }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getExperimentId() { return experimentId; }
    public void setExperimentId(String experimentId) { this.experimentId = experimentId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) {
        this.course = course;
        if (course != null) {
            course.addExperiment(this);
        }
    }

    @Override
    public String toString() {
        return getFullInfo();
    }
}
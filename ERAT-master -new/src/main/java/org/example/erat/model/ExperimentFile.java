package org.example.erat.model;

import java.util.Collections;
import java.util.List;

public class ExperimentFile extends ReportComponent {
    private String studentId;
    private String experimentId;
    private Course course;

    public ExperimentFile(String studentId, String experimentId) {
        super("实验报告");
        this.studentId = studentId;
        this.experimentId = experimentId;
    }

    @Override
    public List<String> getExperiments() {
        return Collections.singletonList(experimentId);
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        course.addExperiment(this);
    }
}
package org.example.erat.service;

import org.example.erat.model.Course;
import org.example.erat.model.stuClass;
import org.example.erat.model.ExperimentFile;
import org.example.erat.model.Student;

import java.util.*;

public class AnalysisService {
    public Map<String, Integer> analyzeMissingByStudent(List<Student> students, List<ExperimentFile> reports) {
        Map<String, Integer> missingCount = new HashMap<>();
        Set<String> allExps = new HashSet<>();

        for (ExperimentFile report : reports) {
            allExps.add(report.getExperimentId());
        }

        for (Student student : students) {
            int missing = 0;
            for (String exp : allExps) {
                boolean hasReport = reports.stream()
                        .anyMatch(r -> r.getStudentId().equals(student.getStudentId()) && r.getExperimentId().equals(exp));
                if (!hasReport) missing++;
            }
            missingCount.put(student.getStudentId(), missing);
        }

        return missingCount;
    }

    public Map<String, Integer> analyzeMissingByExperiment(List<Student> students, List<ExperimentFile> reports) {
        Map<String, Integer> missingPerExp = new HashMap<>();
        Set<String> allExps = new HashSet<>();

        for (ExperimentFile report : reports) {
            allExps.add(report.getExperimentId());
        }

        for (String exp : allExps) {
            int count = 0;
            for (Student student : students) {
                boolean hasReport = reports.stream()
                        .anyMatch(r -> r.getStudentId().equals(student.getStudentId()) && r.getExperimentId().equals(exp));
                if (!hasReport) count++;
            }
            missingPerExp.put(exp, count);
        }

        return missingPerExp;
    }
    // 返回 Map<实验编号, 提交率>
    public Map<String, Double> analyzeSubmissionRates(List<Student> students, List<ExperimentFile> reports) {
        Set<String> allExperiments = new HashSet<>();
        for (ExperimentFile report : reports) {
            allExperiments.add(report.getExperimentId());
        }

        Map<String, Double> result = new LinkedHashMap<>();
        for (String expId : allExperiments) {
            int submittedCount = 0;
            for (Student student : students) {
                boolean hasReport = reports.stream()
                        .anyMatch(r -> r.getStudentId().equals(student.getStudentId())
                                && r.getExperimentId().equals(expId));
                if (hasReport) submittedCount++;
            }
            double rate = (double) submittedCount / students.size() * 100;
            result.put(expId, rate);
        }
        return result;
    }

    public Map<String, Integer> analyzeMissingByClass(List<stuClass> classes, List<ExperimentFile> reports) {
        Map<String, Integer> missingPerClass = new HashMap<>();
        for (stuClass studentClass : classes) {
            int missing = 0;
            for (Student student : studentClass.getStudents()) {
                for (ExperimentFile report : reports) {
                    if (report.getStudentId().equals(student.getStudentId())) {
                        boolean hasReport = false;
                        for (ExperimentFile r : reports) {
                            if (r.getStudentId().equals(student.getStudentId()) && r.getExperimentId().equals(report.getExperimentId())) {
                                hasReport = true;
                                break;
                            }
                        }
                        if (!hasReport) missing++;
                    }
                }
            }
            missingPerClass.put(studentClass.getClassId(), missing);
        }
        return missingPerClass;
    }

    public Map<String, Integer> analyzeMissingByCourse(List<Course> courses, List<Student> students) {
        Map<String, Integer> missingPerCourse = new HashMap<>();
        for (Course course : courses) {
            int missing = 0;
            for (Student student : students) {
                for (ExperimentFile report : course.getExperiments()) {
                    if (report.getStudentId().equals(student.getStudentId())) {
                        boolean hasReport = course.getExperiments().stream()
                                .anyMatch(r -> r.getStudentId().equals(student.getStudentId()) && r.getExperimentId().equals(report.getExperimentId()));
                        if (!hasReport) missing++;
                    }
                }
            }
            missingPerCourse.put(course.getCourseId(), missing);
        }
        return missingPerCourse;
    }

    public Map<String, Double> analyzeSubmissionRatesByClass(List<stuClass> classes, List<ExperimentFile> reports) {
        Map<String, Double> submissionRatesByClass = new LinkedHashMap<>();
        for (stuClass clazz : classes) {
            int submittedCount = 0;
            int totalCount = clazz.getStudents().size();
            for (Student student : clazz.getStudents()) {
                for (ExperimentFile report : reports) {
                    if (report.getStudentId().equals(student.getStudentId())) {
                        boolean hasReport = reports.stream()
                                .anyMatch(r -> r.getStudentId().equals(student.getStudentId()) && r.getExperimentId().equals(report.getExperimentId()));
                        if (hasReport) submittedCount++;
                    }
                }
            }
            double rate = (double) submittedCount / totalCount * 100;
            submissionRatesByClass.put(clazz.getClassId(), rate);
        }
        return submissionRatesByClass;
    }

    public Map<String, Double> analyzeSubmissionRatesByCourse(List<Course> courses, List<Student> students) {
        Map<String, Double> submissionRatesByCourse = new LinkedHashMap<>();
        for (Course course : courses) {
            int submittedCount = 0;
            int totalCount = students.size();
            for (Student student : students) {
                for (ExperimentFile report : course.getExperiments()) {
                    if (report.getStudentId().equals(student.getStudentId())) {
                        boolean hasReport = course.getExperiments().stream()
                                .anyMatch(r -> r.getStudentId().equals(student.getStudentId()) && r.getExperimentId().equals(report.getExperimentId()));
                        if (hasReport) submittedCount++;
                    }
                }
            }
            double rate = (double) submittedCount / totalCount * 100;
            submissionRatesByCourse.put(course.getCourseId(), rate);
        }
        return submissionRatesByCourse;
    }
}
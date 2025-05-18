package org.example.erat.model;

import java.util.List;
import java.util.Map;

public class AnalysisResult {
    private Map<String, Integer> byStudent; // 学生ID -> 缺交次数
    private Map<String, Integer> byExperiment; // 实验ID -> 缺交人数
    private List<Student> targetStudents; // 分析的目标学生
    private String scope; // 分析范围描述
    private Map<String, String> missingExpsByStudent; // 新增：学生ID -> 缺交实验列表（如 "实验1, 实验2"）
    private Map<String, String> missingStudentsByExp;

    // Getters and Setters
    public Map<String, Integer> getByStudent() {
        return byStudent;
    }

    public void setByStudent(Map<String, Integer> byStudent) {
        this.byStudent = byStudent;
    }

    public Map<String, Integer> getByExperiment() {
        return byExperiment;
    }

    public void setByExperiment(Map<String, Integer> byExperiment) {
        this.byExperiment = byExperiment;
    }

    public List<Student> getTargetStudents() {
        return targetStudents;
    }

    public void setTargetStudents(List<Student> targetStudents) {
        this.targetStudents = targetStudents;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Map<String, String> getMissingExpsByStudent() {
        return missingExpsByStudent;
    }

    public void setMissingExpsByStudent(Map<String, String> missingExpsByStudent) {
        this.missingExpsByStudent = missingExpsByStudent;
    }

    public Map<String, String> getMissingStudentsByExp() {
        return missingStudentsByExp;
    }

    public void setMissingStudentsByExp(Map<String, String> missingStudentsByExp) {
        this.missingStudentsByExp = missingStudentsByExp;
    }

}
package org.example.erat.model;

import java.util.*;

public class AnalysisResult {
    private Map<String, Integer> byStudent; // 学生ID -> 缺交次数
    private Map<String, Integer> byExperiment; // 实验ID -> 缺交人数
    private List<Student> targetStudents; // 分析的目标学生
    private Map<String, List<String>> missingStudentsByExp; // 实验ID -> 缺交学生列表\
    private Map<String, String> missingExpsByStudent;
    private String scope; // 分析范围描述
    private List<ExperimentFile> targetFiles; // 目标文件列表（所有应提交的文件）
    private List<ExperimentFile> submittedFiles; // 已提交的文件列表

    public AnalysisResult() {
        this.byStudent = new HashMap<>();
        this.byExperiment = new HashMap<>();
        this.targetStudents = new ArrayList<>();
        this.missingExpsByStudent = new HashMap<>();
        this.missingStudentsByExp = new HashMap<>();
        this.targetFiles = new ArrayList<>();
        this.submittedFiles = new ArrayList<>();
    }

    // 获取学生对象（辅助方法）
    public Student findStudentById(String studentId, List<Student> students) {
        return students.stream()
                .filter(student -> student.getStudentId().equals(studentId))
                .findFirst()
                .orElse(null);
    }

    // 计算并生成缺交信息
    public void calculateMissingInfo() throws IllegalArgumentException {
        // 清空现有统计
        byStudent.clear();
        byExperiment.clear();
        missingExpsByStudent.clear();
        missingStudentsByExp.clear();

        // 校验提交文件中的学生ID是否存在于目标学生列表
        validateSubmittedStudentIds();

        // 获取所有学生ID和实验ID
        Set<String> allStudentIds = new HashSet<>();
        Set<String> allExperimentIds = new HashSet<>();

        for (Student student : targetStudents) {
            allStudentIds.add(student.getStudentId());
        }

        for (ExperimentFile file : targetFiles) {
            allExperimentIds.add(file.getExperimentId());
        }

        // 构建已提交文件的映射：学生ID -> 已提交的实验ID集合
        Map<String, Set<String>> submittedMap = new HashMap<>();
        for (ExperimentFile file : submittedFiles) {
            submittedMap.computeIfAbsent(file.getStudentId(), k -> new HashSet<>())
                    .add(file.getExperimentId());
        }

        // 计算每个学生的缺交实验
        for (String studentId : allStudentIds) {
            Set<String> submittedExps = submittedMap.getOrDefault(studentId, Collections.emptySet());
            List<String> missingExps = new ArrayList<>();

            for (String expId : allExperimentIds) {
                if (!submittedExps.contains(expId)) {
                    missingExps.add(expId);
                }
            }

            int missingCount = missingExps.size();
            if (missingCount > 0) {
                byStudent.put(studentId, missingCount);
                missingExpsByStudent.put(studentId, String.join("、", missingExps));
            } else {
                // 无缺交时从结果中移除（与测试用例逻辑一致）
                byStudent.remove(studentId);
                missingExpsByStudent.remove(studentId);
            }
        }

        // 计算每个实验的缺交学生
        for (String expId : allExperimentIds) {
            List<String> missingStudents = new ArrayList<>();

            for (String studentId : allStudentIds) {
                Set<String> submittedExps = submittedMap.getOrDefault(studentId, Collections.emptySet());
                if (!submittedExps.contains(expId)) {
                    missingStudents.add(studentId);
                }
            }

            int missingCount = missingStudents.size();
            if (missingCount > 0) {
                byExperiment.put(expId, missingCount);
                missingStudentsByExp.put(expId, missingStudents);
            } else {
                // 无缺交时从结果中移除（与测试用例逻辑一致）
                byExperiment.remove(expId);
                missingStudentsByExp.remove(expId);
            }
        }
    }

    // 校验提交文件中的学生ID是否存在
    private void validateSubmittedStudentIds() throws IllegalArgumentException {
        Set<String> validStudentIds = new HashSet<>();
        for (Student student : targetStudents) {
            validStudentIds.add(student.getStudentId());
        }

        for (ExperimentFile file : submittedFiles) {
            String studentId = file.getStudentId();
            if (!validStudentIds.contains(studentId)) {
                throw new IllegalArgumentException("提交文件中存在无效学号: " + studentId);
            }
        }
    }

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

    public Map<String, List<String>> getMissingStudentsByExp() {
        return missingStudentsByExp;
    }

    public void setMissingStudentsByExp(Map<String, List<String>> missingStudentsByExp) {
        this.missingStudentsByExp = missingStudentsByExp;
    }

    public List<ExperimentFile> getTargetFiles() {
        return targetFiles;
    }

    public void setTargetFiles(List<ExperimentFile> targetFiles) {
        this.targetFiles = targetFiles;
    }

    public List<ExperimentFile> getSubmittedFiles() {
        return submittedFiles;
    }

    public void setSubmittedFiles(List<ExperimentFile> submittedFiles) {
        this.submittedFiles = submittedFiles;
    }
}


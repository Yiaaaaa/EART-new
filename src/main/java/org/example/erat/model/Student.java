package org.example.erat.model;

import java.util.List;
import java.util.Objects;

public class Student {
    private String studentId;
    private String name;
    private String grade;
    private String major;
    private stuClass stuClass;  // 学生只能属于一个班级

    public Student(String studentId, String name, String grade, String major) {
        this.studentId = studentId;
        this.name = name;
        this.grade = grade;
        this.major = major;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getGrade() { return grade; }
    public String getMajor() { return major; }
    public stuClass getStuClass() { return stuClass; }

    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setName(String name) { this.name = name; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setMajor(String major) { this.major = major; }

    /**
     * 设置学生班级（内部方法，由stuClass.addStudent()调用）
     */
    protected void setStuClass(stuClass stuClass) {
        this.stuClass = stuClass;
    }

    /**
     * 获取班级信息（用于显示）
     */
    public String getClassInfo() {
        return stuClass == null ? "未分配" : stuClass.getClassName();
    }

    public static Student findStudentById(String studentId, List<Student> students) {
        return students.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentId.equals(student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
}
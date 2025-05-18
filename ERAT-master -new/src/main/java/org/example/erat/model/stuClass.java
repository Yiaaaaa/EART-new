package org.example.erat.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class stuClass {
    private String classId;
    private String className;
    private Set<Student> students;  // 使用Set避免重复

    public stuClass(String classId, String className) {
        this.classId = classId;
        this.className = className;
        this.students = new HashSet<>();
    }

    // ====== 基本Getter/Setter ======
    public String getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * 添加学生（双向关联）
     * 如果学生已属于其他班级，则先从原班级移除
     */
    public void addStudent(Student student) {
        if (student == null || students.contains(student)) {
            return;
        }

        // 如果学生已有班级，先从原班级移除
        if (student.getStuClass() != null) {
            student.getStuClass().removeStudent(student);
        }

        students.add(student);
        student.setStuClass(this);  // 设置学生的新班级
    }

    /**
     * 移除学生（双向操作）
     */
    public void removeStudent(Student student) {
        if (student == null || !students.contains(student)) {
            return;
        }

        students.remove(student);
        if (this.equals(student.getStuClass())) {
            student.setStuClass(null);  // 清除学生的班级引用
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        stuClass stuClass = (stuClass) o;
        return classId.equals(stuClass.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classId);
    }
}
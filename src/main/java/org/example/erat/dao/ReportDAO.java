package org.example.erat.dao;

import org.example.erat.model.ExperimentFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportDAO {
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^实验(\\d+)_(\\d+)_([\\u4e00-\\u9fa5]+)(\\.\\w+)$");

    public List<ExperimentFile> loadReportsFromDirectory(String dirPath) {
        List<ExperimentFile> reports = new ArrayList<>();
        File rootDir = new File(dirPath);

        if (!rootDir.exists()) {
            System.err.println("目录不存在: " + dirPath);
            return reports;
        }
        // 遍历课程→班级→实验目录结构
        for (File courseDir : getValidSubDirs(rootDir)) {
            for (File classDir : getValidSubDirs(courseDir)) {
                for (File expDir : getValidSubDirs(classDir)) {
                    processExperimentDir(
                            courseDir.getName(),
                            classDir.getName(),
                            expDir,
                            reports
                    );
                }
            }
        }
        return reports;
    }

    private void processExperimentDir(String courseName, String className, File expDir, List<ExperimentFile> reports) {
        for (File file : expDir.listFiles(File::isFile)) {
            Matcher matcher = FILE_NAME_PATTERN.matcher(file.getName());
            if (matcher.matches()) {
                reports.add(new ExperimentFile(
                        matcher.group(2),  // 学号
                        "实验" + matcher.group(1),  // 实验ID
                        matcher.group(3),  // 学生姓名
                        courseName,
                        className,
                        file.getAbsolutePath()
                ));
            } else {
                System.err.println("文件名格式不正确: " + file.getName());
            }
        }
    }

    private File[] getValidSubDirs(File dir) {
        return dir.listFiles(file -> file.isDirectory() && !file.getName().startsWith("."));
    }
}
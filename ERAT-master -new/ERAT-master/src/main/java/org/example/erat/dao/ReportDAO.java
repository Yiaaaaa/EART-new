package org.example.erat.dao;

import org.example.erat.exception.InvalidFileNameException;
import org.example.erat.model.ExperimentFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportDAO {

    /**
     * 从指定目录加载所有实验报告文件
     * 假设文件名为：学号_实验编号.pdf
     */
    public List<ExperimentFile> loadReportsFromDirectory(String dirPath) {
        List<ExperimentFile> reports = new ArrayList<>();
        File dir = new File(dirPath);

        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("指定的目录不存在或不是有效目录: " + dirPath);
            return reports;
        }

        // 文件名格式校验正则表达式：允许学号_实验编号.pdf 或 学号-实验编号.docx
        Pattern pattern = Pattern.compile("(\\d+)_(实验\\d+)(\\.\\w+)");

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.matches()) {
                    String studentId = matcher.group(1);
                    String experimentId = matcher.group(2);
                    reports.add(new ExperimentFile(studentId, experimentId));
                } else {
                    try {
                        throw new InvalidFileNameException("文件名不符合规范: " + file.getName());
                    } catch (InvalidFileNameException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return reports;
    }
}
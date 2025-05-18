package org.example.erat.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.erat.model.AnalysisResult;
import org.example.erat.model.Student;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExportUtil {

    public static void exportToExcel(AnalysisResult result, File outputFile) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 1. 创建学生缺交统计工作表
            Sheet studentSheet = workbook.createSheet("学生缺交统计");
            createStudentSheet(studentSheet, result);

            // 2. 创建实验缺交统计工作表
            Sheet experimentSheet = workbook.createSheet("实验缺交统计");
            createExperimentSheet(experimentSheet, result);

            // 3. 写入文件
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }
        }
    }

    public static void exportToCsv(AnalysisResult result, File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            // 1. 写入学生缺交统计
            fos.write("学生缺交统计\n".getBytes());
            writeStudentCsv(fos, result);

            // 2. 写入实验缺交统计
            fos.write("\n实验缺交统计\n".getBytes());
            writeExperimentCsv(fos, result);
        }
    }

    private static void createStudentSheet(Sheet sheet, AnalysisResult result) {
        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"学号", "姓名", "班级", "缺交次数", "缺交实验列表"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 填充数据
        int rowNum = 1;
        for (Map.Entry<String, Integer> entry : result.getByStudent().entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Student s = Student.findStudentById(entry.getKey(), result.getTargetStudents());

            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(s != null ? s.getName() : "未知");
            row.createCell(2).setCellValue(s != null && s.getStuClass() != null ?
                    s.getStuClass().getClassName() : "未分配");
            row.createCell(3).setCellValue(entry.getValue());
            row.createCell(4).setCellValue(result.getMissingExpsByStudent().get(entry.getKey()));
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void createExperimentSheet(Sheet sheet, AnalysisResult result) {
        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"实验编号", "缺交人数", "缺交学生列表"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 填充数据
        int rowNum = 1;
        for (Map.Entry<String, Integer> entry : result.getByExperiment().entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
            row.createCell(2).setCellValue(result.getMissingStudentsByExp().get(entry.getKey()));
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void writeStudentCsv(FileOutputStream fos, AnalysisResult result) throws IOException {
        // 写入标题行
        fos.write("学号,姓名,班级,缺交次数,缺交实验列表\n".getBytes());

        // 写入数据行
        for (Map.Entry<String, Integer> entry : result.getByStudent().entrySet()) {
            Student s = Student.findStudentById(entry.getKey(), result.getTargetStudents()); // 修改这里
            String line = String.format("%s,%s,%s,%d,%s\n",
                    entry.getKey(),
                    s != null ? s.getName() : "未知",
                    s != null && s.getStuClass() != null ? s.getStuClass().getClassName() : "未分配",
                    entry.getValue(),
                    result.getMissingExpsByStudent().get(entry.getKey()));
            fos.write(line.getBytes());
        }
    }

    private static void writeExperimentCsv(FileOutputStream fos, AnalysisResult result) throws IOException {
        // 写入标题行
        fos.write("实验编号,缺交人数,缺交学生列表\n".getBytes());

        // 写入数据行
        for (Map.Entry<String, Integer> entry : result.getByExperiment().entrySet()) {
            String line = String.format("%s,%d,%s\n",
                    entry.getKey(),
                    entry.getValue(),
                    result.getMissingStudentsByExp().get(entry.getKey()));
            fos.write(line.getBytes());
        }
    }
}
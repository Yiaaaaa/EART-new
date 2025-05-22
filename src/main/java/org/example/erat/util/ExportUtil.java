package org.example.erat.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.erat.model.AnalysisResult;
import org.example.erat.model.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ExportUtil {

    public enum ExportDimension {
        STUDENT, EXPERIMENT
    }

    public static void exportToExcel(AnalysisResult result, File outputFile, ExportDimension dimension) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle wrapStyle = workbook.createCellStyle();
            wrapStyle.setWrapText(true);

            if (dimension == ExportDimension.STUDENT) {
                Sheet studentSheet = workbook.createSheet("学生缺交统计");
                createStudentSheet(studentSheet, result, wrapStyle);
            } else if (dimension == ExportDimension.EXPERIMENT) {
                Sheet experimentSheet = workbook.createSheet("实验缺交统计");
                createExperimentSheet(experimentSheet, result, wrapStyle);
            }

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }
        }
    }

    public static void exportToCsv(AnalysisResult result, File outputFile, ExportDimension dimension) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {

            if (dimension == ExportDimension.STUDENT) {
                writer.println("学生缺交统计");
                writer.println("学号,姓名,班级,缺交次数,缺交实验列表");
                writeStudentCsv(writer, result);
            } else if (dimension == ExportDimension.EXPERIMENT) {
                writer.println("实验缺交统计");
                writer.println("实验编号,缺交人数,缺交学生列表");
                writeExperimentCsv(writer, result);
            }
        }
    }

    private static void createStudentSheet(Sheet sheet, AnalysisResult result, CellStyle wrapStyle) {
        // 创建标题行
        String[] headers = {"学号", "姓名", "班级", "缺交次数", "缺交实验列表"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 填充数据
        int rowNum = 1;
        for (Map.Entry<String, Integer> entry : result.getByStudent().entrySet()) {
            Row row = sheet.createRow(rowNum++);
            String studentId = entry.getKey();
            Student s = result.findStudentById(studentId, result.getTargetStudents());

            // 直接使用预计算的缺交实验列表
            String missingExps = result.getMissingExpsByStudent().getOrDefault(studentId, "无");

            row.createCell(0).setCellValue(studentId);
            row.createCell(1).setCellValue(s != null ? s.getName() : "未知");
            row.createCell(2).setCellValue(s != null && s.getStuClass() != null ?
                    s.getStuClass().getClassName() : "未分配");
            row.createCell(3).setCellValue(entry.getValue());

            Cell missingCell = row.createCell(4);
            missingCell.setCellValue(missingExps);
            missingCell.setCellStyle(wrapStyle);
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, i == 4 ? 30 * 256 : 15 * 256);
        }
    }

    private static void createExperimentSheet(Sheet sheet, AnalysisResult result, CellStyle wrapStyle) {
        String[] headers = {"实验编号", "缺交人数", "缺交学生列表"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (Map.Entry<String, Integer> entry : result.getByExperiment().entrySet()) {
            Row row = sheet.createRow(rowNum++);
            String expId = entry.getKey();

            // 获取缺交学生姓名列表
            String missingStudents = result.getMissingStudentsByExp().getOrDefault(expId, List.of())
                    .stream()
                    .map(id -> {
                        Student s = result.findStudentById(id, result.getTargetStudents());
                        return s != null ? s.getName() : id;
                    })
                    .collect(Collectors.joining(", "));

            row.createCell(0).setCellValue(expId);
            row.createCell(1).setCellValue(entry.getValue());

            Cell studentsCell = row.createCell(2);
            studentsCell.setCellValue(missingStudents.isEmpty() ? "无" : missingStudents);
            studentsCell.setCellStyle(wrapStyle);
        }

        // 调整列宽
        sheet.setColumnWidth(2, 40 * 256);
    }

    private static void writeStudentCsv(PrintWriter writer, AnalysisResult result) {
        for (Map.Entry<String, Integer> entry : result.getByStudent().entrySet()) {
            String studentId = entry.getKey();
            Student s = result.findStudentById(studentId, result.getTargetStudents());

            writer.printf("%s,%s,%s,%d,\"%s\"%n",
                    studentId,
                    escapeCsv(s != null ? s.getName() : "未知"),
                    escapeCsv(s != null && s.getStuClass() != null ?
                            s.getStuClass().getClassName() : "未分配"),
                    entry.getValue(),
                    escapeCsv(result.getMissingExpsByStudent().getOrDefault(studentId, "无")));
        }
    }

    private static void writeExperimentCsv(PrintWriter writer, AnalysisResult result) {
        for (Map.Entry<String, Integer> entry : result.getByExperiment().entrySet()) {
            String expId = entry.getKey();

            String missingStudents = result.getMissingStudentsByExp().getOrDefault(expId, List.of())
                    .stream()
                    .map(id -> {
                        Student s = result.findStudentById(id, result.getTargetStudents());
                        return s != null ? escapeCsv(s.getName()) : id;
                    })
                    .collect(Collectors.joining(", "));

            writer.printf("%s,%d,\"%s\"%n",
                    expId,
                    entry.getValue(),
                    missingStudents.isEmpty() ? "无" : missingStudents);
        }
    }

    private static String escapeCsv(String input) {
        return input == null ? "" : input.replace("\"", "\"\"");
    }
}
package org.example.erat.parser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.erat.model.Student;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class ExcelParser implements FileParser {
    @Override
    public List<Student> parseStudents(File file) throws Exception {
        List<Student> students = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(file))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                String id = row.getCell(0).getStringCellValue();
                String name = row.getCell(1).getStringCellValue();
                students.add(new Student(id, name));
            }
        }
        return students;
    }
}
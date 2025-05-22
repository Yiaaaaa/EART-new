package org.example.erat.parser;

import com.opencsv.CSVReader;
import org.example.erat.model.Student;

import java.io.FileReader;
import java.io.File;
import java.util.*;

public class CSVParser implements FileParser {
    @Override
    public List<Student> parseStudents(File file) throws Exception {
        List<Student> students = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                String id = row[0];
                String name = row[1];
                String grade = row[2];
                String major = row[3];
                students.add(new Student(id, name, grade, major));
            }
        }
        return students;
    }
}
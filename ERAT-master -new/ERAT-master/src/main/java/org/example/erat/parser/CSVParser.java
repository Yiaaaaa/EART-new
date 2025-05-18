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
                students.add(new Student(row[0], row[1]));
            }
        }
        return students;
    }
}
package org.example.erat.parser;

import org.example.erat.model.Student;

import java.io.File;
import java.util.List;

public interface FileParser {
    List<Student> parseStudents(File file) throws Exception;
}
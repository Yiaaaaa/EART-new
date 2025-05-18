package org.example.erat.parser;

import java.io.File;

public class ParserFactory {
    public static FileParser createParser(File file) {
        if (file.getName().endsWith(".xlsx")) {
            return new ExcelParser();
        } else if (file.getName().endsWith(".csv")) {
            return new CSVParser();
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }
}
package org.example.erat.ui;

import org.example.erat.chart.ChartGenerator;
import org.example.erat.dao.ReportDAO;
import org.example.erat.model.ExperimentFile;
import org.example.erat.model.Student;
import org.example.erat.parser.ParserFactory;
import org.example.erat.parser.FileParser;
import org.example.erat.service.AnalysisService;
import org.example.erat.exception.InvalidFileNameException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainWindow extends JFrame {
    private List<Student> students = new ArrayList<>();

    public MainWindow() {
        setTitle("实验报告统计分析工具 (ERAT v0.1)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JButton importBtn = new JButton("导入学生名单");
        JButton analyzeBtn = new JButton("分析缺交情况");
        JButton chartBtn = new JButton("生成对应图");

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);

        importBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int result = fc.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fc.getSelectedFile();
                    FileParser parser = ParserFactory.createParser(file);
                    students = parser.parseStudents(file);
                    JOptionPane.showMessageDialog(this, "成功导入" + students.size() + "名学生");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "导入失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        analyzeBtn.addActionListener(e -> {
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请先导入学生名单！");
                return;
            }

            List<ExperimentFile> reports = new ReportDAO().loadReportsFromDirectory("reports/");
            AnalysisService service = new AnalysisService();

            Map<String, Integer> byStudent = service.analyzeMissingByStudent(students, reports);
            Map<String, Integer> byExperiment = service.analyzeMissingByExperiment(students, reports);

            StringBuilder sb = new StringBuilder();
            sb.append("| 学号 | 缺交次数 |\n| --- | --- |\n");
            for (Map.Entry<String, Integer> entry : byStudent.entrySet()) {
                sb.append("| ").append(entry.getKey()).append(" | ").append(entry.getValue()).append(" |\n");
            }

            sb.append("\n| 实验编号 | 缺交人数 |\n| --- | --- |\n");
            for (Map.Entry<String, Integer> entry : byExperiment.entrySet()) {
                sb.append("| ").append(entry.getKey()).append(" | ").append(entry.getValue()).append(" |\n");
            }

            outputArea.setText(sb.toString());
        });

        chartBtn.addActionListener(e -> {
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请先导入学生名单！");
                return;
            }

            List<ExperimentFile> reports = new ReportDAO().loadReportsFromDirectory("reports/");
            AnalysisService service = new AnalysisService();

            try {
                Map<String, Double> rates = service.analyzeSubmissionRates(students, reports);
                if (rates.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "未找到任何实验记录！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                ChartGenerator.generateLineChart(rates); // 调用图表生成
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "生成图表失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(importBtn, BorderLayout.NORTH);
        panel.add(analyzeBtn, BorderLayout.CENTER);
        panel.add(chartBtn, BorderLayout.SOUTH);
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}
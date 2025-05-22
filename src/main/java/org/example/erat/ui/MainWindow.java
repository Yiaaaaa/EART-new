package org.example.erat.ui;

import org.example.erat.chart.ChartGenerator;
import org.example.erat.dao.ReportDAO;
import org.example.erat.model.*;
import org.example.erat.parser.ParserFactory;
import org.example.erat.parser.FileParser;
import org.example.erat.service.AnalysisService;
import org.example.erat.util.ExportUtil;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {
    private List<Student> students = new ArrayList<>();
    private List<stuClass> classes = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private AnalysisResult currentAnalysisResult;

    //private stuClassService classService = new stuClassService();
    //private CourseService courseService = new CourseService();

    private JTextArea outputArea = new JTextArea();

    // 主功能按钮
    private JButton studentManagementBtn;
    private JButton classManagementBtn;
    private JButton courseManagementBtn;

    // 学生管理按钮
    private JButton importStuBtn;
    private JButton addStuBtn;
    private JButton deleteStuBtn;
    private JButton viewStudentsBtn;  // 查看学生列表按钮

    // 班级管理按钮
    private JButton addClassBtn;
    private JButton editClassBtn;
    private JButton deleteClassBtn;
    private JButton viewClassesBtn;

    // 课程管理按钮
    private JButton addCourseBtn;
    private JButton editCourseBtn;
    private JButton deleteCourseBtn;
    private JButton viewCoursesBtn;

    // 分析功能按钮
    private JButton analyzeBtn;
    private JButton chartBtn;
    private JButton exportBtn;

    private JPanel cardPanel; // 卡片面板
    private CardLayout cardLayout; // 卡片布局

    public MainWindow() {
        setTitle("实验报告统计分析工具 (ERAT v0.1)");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        initUI();
    }

    private void initComponents() {
        // 初始化所有按钮
        studentManagementBtn = new JButton("学生管理");
        classManagementBtn = new JButton("班级管理");
        courseManagementBtn = new JButton("课程管理");
        analyzeBtn = new JButton("分析缺交情况");
        chartBtn = new JButton("生成对应图");
        exportBtn=new JButton("导出结果");

        importStuBtn = new JButton("导入学生名单");
        addStuBtn = new JButton("添加学生信息");
        deleteStuBtn = new JButton("删除学生信息");
        viewStudentsBtn = new JButton("查看学生列表");

        addClassBtn = new JButton("新增班级");
        editClassBtn = new JButton("编辑班级");
        deleteClassBtn = new JButton("删除班级");
        viewClassesBtn = new JButton("查看班级列表");

        addCourseBtn = new JButton("新增课程");
        editCourseBtn = new JButton("编辑课程");
        deleteCourseBtn = new JButton("删除课程");
        viewCoursesBtn = new JButton("查看课程列表");

        // 设置按钮大小
        Dimension buttonSize = new Dimension(300, 40);
        studentManagementBtn.setPreferredSize(buttonSize);
        classManagementBtn.setPreferredSize(buttonSize);
        courseManagementBtn.setPreferredSize(buttonSize);
        analyzeBtn.setPreferredSize(buttonSize);
        chartBtn.setPreferredSize(buttonSize);
        exportBtn.setPreferredSize(buttonSize);

        importStuBtn.setPreferredSize(buttonSize);
        addStuBtn.setPreferredSize(buttonSize);
        deleteStuBtn.setPreferredSize(buttonSize);

        addClassBtn.setPreferredSize(buttonSize);
        editClassBtn.setPreferredSize(buttonSize);
        deleteClassBtn.setPreferredSize(buttonSize);
        viewClassesBtn.setPreferredSize(buttonSize);

        addCourseBtn.setPreferredSize(buttonSize);
        editCourseBtn.setPreferredSize(buttonSize);
        deleteCourseBtn.setPreferredSize(buttonSize);
        viewCoursesBtn.setPreferredSize(buttonSize);
    }

    private void initUI() {
        // 主面板使用BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建左侧面板，包含主菜单和功能操作面板
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(350, getHeight()));

        // 主菜单面板
        JPanel mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new BoxLayout(mainMenuPanel, BoxLayout.Y_AXIS));
        mainMenuPanel.setBorder(BorderFactory.createTitledBorder("主菜单"));
        mainMenuPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 左对齐
        mainMenuPanel.add(Box.createVerticalStrut(15));
        mainMenuPanel.add(studentManagementBtn);
        mainMenuPanel.add(Box.createVerticalStrut(15));
        mainMenuPanel.add(classManagementBtn);
        mainMenuPanel.add(Box.createVerticalStrut(  15));
        mainMenuPanel.add(courseManagementBtn);
        mainMenuPanel.add(Box.createVerticalStrut(15));
        mainMenuPanel.add(analyzeBtn);
        mainMenuPanel.add(Box.createVerticalStrut(15));
        mainMenuPanel.add(chartBtn);
        mainMenuPanel.add(Box.createVerticalStrut(15));
        mainMenuPanel.add(exportBtn);

        // 功能操作面板 - 现在放在主菜单下方
        cardPanel = new JPanel(cardLayout = new CardLayout());
        initializeCardPanel(); // 初始化功能按钮面板
        JPanel functionPanel = new JPanel(new BorderLayout());
        functionPanel.setBorder(BorderFactory.createTitledBorder("功能操作"));
        functionPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 左对齐
        functionPanel.add(cardPanel, BorderLayout.CENTER);

        // 将主菜单面板和功能操作面板添加到左侧面板
        leftPanel.add(mainMenuPanel);
        leftPanel.add(Box.createVerticalStrut(20)); // 增加间距
        leftPanel.add(functionPanel);

        // 创建右侧内容区域
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));

        // 输出区域
        outputArea.setEditable(false);
        outputArea.setFont(new Font("宋体", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(700, 400));

        // 组装右侧面板
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // 使用JSplitPane将左侧面板和右侧面板垂直分割
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(150); // 设置分割条的初始位置
        splitPane.setDividerSize(5); // 设置分割条的大小
        splitPane.setResizeWeight(0.5); // 设置左侧面板和右侧面板的大小比例

        // 添加分割面板到主面板
        mainPanel.add(splitPane);

        // 添加主面板到窗口
        add(mainPanel);

        // 设置事件监听
        setupEventListeners();
    }

    private void initializeCardPanel() {
        // 学生管理面板
        JPanel studentPanel = new JPanel();
        studentPanel.setLayout(new BoxLayout(studentPanel, BoxLayout.Y_AXIS));
        studentPanel.add(Box.createVerticalStrut(15));
        studentPanel.add(importStuBtn);
        studentPanel.add(Box.createVerticalStrut(15));
        studentPanel.add(addStuBtn);
        studentPanel.add(Box.createVerticalStrut(15));
        studentPanel.add(deleteStuBtn);
        studentPanel.add(Box.createVerticalStrut(15));
        studentPanel.add(viewStudentsBtn);
        studentPanel.add(Box.createVerticalStrut(15));

        // 班级管理面板
        JPanel classPanel = new JPanel();
        classPanel.setLayout(new BoxLayout(classPanel, BoxLayout.Y_AXIS));
        classPanel.add(Box.createVerticalStrut(15));
        classPanel.add(addClassBtn);
        classPanel.add(Box.createVerticalStrut(15));
        classPanel.add(editClassBtn);
        classPanel.add(Box.createVerticalStrut(15));
        classPanel.add(deleteClassBtn);
        classPanel.add(Box.createVerticalStrut(15));
        classPanel.add(viewClassesBtn);
        classPanel.add(Box.createVerticalStrut(15));

        // 课程管理面板
        JPanel coursePanel = new JPanel();
        coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));
        coursePanel.add(Box.createVerticalStrut(15));
        coursePanel.add(addCourseBtn);
        coursePanel.add(Box.createVerticalStrut(15));
        coursePanel.add(editCourseBtn);
        coursePanel.add(Box.createVerticalStrut(15));
        coursePanel.add(deleteCourseBtn);
        coursePanel.add(Box.createVerticalStrut(15));
        coursePanel.add(viewCoursesBtn);
        coursePanel.add(Box.createVerticalStrut(15));

        // 空面板（用于隐藏功能按钮）
        JPanel emptyPanel = new JPanel();

        // 添加面板到卡片布局
        cardPanel.add(new JPanel(), "empty");
        cardPanel.add(studentPanel, "student");
        cardPanel.add(classPanel, "class");
        cardPanel.add(coursePanel, "course");

        cardLayout.show(cardPanel, "empty");
    }

    private void setupEventListeners() {
        // 主菜单按钮事件
        studentManagementBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "student");
        });

        classManagementBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "class");
        });

        courseManagementBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "course");
        });

        importStuBtn.addActionListener(e -> {
            // 文件选择对话框
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fc.getSelectedFile();
                    FileParser parser = ParserFactory.createParser(file);
                    List<Student> importedStudents = new ArrayList<>(parser.parseStudents(file));

                    if (importedStudents.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "导入的文件中没有学生数据",
                                "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // 初始化学生列表（如果为空）
                    if (students == null)  students = new ArrayList<>();

                    // 处理重复学生
                    List<Student> uniqueStudents = new ArrayList<>();
                    List<Student> duplicateStudents = new ArrayList<>();

                    for (Student newStudent : importedStudents) {
                        boolean exists = students.stream()
                                .anyMatch(s -> s.getStudentId().equals(newStudent.getStudentId()));
                        if (exists) {
                            duplicateStudents.add(newStudent);
                        } else {
                            uniqueStudents.add(newStudent);
                        }
                    }

                    // 添加非重复学生到全局列表
                    students.addAll(uniqueStudents);

                    // 构建结果显示
                    StringBuilder result = new StringBuilder();
                    result.append("=== 学生导入结果 ===\n")
                            .append("文件路径: ").append(file.getAbsolutePath()).append("\n")
                            .append("解析成功: ").append(importedStudents.size()).append("名学生\n")
                            .append("新增学生: ").append(uniqueStudents.size()).append("名\n")
                            .append("跳过重复: ").append(duplicateStudents.size()).append("名\n\n")
                            .append("新增学生列表:\n");

                    // 添加学生详情
                    uniqueStudents.forEach(student -> {
                        result.append("  • ")
                                .append(student.getStudentId())
                                .append(" - ")
                                .append(student.getName())
                                .append(" - ")
                                .append(student.getGrade())
                                .append(" - ")
                                .append(student.getMajor())
                                .append("\n");
                    });

                    // 添加重复学生信息（如果有）
                    if (!duplicateStudents.isEmpty()) {
                        result.append("\n重复学生（未导入）:\n");
                        duplicateStudents.forEach(student -> {
                            result.append("  • ")
                                    .append(student.getStudentId())
                                    .append(" - ")
                                    .append(student.getName())
                                    .append("\n");
                        });
                    }

                    // 如果有班级存在，询问是否分配
                    if (!classes.isEmpty()) {
                        int option = JOptionPane.showConfirmDialog(
                                this,
                                "是否将学生分配到现有班级？",
                                "班级分配",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (option == JOptionPane.YES_OPTION) {
                            // 创建班级选择对话框（直接内联实现）
                            JDialog classDialog = new JDialog(this, "分配学生到班级", true);
                            classDialog.setLayout(new BorderLayout(10, 10));
                            classDialog.setSize(500, 400);

                            // 主面板
                            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
                            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                            // 信息显示
                            JTextArea infoArea = new JTextArea(result.toString());
                            infoArea.setEditable(false);
                            mainPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

                            // 班级选择
                            JPanel classPanel = new JPanel(new GridLayout(2, 1, 5, 5));
                            classPanel.add(new JLabel("选择目标班级:"));

                            DefaultComboBoxModel<String> classModel = new DefaultComboBoxModel<>();
                            classes.forEach(c -> classModel.addElement(c.getClassName() + " (ID:" + c.getClassId() + ")"));
                            JComboBox<String> classCombo = new JComboBox<>(classModel);
                            classPanel.add(classCombo);
                            mainPanel.add(classPanel, BorderLayout.NORTH);

                            // 按钮面板
                            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                            JButton confirmBtn = new JButton("确认分配");
                            JButton cancelBtn = new JButton("取消");

                            confirmBtn.addActionListener(ev -> {
                                String selected = (String) classCombo.getSelectedItem();
                                String[] parts = selected.replace(" (ID:", ",").replace(")", "").split(",");
                                String className = parts[0];
                                String classId = parts[1];

                                classes.stream()
                                        .filter(c -> c.getClassId().equals(classId))
                                        .findFirst()
                                        .ifPresent(targetClass -> {
                                            uniqueStudents.forEach(targetClass::addStudent);
                                            result.append("\n\n=== 分配结果 ===\n")
                                                    .append("班级: ").append(className).append("\n")
                                                    .append("新增学生: ").append(uniqueStudents.size()).append("\n")
                                                    .append("班级总人数: ").append(targetClass.getStudents().size());
                                            outputArea.setText(result.toString());
                                        });
                                classDialog.dispose();
                            });

                            cancelBtn.addActionListener(ev -> {
                                result.append("\n\n提示：学生未分配到班级");
                                outputArea.setText(result.toString());
                                classDialog.dispose();
                            });

                            buttonPanel.add(cancelBtn);
                            buttonPanel.add(confirmBtn);
                            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

                            classDialog.add(mainPanel);
                            classDialog.setLocationRelativeTo(this);
                            classDialog.setVisible(true);
                        } else {
                            result.append("\n\n提示：学生未分配到班级");
                            outputArea.setText(result.toString());
                        }
                    } else {
                        result.append("\n\n提示：系统中暂无班级，学生未分配");
                        outputArea.setText(result.toString());
                    }

                    // 成功提示
                    JOptionPane.showMessageDialog(this,
                            String.format("成功导入 %d 名学生", uniqueStudents.size()),
                            "导入完成",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    // 错误处理
                    String errorMsg = "导入失败: " + ex.getMessage();
                    outputArea.setText(errorMsg + "\n");
                    JOptionPane.showMessageDialog(this,
                            errorMsg,
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        addStuBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "添加学生信息", true);
            dialog.setSize(400, 300);
            dialog.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel idLabel = new JLabel("学号(12位):");
            JTextField idField = new JTextField();
            JLabel nameLabel = new JLabel("姓名:");
            JTextField nameField = new JTextField();
            JLabel gradeLabel = new JLabel("年级:");
            JTextField gradeField = new JTextField();
            JLabel majorLabel = new JLabel("专业:");
            JTextField majorField = new JTextField();

            formPanel.add(idLabel);
            formPanel.add(idField);
            formPanel.add(nameLabel);
            formPanel.add(nameField);
            formPanel.add(gradeLabel);
            formPanel.add(gradeField);
            formPanel.add(majorLabel);
            formPanel.add(majorField);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton confirmBtn = new JButton("确认");
            JButton cancelBtn = new JButton("取消");

            confirmBtn.addActionListener(ev -> {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String grade = gradeField.getText().trim();
                String major = majorField.getText().trim();

                if (id.isEmpty() || name.isEmpty() || grade.isEmpty() || major.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写完整的学生信息！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (id.length() != 12) {
                    JOptionPane.showMessageDialog(dialog, "学号必须为12位数字！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 检查学号是否已存在
                boolean idExists = students.stream()
                        .anyMatch(s -> s.getStudentId().equals(id));

                if (idExists) {
                    JOptionPane.showMessageDialog(dialog,
                            "学号 " + id + " 已存在，不能重复添加！",
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Student newStudent = new Student(id, name, grade, major);
                students.add(newStudent);
                outputArea.append("添加学生: " + id + " - " + name + "\n");
                dialog.dispose();
            });

            cancelBtn.addActionListener(ev -> dialog.dispose());

            buttonPanel.add(confirmBtn);
            buttonPanel.add(cancelBtn);

            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        deleteStuBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "请输入要删除的学生学号:");
            if (id == null || id.isEmpty()) return;

            boolean found = false;
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getStudentId().equals(id)) {
                    students.remove(i);
                    outputArea.append("已删除学号为 " + id + " 的学生\n");
                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "未找到该学号的学生信息", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewStudentsBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("=== 学生列表 ===\n");
            sb.append(String.format("%-12s %-15s %-8s %-20s %-15s\n",
                    "学号", "姓名", "年级", "专业", "所属班级"));
            sb.append("--------------------------------------------------\n");

            for (Student student : students) {
                sb.append(String.format("%-12s %-15s %-8s %-20s %-15s\n",
                        student.getStudentId(),
                        student.getName(),
                        student.getGrade(),
                        student.getMajor(),
                        student.getClassInfo()));  // 使用getClassInfo()显示多个班级
            }

            outputArea.setText(sb.toString());
        });


        addClassBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "新增班级", true);
            dialog.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
            JLabel idLabel = new JLabel("班级编号:");
            JTextField idField = new JTextField();
            JLabel nameLabel = new JLabel("班级名称:");
            JTextField nameField = new JTextField();

            formPanel.add(idLabel);
            formPanel.add(idField);
            formPanel.add(nameLabel);
            formPanel.add(nameField);

            JLabel studentLabel = new JLabel("选择学生（按住Ctrl键可多选，可选）:");

            // 先按学号排序学生列表
            List<Student> sortedStudents = new ArrayList<>(students);
            sortedStudents.sort(Comparator.comparing(Student::getStudentId));

            // 获取未分配班级的学生（如果全部已分配，则显示全部并提示）
            List<Student> availableStudents = sortedStudents.stream()
                    .filter(student -> student.getClassInfo() == null || student.getClassInfo().isEmpty())
                    .collect(Collectors.toList());

            // 如果没有未分配的学生，使用全部学生并显示警告
            if (availableStudents.isEmpty()) {
                availableStudents = sortedStudents;
                studentLabel.setText("<html>选择学生（所有学生已有班级，将重新分配）:<br><font color='red'>注意：这将把学生从原班级移除</font></html>");
            }

            JList<Student> studentList = new JList<>(new Vector<>(availableStudents));
            studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            // 设置自定义渲染器，显示"学号 - 姓名 - 当前班级"
            studentList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Student) {
                        Student student = (Student) value;
                        String classInfo = student.getClassInfo();
                        setText(student.getStudentId() + " - " + student.getName() +
                                (classInfo != null && !classInfo.isEmpty() ? " (" + classInfo + ")" : ""));
                    }
                    return this;
                }
            });

            JScrollPane studentScroll = new JScrollPane(studentList);
            studentScroll.setPreferredSize(new Dimension(300, 150));

            formPanel.add(studentLabel);
            formPanel.add(studentScroll);

            JButton confirmBtn = new JButton("确认");
            confirmBtn.addActionListener(ev -> {
                String classId = idField.getText().trim();
                String className = nameField.getText().trim();

                if (classId.isEmpty() || className.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写完整班级信息");
                    return;
                }

                List<Student> selectedStudents = studentList.getSelectedValuesList();

                stuClass newClass = new stuClass(classId, className);

                // 处理学生分配（包括从原班级移除）
                for (Student student : selectedStudents) {
                    // 如果学生已有班级，先从原班级移除
                    if (student.getClassInfo() != null && !student.getClassInfo().isEmpty()) {
                        classes.stream()
                                .filter(c -> c.getStudents().contains(student))
                                .findFirst()
                                .ifPresent(c -> c.removeStudent(student));
                    }
                    // 添加到新班级
                    newClass.addStudent(student);
                }

                classes.add(newClass);
                outputArea.append("新增班级: " + className + " (ID:" + classId + ")，包含" +
                        selectedStudents.size() + "名学生\n");
                dialog.dispose();
            });

            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(confirmBtn, BorderLayout.SOUTH);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        editClassBtn.addActionListener(e -> {
            if (classes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "当前没有班级数据", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 创建选择班级对话框
            JDialog selectDialog = new JDialog(this, "选择要编辑的班级", true);
            selectDialog.setLayout(new BorderLayout());

            // 创建班级列表
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (stuClass cls : classes) {
                listModel.addElement(cls.getClassId() + " - " + cls.getClassName() + " (" + cls.getStudents().size() + "名学生)");
            }

            JList<String> classList = new JList<>(listModel);
            classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton selectBtn = new JButton("选择");
            JButton cancelBtn = new JButton("取消");

            selectBtn.addActionListener(ev -> {
                int selectedIndex = classList.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(selectDialog, "请选择一个班级", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                stuClass selectedClass = classes.get(selectedIndex);
                selectDialog.dispose();

                JDialog editDialog = new JDialog(this, "编辑班级信息", true);
                editDialog.setLayout(new BorderLayout());

                JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 10));
                infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                JLabel idLabel = new JLabel("班级编号:");
                JTextField idField = new JTextField(selectedClass.getClassId());
                idField.setEditable(false); // 编号不可编辑
                JLabel nameLabel = new JLabel("班级名称:");
                JTextField nameField = new JTextField(selectedClass.getClassName());

                infoPanel.add(idLabel);
                infoPanel.add(idField);
                infoPanel.add(nameLabel);
                infoPanel.add(nameField);

                JLabel studentLabel = new JLabel("班级学生（按住Ctrl键可多选）:");

                // 获取当前班级学生
                Set<Student> classStudents = selectedClass.getStudents();

                // 获取可选学生（系统所有学生排除当前班级学生）
                List<Student> availableStudents = new ArrayList<>(students);
                availableStudents.removeAll(classStudents);

                // 创建学生列表
                DefaultListModel<Student> studentModel = new DefaultListModel<>();
                classStudents.forEach(studentModel::addElement);

                JList<Student> studentList = new JList<>(studentModel);
                studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

                // 设置自定义渲染器，显示"学号 - 姓名"
                studentList.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list, Object value, int index,
                            boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof Student) {
                            Student student = (Student) value;
                            setText(student.getStudentId() + " - " + student.getName());
                        }
                        return this;
                    }
                });

                JScrollPane studentScroll = new JScrollPane(studentList);
                studentScroll.setPreferredSize(new Dimension(300, 150));

                JPanel studentBtnPanel = new JPanel(new FlowLayout());
                JButton addBtn = new JButton("添加学生");
                JButton removeBtn = new JButton("移除选中");

                addBtn.addActionListener(addEv -> {
                    if (availableStudents.isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog, "没有可添加的学生", "提示", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    JDialog addStudentDialog = new JDialog(editDialog, "添加学生到班级", true);
                    addStudentDialog.setLayout(new BorderLayout());

                    // 创建可选学生列表
                    DefaultListModel<Student> availableModel = new DefaultListModel<>();
                    availableStudents.forEach(availableModel::addElement);

                    JList<Student> availableList = new JList<>(availableModel);
                    availableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

                    // 设置自定义渲染器
                    availableList.setCellRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(
                                JList<?> list, Object value, int index,
                                boolean isSelected, boolean cellHasFocus) {
                            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            if (value instanceof Student) {
                                Student student = (Student) value;
                                setText(student.getStudentId() + " - " + student.getName());
                            }
                            return this;
                        }
                    });

                    JScrollPane availableScroll = new JScrollPane(availableList);

                    JPanel addBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton confirmAddBtn = new JButton("确认添加");
                    JButton cancelAddBtn = new JButton("取消");

                    confirmAddBtn.addActionListener(confirmAddEv -> {
                        List<Student> selectedToAdd = availableList.getSelectedValuesList();
                        if (selectedToAdd.isEmpty()) {
                            JOptionPane.showMessageDialog(addStudentDialog, "请选择要添加的学生", "提示", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        // 添加学生到班级
                        for (Student student : selectedToAdd) {
                            // 如果学生已有班级，先从原班级移除
                            if (student.getClassInfo() != null && !student.getClassInfo().isEmpty()) {
                                classes.stream()
                                        .filter(c -> c.getStudents().contains(student))
                                        .findFirst()
                                        .ifPresent(c -> c.removeStudent(student));
                            }
                            // 添加到当前编辑的班级
                            selectedClass.addStudent(student);
                            studentModel.addElement(student);
                            availableModel.removeElement(student);
                        }

                        availableStudents.removeAll(selectedToAdd);
                        addStudentDialog.dispose();
                    });

                    cancelAddBtn.addActionListener(cancelAddEv -> addStudentDialog.dispose());

                    addBtnPanel.add(cancelAddBtn);
                    addBtnPanel.add(confirmAddBtn);

                    addStudentDialog.add(new JLabel("选择要添加的学生:"), BorderLayout.NORTH);
                    addStudentDialog.add(availableScroll, BorderLayout.CENTER);
                    addStudentDialog.add(addBtnPanel, BorderLayout.SOUTH);
                    addStudentDialog.setSize(400, 300);
                    addStudentDialog.setLocationRelativeTo(editDialog);
                    addStudentDialog.setVisible(true);
                });

                removeBtn.addActionListener(removeEv -> {
                    List<Student> selectedToRemove = studentList.getSelectedValuesList();
                    if (selectedToRemove.isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog, "请选择要移除的学生", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // 从班级移除学生
                    for (Student student : selectedToRemove) {
                        selectedClass.removeStudent(student);
                        studentModel.removeElement(student);
                        availableStudents.add(student);
                    }
                });

                studentBtnPanel.add(addBtn);
                studentBtnPanel.add(removeBtn);

                JPanel studentPanel = new JPanel(new BorderLayout());
                studentPanel.add(studentLabel, BorderLayout.NORTH);
                studentPanel.add(studentScroll, BorderLayout.CENTER);
                studentPanel.add(studentBtnPanel, BorderLayout.SOUTH);

                JPanel editButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton saveBtn = new JButton("保存");
                JButton editCancelBtn = new JButton("取消");

                saveBtn.addActionListener(saveEv -> {
                    String newName = nameField.getText().trim();
                    if (newName.isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog, "班级名称不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    selectedClass.setClassName(newName);
                    outputArea.append("修改班级: " + selectedClass.getClassId() + " -> " + newName + "，当前" +
                            selectedClass.getStudents().size() + "名学生\n");
                    editDialog.dispose();
                });

                editCancelBtn.addActionListener(cancelEv -> editDialog.dispose());

                editButtonPanel.add(saveBtn);
                editButtonPanel.add(editCancelBtn);

                // 添加所有面板到对话框
                JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoPanel, studentPanel);
                splitPane.setDividerLocation(100);
                splitPane.setResizeWeight(0.3);

                editDialog.add(splitPane, BorderLayout.CENTER);
                editDialog.add(editButtonPanel, BorderLayout.SOUTH);
                editDialog.setSize(500, 450);
                editDialog.setLocationRelativeTo(this);
                editDialog.setVisible(true);
            });

            cancelBtn.addActionListener(ev -> selectDialog.dispose());

            buttonPanel.add(selectBtn);
            buttonPanel.add(cancelBtn);

            selectDialog.add(new JScrollPane(classList), BorderLayout.CENTER);
            selectDialog.add(buttonPanel, BorderLayout.SOUTH);
            selectDialog.setSize(300, 300);
            selectDialog.setLocationRelativeTo(this);
            selectDialog.setVisible(true);
        });

        deleteClassBtn.addActionListener(e -> {
            if (classes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "当前没有班级数据", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog(this, "删除班级", true);
            dialog.setLayout(new BorderLayout());

            // 创建班级列表（显示班级ID、名称和学生数量）
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (stuClass cls : classes) {
                listModel.addElement(cls.getClassId() + " - " + cls.getClassName() + " (" + cls.getStudents().size() + "名学生)");
            }

            JList<String> classList = new JList<>(listModel);
            classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton deleteBtn = new JButton("删除");
            JButton cancelBtn = new JButton("取消");

            deleteBtn.addActionListener(ev -> {
                int selectedIndex = classList.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(dialog, "请选择一个班级", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                stuClass selectedClass = classes.get(selectedIndex);

                StringBuilder confirmMsg = new StringBuilder();
                confirmMsg.append("确定要删除班级: ")
                        .append(selectedClass.getClassId())
                        .append(" - ")
                        .append(selectedClass.getClassName())
                        .append("?\n\n")
                        .append("该班级有 ")
                        .append(selectedClass.getStudents().size())
                        .append(" 名学生，删除后这些学生将变为未分配状态。");

                int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        confirmMsg.toString(),
                        "确认删除",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    // 创建学生副本列表
                    List<Student> studentsToRemove = new ArrayList<>(selectedClass.getStudents());

                    // 从班级中移除所有学生（修改处）
                    for (Student student : studentsToRemove) {
                        selectedClass.removeStudent(student); // 直接调用，不使用返回值
                    }

                    classes.remove(selectedIndex);

                    // 更新输出信息
                    outputArea.append("已删除班级: " +
                            selectedClass.getClassId() +
                            " - " +
                            selectedClass.getClassName() +
                            "，" +
                            studentsToRemove.size() +
                            "名学生已变为未分配状态\n");

                    dialog.dispose();
                }
            });

            cancelBtn.addActionListener(ev -> dialog.dispose());

            buttonPanel.add(deleteBtn);
            buttonPanel.add(cancelBtn);

            dialog.add(new JScrollPane(classList), BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setSize(400, 300);  // 增加宽度以显示完整信息
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        viewClassesBtn.addActionListener(e -> {
            if (classes.isEmpty()) {
                outputArea.setText("当前没有班级数据\n");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("=== 班级详细信息 ===\n");

            sb.append(String.format("%-12s %-20s %-8s %-15s %s\n",
                    "班级编号", "班级名称", "年级", "专业", "学生列表"));
            sb.append("-----------------------------------------------------------------\n");

            for (stuClass cls : classes) {
                // 获取班级的代表性年级和专业（取第一个学生的信息）
                String classGrade = "未知";
                String classMajor = "未知";
                if (cls.getStudents() != null && !cls.getStudents().isEmpty()) {
                    // 从Set中获取一个学生
                    Student firstStudent = cls.getStudents().stream().findFirst().orElse(null);
                    if (firstStudent != null) {
                        classGrade = firstStudent.getGrade() != null ? firstStudent.getGrade() : "未知";
                        classMajor = firstStudent.getMajor() != null ? firstStudent.getMajor() : "未知";
                    }
                }

                sb.append(String.format("%-12s %-20s %-8s %-15s",
                        cls.getClassId(),
                        cls.getClassName(),
                        classGrade,
                        classMajor));

                if (cls.getStudents() == null || cls.getStudents().isEmpty()) {
                    sb.append("暂无学生\n");
                } else {
                    List<Student> sampleStudents = new ArrayList<>(cls.getStudents());
                    sampleStudents = sampleStudents.size() > 3 ?
                            sampleStudents.subList(0, 3) : sampleStudents;

                    sb.append("[");
                    for (Student stu : sampleStudents) {
                        sb.append(stu.getStudentId()).append("-").append(stu.getName()).append(",");
                    }
                    sb.deleteCharAt(sb.length() - 1);

                    if (cls.getStudents().size() > 3) {
                        sb.append("...等").append(cls.getStudents().size()).append("人");
                    }
                    sb.append("]\n");
                }
            }

            outputArea.setText(sb.toString());
        });

        addCourseBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "新增课程", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(600, 500);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            infoPanel.add(new JLabel("课程编号:"));
            infoPanel.add(idField);
            infoPanel.add(new JLabel("课程名称:"));
            infoPanel.add(nameField);
            mainPanel.add(infoPanel);

            // 选择类型（学生或班级） - 修改为互斥选择
            JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            typePanel.setBorder(BorderFactory.createTitledBorder("关联方式 (只能选择一种)"));
            ButtonGroup group = new ButtonGroup();
            JRadioButton studentRadio = new JRadioButton("关联学生", true);
            JRadioButton classRadio = new JRadioButton("关联班级");
            group.add(studentRadio);
            group.add(classRadio);
            typePanel.add(studentRadio);
            typePanel.add(classRadio);
            mainPanel.add(typePanel);

            JPanel studentPanel = new JPanel(new BorderLayout());
            studentPanel.setBorder(BorderFactory.createTitledBorder("选择学生"));

            DefaultListModel<Student> studentModel = new DefaultListModel<>();
            if (students != null) {
                students.forEach(studentModel::addElement);
            }

            JList<Student> studentList = new JList<>(studentModel);
            studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            studentList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Student) {
                        Student s = (Student) value;
                        String classInfo = s.getStuClass() != null ?
                                " (" + s.getStuClass().getClassName() + ")" : " (未分配)";
                        setText(s.getStudentId() + " - " + s.getName() + classInfo);
                    }
                    return this;
                }
            });
            studentPanel.add(new JScrollPane(studentList), BorderLayout.CENTER);

            JPanel classPanel = new JPanel(new BorderLayout());
            classPanel.setBorder(BorderFactory.createTitledBorder("选择班级"));

            DefaultListModel<stuClass> classModel = new DefaultListModel<>();
            if (classes != null) {
                classes.forEach(classModel::addElement);
            }

            JList<stuClass> classList = new JList<>(classModel);
            classList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            classList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof stuClass) {
                        stuClass c = (stuClass) value;
                        setText(c.getClassId() + " - " + c.getClassName() +
                                " (" + c.getStudents().size() + "名学生)");
                    }
                    return this;
                }
            });
            classPanel.add(new JScrollPane(classList), BorderLayout.CENTER);

            // 使用CardLayout切换面板
            JPanel cardPanel = new JPanel(new CardLayout());
            cardPanel.add(studentPanel, "STUDENT");
            cardPanel.add(classPanel, "CLASS");
            mainPanel.add(cardPanel);

            // 单选按钮事件 - 修改为互斥选择
            ItemListener radioListener = ev -> {
                CardLayout cl = (CardLayout) (cardPanel.getLayout());
                if (studentRadio.isSelected()) {
                    cl.show(cardPanel, "STUDENT");
                    // 清空班级选择
                    classList.clearSelection();
                } else {
                    cl.show(cardPanel, "CLASS");
                    // 清空学生选择
                    studentList.clearSelection();
                }
            };
            studentRadio.addItemListener(radioListener);
            classRadio.addItemListener(radioListener);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton confirmBtn = new JButton("确认");
            JButton cancelBtn = new JButton("取消");

            confirmBtn.addActionListener(ev -> {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();

                if (id.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写完整课程信息", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (courses.stream().anyMatch(c -> c.getCourseId().equals(id))) {
                    JOptionPane.showMessageDialog(dialog, "课程编号已存在", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Course newCourse = new Course(id, name);
                StringBuilder result = new StringBuilder();
                result.append("=== 新增课程 ===\n")
                        .append("课程编号: ").append(id).append("\n")
                        .append("课程名称: ").append(name).append("\n");

                if (studentRadio.isSelected()) {
                    // 关联学生模式
                    List<Student> selectedStudents = studentList.getSelectedValuesList();
                    if (selectedStudents.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "请至少选择一名学生", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    selectedStudents.forEach(newCourse::addStudent);
                    result.append("关联方式: 直接关联学生\n")
                            .append("关联学生数: ").append(selectedStudents.size()).append("\n")
                            .append("学生列表:\n");

                    selectedStudents.forEach(s -> {
                        String classInfo = s.getStuClass() != null ?
                                " (" + s.getStuClass().getClassName() + ")" : " (未分配)";
                        result.append("  • ").append(s.getStudentId())
                                .append(" - ").append(s.getName())
                                .append(classInfo).append("\n");
                    });
                } else {
                    // 关联班级模式
                    List<stuClass> selectedClasses = classList.getSelectedValuesList();
                    if (selectedClasses.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "请至少选择一个班级", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    selectedClasses.forEach(c -> {
                        c.getStudents().forEach(newCourse::addStudent);
                        result.append("关联班级: ").append(c.getClassId())
                                .append(" - ").append(c.getClassName())
                                .append(" (").append(c.getStudents().size()).append("名学生)\n");
                    });

                    result.append("关联学生总数: ").append(newCourse.getStudents().size()).append("\n");
                }

                courses.add(newCourse);
                outputArea.append(result.toString() + "\n");
                dialog.dispose();
            });

            cancelBtn.addActionListener(ev -> dialog.dispose());

            buttonPanel.add(cancelBtn);
            buttonPanel.add(confirmBtn);

            dialog.add(mainPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        editCourseBtn.addActionListener(e -> {
            if (courses.isEmpty()) {
                JOptionPane.showMessageDialog(this, "当前没有课程数据", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog selectDialog = new JDialog(this, "选择要编辑的课程", true);
            selectDialog.setLayout(new BorderLayout());
            selectDialog.setLocationRelativeTo(this);
            selectDialog.setSize(400, 400);

            // 创建课程列表（显示关联信息）
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (Course course : courses) {
                String info = String.format("%s - %s (学生:%d 班级:%d)",
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getStudents().size(),
                        course.getStudents().stream()
                                .map(s -> s.getStuClass())
                                .filter(Objects::nonNull)
                                .distinct()
                                .count());
                listModel.addElement(info);
            }

            JList<String> courseList = new JList<>(listModel);
            courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton selectBtn = new JButton("选择");
            JButton cancelBtn = new JButton("取消");

            selectBtn.addActionListener(ev -> {
                int selectedIndex = courseList.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(selectDialog, "请选择一个课程", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Course selectedCourse = courses.get(selectedIndex);
                selectDialog.dispose();

                JDialog editDialog = new JDialog(this, "编辑课程: " + selectedCourse.getCourseId(), true);
                editDialog.setLayout(new BorderLayout());
                editDialog.setSize(600, 500);

                JPanel mainPanel = new JPanel();
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
                mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

                JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 10));
                JTextField idField = new JTextField(selectedCourse.getCourseId());
                idField.setEditable(false);
                JTextField nameField = new JTextField(selectedCourse.getCourseName());
                infoPanel.add(new JLabel("课程编号:"));
                infoPanel.add(idField);
                infoPanel.add(new JLabel("课程名称:"));
                infoPanel.add(nameField);
                mainPanel.add(infoPanel);

                // 关联方式选择 - 修改为互斥选择
                JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                typePanel.setBorder(BorderFactory.createTitledBorder("关联方式 (只能选择一种)"));
                ButtonGroup group = new ButtonGroup();
                JRadioButton studentRadio = new JRadioButton("关联学生", true);
                JRadioButton classRadio = new JRadioButton("关联班级");
                group.add(studentRadio);
                group.add(classRadio);
                typePanel.add(studentRadio);
                typePanel.add(classRadio);
                mainPanel.add(typePanel);

                JPanel studentPanel = new JPanel(new BorderLayout());
                studentPanel.setBorder(BorderFactory.createTitledBorder("选择学生"));

                DefaultListModel<Student> studentModel = new DefaultListModel<>();
                if (students != null) {
                    students.forEach(s -> studentModel.addElement(s));
                }

                JList<Student> studentList = new JList<>(studentModel);
                studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

                // 设置自定义渲染器，显示学号和姓名
                studentList.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list, Object value, int index,
                            boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof Student) {
                            Student student = (Student) value;
                            setText(student.getStudentId() + " - " + student.getName());
                        }
                        return this;
                    }
                });

                // 预选已关联的学生
                if (selectedCourse.getStudents() != null) {
                    List<Student> relatedStudents = selectedCourse.getStudents();
                    List<Integer> indices = new ArrayList<>();
                    for (int i = 0; i < studentModel.size(); i++) {
                        Student student = studentModel.get(i);
                        if (relatedStudents.contains(student)) {
                            indices.add(i);
                        }
                    }
                    studentList.setSelectedIndices(indices.stream().mapToInt(Integer::intValue).toArray());
                }

                studentPanel.add(new JScrollPane(studentList), BorderLayout.CENTER);

                JPanel classPanel = new JPanel(new BorderLayout());
                classPanel.setBorder(BorderFactory.createTitledBorder("选择班级"));

                DefaultListModel<String> classModel = new DefaultListModel<>();
                if (classes != null) {
                    classes.forEach(c -> {
                        String studentCount = " (" + c.getStudents().size() + "名学生)";
                        classModel.addElement(c.getClassId() + " - " + c.getClassName() + studentCount);
                    });
                }

                JList<String> classList = new JList<>(classModel);
                classList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

                // 预选已关联的班级（通过学生反向查找）
                if (selectedCourse.getStudents() != null) {
                    Set<stuClass> relatedClasses = selectedCourse.getStudents().stream()
                            .map(Student::getStuClass)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    List<Integer> indices = new ArrayList<>();
                    for (int i = 0; i < classes.size(); i++) {
                        if (relatedClasses.contains(classes.get(i))) {
                            indices.add(i);
                        }
                    }
                    classList.setSelectedIndices(indices.stream().mapToInt(i -> i).toArray());
                }

                classPanel.add(new JScrollPane(classList), BorderLayout.CENTER);

                // 使用CardLayout切换面板
                JPanel cardPanel = new JPanel(new CardLayout());
                cardPanel.add(studentPanel, "STUDENT");
                cardPanel.add(classPanel, "CLASS");
                mainPanel.add(cardPanel);

                // 单选按钮事件 - 修改为互斥选择
                ItemListener radioListener = evt -> {
                    CardLayout cl = (CardLayout) (cardPanel.getLayout());
                    if (studentRadio.isSelected()) {
                        cl.show(cardPanel, "STUDENT");
                        // 清空班级选择
                        classList.clearSelection();
                    } else {
                        cl.show(cardPanel, "CLASS");
                        // 清空学生选择
                        studentList.clearSelection();
                    }
                };
                studentRadio.addItemListener(radioListener);
                classRadio.addItemListener(radioListener);

                JPanel editButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton saveBtn = new JButton("保存");
                JButton editCancelBtn = new JButton("取消");

                saveBtn.addActionListener(saveEv -> {
                    String newName = nameField.getText().trim();
                    if (newName.isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog, "课程名称不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    selectedCourse.setCourseName(newName);

                    // 清空原有关联
                    selectedCourse.getStudents().clear();

                    if (studentRadio.isSelected()) {
                        // 关联学生模式
                        List<Student> selectedStudents = studentList.getSelectedValuesList();
                        selectedStudents.forEach(selectedCourse::addStudent);
                    } else {
                        // 关联班级模式
                        List<stuClass> selectedClasses = classList.getSelectedValuesList().stream()
                                .map(str -> {
                                    for (stuClass c : classes) {
                                        if (c.getClassId().equals(str.split(" - ")[0])) {
                                            return c;
                                        }
                                    }
                                    return null;
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                        selectedClasses.forEach(c -> c.getStudents().forEach(selectedCourse::addStudent));
                    }

                    StringBuilder result = new StringBuilder();
                    result.append("=== 更新课程 ===\n")
                            .append("课程编号: ").append(selectedCourse.getCourseId()).append("\n")
                            .append("新名称: ").append(newName).append("\n")
                            .append("关联学生数: ").append(selectedCourse.getStudents().size()).append("\n")
                            .append("关联班级数: ").append(selectedCourse.getStudents().stream()
                                    .map(Student::getStuClass)
                                    .filter(Objects::nonNull)
                                    .distinct()
                                    .count()).append("\n");

                    outputArea.append(result.toString());
                    editDialog.dispose();
                });

                editCancelBtn.addActionListener(cancelEv -> editDialog.dispose());

                editButtonPanel.add(editCancelBtn);
                editButtonPanel.add(saveBtn);

                editDialog.add(mainPanel, BorderLayout.CENTER);
                editDialog.add(editButtonPanel, BorderLayout.SOUTH);
                editDialog.setLocationRelativeTo(this);
                editDialog.setVisible(true);
            });

            cancelBtn.addActionListener(ev -> selectDialog.dispose());

            buttonPanel.add(cancelBtn);
            buttonPanel.add(selectBtn);

            selectDialog.add(new JScrollPane(courseList), BorderLayout.CENTER);
            selectDialog.add(buttonPanel, BorderLayout.SOUTH);
            selectDialog.setLocationRelativeTo(this);
            selectDialog.setVisible(true);
        });

        deleteCourseBtn.addActionListener(e -> {
            if (courses.isEmpty()) {
                JOptionPane.showMessageDialog(this, "当前没有课程数据", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog(this, "删除课程", true);
            dialog.setLayout(new BorderLayout());

            // 创建课程列表（显示更多信息）
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (Course course : courses) {
                String info = String.format("%s - %s (学生:%d 关联班级:%d)",
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getStudents().size(),
                        course.getStudents().stream()
                                .map(s -> s.getStuClass())
                                .filter(Objects::nonNull)
                                .distinct()
                                .count());
                listModel.addElement(info);
            }

            JList<String> courseList = new JList<>(listModel);
            courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton deleteBtn = new JButton("删除");
            JButton cancelBtn = new JButton("取消");

            deleteBtn.addActionListener(ev -> {
                int selectedIndex = courseList.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(dialog, "请选择一个课程", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Course selectedCourse = courses.get(selectedIndex);

                StringBuilder confirmMsg = new StringBuilder();
                confirmMsg.append("确定要删除以下课程吗？\n\n")
                        .append("课程编号: ").append(selectedCourse.getCourseId()).append("\n")
                        .append("课程名称: ").append(selectedCourse.getCourseName()).append("\n")
                        .append("关联学生数: ").append(selectedCourse.getStudents().size()).append("\n")
                        .append("关联班级数: ").append(selectedCourse.getStudents().stream()
                                .map(Student::getStuClass)
                                .filter(Objects::nonNull)
                                .distinct()
                                .count()).append("\n\n")
                        .append("删除后将无法恢复！");

                int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        confirmMsg.toString(),
                        "确认删除课程",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    // 删除课程
                    courses.remove(selectedIndex);

                    // 更新输出区域
                    outputArea.append("=== 已删除课程 ===\n");
                    outputArea.append("课程编号: " + selectedCourse.getCourseId() + "\n");
                    outputArea.append("课程名称: " + selectedCourse.getCourseName() + "\n");
                    outputArea.append("受影响学生数: " + selectedCourse.getStudents().size() + "\n\n");

                    dialog.dispose();
                }
            });

            cancelBtn.addActionListener(ev -> dialog.dispose());

            buttonPanel.add(deleteBtn);
            buttonPanel.add(cancelBtn);

            dialog.add(new JScrollPane(courseList), BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setSize(500, 300);  // 增加宽度以显示完整信息
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        viewCoursesBtn.addActionListener(e -> {
            if (courses.isEmpty()) {
                outputArea.setText("当前没有课程数据\n");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("=== 课程列表 ===\n\n");

            for (Course course : courses) {
                sb.append(String.format("课程编号: %s\n", course.getCourseId()));
                sb.append(String.format("课程名称: %s\n", course.getCourseName()));
                sb.append("关联学生 (").append(course.getStudents().size()).append("名):\n");
                if (course.getStudents().isEmpty()) {
                    sb.append("  无关联学生\n");
                } else {
                    for (Student student : course.getStudents()) {
                        sb.append(String.format("  • %s - %s",
                                student.getStudentId(),
                                student.getName()));
                        // 显示学生所属班级（如果有）
                        if (student.getStuClass() != null) {
                            sb.append(String.format(" (%s-%s)",
                                    student.getStuClass().getClassId(),
                                    student.getStuClass().getClassName()));
                        }
                        sb.append("\n");
                    }
                }

                // 关联班级信息（通过学生反向查找）
                Set<stuClass> relatedClasses = new HashSet<>();
                for (Student student : course.getStudents()) {
                    if (student.getStuClass() != null) {
                        relatedClasses.add(student.getStuClass());
                    }
                }

                sb.append("关联班级 (").append(relatedClasses.size()).append("个):\n");
                if (relatedClasses.isEmpty()) {
                    sb.append("  无关联班级\n");
                } else {
                    for (stuClass cls : relatedClasses) {
                        sb.append(String.format("  • %s - %s (%d名学生)\n",
                                cls.getClassId(),
                                cls.getClassName(),
                                cls.getStudents().size()));
                    }
                }

                sb.append("------------------------\n");
            }

            outputArea.setText(sb.toString());
        });

        analyzeBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "empty");
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请先导入学生名单！");
                return;
            }

            JDialog analysisDialog = new JDialog(this, "选择分析范围", true);
            analysisDialog.setLayout(new BorderLayout(10, 10));
            analysisDialog.setSize(400, 200);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            typePanel.setBorder(BorderFactory.createTitledBorder("分析类型"));
            ButtonGroup typeGroup = new ButtonGroup();
            JRadioButton byClassRadio = new JRadioButton("按班级分析", true);
            JRadioButton byCourseRadio = new JRadioButton("按课程分析");
            typeGroup.add(byClassRadio);
            typeGroup.add(byCourseRadio);
            typePanel.add(byClassRadio);
            typePanel.add(byCourseRadio);
            mainPanel.add(typePanel);

            JPanel classPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            classPanel.setBorder(BorderFactory.createTitledBorder("选择班级"));
            JLabel classLabel = new JLabel("班级:");
            DefaultComboBoxModel<String> classComboModel = new DefaultComboBoxModel<>();
            classComboModel.addElement("全部班级");
            for (stuClass cls : classes) {
                classComboModel.addElement(cls.getClassName() + " (ID:" + cls.getClassId() + ")");
            }
            JComboBox<String> classCombo = new JComboBox<>(classComboModel);
            classPanel.add(classLabel);
            classPanel.add(classCombo);
            mainPanel.add(classPanel);

            JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            coursePanel.setBorder(BorderFactory.createTitledBorder("选择课程"));
            coursePanel.setVisible(false); // 默认隐藏
            JLabel courseLabel = new JLabel("课程:");
            DefaultComboBoxModel<String> courseComboModel = new DefaultComboBoxModel<>();
            courseComboModel.addElement("全部课程");
            for (Course course : courses) {
                courseComboModel.addElement(course.getCourseName() + " (ID:" + course.getCourseId() + ")");
            }
            JComboBox<String> courseCombo = new JComboBox<>(courseComboModel);
            coursePanel.add(courseLabel);
            coursePanel.add(courseCombo);
            mainPanel.add(coursePanel);

            // 单选按钮切换事件
            ItemListener typeListener = ev -> {
                boolean isClassAnalysis = byClassRadio.isSelected();
                classPanel.setVisible(isClassAnalysis);
                coursePanel.setVisible(!isClassAnalysis);
                analysisDialog.pack();
            };
            byClassRadio.addItemListener(typeListener);
            byCourseRadio.addItemListener(typeListener);

            JButton confirmBtn = new JButton("开始分析");
            confirmBtn.addActionListener(ev -> {
                List<Student> targetStudents = new ArrayList<>();
                String analysisScope = "";

                if (byClassRadio.isSelected()) {
                    // 按班级分析
                    String selected = (String) classCombo.getSelectedItem();
                    analysisScope = "班级: " + selected;

                    if (selected.equals("全部班级")) {
                        targetStudents.addAll(students);
                    } else {
                        String classId = selected.substring(selected.indexOf("ID:") + 3).replace(")", "").trim();
                        targetStudents = students.stream()
                                .filter(s -> s.getStuClass() != null && s.getStuClass().getClassId().equals(classId))
                                .collect(Collectors.toList());
                    }
                } else {
                    // 按课程分析
                    String selected = (String) courseCombo.getSelectedItem();
                    analysisScope = "课程: " + selected;

                    if (selected.equals("全部课程")) {
                        targetStudents.addAll(students);
                    } else {
                        String courseId = selected.substring(selected.indexOf("ID:") + 3).replace(")", "").trim();
                        for (Course course : courses) {
                            if (course.getCourseId().equals(courseId)) {
                                targetStudents.addAll(course.getStudents());
                                break;
                            }
                        }
                    }
                }

                if (targetStudents.isEmpty()) {
                    JOptionPane.showMessageDialog(analysisDialog, "所选范围内没有学生数据！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                List<ExperimentFile> reports = new ReportDAO().loadReportsFromDirectory("reports/");
                AnalysisService service = new AnalysisService();
                Map<String, Integer> byStudent = service.analyzeMissingByStudent(targetStudents, reports);
                Map<String, Integer> byExperiment = service.analyzeMissingByExperiment(targetStudents, reports);
                Map<String, String> missingExpsByStudent = service.getMissingExperimentsByStudent(targetStudents, reports);
                Map<String, List<String>> missingStudentsByExp = service.getMissingStudentsByExperiment(targetStudents, reports);

                currentAnalysisResult = new AnalysisResult();
                currentAnalysisResult.setByStudent(byStudent);
                currentAnalysisResult.setByExperiment(byExperiment);
                currentAnalysisResult.setTargetStudents(targetStudents);
                currentAnalysisResult.setScope(analysisScope);
                currentAnalysisResult.setMissingExpsByStudent(missingExpsByStudent);
                currentAnalysisResult.setMissingStudentsByExp(missingStudentsByExp);

                StringBuilder sb = new StringBuilder();
                sb.append("=== 分析范围: ").append(analysisScope).append(" ===\n");
                sb.append("总学生数: ").append(targetStudents.size()).append("\n");
                sb.append("实际提交报告学生数: ").append(byStudent.size()).append("\n\n");

                sb.append("【按学生统计】\n");
                sb.append(String.format("%-12s %-8s %-10s %-6s %-20s\n",
                        "学号", "姓名", "班级", "缺交次数", "缺交实验列表"));
                sb.append("------------------------------------------------------------\n");

                for (Map.Entry<String, Integer> entry : byStudent.entrySet()) {
                    String studentId = entry.getKey();
                    Student s = Student.findStudentById(studentId, students);

                    // 关键修改：从 missingExpsByStudent 获取缺交实验列表
                    String missingExps = currentAnalysisResult.getMissingExpsByStudent()
                            .getOrDefault(studentId, "无");

                    sb.append(String.format("%-12s %-8s %-10s %-6d %-20s\n",
                            studentId,
                            s != null ? s.getName() : "未知",
                            s != null && s.getStuClass() != null ? s.getStuClass().getClassName() : "未分配",
                            entry.getValue(),
                            missingExps)); // 显示缺交实验列表
                }

                sb.append("\n【按实验统计】\n");
                sb.append(String.format("%-12s %-8s %-20s\n", "实验编号", "缺交人数", "缺交学生列表"));
                sb.append("------------------------------------------------\n");
                for (Map.Entry<String, Integer> entry : byExperiment.entrySet()) {
                    String expId = entry.getKey();
                    List<String> missingStudents = missingStudentsByExp.getOrDefault(expId, Collections.emptyList());

                    // 将学号列表转换为姓名列表
                    List<String> missingStudentNames = missingStudents.stream()
                            .map(id -> {
                                Student s = Student.findStudentById(id, students);
                                return s != null ? s.getName() : id + "(未知)";
                            })
                            .collect(Collectors.toList());

                    String missingStudentsStr = missingStudentNames.isEmpty() ? "无" :
                            String.join(", ", missingStudentNames.stream().limit(3).collect(Collectors.toList())) +
                                    (missingStudentNames.size() > 3 ? "等" + missingStudentNames.size() + "人" : "");

                    sb.append(String.format("%-12s %-8d %-20s\n",
                            expId, entry.getValue(), missingStudentsStr));
                }

                outputArea.setText(sb.toString());
                analysisDialog.dispose();
                exportBtn.setEnabled(true); // 确保导出按钮可用
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(confirmBtn);

            analysisDialog.add(mainPanel, BorderLayout.CENTER);
            analysisDialog.add(buttonPanel, BorderLayout.SOUTH);
            analysisDialog.setLocationRelativeTo(this);
            analysisDialog.setVisible(true);
        });

        chartBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "empty");
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请先导入学生名单！");
                return;
            }

            JDialog chartDialog = new JDialog(this, "图表分析选项", true);
            chartDialog.setLayout(new BorderLayout(10, 10));
            chartDialog.setSize(450, 300);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            typePanel.setBorder(BorderFactory.createTitledBorder("分析类型"));
            ButtonGroup typeGroup = new ButtonGroup();
            JRadioButton byClassRadio = new JRadioButton("按班级分析", true);
            JRadioButton byCourseRadio = new JRadioButton("按课程分析");
            typeGroup.add(byClassRadio);
            typeGroup.add(byCourseRadio);
            typePanel.add(byClassRadio);
            typePanel.add(byCourseRadio);
            mainPanel.add(typePanel);

            JPanel classPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            classPanel.setBorder(BorderFactory.createTitledBorder("选择班级"));
            JLabel classLabel = new JLabel("班级:");
            DefaultComboBoxModel<String> classComboModel = new DefaultComboBoxModel<>();
            classComboModel.addElement("全部班级");
            classes.forEach(cls ->
                    classComboModel.addElement(cls.getClassName() + " (ID:" + cls.getClassId() + ")")
            );
            JComboBox<String> classCombo = new JComboBox<>(classComboModel);
            classPanel.add(classLabel);
            classPanel.add(classCombo);
            mainPanel.add(classPanel);

            JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            coursePanel.setBorder(BorderFactory.createTitledBorder("选择课程"));
            coursePanel.setVisible(false); // 默认隐藏
            JLabel courseLabel = new JLabel("课程:");
            DefaultComboBoxModel<String> courseComboModel = new DefaultComboBoxModel<>();
            courseComboModel.addElement("全部课程");
            courses.forEach(course ->
                    courseComboModel.addElement(course.getCourseName() + " (ID:" + course.getCourseId() + ")")
            );
            JComboBox<String> courseCombo = new JComboBox<>(courseComboModel);
            coursePanel.add(courseLabel);
            coursePanel.add(courseCombo);
            mainPanel.add(coursePanel);

            // 单选按钮切换事件
            ItemListener typeListener = ev -> {
                boolean isClassAnalysis = byClassRadio.isSelected();
                classPanel.setVisible(isClassAnalysis);
                coursePanel.setVisible(!isClassAnalysis);
                chartDialog.pack();
            };
            byClassRadio.addItemListener(typeListener);
            byCourseRadio.addItemListener(typeListener);

            JPanel chartTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            chartTypePanel.setBorder(BorderFactory.createTitledBorder("图表类型"));
            ButtonGroup chartTypeGroup = new ButtonGroup();
            JRadioButton lineChartRadio = new JRadioButton("折线图", true);
            JRadioButton barChartRadio = new JRadioButton("柱状图");
            chartTypeGroup.add(lineChartRadio);
            chartTypeGroup.add(barChartRadio);
            chartTypePanel.add(lineChartRadio);
            chartTypePanel.add(barChartRadio);
            mainPanel.add(chartTypePanel);

            JButton generateBtn = new JButton("生成图表");
            generateBtn.addActionListener(ev -> {
                List<Student> targetStudents = new ArrayList<>();
                String analysisScope = "";

                if (byClassRadio.isSelected()) {
                    // 按班级分析
                    String selected = (String) classCombo.getSelectedItem();
                    analysisScope = "班级: " + selected;

                    if (selected.equals("全部班级")) {
                        targetStudents.addAll(students);
                    } else {
                        String classId = selected.substring(selected.indexOf("ID:") + 3).replace(")", "").trim();
                        targetStudents = students.stream()
                                .filter(s -> s.getStuClass() != null && s.getStuClass().getClassId().equals(classId))
                                .collect(Collectors.toList());
                    }
                } else {
                    // 按课程分析
                    String selected = (String) courseCombo.getSelectedItem();
                    analysisScope = "课程: " + selected;

                    if (selected.equals("全部课程")) {
                        targetStudents.addAll(students);
                    } else {
                        String courseId = selected.substring(selected.indexOf("ID:") + 3).replace(")", "").trim();
                        for (Course course : courses) {
                            if (course.getCourseId().equals(courseId)) {
                                targetStudents.addAll(course.getStudents());
                                break;
                            }
                        }
                    }
                }

                if (targetStudents.isEmpty()) {
                    JOptionPane.showMessageDialog(chartDialog, "所选范围内没有学生数据！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    // 获取分析数据
                    Map<String, Double> submissionRates = new AnalysisService()
                            .analyzeSubmissionRates(targetStudents, new ReportDAO().loadReportsFromDirectory("reports/"));

                    // 生成图表标题
                    String title = "实验提交率统计 - " + analysisScope;

                    // 根据选择的图表类型生成图表
                    if (lineChartRadio.isSelected()) {
                        ChartGenerator.generateLineChart(submissionRates, title);
                    } else {
                        ChartGenerator.generateBarChart(submissionRates, title);
                    }

                    chartDialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(chartDialog,
                            "生成图表失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(generateBtn);

            chartDialog.add(mainPanel, BorderLayout.CENTER);
            chartDialog.add(buttonPanel, BorderLayout.SOUTH);
            chartDialog.setLocationRelativeTo(this);
            chartDialog.setVisible(true);
        });

        exportBtn.addActionListener(e -> {
            // 检查是否有分析结果
            if (currentAnalysisResult == null) {
                JOptionPane.showMessageDialog(this, "请先执行分析操作！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 选择导出维度（仅保留学生和实验两个选项）
            String[] options = {"按学生导出", "按实验导出"};
            int dimensionChoice = JOptionPane.showOptionDialog(
                    this,
                    "请选择导出维度:",
                    "导出选项",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (dimensionChoice == JOptionPane.CLOSED_OPTION)  return; // 用户取消操作


            ExportUtil.ExportDimension dimension = dimensionChoice == 0
                    ? ExportUtil.ExportDimension.STUDENT
                    : ExportUtil.ExportDimension.EXPERIMENT;

            // 创建文件选择对话框
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("选择导出位置");

            // 设置文件过滤器
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel文件 (*.xlsx)", "xlsx"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV文件 (*.csv)", "csv"));
            fileChooser.setAcceptAllFileFilterUsed(false);

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String format = ((FileNameExtensionFilter)fileChooser.getFileFilter()).getExtensions()[0];

                // 确保文件有正确的扩展名
                if (!file.getName().toLowerCase().endsWith("." + format)) {
                    file = new File(file.getAbsolutePath() + "." + format);
                }

                try {
                    // 使用当前分析结果进行导出，并指定维度
                    if ("xlsx".equals(format)) {
                        ExportUtil.exportToExcel(currentAnalysisResult, file, dimension);
                    } else {
                        ExportUtil.exportToCsv(currentAnalysisResult, file, dimension);
                    }

                    JOptionPane.showMessageDialog(this, "导出成功: " + file.getName(), "成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "导出失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
    }

    private AnalysisResult getCurrentAnalysisResult() {
        return currentAnalysisResult;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            MainWindow window = new MainWindow();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}

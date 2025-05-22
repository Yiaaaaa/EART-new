package org.example.erat;

import org.example.erat.model.AnalysisResult;
import org.example.erat.model.ExperimentFile;
import org.example.erat.model.Student;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class AnalysisResultTest {
    private AnalysisResult analysisResult;
    private List<Student> students;
    private List<ExperimentFile> targetFiles;
    private List<ExperimentFile> submittedFiles;

    @BeforeEach
    void setUp() {
        analysisResult = new AnalysisResult();

        // 初始化基础测试数据
        students = Arrays.asList(
                new Student("121052023048", "路远周", "2023", "软件工程"),
                new Student("121052023056", "林叙言", "2023", "软件工程"),
                new Student("121052023062", "王天烁", "2023", "软件工程")
        );

        targetFiles = Arrays.asList(
                // 实验1文件
                new ExperimentFile("121052023048", "实验1", "路远周", "软件体系模式", "软工2班", "/reports/实验1_121052023048_路远周.doc"),
                new ExperimentFile("121052023056", "实验1", "林叙言", "软件体系模式", "软工2班", "/reports/实验1_121052023056_林叙言.doc"),
                new ExperimentFile("121052023062", "实验1", "王天烁", "软件体系模式", "软工2班", "/reports/实验1_121052023062_王天烁.doc"),
                // 实验2文件
                new ExperimentFile("121052023048", "实验2", "路远周", "软件体系模式", "软工2班", "/reports/实验2_121052023048_路远周.doc"),
                new ExperimentFile("121052023056", "实验2", "林叙言", "软件体系模式", "软工2班", "/reports/实验2_121052023056_林叙言.doc"),
                new ExperimentFile("121052023062", "实验2", "王天烁", "软件体系模式", "软工2班", "/reports/实验2_121052023062_王天烁.doc")
        );

        // 使用ArrayList包装原始数据，支持动态添加元素
        submittedFiles = new ArrayList<>(Arrays.asList(
                // 实验1全提交
                new ExperimentFile("121052023048", "实验1", "路远周", "软件体系模式", "软工2班", "/reports/实验1_121052023048_路远周.doc"),
                new ExperimentFile("121052023056", "实验1", "林叙言", "软件体系模式", "软工2班", "/reports/实验1_121052023056_林叙言.doc"),
                new ExperimentFile("121052023062", "实验1", "王天烁", "软件体系模式", "软工2班", "/reports/实验1_121052023062_王天烁.doc"),
                // 实验2仅王天烁提交
                new ExperimentFile("121052023062", "实验2", "王天烁", "软件体系模式", "软工2班", "/reports/实验2_121052023062_王天烁.doc")
        ));

        analysisResult.setTargetStudents(students);
        analysisResult.setTargetFiles(targetFiles);
        analysisResult.setSubmittedFiles(submittedFiles);
    }

    // ====================== 学生维度统计测试 ======================
    @Test
    void testStudentDimensionStats() {
        analysisResult.calculateMissingInfo();

        Map<String, Integer> byStudent = analysisResult.getByStudent();
        Map<String, String> missingExpsByStudent = analysisResult.getMissingExpsByStudent();

        // 验证缺交学生（路远周、林叙言缺交实验2）
        assertEquals(1, byStudent.get("121052023048"));
        assertEquals("实验2", missingExpsByStudent.get("121052023048"));

        assertEquals(1, byStudent.get("121052023056"));
        assertEquals("实验2", missingExpsByStudent.get("121052023056"));

        // 验证无缺交学生（王天烁全提交）
        assertNull(byStudent.get("121052023062"));
        assertNull(missingExpsByStudent.get("121052023062"));
    }

    // ====================== 实验维度统计测试 ======================
    @Test
    void testExperimentDimensionStats() {
        analysisResult.calculateMissingInfo();

        Map<String, Integer> byExperiment = analysisResult.getByExperiment();
        Map<String, List<String>> missingStudentsByExp = analysisResult.getMissingStudentsByExp();

        // 验证实验1（全提交）
        assertNull(byExperiment.get("实验1"));
        assertTrue(missingStudentsByExp.getOrDefault("实验1", Collections.emptyList()).isEmpty());

        // 验证实验2（缺交2人）
        assertEquals(2, byExperiment.get("实验2"));
        List<String> expectedMissing = Arrays.asList("121052023048", "121052023056");
        assertEquals(expectedMissing, missingStudentsByExp.get("实验2"));
    }

    // ====================== 边界情况测试 ======================
    @Test
    void testSingleStudentSingleExperiment() {
        // 初始化单学生单实验数据
        students = Collections.singletonList(
                new Student("121052023001", "李华", "2023", "软件工程")
        );
        targetFiles = Collections.singletonList(
                new ExperimentFile("121052023001", "实验1", "李华", "软件体系模式", "软工2班", "/reports/实验1_121052023001_李华.doc")
        );
        submittedFiles = Collections.emptyList(); // 故意不提交

        analysisResult.setTargetStudents(students);
        analysisResult.setTargetFiles(targetFiles);
        analysisResult.setSubmittedFiles(submittedFiles);
        analysisResult.calculateMissingInfo();

        // 验证单学生缺交情况
        assertEquals(1, analysisResult.getByStudent().get("121052023001"));
        assertEquals("实验1", analysisResult.getMissingExpsByStudent().get("121052023001"));

        // 验证单实验缺交情况
        assertEquals(1, analysisResult.getByExperiment().get("实验1"));
        assertEquals(Collections.singletonList("121052023001"),
                analysisResult.getMissingStudentsByExp().get("实验1"));
    }

    // ====================== 异常场景测试（修复学生ID校验逻辑） ======================
    @Test
    void testInvalidStudentIdInSubmittedFile() {
        // 构造无效文件（学号不存在）
        ExperimentFile invalidFile = new ExperimentFile(
                "99999999999", "实验1", "无名氏", "软件体系模式", "软工2班", "/reports/实验1_99999999999_无名氏.doc"
        );

        // 创建独立列表，避免污染初始化数据
        List<ExperimentFile> tempSubmittedFiles = new ArrayList<>(submittedFiles);
        tempSubmittedFiles.add(invalidFile);

        analysisResult.setSubmittedFiles(tempSubmittedFiles);

        // 假设AnalysisResult中已实现学生ID校验（需在业务代码中添加）
        assertThrows(IllegalArgumentException.class,
                () -> analysisResult.calculateMissingInfo(),
                "提交文件中存在无效学号"
        );
    }

    // ====================== 重复文件测试（修复断言逻辑） ======================
    @Test
    void testDuplicateSubmittedFiles() {
        // 创建独立列表，避免污染初始化数据
        List<ExperimentFile> tempSubmittedFiles = new ArrayList<>(submittedFiles);

        // 添加重复提交的同一实验文件（实验1路远周的文件）
        ExperimentFile duplicateFile = tempSubmittedFiles.get(0);
        tempSubmittedFiles.add(duplicateFile);

        analysisResult.setSubmittedFiles(tempSubmittedFiles);
        analysisResult.calculateMissingInfo();

        // 验证重复提交不影响统计（路远周在实验1已提交，实验2缺交，总缺交次数仍为1）
        assertEquals(1, analysisResult.getByStudent().getOrDefault("121052023048", 0)); // 使用getOrDefault避免null
        assertNull(analysisResult.getMissingExpsByStudent().get("121052023062")); // 王天烁仍无缺交
    }

    // ====================== 空数据测试 ======================
    @Test
    void testEmptyData() {
        analysisResult.setTargetFiles(Collections.emptyList());
        analysisResult.setSubmittedFiles(Collections.emptyList());
        analysisResult.calculateMissingInfo();

        assertTrue(analysisResult.getByStudent().isEmpty());
        assertTrue(analysisResult.getByExperiment().isEmpty());
        assertTrue(analysisResult.getMissingExpsByStudent().isEmpty());
        assertTrue(analysisResult.getMissingStudentsByExp().isEmpty());
    }
}
package org.example.erat.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

public class ChartGenerator {

    public static void generateLineChart(Map<String, Double> submissionRates) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : submissionRates.entrySet()) {
            dataset.addValue(entry.getValue(), "提交率(%)", entry.getKey());
        }

        // 设置中文字体
        Font font = getChineseFont();

        JFreeChart chart = ChartFactory.createLineChart(
                "实验提交率趋势",
                "实验编号",
                "提交率 (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        // 全局字体设置
        chart.getTitle().setFont(font.deriveFont(Font.BOLD, 16));

        // 设置图例字体（新增样式设置）
        chart.getLegend().setItemFont(font.deriveFont(Font.PLAIN, 12));
        chart.getLegend().setItemPaint(Color.BLACK);  // 确保图例文字颜色不是透明

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // X轴设置
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(font.deriveFont(Font.BOLD, 14));
        domainAxis.setTickLabelFont(font.deriveFont(Font.PLAIN, 12));
        domainAxis.setCategoryLabelPositions(
                org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4)
        );

        // Y轴设置
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(font.deriveFont(Font.BOLD, 14));
        rangeAxis.setTickLabelFont(font.deriveFont(Font.PLAIN, 12));
        rangeAxis.setRange(0, 100);

        // 折线样式设置
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setDefaultShapesVisible(false);  // 关闭数据点形状显示
        renderer.setDefaultLinesVisible(true);     // 确保线条可见
        renderer.setDrawOutlines(true);

        // 设置提示信息字体
//        renderer.setDefaultToolTipFont(font.deriveFont(12f));

        // 显示窗口
        ChartFrame frame = new ChartFrame("实验提交率分析", chart);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static Font getChineseFont() {
        // 扩展字体列表以提高兼容性
        String[] fonts = {
                "Microsoft YaHei",
                "SimSun",
                "STSong",
                "Songti SC",
                "SimHei",
                "KaiTi",
                "Arial Unicode MS",
                "WenQuanYi Zen Hei"
        };

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (String fontName : fonts) {
            if (Arrays.asList(ge.getAvailableFontFamilyNames()).contains(fontName)) {
                return new Font(fontName, Font.PLAIN, 12);
            }
        }
        // 最后尝试加载系统默认SansSerif字体
        return new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    }
}
package org.example.erat.chart;

import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class ChartGenerator {
    // 主题和字体设置
    private static StandardChartTheme chartTheme;
    private static Font chineseFont;

    static {
        initializeTheme();
    }

    private static void initializeTheme() {
        // 1. 创建支持中文的主题
        chartTheme = new StandardChartTheme("CN_Theme");

        // 2. 设置美观字体
        chineseFont = loadChineseFont("Source Han Sans CN", "Microsoft YaHei", "SimSun");
        chartTheme.setExtraLargeFont(chineseFont.deriveFont(18f));  // 标题
        chartTheme.setLargeFont(chineseFont.deriveFont(14f));      // 图例
        chartTheme.setRegularFont(chineseFont.deriveFont(12f));    // 轴标签
        chartTheme.setSmallFont(chineseFont.deriveFont(10f));      // 刻度
        ChartFactory.setChartTheme(chartTheme);
    }

    public static void generateLineChart(Map<String, Double> data, String title) {
        // 创建数据集
        DefaultCategoryDataset dataset = createDataset(data);

        // 创建图表
        JFreeChart chart = ChartFactory.createLineChart(
                title,                  // 图表标题
                "实验编号",             // X轴标签
                "提交率(%)",           // Y轴标签
                dataset,
                PlotOrientation.VERTICAL,
                true,                  // 显示图例
                true,                  // 显示提示
                false
        );

        // 自定义图表外观
        customizeLineChart(chart);

        // 显示图表
        displayChart(chart, title);
    }

    public static void generateBarChart(Map<String, Double> data, String title) {
        // 创建数据集
        DefaultCategoryDataset dataset = createDataset(data);

        // 创建图表
        JFreeChart chart = ChartFactory.createBarChart(
                title,                  // 图表标题
                "实验编号",             // X轴标签
                "提交率(%)",           // Y轴标签
                dataset,
                PlotOrientation.VERTICAL,
                true,                  // 显示图例
                true,                  // 显示提示
                false
        );

        // 自定义图表外观
        customizeBarChart(chart);

        // 显示图表
        displayChart(chart, title);
    }

    private static DefaultCategoryDataset createDataset(Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((key, value) -> dataset.addValue(value, "提交率", key));
        return dataset;
    }

    private static void customizeLineChart(JFreeChart chart) {
        // 1. 整体背景
        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);

        // 2. 绘图区域设置
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        // 3. 坐标轴设置
        setupAxes(plot);

        // 4. 线条和点样式
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189)); // 蓝色线条
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));  // 线宽
        renderer.setSeriesShapesVisible(0, true);            // 显示数据点
        renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6)); // 圆形数据点

        // 5. 图例位置
        chart.getLegend().setItemFont(chineseFont.deriveFont(12f));
    }

    private static void customizeBarChart(JFreeChart chart) {
        // 1. 整体背景
        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);

        // 2. 绘图区域设置
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        // 3. 坐标轴设置
        setupAxes(plot);

        // 4. 柱状图样式
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189)); // 蓝色柱状图
        renderer.setSeriesOutlinePaint(0, Color.BLACK);
        renderer.setDrawBarOutline(true);
        renderer.setMaximumBarWidth(0.1); // 柱状图宽度

        // 设置柱状图阴影效果
        renderer.setShadowVisible(true);
        renderer.setShadowPaint(new Color(100, 100, 100, 100));
        renderer.setShadowXOffset(2);
        renderer.setShadowYOffset(2);

        // 5. 图例位置
        chart.getLegend().setItemFont(chineseFont.deriveFont(12f));
    }

    private static void setupAxes(CategoryPlot plot) {
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // 45度角标签
        domainAxis.setLowerMargin(0.02); // 左边距
        domainAxis.setUpperMargin(0.02); // 右边距
        domainAxis.setTickLabelFont(chineseFont.deriveFont(11f));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, 100);               // 固定0-100%范围
        rangeAxis.setLabel("提交率 (%)");         // 修改Y轴标签
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // 整数刻度
        rangeAxis.setTickLabelFont(chineseFont.deriveFont(11f));
    }

    private static void displayChart(JFreeChart chart, String title) {
        ChartFrame frame = new ChartFrame(title, chart);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);
    }

    private static Font loadChineseFont(String... fontNames) {
        for (String name : fontNames) {
            try {
                Font font = new Font(name, Font.PLAIN, 12);
                if (font.getFamily().equals(name)) {
                    return font;
                }
            } catch (Exception ignored) {}
        }
        return new Font("SansSerif", Font.PLAIN, 12); // 回退字体
    }
}
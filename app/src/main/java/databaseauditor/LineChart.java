package databaseauditor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.JFrame;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.util.List;

public class LineChart extends JFrame {
    static final String ANSI_RED = "\u001B[31m";
    static final String ANSI_RESET = "\u001B[0m";

    /**
     * Constructor to create a line chart
     * 
     * @param x       x-axis values
     * @param y       y-axis values
     * @param label   label for each line
     * @param x_label x-axis label
     * @param y_label y-axis label
     * @param title   title of the chart
     * @param path    path to save the chart
     * @throws Exception exception if something went wrong
     */
    public LineChart(long[] x, List<long[]> y, List<String> label, String x_label, String y_label, String title,
            String path) throws Exception {
        XYDataset dataset = createDataset(x, y, label);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                x_label,
                y_label,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        XYPlot plot = chart.getXYPlot();
        var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesPaint(2, Color.BLACK);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setTitle(new TextTitle(title,
                new Font("Serif", java.awt.Font.BOLD, 18)));

        ChartUtils.saveChartAsPNG(new File(path), chart, 1000, 600);
    }

    /**
     * Method to create a dataset
     * 
     * @param x     x-axis values
     * @param y     y-axis values
     * @param label label for each line
     * @return XYDataset
     */
    private XYDataset createDataset(long[] x, List<long[]> y, List<String> label) {
        var dataset = new XYSeriesCollection();
        for (int i = 0; i < label.size(); i++) {
            var series = new XYSeries(label.get(i));
            for (int j = 0; j < y.get(i).length; j++) {
                series.add(x[j], y.get(i)[j]);
            }

            dataset.addSeries(series);
        }

        return dataset;
    }

    /**
     * Method to plot a line chart
     * 
     * @param x       x-axis values
     * @param y       y-axis values
     * @param label   label for each line
     * @param x_label x-axis label
     * @param y_label y-axis label
     * @param title   title of the chart
     * @param path    path to save the chart
     */
    public static void plot(long[] x, List<long[]> y, List<String> label, String x_label, String y_label,
            String title, String path) {
        EventQueue.invokeLater(() -> {
            try {
                new LineChart(x, y, label, x_label, y_label, title, path);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.out.println(ANSI_RED + "\nSomething went wrong:" + e.getMessage() + "\n" + ANSI_RESET);
            }
        });
    }
}
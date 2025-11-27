package view;

import java.util.List;
import Model.Transaction;
import enums.ETypePayment;
import persistence.HandlingPersistence;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import Model.Transaction;
import enums.ETypePayment;
import persistence.HandlingPersistence;

public class Graphic {
    private final HandlingPersistence handlingPersistence;

    public Graphic(HandlingPersistence handlingPersistence) {
        this.handlingPersistence = handlingPersistence;
    }

    
    public void showBarChartStockByCategory() {
        List<Transaction> products = handlingPersistence.getTestList();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (ETypePayment category : ETypePayment.values()) {
            int totalStock = 0;
            for (Transaction p : products) {
                if (p.getPaymentMethod() == category) {
                    totalStock +=1;
                }
            }
            // Row key: "Cantidad", Column key: nombre de la categoría
            dataset.addValue((double) totalStock, "Cantidad", category.name());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Cantidad de Metodos de Pago de cada Tipo",   // titulo
                "Tipo de Metodo de Pago",                   // y
                "Cantidad sss",                // x
                dataset
        );

        showChart(chart, "Gráfico de barras");
    }
     private void showChart(JFreeChart chart, String title) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(new ChartPanel(chart));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}

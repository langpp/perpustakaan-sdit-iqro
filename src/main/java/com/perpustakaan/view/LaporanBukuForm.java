package com.perpustakaan.view;

import com.perpustakaan.dao.BukuDAO;
import com.perpustakaan.model.Buku;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class LaporanBukuForm extends JPanel {
    private BukuDAO bukuDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    private ChartPanel chartPanel;
    
    public LaporanBukuForm() {
        bukuDAO = new BukuDAO();
        initComponents();
        loadData();
        updateChart();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Title Panel
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 120, 215), 
                        0, getHeight(), new Color(0, 80, 180));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        titlePanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel titleLabel = new JLabel("Laporan Buku");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(createLabel("Filter:"), gbc);
        
        gbc.gridx = 1;
        filterCombo = new JComboBox<>(new String[]{"Semua", "Stok > 0", "Stok = 0"});
        filterCombo.addActionListener(e -> loadData());
        filterPanel.add(filterCombo, gbc);
        
        gbc.gridx = 2;
        JButton exportButton = createButton("Export PDF", e -> exportToPDF());
        filterPanel.add(exportButton, gbc);
        
        add(filterPanel, BorderLayout.CENTER);
        
        // Chart Panel
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        chartPanel = new ChartPanel(null);
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        add(chartContainer, BorderLayout.SOUTH);
        
        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Judul", "Pengarang", 
            "Penerbit", "Tahun", "Kategori", "Stok"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(0, 120, 215));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        
        // Add scroll functionality
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }
    
    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(0, 120, 215));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(listener);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 100, 195));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 120, 215));
            }
        });
        
        return button;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Buku> bukuList = bukuDAO.getAllBuku();
        
        String filter = (String)filterCombo.getSelectedItem();
        for (Buku buku : bukuList) {
            if (filter.equals("Semua") || 
                (filter.equals("Stok > 0") && buku.getStok() > 0) ||
                (filter.equals("Stok = 0") && buku.getStok() == 0)) {
                tableModel.addRow(new Object[]{
                    buku.getIdBuku(),
                    buku.getJudulBuku(),
                    buku.getPengarang(),
                    buku.getPenerbit(),
                    buku.getTahunTerbit(),
                    buku.getNamaKategori(),
                    buku.getStok()
                });
            }
        }
        
        updateChart();
    }
    
    private void updateChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Buku> bukuList = bukuDAO.getAllBuku();
        
        for (Buku buku : bukuList) {
            dataset.addValue(buku.getStok(), "Stok", buku.getJudulBuku());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Stok Buku per Judul",
            "Judul Buku",
            "Jumlah Stok",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        chart.getCategoryPlot().getDomainAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        chart.getCategoryPlot().getRangeAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        chartPanel.setChart(chart);
    }
    
    private void exportToPDF() {
        // TODO: Implement PDF export functionality
        JOptionPane.showMessageDialog(this, "Fitur export PDF akan segera tersedia", 
            "Info", JOptionPane.INFORMATION_MESSAGE);
    }
} 
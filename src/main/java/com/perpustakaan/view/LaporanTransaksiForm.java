package com.perpustakaan.view;

import com.perpustakaan.dao.PeminjamanDAO;
import com.perpustakaan.dao.PengembalianDAO;
import com.perpustakaan.model.Anggota;
import com.perpustakaan.model.Peminjaman;
import com.perpustakaan.model.Pengembalian;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class LaporanTransaksiForm extends JPanel {
    private PeminjamanDAO peminjamanDAO;
    private PengembalianDAO pengembalianDAO;
    private JComboBox<String> bulanCombo;
    private JComboBox<String> tahunCombo;
    private ChartPanel chartPanel;
    private JLabel totalPeminjamanLabel;
    private JLabel totalDendaLabel;
    private DecimalFormat currencyFormat;
    private DefaultTableModel printTableModel;
    private JTable printTable;
    
    public LaporanTransaksiForm() {
        peminjamanDAO = new PeminjamanDAO();
        pengembalianDAO = new PengembalianDAO();
        currencyFormat = new DecimalFormat("Rp #,##0");
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
        
        JLabel titleLabel = new JLabel("Laporan Transaksi");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main Content Panel with Scroll
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(createLabel("Bulan:"), gbc);
        
        gbc.gridx = 1;
        bulanCombo = new JComboBox<>(new String[]{"Januari", "Februari", "Maret", "April", 
            "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"});
        bulanCombo.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        bulanCombo.addActionListener(e -> loadData());
        filterPanel.add(bulanCombo, gbc);
        
        gbc.gridx = 2;
        filterPanel.add(createLabel("Tahun:"), gbc);
        
        gbc.gridx = 3;
        tahunCombo = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 5; i <= currentYear; i++) {
            tahunCombo.addItem(String.valueOf(i));
        }
        tahunCombo.setSelectedItem(String.valueOf(currentYear));
        tahunCombo.addActionListener(e -> loadData());
        filterPanel.add(tahunCombo, gbc);
        
        gbc.gridx = 4;
        JButton printButton = createButton("Cetak", e -> printReport());
        filterPanel.add(printButton, gbc);
        
        mainContentPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Statistics Panel
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        statsPanel.setBackground(Color.WHITE);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        statsPanel.add(createLabel("Total Peminjaman:"), gbc);
        
        gbc.gridx = 1;
        totalPeminjamanLabel = createLabel("0");
        totalPeminjamanLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalPeminjamanLabel.setForeground(new Color(0, 120, 215));
        statsPanel.add(totalPeminjamanLabel, gbc);
        
        gbc.gridx = 2;
        statsPanel.add(createLabel("Total Denda:"), gbc);
        
        gbc.gridx = 3;
        totalDendaLabel = createLabel("Rp 0");
        totalDendaLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalDendaLabel.setForeground(new Color(0, 120, 215));
        statsPanel.add(totalDendaLabel, gbc);
        
        mainContentPanel.add(statsPanel, BorderLayout.CENTER);
        
        // Chart Panel
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        chartContainer.setBackground(Color.WHITE);
        chartPanel = new ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(0, 400));
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        
        mainContentPanel.add(chartContainer, BorderLayout.SOUTH);
        
        // Add main content panel to scroll pane
        JScrollPane mainScrollPane = new JScrollPane(mainContentPanel);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Add all components to main panel
        add(mainScrollPane, BorderLayout.CENTER);
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
        int bulan = bulanCombo.getSelectedIndex() + 1;
        int tahun = Integer.parseInt((String)tahunCombo.getSelectedItem());
        
        Calendar cal = Calendar.getInstance();
        cal.set(tahun, bulan - 1, 1, 0, 0, 0);
        Date tglAwal = cal.getTime();
        
        cal.set(tahun, bulan, 0, 23, 59, 59);
        Date tglAkhir = cal.getTime();
        
        int totalPeminjaman = 0;
        double totalDenda = 0;
        
        // Initialize print table model
        printTableModel = new DefaultTableModel(new Object[]{"ID", "Anggota", "Tanggal", 
            "Status", "Denda"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        try {
            // Load peminjaman data
            List<Peminjaman> peminjamanList = peminjamanDAO.getAllPeminjaman();
            for (Peminjaman peminjaman : peminjamanList) {
                if (peminjaman.getTanggalPinjam() != null && 
                    peminjaman.getTanggalPinjam().after(tglAwal) && 
                    peminjaman.getTanggalPinjam().before(tglAkhir)) {
                    
                    // Get anggota data
                    Anggota anggota = peminjaman.getAnggota();
                    String namaAnggota = (anggota != null) ? anggota.getNama() : "Unknown";
                    
                    printTableModel.addRow(new Object[]{
                        peminjaman.getIdPeminjaman(),
                        namaAnggota,
                        peminjaman.getTanggalPinjam(),
                        peminjaman.getStatus(),
                        "-"
                    });
                    totalPeminjaman++;
                }
            }
            
            // Load pengembalian data
            List<Pengembalian> pengembalianList = pengembalianDAO.getAllPengembalian();
            for (Pengembalian pengembalian : pengembalianList) {
                if (pengembalian.getTglKembali() != null && 
                    pengembalian.getTglKembali().after(tglAwal) && 
                    pengembalian.getTglKembali().before(tglAkhir)) {
                    
                    Peminjaman peminjaman = pengembalian.getPeminjaman();
                    String namaAnggota = "Unknown";
                    if (peminjaman != null && peminjaman.getAnggota() != null) {
                        namaAnggota = peminjaman.getAnggota().getNama();
                    }
                    
                    printTableModel.addRow(new Object[]{
                        pengembalian.getIdPengembalian(),
                        namaAnggota,
                        pengembalian.getTglKembali(),
                        "Dikembalikan",
                        currencyFormat.format(pengembalian.getDenda())
                    });
                    totalDenda += pengembalian.getDenda();
                }
            }
            
            totalPeminjamanLabel.setText(String.valueOf(totalPeminjaman));
            totalDendaLabel.setText(currencyFormat.format(totalDenda));
            
        } catch (Exception e) {
            System.out.println("Error loading data:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        updateChart();
    }
    
    private void updateChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int bulan = bulanCombo.getSelectedIndex() + 1;
        int tahun = Integer.parseInt((String)tahunCombo.getSelectedItem());
        
        Calendar cal = Calendar.getInstance();
        cal.set(tahun, bulan - 1, 1, 0, 0, 0);
        Date tglAwal = cal.getTime();
        
        cal.set(tahun, bulan, 0, 23, 59, 59);
        Date tglAkhir = cal.getTime();
        
        Map<Integer, Integer> dipinjamPerHari = new HashMap<>();
        Map<Integer, Integer> dikembalikanPerHari = new HashMap<>();
        
        // Count dipinjam per day
        List<Peminjaman> peminjamanList = peminjamanDAO.getAllPeminjaman();
        for (Peminjaman peminjaman : peminjamanList) {
            if (peminjaman.getTanggalPinjam() != null && 
                peminjaman.getTanggalPinjam().after(tglAwal) && 
                peminjaman.getTanggalPinjam().before(tglAkhir)) {
                cal.setTime(peminjaman.getTanggalPinjam());
                int hari = cal.get(Calendar.DAY_OF_MONTH);
                dipinjamPerHari.put(hari, dipinjamPerHari.getOrDefault(hari, 0) + 1);
            }
        }
        
        // Count dikembalikan per day
        List<Pengembalian> pengembalianList = pengembalianDAO.getAllPengembalian();
        for (Pengembalian pengembalian : pengembalianList) {
            if (pengembalian.getTglKembali() != null && 
                pengembalian.getTglKembali().after(tglAwal) && 
                pengembalian.getTglKembali().before(tglAkhir)) {
                cal.setTime(pengembalian.getTglKembali());
                int hari = cal.get(Calendar.DAY_OF_MONTH);
                dikembalikanPerHari.put(hari, dikembalikanPerHari.getOrDefault(hari, 0) + 1);
            }
        }
        
        // Add data to dataset
        for (int i = 1; i <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dataset.addValue(dipinjamPerHari.getOrDefault(i, 0), "Dipinjam", String.valueOf(i));
            dataset.addValue(dikembalikanPerHari.getOrDefault(i, 0), "Dikembalikan", String.valueOf(i));
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Jumlah Peminjaman dan Pengembalian per Hari",
            "Tanggal",
            "Jumlah",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Customize chart appearance
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(200, 200, 200));
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        
        plot.getDomainAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(255, 0, 0)); // Red for Dipinjam
        renderer.setSeriesPaint(1, new Color(0, 120, 215)); // Blue for Dikembalikan
        renderer.setDrawBarOutline(false);
        
        chartPanel.setChart(chart);
    }
    
    private void printReport() {
        try {
            // Create a new table model for the report
            DefaultTableModel reportModel = new DefaultTableModel();
            reportModel.addColumn("LAPORAN TRANSAKSI PEMINJAMAN");
            
            // Add header information
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
            String currentDate = dateFormat.format(new Date());
            
            reportModel.addRow(new Object[]{"PERPUSTAKAAN"});
            reportModel.addRow(new Object[]{"SD IT IQRO BEKASI"});
            reportModel.addRow(new Object[]{""});
            reportModel.addRow(new Object[]{"Periode: " + bulanCombo.getSelectedItem() + " " + tahunCombo.getSelectedItem()});
            reportModel.addRow(new Object[]{""});
            
            // Add total loans
            reportModel.addRow(new Object[]{"1. JUMLAH TOTAL PEMINJAMAN PER BULAN"});
            reportModel.addRow(new Object[]{"   Total Peminjaman: " + totalPeminjamanLabel.getText()});
            reportModel.addRow(new Object[]{""});
            
            // Add loan status
            reportModel.addRow(new Object[]{"2. STATUS PEMINJAMAN"});
            reportModel.addRow(new Object[]{"   - Dipinjam: " + getStatusCount("Dipinjam")});
            reportModel.addRow(new Object[]{"   - Dikembalikan: " + getStatusCount("Dikembalikan")});
            reportModel.addRow(new Object[]{""});
            
            // Add top 5 books
            reportModel.addRow(new Object[]{"3. 5 BUKU PALING SERING DIPINJAM"});
            String[] topBooks = getTop5Books().split("\n");
            for (String book : topBooks) {
                reportModel.addRow(new Object[]{book});
            }
            reportModel.addRow(new Object[]{""});
            
            // Add fines
            reportModel.addRow(new Object[]{"4. DENDA"});
            reportModel.addRow(new Object[]{"   Total Denda: " + totalDendaLabel.getText()});
            reportModel.addRow(new Object[]{""});
            
            // Add footer
            reportModel.addRow(new Object[]{""});
            reportModel.addRow(new Object[]{"Mengetahui,"});
            reportModel.addRow(new Object[]{"Petugas Perpustakaan"});
            reportModel.addRow(new Object[]{""});
            reportModel.addRow(new Object[]{""});
            reportModel.addRow(new Object[]{""});
            reportModel.addRow(new Object[]{"(_________________)"});
            reportModel.addRow(new Object[]{"NIP:"});
            reportModel.addRow(new Object[]{"Tanggal: " + currentDate});
            
            // Create PDF file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Simpan Laporan PDF");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }
                
                // Create PDF document
                PDDocument document = new PDDocument();
                PDPage page = new PDPage();
                document.addPage(page);
                
                // Create content stream
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                
                // Set font
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                
                // Add title
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("LAPORAN TRANSAKSI PEMINJAMAN");
                contentStream.endText();
                
                // Add content
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);
                
                for (int i = 0; i < reportModel.getRowCount(); i++) {
                    String rowText = reportModel.getValueAt(i, 0).toString();
                    contentStream.showText(rowText);
                    contentStream.newLineAtOffset(0, -20);
                }
                
                contentStream.endText();
                contentStream.close();
                
                // Save the document
                document.save(filePath);
                document.close();
                
                JOptionPane.showMessageDialog(this, "Laporan berhasil disimpan sebagai PDF",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal membuat laporan PDF: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getStatusCount(String status) {
        int count = 0;
        for (int i = 0; i < printTableModel.getRowCount(); i++) {
            if (printTableModel.getValueAt(i, 3).equals(status)) {
                count++;
            }
        }
        return String.valueOf(count);
    }
    
    private String getTop5Books() {
        try {
            // Get the top 5 most borrowed books
            List<Object[]> topBooks = peminjamanDAO.getTop5BorrowedBooks();
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < topBooks.size(); i++) {
                Object[] book = topBooks.get(i);
                result.append(String.format("   %d. %s - Dipinjam %s kali\n",
                    i + 1,
                    book[0], // book title
                    book[1]  // borrow count
                ));
            }
            
            return result.toString();
        } catch (Exception e) {
            return "   Error mendapatkan data buku\n";
        }
    }
} 
package com.perpustakaan.view;

import com.perpustakaan.dao.PeminjamanDAO;
import com.perpustakaan.dao.PengembalianDAO;
import com.perpustakaan.model.Peminjaman;
import com.perpustakaan.model.Pengembalian;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PengembalianForm extends JPanel {
    private PengembalianDAO pengembalianDAO;
    private PeminjamanDAO peminjamanDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Peminjaman> peminjamanCombo;
    private JDateChooser tglKembaliChooser;
    private JTextField dendaField;
    private JTextField searchField;
    
    public PengembalianForm() {
        pengembalianDAO = new PengembalianDAO();
        peminjamanDAO = new PeminjamanDAO();
        initComponents();
        loadData();
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
        
        JLabel titleLabel = new JLabel("Manajemen Pengembalian");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel searchLabel = new JLabel("Cari:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchPanel.add(searchLabel);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchData();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchData();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchData();
            }
        });
        searchPanel.add(searchField);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Content Panel (Form + Table)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints contentGbc = new GridBagConstraints();
        contentGbc.fill = GridBagConstraints.BOTH;
        contentGbc.weightx = 0.4; // Form takes 40% of width
        contentGbc.weighty = 1.0;
        contentGbc.gridx = 0;
        contentGbc.gridy = 0;
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Labels and Fields with consistent width
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3; // Label takes 30% of form width
        formPanel.add(createLabel("Peminjaman:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7; // Field takes 70% of form width
        peminjamanCombo = new JComboBox<>();
        peminjamanCombo.setRenderer(new PeminjamanComboBoxRenderer());
        peminjamanCombo.setPreferredSize(new Dimension(250, 30));
        loadPeminjamanCombo();
        formPanel.add(peminjamanCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Tgl. Kembali:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        tglKembaliChooser = new JDateChooser();
        tglKembaliChooser.setDateFormatString("dd/MM/yyyy");
        tglKembaliChooser.setDate(new Date());
        tglKembaliChooser.setPreferredSize(new Dimension(250, 30));
        formPanel.add(tglKembaliChooser, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Denda:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        dendaField = new JTextField(10);
        dendaField.setEditable(false);
        dendaField.setPreferredSize(new Dimension(250, 30));
        formPanel.add(dendaField, gbc);
        
        // Buttons Panel with better spacing
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton hitungDendaButton = createButton("Hitung Denda", e -> hitungDenda());
        hitungDendaButton.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(hitungDendaButton);
        
        JButton simpanButton = createButton("Simpan", e -> savePengembalian());
        simpanButton.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(simpanButton);
        
        JButton clearButton = createButton("Clear", e -> clearForm());
        clearButton.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(clearButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 5, 5, 5);
        formPanel.add(buttonPanel, gbc);
        
        contentPanel.add(formPanel, contentGbc);
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table with better styling
        tableModel = new DefaultTableModel(new Object[]{"ID", "Anggota", "Tgl. Pinjam", 
            "Tgl. Kembali", "Denda"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(0, 120, 215));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        
        // Add scroll functionality
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        contentGbc.gridx = 1;
        contentGbc.weightx = 0.6; // Table takes 60% of width
        contentPanel.add(tablePanel, contentGbc);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
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
    
    private void loadPeminjamanCombo() {
        try {
            System.out.println("Loading peminjaman combo...");
            List<Peminjaman> peminjamanList = pengembalianDAO.getPeminjamanAktif();
            peminjamanCombo.removeAllItems();
            for (Peminjaman peminjaman : peminjamanList) {
                peminjamanCombo.addItem(peminjaman);
            }
            System.out.println("Loaded " + peminjamanList.size() + " active peminjaman");
        } catch (Exception e) {
            System.out.println("Error loading peminjaman combo:");
            e.printStackTrace();
        }
    }
    
    private void loadData() {
        try {
            System.out.println("Loading pengembalian data...");
            tableModel.setRowCount(0);
            
            List<Pengembalian> pengembalianList = pengembalianDAO.getAllPengembalian();
            System.out.println("Found " + pengembalianList.size() + " pengembalian records");
            
            for (Pengembalian pengembalian : pengembalianList) {
                Peminjaman peminjaman = pengembalian.getPeminjaman();
                if (peminjaman != null) {
                    tableModel.addRow(new Object[]{
                        pengembalian.getIdPengembalian(),
                        peminjaman.getAnggota().getNama(),
                        new java.text.SimpleDateFormat("dd/MM/yyyy").format(peminjaman.getTanggalPinjam()),
                        new java.text.SimpleDateFormat("dd/MM/yyyy").format(pengembalian.getTglKembali()),
                        "Rp " + String.format("%,d", pengembalian.getDenda())
                    });
                }
            }
            
            System.out.println("Successfully loaded " + tableModel.getRowCount() + " rows into table");
        } catch (Exception e) {
            System.out.println("Error loading pengembalian data:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data pengembalian: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hitungDenda() {
        try {
            Peminjaman peminjaman = (Peminjaman)peminjamanCombo.getSelectedItem();
            if (peminjaman == null) {
                JOptionPane.showMessageDialog(this, "Pilih peminjaman terlebih dahulu", "Peringatan", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Date tglKembali = tglKembaliChooser.getDate();
            if (tglKembali == null) {
                JOptionPane.showMessageDialog(this, "Pilih tanggal kembali terlebih dahulu", "Peringatan", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Date tglJatuhTempo = peminjaman.getTanggalKembali();
            System.out.println("Tanggal Jatuh Tempo: " + tglJatuhTempo);
            System.out.println("Tanggal Dikembalikan: " + tglKembali);
            
            if (tglKembali.after(tglJatuhTempo)) {
                // Hitung selisih hari
                long diffInMillies = tglKembali.getTime() - tglJatuhTempo.getTime();
                long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
                
                // Denda Rp 500 per hari
                int denda = (int)diffInDays * 500;
                
                System.out.println("Selisih hari: " + diffInDays);
                System.out.println("Denda: Rp " + denda);
                
                dendaField.setText(String.format("%,d", denda));
            } else {
                System.out.println("Tidak ada denda");
                dendaField.setText("0");
            }
        } catch (Exception e) {
            System.out.println("Error menghitung denda:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menghitung denda: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void savePengembalian() {
        try {
            System.out.println("Starting save pengembalian...");
            
            if (!validateInput()) {
                return;
            }
            
            if (dendaField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Silakan hitung denda terlebih dahulu", 
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Peminjaman peminjaman = (Peminjaman)peminjamanCombo.getSelectedItem();
            
            Pengembalian pengembalian = new Pengembalian();
            pengembalian.setIdPeminjaman(peminjaman.getIdPeminjaman());
            pengembalian.setTglKembali(tglKembaliChooser.getDate());
            pengembalian.setDenda(Integer.parseInt(dendaField.getText().replace(",", "")));
            
            System.out.println("Saving pengembalian data:");
            System.out.println("- ID Peminjaman: " + pengembalian.getIdPeminjaman());
            System.out.println("- Tanggal Kembali: " + pengembalian.getTglKembali());
            System.out.println("- Denda: " + pengembalian.getDenda());
            
            pengembalianDAO.addPengembalian(pengembalian);
            
            System.out.println("Pengembalian saved successfully");
            
            loadData();
            clearForm();
            
            JOptionPane.showMessageDialog(this, "Pengembalian berhasil disimpan", "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.out.println("Error saving pengembalian:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan pengembalian: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        try {
            System.out.println("Clearing form...");
            
            // Reset combo box
            peminjamanCombo.removeAllItems();
            loadPeminjamanCombo();
            
            // Reset date
            tglKembaliChooser.setDate(new Date());
            
            // Reset denda
            dendaField.setText("");
            
            System.out.println("Form cleared successfully");
        } catch (Exception e) {
            System.out.println("Error clearing form:");
            e.printStackTrace();
        }
    }
    
    private boolean validateInput() {
        if (peminjamanCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih peminjaman terlebih dahulu", "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (tglKembaliChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Tanggal kembali tidak boleh kosong", "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (dendaField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hitung denda terlebih dahulu", "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private class PeminjamanComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Peminjaman) {
                Peminjaman p = (Peminjaman) value;
                setText("ID: " + p.getIdPeminjaman() + " - " + p.getAnggota().getNama());
            }
            return this;
        }
    }
    
    private void searchData() {
        String keyword = searchField.getText().toLowerCase();
        try {
            tableModel.setRowCount(0);
            List<Pengembalian> pengembalianList = pengembalianDAO.getAllPengembalian();
            
            for (Pengembalian pengembalian : pengembalianList) {
                Peminjaman peminjaman = pengembalian.getPeminjaman();
                if (peminjaman != null) {
                    String namaAnggota = peminjaman.getAnggota().getNama().toLowerCase();
                    String tglPinjam = new java.text.SimpleDateFormat("dd/MM/yyyy")
                        .format(peminjaman.getTanggalPinjam());
                    String tglKembali = new java.text.SimpleDateFormat("dd/MM/yyyy")
                        .format(pengembalian.getTglKembali());
                    String denda = String.valueOf(pengembalian.getDenda());
                    
                    if (namaAnggota.contains(keyword) || 
                        tglPinjam.contains(keyword) || 
                        tglKembali.contains(keyword) || 
                        denda.contains(keyword)) {
                        
                        tableModel.addRow(new Object[]{
                            pengembalian.getIdPengembalian(),
                            peminjaman.getAnggota().getNama(),
                            tglPinjam,
                            tglKembali,
                            "Rp " + String.format("%,d", pengembalian.getDenda())
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error searching data:");
            e.printStackTrace();
        }
    }
} 
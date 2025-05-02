package com.perpustakaan.view;

import com.perpustakaan.dao.KategoriDAO;
import com.perpustakaan.model.Kategori;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class KategoriForm extends JPanel {
    private KategoriDAO kategoriDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField namaField;
    private JTextField searchField;
    
    public KategoriForm() {
        kategoriDAO = new KategoriDAO();
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
        
        JLabel titleLabel = new JLabel("Data Kategori");
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
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Nama Kategori:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        namaField = createTextField();
        formPanel.add(namaField, gbc);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.add(createButton("Tambah", e -> addKategori()));
        buttonPanel.add(createButton("Update", e -> updateKategori()));
        buttonPanel.add(createButton("Hapus", e -> deleteKategori()));
        buttonPanel.add(createButton("Clear", e -> clearForm()));
        
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        formPanel.add(buttonPanel, gbc);
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama Kategori"}, 0) {
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
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    namaField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                }
            }
        });
        
        // Add scroll functionality
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to main panel
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
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
        List<Kategori> kategoriList = kategoriDAO.getAllKategori();
        for (Kategori kategori : kategoriList) {
            tableModel.addRow(new Object[]{
                kategori.getIdKategori(),
                kategori.getNamaKategori()
            });
        }
    }
    
    private void addKategori() {
        String nama = namaField.getText().trim();
        
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori harus diisi", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Kategori kategori = new Kategori();
        kategori.setNamaKategori(nama);
        
        if (kategoriDAO.addKategori(kategori)) {
            JOptionPane.showMessageDialog(this, "Kategori berhasil ditambahkan", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan kategori", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateKategori() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang akan diupdate", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String nama = namaField.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori harus diisi", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (int)tableModel.getValueAt(selectedRow, 0);
        Kategori kategori = new Kategori();
        kategori.setIdKategori(id);
        kategori.setNamaKategori(nama);
        
        if (kategoriDAO.updateKategori(kategori)) {
            JOptionPane.showMessageDialog(this, "Kategori berhasil diupdate", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate kategori", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteKategori() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang akan dihapus", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (int)tableModel.getValueAt(selectedRow, 0);
        if (kategoriDAO.deleteKategori(id)) {
            JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus kategori", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        namaField.setText("");
        table.clearSelection();
    }
    
    private void searchData() {
        String keyword = searchField.getText().toLowerCase();
        try {
            List<Kategori> kategoriList = kategoriDAO.getAllKategori();
            tableModel.setRowCount(0);
            
            for (Kategori kategori : kategoriList) {
                if (kategori.getNamaKategori().toLowerCase().contains(keyword)) {
                    tableModel.addRow(new Object[]{
                        kategori.getIdKategori(),
                        kategori.getNamaKategori()
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Error searching data:");
            e.printStackTrace();
        }
    }
} 
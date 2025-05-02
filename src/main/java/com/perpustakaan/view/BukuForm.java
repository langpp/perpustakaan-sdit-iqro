package com.perpustakaan.view;

import com.perpustakaan.dao.BukuDAO;
import com.perpustakaan.dao.KategoriDAO;
import com.perpustakaan.model.Buku;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class BukuForm extends JPanel {
    private BukuDAO bukuDAO;
    private KategoriDAO kategoriDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField judulField;
    private JTextField pengarangField;
    private JTextField penerbitField;
    private JTextField tahunField;
    private JTextField stokField;
    private JComboBox<Kategori> kategoriCombo;
    private JTextField searchField;
    
    public BukuForm() {
        bukuDAO = new BukuDAO();
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
        
        JLabel titleLabel = new JLabel("Manajemen Buku");
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
        
        // Labels and Fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Judul Buku:"), gbc);
        gbc.gridx = 1;
        judulField = createTextField();
        formPanel.add(judulField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("Pengarang:"), gbc);
        gbc.gridx = 1;
        pengarangField = createTextField();
        formPanel.add(pengarangField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel("Penerbit:"), gbc);
        gbc.gridx = 1;
        penerbitField = createTextField();
        formPanel.add(penerbitField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createLabel("Tahun Terbit:"), gbc);
        gbc.gridx = 1;
        tahunField = createTextField();
        formPanel.add(tahunField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(createLabel("Kategori:"), gbc);
        gbc.gridx = 1;
        kategoriCombo = new JComboBox<>();
        loadKategoriCombo();
        formPanel.add(kategoriCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(createLabel("Stok:"), gbc);
        gbc.gridx = 1;
        stokField = createTextField();
        formPanel.add(stokField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton("Tambah", e -> addBuku()));
        buttonPanel.add(createButton("Update", e -> updateBuku()));
        buttonPanel.add(createButton("Hapus", e -> deleteBuku()));
        buttonPanel.add(createButton("Clear", e -> clearForm()));
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        contentPanel.add(formPanel, contentGbc);
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Judul", "Pengarang", "Penerbit", 
            "Tahun", "Kategori", "Stok"}, 0) {
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
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tableMouseClicked(e);
            }
        });
        
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
    
    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
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
    
    private void loadKategoriCombo() {
        try {
            List<Kategori> kategoriList = kategoriDAO.getAllKategori();
            kategoriCombo.removeAllItems();
            for (Kategori kategori : kategoriList) {
                kategoriCombo.addItem(kategori);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data kategori: " + e.getMessage());
        }
    }
    
    private void loadData() {
        try {
            List<Buku> bukuList = bukuDAO.getAllBuku();
            tableModel.setRowCount(0);
            
            for (Buku buku : bukuList) {
                Kategori kategori = kategoriDAO.getKategoriById(buku.getIdKategori());
                tableModel.addRow(new Object[]{
                    buku.getIdBuku(),
                    buku.getJudulBuku(),
                    buku.getPengarang(),
                    buku.getPenerbit(),
                    buku.getTahunTerbit(),
                    kategori != null ? kategori.getNamaKategori() : "",
                    buku.getStok()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data buku: " + e.getMessage());
        }
    }
    
    private void addBuku() {
        if (!validateInput()) {
            return;
        }
        
        try {
            Buku buku = new Buku();
            buku.setJudulBuku(judulField.getText());
            buku.setPengarang(pengarangField.getText());
            buku.setPenerbit(penerbitField.getText());
            buku.setTahunTerbit(Integer.parseInt(tahunField.getText()));
            buku.setIdKategori(((Kategori)kategoriCombo.getSelectedItem()).getIdKategori());
            buku.setStok(Integer.parseInt(stokField.getText()));
            
            bukuDAO.addBuku(buku);
            JOptionPane.showMessageDialog(this, "Data buku berhasil ditambahkan");
            loadData();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private void updateBuku() {
        if (!validateInput()) {
            return;
        }
        
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data buku yang akan diupdate");
                return;
            }
            
            Buku buku = new Buku();
            buku.setIdBuku((int)tableModel.getValueAt(selectedRow, 0));
            buku.setJudulBuku(judulField.getText());
            buku.setPengarang(pengarangField.getText());
            buku.setPenerbit(penerbitField.getText());
            buku.setTahunTerbit(Integer.parseInt(tahunField.getText()));
            buku.setIdKategori(((Kategori)kategoriCombo.getSelectedItem()).getIdKategori());
            buku.setStok(Integer.parseInt(stokField.getText()));
            
            bukuDAO.updateBuku(buku);
            JOptionPane.showMessageDialog(this, "Data buku berhasil diupdate");
            loadData();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private void deleteBuku() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data buku yang akan dihapus");
                return;
            }
            
            int idBuku = (int)tableModel.getValueAt(selectedRow, 0);
            if (JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", 
                    "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                bukuDAO.deleteBuku(idBuku);
                JOptionPane.showMessageDialog(this, "Data buku berhasil dihapus");
                loadData();
                clearForm();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        judulField.setText("");
        pengarangField.setText("");
        penerbitField.setText("");
        tahunField.setText("");
        stokField.setText("");
        kategoriCombo.setSelectedIndex(0);
        table.clearSelection();
    }
    
    private boolean validateInput() {
        if (judulField.getText().isEmpty() || pengarangField.getText().isEmpty() || 
            penerbitField.getText().isEmpty() || tahunField.getText().isEmpty() || 
            stokField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi");
            return false;
        }
        
        try {
            Integer.parseInt(tahunField.getText());
            Integer.parseInt(stokField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tahun dan stok harus berupa angka");
            return false;
        }
        
        return true;
    }

    private void tableMouseClicked(MouseEvent evt) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            judulField.setText(table.getValueAt(selectedRow, 1).toString());
            pengarangField.setText(table.getValueAt(selectedRow, 2).toString());
            penerbitField.setText(table.getValueAt(selectedRow, 3).toString());
            tahunField.setText(table.getValueAt(selectedRow, 4).toString());
            
            // Set kategori combo box
            String namaKategori = table.getValueAt(selectedRow, 5).toString();
            for (int i = 0; i < kategoriCombo.getItemCount(); i++) {
                Kategori kategori = (Kategori) kategoriCombo.getItemAt(i);
                if (kategori.getNamaKategori().equals(namaKategori)) {
                    kategoriCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            stokField.setText(table.getValueAt(selectedRow, 6).toString());
        }
    }

    private void searchData() {
        String keyword = searchField.getText().toLowerCase();
        try {
            List<Buku> bukuList = bukuDAO.getAllBuku();
            tableModel.setRowCount(0);
            
            for (Buku buku : bukuList) {
                Kategori kategori = kategoriDAO.getKategoriById(buku.getIdKategori());
                String namaKategori = kategori != null ? kategori.getNamaKategori().toLowerCase() : "";
                
                if (buku.getJudulBuku().toLowerCase().contains(keyword) ||
                    buku.getPengarang().toLowerCase().contains(keyword) ||
                    buku.getPenerbit().toLowerCase().contains(keyword) ||
                    String.valueOf(buku.getTahunTerbit()).contains(keyword) ||
                    namaKategori.contains(keyword) ||
                    String.valueOf(buku.getStok()).contains(keyword)) {
                    
                    tableModel.addRow(new Object[]{
                        buku.getIdBuku(),
                        buku.getJudulBuku(),
                        buku.getPengarang(),
                        buku.getPenerbit(),
                        buku.getTahunTerbit(),
                        kategori != null ? kategori.getNamaKategori() : "",
                        buku.getStok()
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Error searching data:");
            e.printStackTrace();
        }
    }
} 
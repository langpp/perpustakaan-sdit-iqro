package com.perpustakaan.view;

import com.perpustakaan.dao.AnggotaDAO;
import com.perpustakaan.model.Anggota;
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

public class AnggotaForm extends JPanel {
    private AnggotaDAO anggotaDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nisField;
    private JTextField namaField;
    private JTextField kelasField;
    private JTextField teleponField;
    private JTextField searchField;
    
    public AnggotaForm() {
        anggotaDAO = new AnggotaDAO();
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
        
        JLabel titleLabel = new JLabel("Manajemen Anggota");
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
        formPanel.add(createLabel("NIS:"), gbc);
        gbc.gridx = 1;
        nisField = createTextField();
        formPanel.add(nisField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("Nama:"), gbc);
        gbc.gridx = 1;
        namaField = createTextField();
        formPanel.add(namaField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel("Kelas:"), gbc);
        gbc.gridx = 1;
        kelasField = createTextField();
        formPanel.add(kelasField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createLabel("Telepon:"), gbc);
        gbc.gridx = 1;
        teleponField = createTextField();
        formPanel.add(teleponField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton("Tambah", e -> addAnggota()));
        buttonPanel.add(createButton("Update", e -> updateAnggota()));
        buttonPanel.add(createButton("Hapus", e -> deleteAnggota()));
        buttonPanel.add(createButton("Clear", e -> clearForm()));
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        contentGbc.gridx = 0;
        contentGbc.gridy = 0;
        contentGbc.weightx = 0.4; // Form takes 40% of width
        contentGbc.weighty = 1.0;
        contentPanel.add(formPanel, contentGbc);
        
        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "NIS", "Nama", "Kelas", 
            "Telepon", "Tanggal Daftar"}, 0) {
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
    
    private void loadData() {
        try {
            List<Anggota> anggotaList = anggotaDAO.getAllAnggota();
            tableModel.setRowCount(0);
            
            for (Anggota anggota : anggotaList) {
                tableModel.addRow(new Object[]{
                    anggota.getIdAnggota(),
                    anggota.getNis(),
                    anggota.getNama(),
                    anggota.getKelas(),
                    anggota.getTelepon(),
                    anggota.getTanggalDaftar()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data anggota: " + e.getMessage());
        }
    }
    
    private void addAnggota() {
        if (!validateInput()) {
            return;
        }
        
        try {
            Anggota anggota = new Anggota();
            anggota.setNis(nisField.getText());
            anggota.setNama(namaField.getText());
            anggota.setKelas(kelasField.getText());
            anggota.setTelepon(teleponField.getText());
            anggota.setTanggalDaftar(new java.util.Date());
            
            anggotaDAO.addAnggota(anggota);
            JOptionPane.showMessageDialog(this, "Data anggota berhasil ditambahkan");
            loadData();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private void updateAnggota() {
        if (!validateInput()) {
            return;
        }
        
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data anggota yang akan diupdate");
                return;
            }
            
            Anggota anggota = new Anggota();
            anggota.setIdAnggota((int)tableModel.getValueAt(selectedRow, 0));
            anggota.setNis(nisField.getText());
            anggota.setNama(namaField.getText());
            anggota.setKelas(kelasField.getText());
            anggota.setTelepon(teleponField.getText());
            anggota.setTanggalDaftar((java.util.Date)tableModel.getValueAt(selectedRow, 5));
            
            anggotaDAO.updateAnggota(anggota);
            JOptionPane.showMessageDialog(this, "Data anggota berhasil diupdate");
            loadData();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private void deleteAnggota() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data anggota yang akan dihapus");
                return;
            }
            
            int idAnggota = (int)tableModel.getValueAt(selectedRow, 0);
            if (JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", 
                    "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                anggotaDAO.deleteAnggota(idAnggota);
                JOptionPane.showMessageDialog(this, "Data anggota berhasil dihapus");
                loadData();
                clearForm();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        nisField.setText("");
        namaField.setText("");
        kelasField.setText("");
        teleponField.setText("");
        table.clearSelection();
    }
    
    private boolean validateInput() {
        if (nisField.getText().isEmpty() || namaField.getText().isEmpty() || 
            kelasField.getText().isEmpty() || teleponField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi");
            return false;
        }
        
        if (!teleponField.getText().matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Nomor telepon harus berupa angka");
            return false;
        }
        
        return true;
    }

    private void tableMouseClicked(MouseEvent evt) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            nisField.setText(table.getValueAt(selectedRow, 1).toString());
            namaField.setText(table.getValueAt(selectedRow, 2).toString());
            kelasField.setText(table.getValueAt(selectedRow, 3).toString());
            teleponField.setText(table.getValueAt(selectedRow, 4).toString());
        }
    }

    private void searchData() {
        String keyword = searchField.getText().toLowerCase();
        try {
            List<Anggota> anggotaList = anggotaDAO.getAllAnggota();
            tableModel.setRowCount(0);
            
            for (Anggota anggota : anggotaList) {
                if (anggota.getNis().toLowerCase().contains(keyword) ||
                    anggota.getNama().toLowerCase().contains(keyword) ||
                    anggota.getKelas().toLowerCase().contains(keyword) ||
                    anggota.getTelepon().toLowerCase().contains(keyword)) {
                    
                    tableModel.addRow(new Object[]{
                        anggota.getIdAnggota(),
                        anggota.getNis(),
                        anggota.getNama(),
                        anggota.getKelas(),
                        anggota.getTelepon(),
                        anggota.getTanggalDaftar()
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Error searching data:");
            e.printStackTrace();
        }
    }
} 
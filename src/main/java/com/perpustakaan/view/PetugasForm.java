package com.perpustakaan.view;

import com.perpustakaan.dao.PetugasDAO;
import com.perpustakaan.model.Petugas;
import com.perpustakaan.util.PasswordHasher;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class PetugasForm extends JPanel {
    private PetugasDAO petugasDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField namaField;
    
    public PetugasForm() {
        petugasDAO = new PetugasDAO();
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
        
        JLabel titleLabel = new JLabel("Data Petugas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = createTextField();
        formPanel.add(usernameField, gbc);
        
        // Password Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(createLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = createPasswordField();
        formPanel.add(passwordField, gbc);
        
        // Nama Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(createLabel("Nama:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        namaField = createTextField();
        formPanel.add(namaField, gbc);
        
        // Buttons
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weightx = 0.0;
        JButton addButton = createButton("Tambah", e -> addPetugas());
        formPanel.add(addButton, gbc);
        
        gbc.gridx = 3;
        JButton updateButton = createButton("Update", e -> updatePetugas());
        formPanel.add(updateButton, gbc);
        
        gbc.gridx = 4;
        JButton deleteButton = createButton("Hapus", e -> deletePetugas());
        formPanel.add(deleteButton, gbc);
        
        gbc.gridx = 5;
        JButton clearButton = createButton("Clear", e -> clearForm());
        formPanel.add(clearButton, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Username", "Nama"}, 0) {
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
                    usernameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    namaField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    passwordField.setText("");
                }
            }
        });
        
        // Add scroll functionality
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        add(scrollPane, BorderLayout.SOUTH);
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
    
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
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
        List<Petugas> petugasList = petugasDAO.getAllPetugas();
        for (Petugas petugas : petugasList) {
            tableModel.addRow(new Object[]{
                petugas.getIdPetugas(),
                petugas.getUsername(),
                petugas.getNama()
            });
        }
    }
    
    private void addPetugas() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String nama = namaField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Petugas petugas = new Petugas();
        petugas.setUsername(username);
        petugas.setPassword(PasswordHasher.hashPassword(password));
        petugas.setNama(nama);
        
        if (petugasDAO.addPetugas(petugas)) {
            JOptionPane.showMessageDialog(this, "Petugas berhasil ditambahkan", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan petugas", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updatePetugas() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih petugas yang akan diupdate", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String nama = namaField.getText().trim();
        
        if (username.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan nama harus diisi", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (int)tableModel.getValueAt(selectedRow, 0);
        Petugas petugas = new Petugas();
        petugas.setIdPetugas(id);
        petugas.setUsername(username);
        petugas.setNama(nama);
        
        // Only update password if it's not empty
        if (!password.isEmpty()) {
            petugas.setPassword(PasswordHasher.hashPassword(password));
        } else {
            // Get existing password
            Petugas existingPetugas = petugasDAO.getPetugasById(id);
            if (existingPetugas != null) {
                petugas.setPassword(existingPetugas.getPassword());
            }
        }
        
        if (petugasDAO.updatePetugas(petugas)) {
            JOptionPane.showMessageDialog(this, "Petugas berhasil diupdate", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate petugas", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deletePetugas() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih petugas yang akan dihapus", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int id = (int)tableModel.getValueAt(selectedRow, 0);
        if (petugasDAO.deletePetugas(id)) {
            JOptionPane.showMessageDialog(this, "Petugas berhasil dihapus", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus petugas", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        namaField.setText("");
        table.clearSelection();
    }
} 
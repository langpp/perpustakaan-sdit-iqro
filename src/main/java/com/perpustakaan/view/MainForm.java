package com.perpustakaan.view;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainForm extends JFrame {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JPanel menuPanel;
    
    public MainForm() {
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Sistem Perpustakaan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header Panel
        JPanel headerPanel = new JPanel() {
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
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Sistem Perpustakaan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel logoutLabel = new JLabel("Logout");
        logoutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutLabel.setForeground(Color.WHITE);
        logoutLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginForm().setVisible(true);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutLabel.setForeground(new Color(200, 200, 200));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                logoutLabel.setForeground(Color.WHITE);
            }
        });
        headerPanel.add(logoutLabel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Menu Panel
        menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(new Color(240, 240, 240));
        menuPanel.setPreferredSize(new Dimension(250, 0));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        addMenuButtons();
        
        mainPanel.add(menuPanel, BorderLayout.WEST);
        
        // Content Panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void addMenuButtons() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        addMenuButton("Data Buku", 0, gbc, () -> showContent(new BukuForm()));
        addMenuButton("Data Anggota", 1, gbc, () -> showContent(new AnggotaForm()));
        addMenuButton("Peminjaman", 2, gbc, () -> showContent(new PeminjamanForm()));
        addMenuButton("Pengembalian", 3, gbc, () -> showContent(new PengembalianForm()));
        addMenuButton("Laporan Buku", 4, gbc, () -> showContent(new LaporanBukuForm()));
        addMenuButton("Laporan Transaksi", 5, gbc, () -> showContent(new LaporanTransaksiForm()));
        addMenuButton("Data Kategori", 6, gbc, () -> showContent(new KategoriForm()));
        addMenuButton("Data Petugas", 7, gbc, () -> showContent(new PetugasForm()));
    }
    
    private void addMenuButton(String text, int gridy, GridBagConstraints gbc, Runnable action) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel buttonLabel = new JLabel(text);
        buttonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        buttonLabel.setForeground(new Color(80, 80, 80));
        
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                buttonPanel.setBackground(new Color(230, 230, 230));
                buttonLabel.setForeground(new Color(0, 120, 215));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                buttonPanel.setBackground(new Color(240, 240, 240));
                buttonLabel.setForeground(new Color(80, 80, 80));
            }
        });
        
        buttonPanel.add(buttonLabel, BorderLayout.WEST);
        
        gbc.gridy = gridy;
        menuPanel.add(buttonPanel, gbc);
    }
    
    private void showContent(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainForm().setVisible(true);
        });
    }
} 
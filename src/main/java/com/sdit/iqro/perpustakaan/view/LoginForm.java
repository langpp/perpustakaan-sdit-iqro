package com.sdit.iqro.perpustakaan.view;

import com.sdit.iqro.perpustakaan.dao.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.sdit.iqro.perpustakaan.model.User;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    
    public LoginForm() {
        initComponents();
    }
    
    private void initComponents() {
        // Set up the JFrame
        setTitle("Login Aplikasi Perpustakaan");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(51, 102, 255));
        headerPanel.setPreferredSize(new Dimension(400, 60));
        
        JLabel lblHeader = new JLabel("SISTEM INFORMASI PERPUSTAKAAN");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(lblHeader);
        
        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 30, 80, 25);
        formPanel.add(lblUsername);
        
        txtUsername = new JTextField();
        txtUsername.setBounds(130, 30, 200, 25);
        formPanel.add(txtUsername);
        
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 70, 80, 25);
        formPanel.add(lblPassword);
        
        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 70, 200, 25);
        formPanel.add(txtPassword);
        
        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(100, 30));
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(100, 30));
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);
        
        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void login() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(username, password);
        
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login berhasil!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Buka form utama dan kirim data user
            BerandaForm beranda = new BerandaForm(user);
            beranda.setVisible(true);
            this.dispose(); // Tutup form login
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Tampilkan form login
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}
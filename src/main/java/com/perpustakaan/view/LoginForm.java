package com.perpustakaan.view;

import com.perpustakaan.dao.PetugasDAO;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel mainPanel;
    private float alpha = 0f;
    private Timer fadeInTimer;
    
    public LoginForm() {
        initComponents();
        setupFadeInAnimation();
    }
    
    private void initComponents() {
        setTitle("Login - Sistem Perpustakaan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, (int)(alpha * 255)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Sistem Perpustakaan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 120, 215));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Username Field
        gbc.gridy = 0;
        JLabel usernameLabel = createLabel("Username");
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridy = 1;
        usernameField = createTextField();
        formPanel.add(usernameField, gbc);
        
        // Password Field
        gbc.gridy = 2;
        JLabel passwordLabel = createLabel("Password");
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridy = 3;
        passwordField = createPasswordField();
        formPanel.add(passwordField, gbc);
        
        // Login Button
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 0, 0);
        loginButton = createButton("Login");
        formPanel.add(loginButton, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Add action listener to login button
        loginButton.addActionListener(e -> login());
        
        // Add enter key listener to password field
        passwordField.addActionListener(e -> login());
    }
    
    private void setupFadeInAnimation() {
        fadeInTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.05f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    fadeInTimer.stop();
                }
                mainPanel.repaint();
            }
        });
        fadeInTimer.start();
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(80, 80, 80));
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(new Color(250, 250, 250));
        return field;
    }
    
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(new Color(250, 250, 250));
        return field;
    }
    
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(0, 120, 215));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(0, 40));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 100, 195));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 120, 215));
            }
        });
        
        return button;
    }
    
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password harus diisi!");
            return;
        }
        
        try {
            System.out.println("Attempting login for user: " + username);
            PetugasDAO petugasDAO = new PetugasDAO();
            boolean isAuthenticated = petugasDAO.authenticate(username, password);
            System.out.println("Login result: " + isAuthenticated);
            
            if (isAuthenticated) {
                dispose();
                new MainForm().setVisible(true);
            } else {
                showError("Username atau password salah!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Terjadi kesalahan saat login: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        JLabel errorLabel = new JLabel(message);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(Color.RED);
        
        JPanel errorPanel = new JPanel();
        errorPanel.setOpaque(false);
        errorPanel.add(errorLabel);
        
        mainPanel.add(errorPanel, BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();
        
        Timer timer = new Timer(3000, e -> {
            mainPanel.remove(errorPanel);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
} 
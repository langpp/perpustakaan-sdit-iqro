package com.perpustakaan;

import com.perpustakaan.view.LoginForm;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {
    private static JFrame splashFrame;
    private static float alpha = 0f;
    private static Timer fadeInTimer;
    private static Timer fadeOutTimer;
    
    public static void main(String[] args) {
        // Create and show splash screen
        createSplashScreen();
        
        // Simulate loading time
        Timer loadingTimer = new Timer(3000, e -> {
            fadeOutTimer.start();
        });
        loadingTimer.setRepeats(false);
        loadingTimer.start();
    }
    
    private static void createSplashScreen() {
        splashFrame = new JFrame();
        splashFrame.setUndecorated(true);
        splashFrame.setSize(400, 300);
        splashFrame.setLocationRelativeTo(null);
        
        JPanel splashPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Draw background gradient
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 120, 215), 
                        0, getHeight(), new Color(0, 80, 180));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw fade effect
                g2d.setColor(new Color(0, 0, 0, (int)(alpha * 255)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        splashPanel.setLayout(new BorderLayout());
        
        // Add title
        JLabel titleLabel = new JLabel("Sistem Perpustakaan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        splashPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Add loading text
        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setHorizontalAlignment(JLabel.CENTER);
        splashPanel.add(loadingLabel, BorderLayout.SOUTH);
        
        splashFrame.add(splashPanel);
        splashFrame.setVisible(true);
        
        // Setup fade in animation
        fadeInTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.05f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    fadeInTimer.stop();
                }
                splashPanel.repaint();
            }
        });
        fadeInTimer.start();
        
        // Setup fade out animation
        fadeOutTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.05f;
                if (alpha <= 0f) {
                    alpha = 0f;
                    fadeOutTimer.stop();
                    splashFrame.dispose();
                    showLoginForm();
                }
                splashPanel.repaint();
            }
        });
    }
    
    private static void showLoginForm() {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
} 
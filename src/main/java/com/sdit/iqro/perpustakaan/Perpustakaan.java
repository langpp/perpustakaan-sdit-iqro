package com.sdit.iqro.perpustakaan;

import com.sdit.iqro.perpustakaan.view.LoginForm;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

public class Perpustakaan {
    
    public static void main(String[] args) {
        System.out.println("=== SISTEM INFORMASI PERPUSTAKAAN ===");
        System.out.println("Memulai aplikasi...");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Look and Feel: " + UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting Look and Feel: " + e.getMessage());
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
                System.out.println("Login form ditampilkan");
            }
        });
    }
}
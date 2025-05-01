package com.sdit.iqro.perpustakaan.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.sdit.iqro.perpustakaan.model.User;

public class BerandaForm extends JFrame {
    private User user;
    private JLabel lblWelcome;
    private JLabel lblDate;
    private JLabel lblTime;
    private JPanel contentPanel;
    private Timer timer;
    
    // Menu items
    private JMenuBar menuBar;
    private JMenu menuMaster, menuTransaksi, menuLaporan, menuSistem;
    private JMenuItem menuBuku, menuAnggota, menuKategori, menuPetugas;
    private JMenuItem menuPeminjaman, menuPengembalian;
    private JMenuItem menuLaporanBuku, menuLaporanAnggota, menuLaporanTransaksi;
    private JMenuItem menuLogout, menuExit;
    
    // JButton untuk dashboard
    private JButton btnBuku, btnAnggota, btnPeminjaman, btnPengembalian, btnLaporan;
    
    public BerandaForm(User user) {
        this.user = user;
        initComponents();
        startClock();
    }
    
    private void initComponents() {
        // Setup the JFrame
        setTitle("Sistem Informasi Perpustakaan");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Setup menu bar
        setupMenuBar();
        setJMenuBar(menuBar);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(51, 102, 255));
        headerPanel.setPreferredSize(new Dimension(1000, 80));
        headerPanel.setLayout(new BorderLayout());
        
        // Left side of header (welcome message)
        JPanel leftHeader = new JPanel();
        leftHeader.setBackground(new Color(51, 102, 255));
        leftHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 25));
        
        lblWelcome = new JLabel("Selamat Datang, " + user.getNama());
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        leftHeader.add(lblWelcome);
        
        // Right side of header (date & time)
        JPanel rightHeader = new JPanel();
        rightHeader.setBackground(new Color(51, 102, 255));
        rightHeader.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        
        lblDate = new JLabel(dateFormat.format(now));
        lblDate.setForeground(Color.WHITE);
        lblDate.setFont(new Font("Arial", Font.PLAIN, 14));
        
        lblTime = new JLabel("00:00:00");
        lblTime.setForeground(Color.WHITE);
        lblTime.setFont(new Font("Arial", Font.PLAIN, 14));
        
        rightHeader.add(lblDate);
        rightHeader.add(lblTime);
        
        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        
        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        
        // Create Dashboard Panel
        JPanel dashboardPanel = createDashboardPanel();
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        
        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(220, 220, 220));
        footerPanel.setPreferredSize(new Dimension(1000, 25));
        
        JLabel lblFooter = new JLabel("© 2025 Sistem Informasi Perpustakaan | Developed by Your Name");
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 12));
        footerPanel.add(lblFooter);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        
        // Menu Master
        menuMaster = new JMenu("Master Data");
        menuBuku = new JMenuItem("Data Buku");
        menuAnggota = new JMenuItem("Data Anggota");
        menuKategori = new JMenuItem("Data Kategori");
        menuPetugas = new JMenuItem("Data Petugas");
        
        menuMaster.add(menuBuku);
        menuMaster.add(menuAnggota);
        menuMaster.add(menuKategori);
        menuMaster.add(menuPetugas);
        
        // Menu Transaksi
        menuTransaksi = new JMenu("Transaksi");
        menuPeminjaman = new JMenuItem("Peminjaman Buku");
        menuPengembalian = new JMenuItem("Pengembalian Buku");
        
        menuTransaksi.add(menuPeminjaman);
        menuTransaksi.add(menuPengembalian);
        
        // Menu Laporan
        menuLaporan = new JMenu("Laporan");
        menuLaporanBuku = new JMenuItem("Laporan Data Buku");
        menuLaporanAnggota = new JMenuItem("Laporan Data Anggota");
        menuLaporanTransaksi = new JMenuItem("Laporan Transaksi");
        
        menuLaporan.add(menuLaporanBuku);
        menuLaporan.add(menuLaporanAnggota);
        menuLaporan.add(menuLaporanTransaksi);
        
        // Menu Sistem
        menuSistem = new JMenu("Sistem");
        menuLogout = new JMenuItem("Logout");
        menuExit = new JMenuItem("Exit");
        
        menuSistem.add(menuLogout);
        menuSistem.addSeparator();
        menuSistem.add(menuExit);
        
        // Add action listeners
        menuLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        
        menuExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Add all menus to menu bar
        menuBar.add(menuMaster);
        menuBar.add(menuTransaksi);
        menuBar.add(menuLaporan);
        menuBar.add(menuSistem);
    }
    
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setPreferredSize(new Dimension(1000, 60));
        
        JLabel lblTitle = new JLabel("DASHBOARD PERPUSTAKAAN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(lblTitle);
        
        // Dashboard content
        JPanel dashContent = new JPanel();
        dashContent.setLayout(new GridLayout(1, 5, 20, 20));
        dashContent.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Create menu buttons with icons
        btnBuku = createButton("Data Buku", "book.png", new Color(52, 152, 219));
        btnAnggota = createButton("Data Anggota", "member.png", new Color(46, 204, 113));
        btnPeminjaman = createButton("Peminjaman", "borrow.png", new Color(155, 89, 182));
        btnPengembalian = createButton("Pengembalian", "return.png", new Color(230, 126, 34));
        btnLaporan = createButton("Laporan", "report.png", new Color(231, 76, 60));
        
        dashContent.add(btnBuku);
        dashContent.add(btnAnggota);
        dashContent.add(btnPeminjaman);
        dashContent.add(btnPengembalian);
        dashContent.add(btnLaporan);
        
        // Statistik panel
        JPanel statPanel = new JPanel();
        statPanel.setLayout(new GridLayout(1, 4, 15, 15));
        statPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));
        
        // Statistik cards
        JPanel totalBukuCard = createStatCard("Total Buku", "15", new Color(52, 152, 219));
        JPanel totalAnggotaCard = createStatCard("Total Anggota", "10", new Color(46, 204, 113));
        JPanel peminjamanCard = createStatCard("Dipinjam", "2", new Color(230, 126, 34));
        JPanel pengembalianCard = createStatCard("Dikembalikan", "8", new Color(231, 76, 60));
        
        statPanel.add(totalBukuCard);
        statPanel.add(totalAnggotaCard);
        statPanel.add(peminjamanCard);
        statPanel.add(pengembalianCard);
        
        // Add components to dashboard panel
        dashboardPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Panel for holding both dash content and stats
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(dashContent, BorderLayout.NORTH);
        centerPanel.add(statPanel, BorderLayout.CENTER);
        
        dashboardPanel.add(centerPanel, BorderLayout.CENTER);
        
        return dashboardPanel;
    }
    
    private JButton createButton(String text, String iconName, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 120));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        
        // We'll set an imaginary icon here
        // In a real app, you'd load the actual icon:
        // button.setIcon(new ImageIcon(getClass().getResource("/images/" + iconName)));
        
        return button;
    }
    
    private JPanel createStatCard(String title, String value, Color bgColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(bgColor);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 32));
        lblValue.setForeground(Color.WHITE);
        lblValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblValue.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        
        return card;
    }
    
    private void startClock() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date now = new Date();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                lblTime.setText(timeFormat.format(now));
            }
        });
        timer.start();
    }
    
    private void logout() {
        int option = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin logout?", 
                "Konfirmasi Logout", 
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginForm().setVisible(true);
        }
    }
    
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Contoh untuk testing
        User dummyUser = new User(1, "admin", "password", "Admin Perpustakaan", "NIP001");
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BerandaForm(dummyUser).setVisible(true);
            }
        });
    }
}
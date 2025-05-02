package com.perpustakaan.view;

import com.perpustakaan.dao.AnggotaDAO;
import com.perpustakaan.dao.BukuDAO;
import com.perpustakaan.dao.PeminjamanDAO;
import com.perpustakaan.dao.PetugasDAO;
import com.perpustakaan.model.Anggota;
import com.perpustakaan.model.Buku;
import com.perpustakaan.model.DetailPeminjaman;
import com.perpustakaan.model.Peminjaman;
import com.perpustakaan.model.Petugas;
import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.FlowLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PeminjamanForm extends JPanel {
    private PeminjamanDAO peminjamanDAO;
    private AnggotaDAO anggotaDAO;
    private BukuDAO bukuDAO;
    private PetugasDAO petugasDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Anggota> anggotaCombo;
    private JComboBox<Buku> bukuCombo;
    private JDateChooser tglPinjamChooser;
    private JDateChooser tglKembaliChooser;
    private JTable detailTable;
    private DefaultTableModel detailTableModel;
    private List<DetailPeminjaman> detailList;
    private int currentIdPinjam = -1; // Menyimpan id_pinjam sementara
    private JTextField searchField; // Add search field
    
    public PeminjamanForm() {
        peminjamanDAO = new PeminjamanDAO();
        anggotaDAO = new AnggotaDAO();
        bukuDAO = new BukuDAO();
        petugasDAO = new PetugasDAO();
        detailList = new ArrayList<>();
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
        
        JLabel titleLabel = new JLabel("Manajemen Peminjaman");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Add Search Panel
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

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Labels and Fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Anggota:"), gbc);
        gbc.gridx = 1;
        anggotaCombo = new JComboBox<>();
        loadAnggotaCombo();
        formPanel.add(anggotaCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("Buku:"), gbc);
        gbc.gridx = 1;
        bukuCombo = new JComboBox<>();
        loadBukuCombo();
        formPanel.add(bukuCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel("Tgl. Pinjam:"), gbc);
        gbc.gridx = 1;
        tglPinjamChooser = new JDateChooser();
        tglPinjamChooser.setDateFormatString("dd/MM/yyyy");
        tglPinjamChooser.setDate(new Date());
        formPanel.add(tglPinjamChooser, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createLabel("Tgl. Kembali:"), gbc);
        gbc.gridx = 1;
        tglKembaliChooser = new JDateChooser();
        tglKembaliChooser.setDateFormatString("dd/MM/yyyy");
        tglKembaliChooser.setDate(new Date());
        formPanel.add(tglKembaliChooser, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton("Tambah Buku", e -> addDetail()));
        buttonPanel.add(createButton("Hapus Buku", e -> deleteDetail()));
        buttonPanel.add(createButton("Simpan", e -> savePeminjaman()));
        buttonPanel.add(createButton("Clear", e -> clearForm()));
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        add(formPanel, BorderLayout.WEST);
        
        // Detail Table
        detailTableModel = new DefaultTableModel(new Object[]{"ID", "Judul Buku"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        detailTable = new JTable(detailTableModel);
        detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailTable.getTableHeader().setBackground(new Color(0, 120, 215));
        detailTable.getTableHeader().setForeground(Color.WHITE);
        detailTable.setRowHeight(25);
        
        JScrollPane detailScrollPane = new JScrollPane(detailTable);
        detailScrollPane.setPreferredSize(new Dimension(300, 200));
        formPanel.add(detailScrollPane, gbc);
        
        // Main Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Anggota", "Tgl. Pinjam", 
            "Tgl. Kembali", "Status"}, 0) {
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
        
        // Add mouse listener to show detail dialog
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    int idPeminjaman = (int) tableModel.getValueAt(row, 0);
                    showDetailDialog(idPeminjaman);
                }
            }
        });
        
        // Add scroll functionality
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
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
    
    private void loadAnggotaCombo() {
        List<Anggota> anggotaList = anggotaDAO.getAllAnggota();
        anggotaCombo.removeAllItems();
        for (Anggota anggota : anggotaList) {
            anggotaCombo.addItem(anggota);
        }
    }
    
    private void loadBukuCombo() {
        bukuCombo.removeAllItems();
        List<Buku> bukuList = bukuDAO.getAllBuku();
        for (Buku buku : bukuList) {
            if (buku.getStok() > 0) {
                bukuCombo.addItem(buku);
            }
        }
    }
    
    private void loadData() {
        try {
            System.out.println("=== Loading peminjaman data ===");
            tableModel.setRowCount(0); // Clear existing data
            
            List<Peminjaman> peminjamanList = peminjamanDAO.getAllPeminjaman();
            System.out.println("Found " + peminjamanList.size() + " peminjaman records");
            
            for (Peminjaman peminjaman : peminjamanList) {
                System.out.println("Processing peminjaman: " + peminjaman.getIdPeminjaman());
                
                // Get anggota data
                Anggota anggota = anggotaDAO.getAnggotaById(peminjaman.getIdAnggota());
                String namaAnggota = (anggota != null) ? anggota.getNama() : "Unknown";
                
                // Format dates
                String tglPinjam = (peminjaman.getTanggalPinjam() != null) ? 
                    new java.text.SimpleDateFormat("dd/MM/yyyy").format(peminjaman.getTanggalPinjam()) : "";
                String tglKembali = (peminjaman.getTanggalKembali() != null) ? 
                    new java.text.SimpleDateFormat("dd/MM/yyyy").format(peminjaman.getTanggalKembali()) : "";
                
                // Get status
                String status = (peminjaman.getStatus() != null) ? peminjaman.getStatus() : "";
                
                // Add row to table
                tableModel.addRow(new Object[]{
                    peminjaman.getIdPeminjaman(),
                    namaAnggota,
                    tglPinjam,
                    tglKembali,
                    status
                });
            }
            
            System.out.println("Successfully loaded " + tableModel.getRowCount() + " rows into table");
        } catch (Exception e) {
            System.out.println("Error loading peminjaman data:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data peminjaman: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addDetail() {
        if (currentIdPinjam == -1) {
            // Jika belum ada id_pinjam, buat peminjaman dulu
            try {
                System.out.println("=== Creating new peminjaman ===");
                
                // Validasi input
                if (anggotaCombo.getSelectedItem() == null) {
                    System.out.println("Error: Anggota belum dipilih");
                    JOptionPane.showMessageDialog(this, "Pilih anggota terlebih dahulu", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tglPinjamChooser.getDate() == null) {
                    System.out.println("Error: Tanggal pinjam belum dipilih");
                    JOptionPane.showMessageDialog(this, "Pilih tanggal pinjam terlebih dahulu", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tglKembaliChooser.getDate() == null) {
                    System.out.println("Error: Tanggal kembali belum dipilih");
                    JOptionPane.showMessageDialog(this, "Pilih tanggal kembali terlebih dahulu", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Peminjaman peminjaman = new Peminjaman();
                Anggota selectedAnggota = (Anggota)anggotaCombo.getSelectedItem();
                peminjaman.setIdAnggota(selectedAnggota.getIdAnggota());
                peminjaman.setIdPetugas(1); // Default petugas
                peminjaman.setTanggalPinjam(tglPinjamChooser.getDate());
                peminjaman.setTanggalKembali(tglKembaliChooser.getDate());
                peminjaman.setStatus("Dipinjam"); // Set default status

                System.out.println("Creating new peminjaman with data:");
                System.out.println("- id_anggota: " + peminjaman.getIdAnggota());
                System.out.println("- id_petugas: " + peminjaman.getIdPetugas());
                System.out.println("- tanggal_pinjam: " + peminjaman.getTanggalPinjam());
                System.out.println("- tanggal_kembali: " + peminjaman.getTanggalKembali());
                System.out.println("- status: " + peminjaman.getStatus());

                currentIdPinjam = peminjamanDAO.addPeminjaman(peminjaman);
                System.out.println("Got id_pinjam: " + currentIdPinjam);
                
                if (currentIdPinjam == -1) {
                    System.out.println("Error: Failed to create peminjaman");
                    JOptionPane.showMessageDialog(this, "Gagal membuat peminjaman", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    System.out.println("Success: Peminjaman created with id: " + currentIdPinjam);
                }
            } catch (Exception e) {
                System.out.println("Error in creating peminjaman:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal membuat peminjaman: " + e.getMessage(), "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Buku selectedBuku = (Buku)bukuCombo.getSelectedItem();
        if (selectedBuku == null) {
            System.out.println("Error: Buku belum dipilih");
            JOptionPane.showMessageDialog(this, "Pilih buku terlebih dahulu", "Peringatan", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Cek apakah buku sudah ada di detail table
        for (int i = 0; i < detailTableModel.getRowCount(); i++) {
            if ((int)detailTableModel.getValueAt(i, 0) == selectedBuku.getIdBuku()) {
                System.out.println("Error: Buku sudah ditambahkan sebelumnya");
                JOptionPane.showMessageDialog(this, "Buku sudah ditambahkan", "Peringatan", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            System.out.println("=== Adding detail peminjaman ===");
            System.out.println("- id_pinjam: " + currentIdPinjam);
            System.out.println("- id_buku: " + selectedBuku.getIdBuku());
            
            // Tambahkan ke detail_peminjaman
            DetailPeminjaman detail = new DetailPeminjaman();
            detail.setIdPinjam(currentIdPinjam);
            detail.setIdBuku(selectedBuku.getIdBuku());
            List<DetailPeminjaman> detailList = new ArrayList<>();
            detailList.add(detail);
            
            peminjamanDAO.addDetailPeminjaman(detailList);

            // Tambahkan ke detail table
            detailTableModel.addRow(new Object[]{
                selectedBuku.getIdBuku(),
                selectedBuku.getJudulBuku()
            });

            // Refresh buku combo untuk menampilkan stok yang tersedia
            loadBukuCombo();
            
            System.out.println("Success: Detail peminjaman added successfully");
            JOptionPane.showMessageDialog(this, "Buku berhasil ditambahkan", "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.out.println("Error in adding detail peminjaman:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambahkan buku: " + e.getMessage(), "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteDetail() {
        int selectedRow = detailTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih buku yang akan dihapus", "Peringatan", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Hapus dari detail_peminjaman
            int idBuku = (int)detailTableModel.getValueAt(selectedRow, 0);
            peminjamanDAO.deleteDetailPeminjaman(currentIdPinjam, idBuku);

            // Hapus dari detail table
            detailTableModel.removeRow(selectedRow);
            loadBukuCombo();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menghapus buku: " + e.getMessage(), "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void savePeminjaman() {
        if (validateInput()) {
            try {
                // Update status peminjaman jika ada perubahan
                if (currentIdPinjam != -1) {
                    peminjamanDAO.updateStatusPeminjaman(currentIdPinjam, "Dipinjam");
                }
                
                // Refresh data dan clear form
                loadData();
                clearForm();
                currentIdPinjam = -1; // Reset id_pinjam
                JOptionPane.showMessageDialog(this, "Peminjaman berhasil disimpan", "Sukses", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage(), "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearForm() {
        anggotaCombo.setSelectedIndex(0);
        bukuCombo.setSelectedIndex(0);
        tglPinjamChooser.setDate(new Date());
        tglKembaliChooser.setDate(new Date());
        detailTableModel.setRowCount(0);
        loadBukuCombo();
        currentIdPinjam = -1; // Reset id_pinjam
    }
    
    private boolean validateInput() {
        // Validasi anggota
        if (anggotaCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih anggota terlebih dahulu", "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validasi tanggal pinjam
        if (tglPinjamChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Pilih tanggal pinjam terlebih dahulu", "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validasi tanggal kembali
        if (tglKembaliChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Pilih tanggal kembali terlebih dahulu", "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validasi minimal 1 buku
        if (detailTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tambahkan minimal 1 buku", "Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
    
    private void showDetailDialog(int idPeminjaman) {
        try {
            System.out.println("Showing detail dialog for id: " + idPeminjaman);
            
            // Get peminjaman details
            Peminjaman peminjaman = peminjamanDAO.getPeminjamanById(idPeminjaman);
            if (peminjaman == null) {
                System.out.println("Peminjaman not found");
                JOptionPane.showMessageDialog(this, "Data peminjaman tidak ditemukan", "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Getting detail list...");
            List<DetailPeminjaman> detailList = peminjamanDAO.getDetailPeminjaman(idPeminjaman);
            System.out.println("Detail list size: " + detailList.size());
            
            // Create dialog
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Detail Peminjaman #" + idPeminjaman, true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(800, 500);
            dialog.setLocationRelativeTo(this);
            
            // Create main panel
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Create info panel
            JPanel infoPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Add peminjaman info
            gbc.gridx = 0;
            gbc.gridy = 0;
            infoPanel.add(new JLabel("ID Peminjaman:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(String.valueOf(peminjaman.getIdPeminjaman())), gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 1;
            infoPanel.add(new JLabel("Nama Anggota:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(peminjaman.getAnggota().getNama()), gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 2;
            infoPanel.add(new JLabel("Tanggal Pinjam:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(new java.text.SimpleDateFormat("dd/MM/yyyy")
                .format(peminjaman.getTanggalPinjam())), gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 3;
            infoPanel.add(new JLabel("Tanggal Kembali:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(new java.text.SimpleDateFormat("dd/MM/yyyy")
                .format(peminjaman.getTanggalKembali())), gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 4;
            infoPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(new JLabel(peminjaman.getStatus()), gbc);
            
            mainPanel.add(infoPanel, BorderLayout.NORTH);
            
            // Create table for book details
            String[] columnNames = {"No", "Judul Buku", "Pengarang", "Penerbit", "Tahun Terbit"};
            DefaultTableModel detailTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            JTable detailTable = new JTable(detailTableModel);
            detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            detailTable.getTableHeader().setBackground(new Color(0, 120, 215));
            detailTable.getTableHeader().setForeground(Color.WHITE);
            detailTable.setRowHeight(25);
            
            // Add book details to table
            System.out.println("Adding books to table...");
            int no = 1;
            for (DetailPeminjaman detail : detailList) {
                System.out.println("Adding book: " + detail.getJudulBuku());
                detailTableModel.addRow(new Object[]{
                    no++,
                    detail.getJudulBuku(),
                    detail.getPengarang(),
                    detail.getPenerbit(),
                    detail.getTahunTerbit()
                });
            }
            System.out.println("Total rows in table: " + detailTableModel.getRowCount());
            
            JScrollPane scrollPane = new JScrollPane(detailTable);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Add close button
            JPanel buttonPanel = new JPanel();
            JButton closeButton = new JButton("Tutup");
            closeButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(mainPanel);
            dialog.setVisible(true);
        } catch (Exception e) {
            System.out.println("Error showing detail dialog:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menampilkan detail: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add search method at the end of the class
    private void searchData() {
        String keyword = searchField.getText().toLowerCase();
        try {
            System.out.println("Searching for: " + keyword);
            tableModel.setRowCount(0);
            
            List<Peminjaman> peminjamanList = peminjamanDAO.getAllPeminjaman();
            
            for (Peminjaman peminjaman : peminjamanList) {
                String namaAnggota = peminjaman.getAnggota().getNama().toLowerCase();
                String tglPinjam = new java.text.SimpleDateFormat("dd/MM/yyyy")
                    .format(peminjaman.getTanggalPinjam());
                String tglKembali = peminjaman.getTanggalKembali() != null ? 
                    new java.text.SimpleDateFormat("dd/MM/yyyy").format(peminjaman.getTanggalKembali()) : "";
                String status = peminjaman.getStatus().toLowerCase();
                
                if (namaAnggota.contains(keyword) || 
                    tglPinjam.contains(keyword) || 
                    tglKembali.contains(keyword) || 
                    status.contains(keyword)) {
                    
                    tableModel.addRow(new Object[]{
                        peminjaman.getIdPeminjaman(),
                        peminjaman.getAnggota().getNama(),
                        tglPinjam,
                        tglKembali,
                        peminjaman.getStatus()
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Error searching data:");
            e.printStackTrace();
        }
    }
} 
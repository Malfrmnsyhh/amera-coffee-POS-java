package UI;

import Database.UserDAO;
import Database.UserDAO.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KelolaPegawai extends JFrame {
    
    private JTable tabelPegawai;
    private DefaultTableModel model;
    private JTextField txNama, txUsername, txPassword;
    private JComboBox<String> cbRole;
    private JButton btnTambah, btnEdit, btnHapus, btnBatal;
    private UserDAO dao = new UserDAO();
    private int selectedUserId = 0;

    public KelolaPegawai() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("Kelola Pegawai");
        setResizable(false);
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Judul
        JLabel titleLabel = new JLabel("Manajemen Akun Pegawai (Kasir & Admin)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabel
        model = new DefaultTableModel(new String[]{"ID", "Nama Lengkap", "Username", "Password", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelPegawai = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabelPegawai);
        scrollPane.setPreferredSize(new Dimension(800, 250));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Form Bawah
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Data Pegawai"));

        formPanel.add(new JLabel("Nama Lengkap:"));
        txNama = new JTextField();
        formPanel.add(txNama);

        formPanel.add(new JLabel("Username:"));
        txUsername = new JTextField();
        formPanel.add(txUsername);

        formPanel.add(new JLabel("Password:"));
        txPassword = new JTextField();
        formPanel.add(txPassword);

        formPanel.add(new JLabel("Role:"));
        cbRole = new JComboBox<>(new String[]{"admin", "karyawan"});
        formPanel.add(cbRole);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnTambah = new JButton("Tambah");
        btnEdit = new JButton("Edit");
        btnHapus = new JButton("Hapus");
        btnBatal = new JButton("Batal");

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBatal);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        add(mainPanel);
        pack();

        // Event Listeners
        tabelPegawai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tabelPegawai.getSelectedRow();
                if (row >= 0) {
                    selectedUserId = (int) model.getValueAt(row, 0);
                    txNama.setText(model.getValueAt(row, 1).toString());
                    txUsername.setText(model.getValueAt(row, 2).toString());
                    txPassword.setText(model.getValueAt(row, 3).toString());
                    cbRole.setSelectedItem(model.getValueAt(row, 4).toString());
                }
            }
        });

        btnTambah.addActionListener(e -> tambahPegawai());
        btnEdit.addActionListener(e -> editPegawai());
        btnHapus.addActionListener(e -> hapusPegawai());
        btnBatal.addActionListener(e -> clearForm());
    }

    private void loadData() {
        model.setRowCount(0);
        List<User> users = dao.getAllUsers();
        for (User u : users) {
            model.addRow(new Object[]{u.id, u.namaLengkap, u.username, u.password, u.role});
        }
    }

    private void clearForm() {
        txNama.setText("");
        txUsername.setText("");
        txPassword.setText("");
        cbRole.setSelectedIndex(0);
        selectedUserId = 0;
        tabelPegawai.clearSelection();
    }

    private void tambahPegawai() {
        if (dao.tambahUser(txNama.getText(), txUsername.getText(), txPassword.getText(), cbRole.getSelectedItem().toString())) {
            JOptionPane.showMessageDialog(this, "Pegawai berhasil ditambahkan!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambah pegawai.");
        }
    }

    private void editPegawai() {
        if (selectedUserId == 0) return;
        if (dao.updateUser(selectedUserId, txNama.getText(), txUsername.getText(), txPassword.getText(), cbRole.getSelectedItem().toString())) {
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal update data.");
        }
    }

    private void hapusPegawai() {
        if (selectedUserId == 0) return;
        int conf = JOptionPane.showConfirmDialog(this, "Yakin hapus akun ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            if (dao.hapusUser(selectedUserId)) {
                JOptionPane.showMessageDialog(this, "Akun dihapus!");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal hapus akun.");
            }
        }
    }
}

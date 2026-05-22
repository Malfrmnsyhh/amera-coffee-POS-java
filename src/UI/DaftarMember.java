/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UI;

import Database.MemberDAO;
import Model.Member;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.util.List;

/**
 *
 * @author nitro5
 */
public class DaftarMember extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DaftarMember.class.getName());
    
    private DefaultTableModel model;
    private Member selectedMember;
    private Kasir parentKasir;

    /**
     * Creates new form DaftarMember
     */
    public DaftarMember() {
        initComponents();
        this.selectedMember = null;
        this.parentKasir = null;
        setupTable();
        loadMemberData();
        setupButtonActions();
    }
    
    /**
     * Constructor dengan parent Kasir
     */
    public DaftarMember(Kasir parent) {
        initComponents();
        this.selectedMember = null;
        this.parentKasir = parent;
        if (parentKasir != null) {
            setAlwaysOnTop(true);
            java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(parentKasir);
            if (window != null) {
                setLocationRelativeTo(window);
            }
        }
        setupTable();
        loadMemberData();
        setupButtonActions();
    }
    
    /**
     * Setup table model dengan kolom yang sesuai
     */
    private void setupTable() {
        model = new DefaultTableModel(
            new String[]{"ID", "Kode Member", "Nama", "No HP"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelDaftarMember.setModel(model);
        tabelDaftarMember.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabelDaftarMember.getColumnModel().getColumn(0).setPreferredWidth(30);
        tabelDaftarMember.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabelDaftarMember.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabelDaftarMember.getColumnModel().getColumn(3).setPreferredWidth(120);
    }
    
    /**
     * Load semua data member dari database
     */
    private void loadMemberData() {
        model.setRowCount(0);
        if (!Database.Koneksi.isConnected()) {
            JOptionPane.showMessageDialog(this,
                "Database tidak terhubung.\nCek MySQL (port 3308), database amera_coffee, user/password di Koneksi.java.",
                "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            MemberDAO dao = new MemberDAO();
            List<Member> members = dao.getAllMembers();
            
            if (members.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Tidak ada data member.\nJalankan INSERT di file amera_coffee.sql (MB001, MB002).",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (Member member : members) {
                Object[] row = {
                    member.getId(),
                    member.getKodeMember(),
                    member.getNama(),
                    member.getNoHp() != null ? member.getNoHp() : ""
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Setup button action listeners
     */
    private void setupButtonActions() {
        btnPilih.addActionListener(e -> pilihMember());
        btnBatal.addActionListener(e -> batalkan());
        tabelDaftarMember.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tabelDaftarMember.getSelectedRow() >= 0) {
                    pilihMember();
                }
            }
        });
    }
    
    /**
     * Action saat button Pilih diklik
     */
    private void pilihMember() {
        int viewRow = tabelDaftarMember.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Klik satu baris member di tabel, lalu tekan Pilih.", "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int modelRow = tabelDaftarMember.convertRowIndexToModel(viewRow);
            selectedMember = memberFromTableRow(modelRow);

            if (parentKasir != null) {
                parentKasir.setSelectedMember(selectedMember);
                java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(parentKasir);
                if (window != null) {
                    window.toFront();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Member dipilih: " + selectedMember.getNama()
                        + "\n(Buka form ini dari Kasir agar data terisi otomatis.)",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Member memberFromTableRow(int modelRow) {
        int id = toInt(model.getValueAt(modelRow, 0));
        String kode = String.valueOf(model.getValueAt(modelRow, 1));
        String nama = String.valueOf(model.getValueAt(modelRow, 2));
        String noHp = model.getValueAt(modelRow, 3) != null ? String.valueOf(model.getValueAt(modelRow, 3)) : "";
        return new Member(id, kode, nama, noHp);
    }

    private static int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
    
    /**
     * Action saat button Batal diklik
     */
    private void batalkan() {
        selectedMember = null;
        this.dispose();
    }
    
    /**
     * Get selected member
     */
    public Member getSelectedMember() {
        return selectedMember;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelDaftarMember = new javax.swing.JTable();
        btnPilih = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Daftar member");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(204, 204, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Daftar Member");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        tabelDaftarMember.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabelDaftarMember);

        btnPilih.setText("Pilih");

        btnBatal.setText("Batal");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnBatal)
                        .addGap(18, 18, 18)
                        .addComponent(btnPilih)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPilih)
                    .addComponent(btnBatal))
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new DaftarMember().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnPilih;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelDaftarMember;
    // End of variables declaration//GEN-END:variables
}

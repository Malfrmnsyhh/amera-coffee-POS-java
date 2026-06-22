package UI;

import Database.ProdukDAO;
import Model.Produk;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DaftarProduk extends javax.swing.JFrame {

  private static final java.util.logging.Logger logger = java.util.logging.Logger
      .getLogger(DaftarProduk.class.getName());

  private DefaultTableModel model;
  private Produk selectedProduk;
  private Kasir parentKasir;
  private int selectedProdukId = 0;

  public DaftarProduk() {
    initComponents();
    selectedProduk = null;
    parentKasir = null;
    initLogic();
  }

  public DaftarProduk(Kasir parent) {
    initComponents();
    selectedProduk = null;
    parentKasir = parent;
    if (parentKasir != null) {
      setAlwaysOnTop(true);
      java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(parentKasir);
      if (window != null) {
        setLocationRelativeTo(window);
      }
    }
    initLogic();
  }

  private void initLogic() {
    setupTable();
    loadProdukData();
    setupSearchField();

    // Aksi double click baris tabel atau klik tombol Pilih
    btnPilih.addActionListener(e -> pilihProduk());
    btnBatal.addActionListener(e -> clearForm());

    TabelDaftarProduk.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
        int viewRow = TabelDaftarProduk.getSelectedRow();
        if (viewRow >= 0) {
          int modelRow = TabelDaftarProduk.convertRowIndexToModel(viewRow);
          selectedProdukId = toInt(model.getValueAt(modelRow, 0));
          txKodeProduk.setText(String.valueOf(model.getValueAt(modelRow, 1)));
          txNamaProduk.setText(String.valueOf(model.getValueAt(modelRow, 2)));
          txHarga.setText(String.valueOf(model.getValueAt(modelRow, 3)));
          txStok.setText(String.valueOf(model.getValueAt(modelRow, 4)));
          if (e.getClickCount() == 2 && parentKasir != null) {
            pilihProduk();
          }
        }
      }
    });
    // Menghubungkan tombol CRUD
    btnTambah.addActionListener(e -> tambahProduk());
    btnEdit.addActionListener(e -> editProduk());
    btnHapus.addActionListener(e -> hapusProduk());
  }

  private void setupSearchField() {
    jPanel1.setLayout(new java.awt.BorderLayout(10, 10));
    jPanel1.add(jLabel1, java.awt.BorderLayout.WEST);

    javax.swing.JPanel searchPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
    searchPanel.setOpaque(false);
    javax.swing.JLabel lblSearch = new javax.swing.JLabel("Cari Produk: ");
    lblSearch.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
    searchPanel.add(lblSearch);

    javax.swing.JTextField txSearch = new javax.swing.JTextField(15);
    searchPanel.add(txSearch);
    jPanel1.add(searchPanel, java.awt.BorderLayout.EAST);

    // Setup sorter untuk filter real-time
    javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter =
        new javax.swing.table.TableRowSorter<>(model);
    TabelDaftarProduk.setRowSorter(sorter);

    txSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }

      private void filter() {
        String text = txSearch.getText().trim();
        if (text.isEmpty()) {
          sorter.setRowFilter(null);
        } else {
          // Filter berdasarkan Kode Produk (kolom 1) atau Nama Produk (kolom 2)
          sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text, 1, 2));
        }
      }
    });
  }

  private void setupTable() {
    model = new DefaultTableModel(
        new String[] { "ID", "Kode Produk", "Nama Produk", "Harga", "Stok" },
        0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    TabelDaftarProduk.setModel(model);
    TabelDaftarProduk.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
  }

  private void loadProdukData() {
    model.setRowCount(0);
    if (!Database.Koneksi.isConnected()) {
      JOptionPane.showMessageDialog(this,
          "Database tidak terhubung.\nCek MySQL (port 3308), database amera_coffee, user/password di Koneksi.java.",
          "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
      return;
    }
    try {
      ProdukDAO dao = new ProdukDAO();
      List<Produk> produkList = dao.getAllProduk();

      if (produkList.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Tidak ada data produk.\nJalankan INSERT di file amera_coffee.sql (PR001, PR002, ...).",
            "Info", JOptionPane.INFORMATION_MESSAGE);
        return;
      }

      for (Produk produk : produkList) {
        model.addRow(new Object[] {
            produk.getId(),
            produk.getKodeProduk(),
            produk.getNamaProduk(),
            produk.getHarga(),
            produk.getStok()
        });
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }

  private void pilihProduk() {
    int viewRow = TabelDaftarProduk.getSelectedRow();
    if (viewRow < 0) {
      JOptionPane.showMessageDialog(this, "Klik satu baris produk di tabel, lalu tekan Pilih.", "Validasi",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    try {
      int modelRow = TabelDaftarProduk.convertRowIndexToModel(viewRow);
      selectedProduk = produkFromTableRow(modelRow);

      if (parentKasir != null) {
        parentKasir.setSelectedProduk(selectedProduk);
        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(parentKasir);
        if (window != null) {
          window.toFront();
        }
      } else {
        JOptionPane.showMessageDialog(this,
            "Produk dipilih: " + selectedProduk.getNamaProduk()
                + "\n(Buka form ini dari Kasir agar data terisi otomatis.)",
            "Info", JOptionPane.INFORMATION_MESSAGE);
      }
      dispose();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }

  private Produk produkFromTableRow(int modelRow) {
    int id = toInt(model.getValueAt(modelRow, 0));
    String kode = String.valueOf(model.getValueAt(modelRow, 1));
    String nama = String.valueOf(model.getValueAt(modelRow, 2));
    int harga = toInt(model.getValueAt(modelRow, 3));
    int stok = toInt(model.getValueAt(modelRow, 4));
    return new Produk(id, kode, nama, harga, stok);
  }

  private static int toInt(Object value) {
    if (value instanceof Number) {
      return ((Number) value).intValue();
    }
    return Integer.parseInt(String.valueOf(value));
  }

  private void batalkan() {
    selectedProduk = null;
    dispose();
  }

  public Produk getSelectedProduk() {
    return selectedProduk;
  }

  private void clearForm() {
    txKodeProduk.setText("");
    txNamaProduk.setText("");
    txHarga.setText("");
    txStok.setText("");
    selectedProdukId = 0;
    TabelDaftarProduk.clearSelection();
  }

  private boolean validateInput() {
    if (txKodeProduk.getText().trim().isEmpty() ||
        txNamaProduk.getText().trim().isEmpty() ||
        txHarga.getText().trim().isEmpty() ||
        txStok.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Semua field input harus diisi!", "Validasi Gagal",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    try {
      Integer.parseInt(txHarga.getText().trim());
      Integer.parseInt(txStok.getText().trim());
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka!", "Validasi Gagal",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }

  private void tambahProduk() {
    if (!validateInput())
      return;

    String kode = txKodeProduk.getText().trim();
    String nama = txNamaProduk.getText().trim();
    int harga = Integer.parseInt(txHarga.getText().trim());
    int stok = Integer.parseInt(txStok.getText().trim());

    ProdukDAO dao = new ProdukDAO();
    if (dao.tambahProduk(kode, nama, harga, stok)) {
      JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!");
      loadProdukData();
      clearForm();
    } else {
      JOptionPane.showMessageDialog(this, "Gagal menambah produk. Pastikan Kode Produk tidak duplikat.", "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void editProduk() {
    if (selectedProdukId == 0) {
      JOptionPane.showMessageDialog(this, "Pilih produk di tabel terlebih dahulu!", "Peringatan",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    if (!validateInput())
      return;

    String kode = txKodeProduk.getText().trim();
    String nama = txNamaProduk.getText().trim();
    int harga = Integer.parseInt(txHarga.getText().trim());
    int stok = Integer.parseInt(txStok.getText().trim());

    ProdukDAO dao = new ProdukDAO();
    if (dao.updateProduk(selectedProdukId, kode, nama, harga, stok)) {
      JOptionPane.showMessageDialog(this, "Produk berhasil diperbarui!");
      loadProdukData();
      clearForm();
    } else {
      JOptionPane.showMessageDialog(this, "Gagal memperbarui produk.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void hapusProduk() {
    if (selectedProdukId == 0) {
      JOptionPane.showMessageDialog(this, "Pilih produk di tabel terlebih dahulu!", "Peringatan",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus produk ini?", "Konfirmasi Hapus",
        JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
      ProdukDAO dao = new ProdukDAO();
      if (dao.hapusProduk(selectedProdukId)) {
        JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!");
        loadProdukData();
        clearForm();
      } else {
        JOptionPane.showMessageDialog(this, "Gagal menghapus produk. Produk mungkin telah digunakan dalam transaksi.",
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabelDaftarProduk = new javax.swing.JTable();
        btnPilih = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txKodeProduk = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txNamaProduk = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txHarga = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txStok = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Daftar Produk");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(153, 153, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Daftar Produk");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        TabelDaftarProduk.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(TabelDaftarProduk);

        btnPilih.setText("Pilih");

        btnBatal.setText("Batal");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Tambah Produk dan Perbarui Stok");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Kode Produk");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Nama Produk");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Harga");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Stok");

        btnTambah.setText("Tambah");

        btnEdit.setText("Edit");

        btnHapus.setText("Hapus");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txKodeProduk, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                                    .addComponent(txNamaProduk)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5))
                                .addGap(43, 43, 43)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txStok)
                                    .addComponent(txHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnPilih)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txKodeProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txNamaProduk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnPilih, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  public static void main(String args[]) {
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

    java.awt.EventQueue.invokeLater(() -> new DaftarProduk().setVisible(true));
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TabelDaftarProduk;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnPilih;
    private javax.swing.JButton btnTambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txHarga;
    private javax.swing.JTextField txKodeProduk;
    private javax.swing.JTextField txNamaProduk;
    private javax.swing.JTextField txStok;
    // End of variables declaration//GEN-END:variables
}

package UI;

import Database.MemberDAO;
import Database.ProdukDAO;
import Model.DetailTransaksi;
import Model.Member;
import Model.Produk;
import Model.transaksi;
import Utils.Session;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Kasir extends javax.swing.JPanel {

  private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Kasir.class.getName());

  // Model logic instance
  private DetailTransaksi detailTransaksi;
  private DefaultTableModel model;
  private ProdukDAO produkDAO;
  private MemberDAO memberDAO;
  private Produk produkTerpilih;
  private int selectedMemberId = 0;
  private int kasirId = 0;
  private String namaKasir = "Kasir";
  /** True setelah btnBayar berhasil dan uang bayar >= total. */
  private boolean pembayaranSudahValid = false;

  public Kasir() {
    initComponents();
    detailTransaksi = new DetailTransaksi();
    produkDAO = new ProdukDAO();
    memberDAO = new MemberDAO();
    jButton1.addActionListener(this::btnCariMemberActionPerformed);
    btnBayar.addActionListener(this::btnBayarActionPerformed);
    loadDataKasirLogin();
    setupFieldPembayaran();
    setupTableModel();
    loadNoTransaksi();
    setTanggal();
  }

  /** Ambil ID kasir dari user yang login (tabel users). */
  private void loadDataKasirLogin() {
    if (Session.isLoggedIn()) {
      kasirId = Session.getUserId();
      namaKasir = Session.getUserName();
    } else {
      kasirId = 0;
      namaKasir = "Kasir (belum login)";
    }
  }

  private void setupFieldPembayaran() {
    jTextField1.setEditable(false);
    txTotalBayar.setEditable(false);
    txKembalian.setEditable(false);
    txKembalian.setText("Rp 0");
  }

  /**
   * Setup table model untuk tabel item pesanan
   */
  private void setupTableModel() {
    model = new DefaultTableModel(
        new String[] { "ID Menu", "Nama Menu", "Harga", "Jumlah", "Subtotal" },
        0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false; // Read-only
      }
    };
    jTable1.setModel(model);
  }

  /**
   * Load No Transaksi otomatis saat form dibuka
   */
  private void loadNoTransaksi() {
    String noTransaksi = detailTransaksi.generateNoTransaksi();
    txNoTransaksi.setText(noTransaksi);
    detailTransaksi.getTransaksiAktif().setNoTransaksi(noTransaksi);
  }

  /**
   * Set tanggal hari ini
   */
  private void setTanggal() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    txTanggal.setText(sdf.format(new Date()));
    txTanggal.setEditable(false);
  }

  /**
   * Refresh table dengan data dari detailTransaksi
   */
  private void refreshTableItem() {
    model.setRowCount(0); // Clear table
    int totalBayar = 0;

    for (DetailTransaksi.DetailItem item : detailTransaksi.getItemList()) {
      Object[] row = {
          item.getIdMenu(),
          item.getNamaMenu(),
          "Rp " + item.getHargaSatuan(),
          item.getJumlah(),
          "Rp " + item.getSubtotal()
      };
      model.addRow(row);
      totalBayar += item.getSubtotal();
    }

    updateTampilanTotal(totalBayar);
    resetStatusPembayaran();
  }

  /** Sinkronkan label subtotal & total, kosongkan bayar/kembalian setelah keranjang berubah. */
  private void updateTampilanTotal(int total) {
    String formatted = formatRupiah(total);
    jTextField1.setText("Rp " + formatted);
    txTotalBayar.setText("Rp " + formatted);
  }

  private void resetStatusPembayaran() {
    pembayaranSudahValid = false;
    txBayar.setText("");
    txKembalian.setText("Rp 0");
    txKembalian.setForeground(java.awt.Color.BLACK);
  }

  private static String formatRupiah(int angka) {
    return String.format("%,d", angka).replace(',', '.');
  }

  private int parseAngka(javax.swing.JTextField field) {
    String text = field.getText().replaceAll("[^0-9]", "");
    if (text.isEmpty()) {
      return -1;
    }
    return Integer.parseInt(text);
  }

  /**
   * Hitung kembalian: total dari item, bayar dari field txBayar.
   * Dipanggil dari btnBayar dan Enter di field bayar.
   */
  private void hitungDanTampilkanKembalian() {
    if (detailTransaksi.getItemList().isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Tambahkan item pesanan terlebih dahulu sebelum menghitung pembayaran!",
          "Validasi", JOptionPane.WARNING_MESSAGE);
      return;
    }

    int total = detailTransaksi.hitungTotal();
    updateTampilanTotal(total);

    int bayar = parseAngka(txBayar);
    if (bayar < 0) {
      JOptionPane.showMessageDialog(this,
          "Masukkan jumlah uang bayar (angka saja, tanpa \"Rp\").",
          "Validasi", JOptionPane.WARNING_MESSAGE);
      txBayar.requestFocus();
      pembayaranSudahValid = false;
      return;
    }

    int kembalian = detailTransaksi.hitungKembalian(total, bayar);
    pembayaranSudahValid = kembalian >= 0;

    if (kembalian < 0) {
      int kurang = Math.abs(kembalian);
      txKembalian.setText("Kurang Rp " + formatRupiah(kurang));
      txKembalian.setForeground(new java.awt.Color(255, 0, 0));
      JOptionPane.showMessageDialog(this,
          "Uang bayar kurang Rp " + formatRupiah(kurang) + "!\nTotal: Rp " + formatRupiah(total),
          "Pembayaran Kurang", JOptionPane.WARNING_MESSAGE);
    } else {
      txKembalian.setText("Rp " + formatRupiah(kembalian));
      txKembalian.setForeground(new java.awt.Color(0, 128, 0));
    }
  }

  private void clearMenuInput() {
    txIDMenu.setText("");
    txNamaMenu.setText("");
    txHargaSatuan.setText("");
    txJumlah.setText("");
    produkTerpilih = null;
  }

  private void resetForm() {
    clearMenuInput();
    txIDMember.setText("");
    txNamaCustomer.setText("");
    selectedMemberId = 0;
    resetStatusPembayaran();
    model.setRowCount(0);
    CbNomorMeja.setSelectedIndex(0);

    // Reset model
    detailTransaksi = new DetailTransaksi();
    loadNoTransaksi();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txNoTransaksi = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txIDMember = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txNamaCustomer = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txTanggal = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        CbNomorMeja = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txIDMenu = new javax.swing.JTextField();
        btnCariMenu = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txNamaMenu = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txJumlah = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txHargaSatuan = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        btnHapusItem = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txTotalBayar = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txBayar = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txKembalian = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JButton();
        btnCetakStruck = new javax.swing.JButton();
        btnBayar = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("No Transaksi");

        txNoTransaksi.setEditable(false);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("ID Member");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Nama Customer");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Tanggal");

        txTanggal.setEditable(false);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Nomor Meja");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        CbNomorMeja.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih Meja--", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" }));

        jButton1.setText("Cari");

        jLabel6.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel6.setText("Detail Pesanan");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("ID Menu");

        txIDMenu.setEditable(false);
        txIDMenu.addActionListener(this::txIDMenuActionPerformed);

        btnCariMenu.setText("Cari");
        btnCariMenu.addActionListener(this::btnCariMenuActionPerformed);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Nama Menu");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Harga Satuan");

        txJumlah.addActionListener(this::txJumlahActionPerformed);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Jumlah");

        txHargaSatuan.addActionListener(this::txHargaSatuanActionPerformed);

        btnTambah.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTambah.setText("Tambah");
        btnTambah.addActionListener(this::btnTambahActionPerformed);

        btnHapusItem.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnHapusItem.setText("Hapus Item");
        btnHapusItem.addActionListener(this::btnHapusItemActionPerformed);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Subtotal");

        jTextField1.setText("Rp.0");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Total Bayar");

        txTotalBayar.setEditable(false);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Bayar");

        txBayar.addActionListener(this::txBayarActionPerformed);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Kembalian");

        txKembalian.setEditable(false);

        btnSimpan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSimpan.setText("Simpan");
        btnSimpan.addActionListener(this::btnSimpanActionPerformed);

        btnCetakStruck.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCetakStruck.setText("Cetak Struck");
        btnCetakStruck.addActionListener(this::btnCetakStruckActionPerformed);

        btnBayar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnBayar.setText("Bayar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addGap(18, 18, 18)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(29, 29, 29)
                            .addComponent(btnBayar)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnHapusItem)
                            .addGap(18, 18, 18)
                            .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(13, 13, 13))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel7)
                                    .addGap(28, 28, 28)
                                    .addComponent(txIDMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnCariMenu)
                                    .addGap(19, 19, 19)
                                    .addComponent(jLabel8)
                                    .addGap(9, 9, 9)
                                    .addComponent(txNamaMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel9)
                                    .addGap(9, 9, 9)
                                    .addComponent(txHargaSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(10, 10, 10)
                                    .addComponent(jLabel10)
                                    .addGap(13, 13, 13)
                                    .addComponent(txJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(49, 49, 49)
                                .addComponent(txNoTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(jLabel2)
                                .addGap(28, 28, 28)
                                .addComponent(txIDMember, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jButton1)
                                .addGap(19, 19, 19)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(txNamaCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(CbNomorMeja, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txTotalBayar)
                            .addComponent(txBayar)
                            .addComponent(txKembalian, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(btnCetakStruck)))
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(txIDMember, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(txTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txNoTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txNamaCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(CbNomorMeja, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21)
                .addComponent(jLabel6)
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(txNamaMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txHargaSatuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txIDMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7))
                            .addComponent(btnCariMenu))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBayar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnHapusItem, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCetakStruck, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39))
        );

    }// </editor-fold>//GEN-END:initComponents

  private void txIDMenuActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txIDMenuActionPerformed
    String kode = txIDMenu.getText().trim();
    if (!kode.isEmpty()) {
      cariProdukDariKode(kode);
    }
  }// GEN-LAST:event_txIDMenuActionPerformed

  private void txHargaSatuanActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txHargaSatuanActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_txHargaSatuanActionPerformed

  private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnTambahActionPerformed
    try {
      String idMenu = txIDMenu.getText().trim();
      String namaMenu = txNamaMenu.getText().trim();

      if (idMenu.isEmpty() || namaMenu.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Silakan cari menu terlebih dahulu!", "Validasi",
            JOptionPane.WARNING_MESSAGE);
        return;
      }

      int hargaSatuan = Integer.parseInt(txHargaSatuan.getText().isEmpty() ? "0" : txHargaSatuan.getText());
      int jumlah = Integer.parseInt(txJumlah.getText().isEmpty() ? "1" : txJumlah.getText());

      if (hargaSatuan <= 0 || jumlah <= 0) {
        JOptionPane.showMessageDialog(this, "Harga dan jumlah harus lebih dari 0!", "Validasi",
            JOptionPane.WARNING_MESSAGE);
        return;
      }

      // Tambah item ke model
      detailTransaksi.tambahItem(idMenu, namaMenu, hargaSatuan, jumlah);

      // Refresh tabel
      refreshTableItem();

      // Clear input
      clearMenuInput();
      txIDMenu.requestFocus();
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "Format input tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }// GEN-LAST:event_btnTambahActionPerformed

  private void btnHapusItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnHapusItemActionPerformed
    int selectedRow = jTable1.getSelectedRow();
    if (selectedRow < 0) {
      JOptionPane.showMessageDialog(this, "Pilih item yang akan dihapus!", "Validasi", JOptionPane.WARNING_MESSAGE);
      return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Hapus item ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
      detailTransaksi.hapusItem(selectedRow);
      refreshTableItem();
    }
  }// GEN-LAST:event_btnHapusItemActionPerformed

  private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {
    hitungDanTampilkanKembalian();
  }

  private void txBayarActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txBayarActionPerformed
    hitungDanTampilkanKembalian();
  }// GEN-LAST:event_txBayarActionPerformed

  private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSimpanActionPerformed
    try {
      // Validasi
      if (detailTransaksi.getItemList().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Tambahkan item terlebih dahulu!", "Validasi", JOptionPane.WARNING_MESSAGE);
        return;
      }

      String namaCustomer = txNamaCustomer.getText().trim();
      if (namaCustomer.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama customer harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
        return;
      }

      if (CbNomorMeja.getSelectedIndex() == 0) {
        JOptionPane.showMessageDialog(this, "Pilih nomor meja!", "Validasi", JOptionPane.WARNING_MESSAGE);
        return;
      }

      int total = detailTransaksi.hitungTotal();
      int bayar = parseAngka(txBayar);
      if (bayar < 0) {
        JOptionPane.showMessageDialog(this, "Masukkan jumlah bayar lalu klik tombol Bayar!",
            "Validasi", JOptionPane.WARNING_MESSAGE);
        txBayar.requestFocus();
        return;
      }

      if (detailTransaksi.hitungKembalian(total, bayar) < 0) {
        JOptionPane.showMessageDialog(this,
            "Uang bayar belum mencukupi!\nKlik tombol Bayar untuk menghitung kembalian.",
            "Validasi", JOptionPane.WARNING_MESSAGE);
        hitungDanTampilkanKembalian();
        return;
      }

      if (!pembayaranSudahValid) {
        hitungDanTampilkanKembalian();
        if (!pembayaranSudahValid) {
          JOptionPane.showMessageDialog(this,
              "Klik tombol Bayar terlebih dahulu untuk konfirmasi pembayaran.",
              "Validasi", JOptionPane.WARNING_MESSAGE);
          return;
        }
      }

      // Get data
      String noTransaksi = txNoTransaksi.getText();
      int memberId = selectedMemberId; // 0 = walk-in (tanpa member)

      int nomorMeja = Integer.parseInt(CbNomorMeja.getSelectedItem().toString());
      LocalDate tanggal = LocalDate.now();

      // Simpan transaksi
      boolean success = detailTransaksi.simpanTransaksi(
          noTransaksi, kasirId, namaKasir, memberId, namaCustomer,
          nomorMeja, tanggal, total, bayar);

      if (success) {
        JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        resetForm();
      } else {
        String pesan = detailTransaksi.getLastError();
        if (pesan == null || pesan.isEmpty()) {
          pesan = "Gagal menyimpan transaksi.";
        }
        JOptionPane.showMessageDialog(this, pesan, "Error", JOptionPane.ERROR_MESSAGE);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }// GEN-LAST:event_btnSimpanActionPerformed

  private void btnCetakStruckActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCetakStruckActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_btnCetakStruckActionPerformed

  private void txJumlahActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txJumlahActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_txJumlahActionPerformed

  /**
   * Cari produk by kode (field ID Menu). Return true jika ketemu.
   */
  private boolean cariProdukDariKode(String kode) {
    if (kode == null || kode.trim().isEmpty()) {
      return false;
    }
    if (!Database.Koneksi.isConnected()) {
      JOptionPane.showMessageDialog(this,
          "Database tidak terhubung. Cek MySQL dan file Koneksi.java.", "Koneksi Gagal",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
    Produk produk = produkDAO.cariByKode(kode.trim());
    if (produk != null) {
      setSelectedProduk(produk);
      return true;
    }
    return false;
  }

  private void btnCariMenuActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCariMenuActionPerformed
    String kode = txIDMenu.getText().trim();
    if (cariProdukDariKode(kode)) {
      return;
    }
    if (!kode.isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Kode \"" + kode + "\" tidak ditemukan.\nBuka daftar produk untuk memilih.", "Tidak Ditemukan",
          JOptionPane.WARNING_MESSAGE);
    }
    DaftarProduk daftarProduk = new DaftarProduk(this);
    java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
    if (window != null) {
      daftarProduk.setLocationRelativeTo(window);
    }
    daftarProduk.setVisible(true);
  }// GEN-LAST:event_btnCariMenuActionPerformed

  private void btnCariMemberActionPerformed(java.awt.event.ActionEvent evt) {
    String kode = txIDMember.getText().trim();
    if (!kode.isEmpty() && Database.Koneksi.isConnected()) {
      Member member = memberDAO.cariByKode(kode);
      if (member != null) {
        setSelectedMember(member);
        return;
      }
    }
    DaftarMember daftarMember = new DaftarMember(this);
    java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
    if (window != null) {
      daftarMember.setLocationRelativeTo(window);
    }
    daftarMember.setVisible(true);
  }

  /**
   * Dipanggil dari DaftarMember saat member dipilih.
   */
  public void setSelectedMember(Member member) {
    if (member != null) {
      selectedMemberId = member.getId();
      txIDMember.setText(member.getKodeMember());
      txNamaCustomer.setText(member.getNama());
      txIDMember.repaint();
      txNamaCustomer.repaint();
    }
  }

  /**
   * Dipanggil dari DaftarProduk saat produk dipilih.
   */
  public void setSelectedProduk(Produk produk) {
    if (produk != null) {
      produkTerpilih = produk;
      txIDMenu.setText(produk.getKodeProduk());
      txNamaMenu.setText(produk.getNamaProduk());
      txHargaSatuan.setText(String.valueOf(produk.getHarga()));
      txIDMenu.repaint();
      txNamaMenu.repaint();
      txHargaSatuan.repaint();
      if (txJumlah.getText().trim().isEmpty()) {
        txJumlah.setText("1");
      }
      txJumlah.requestFocus();
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
    // (optional) ">
    /*
     * If Nimbus (introduced in Java SE 6) is not available, stay with the default
     * look and feel.
     * For details see
     * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
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
    // </editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(() -> {
      JFrame frame = new JFrame("Kasir - Amera Coffee");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(new Kasir());
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CbNomorMeja;
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnCariMenu;
    private javax.swing.JButton btnCetakStruck;
    private javax.swing.JButton btnHapusItem;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField txBayar;
    private javax.swing.JTextField txHargaSatuan;
    private javax.swing.JTextField txIDMember;
    private javax.swing.JTextField txIDMenu;
    private javax.swing.JTextField txJumlah;
    private javax.swing.JTextField txKembalian;
    private javax.swing.JTextField txNamaCustomer;
    private javax.swing.JTextField txNamaMenu;
    private javax.swing.JTextField txNoTransaksi;
    private javax.swing.JTextField txTanggal;
    private javax.swing.JTextField txTotalBayar;
    // End of variables declaration//GEN-END:variables
}

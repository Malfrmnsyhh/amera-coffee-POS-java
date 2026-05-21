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
    btnPilih.addActionListener(e -> pilihProduk());
    btnBatal.addActionListener(e -> batalkan());
    TabelDaftarProduk.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2 && TabelDaftarProduk.getSelectedRow() >= 0) {
          pilihProduk();
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

  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    TabelDaftarProduk = new javax.swing.JTable();
    btnPilih = new javax.swing.JButton();
    btnBatal = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addContainerGap(15, Short.MAX_VALUE)));

    TabelDaftarProduk.setModel(new javax.swing.table.DefaultTableModel(
        new Object[][] {
            { null, null, null, null },
            { null, null, null, null },
            { null, null, null, null },
            { null, null, null, null }
        },
        new String[] {
            "Title 1", "Title 2", "Title 3", "Title 4"
        }));
    jScrollPane1.setViewportView(TabelDaftarProduk);

    btnPilih.setText("Pilih");

    btnBatal.setText("Batal");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING,
                        javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnBatal)
                        .addGap(18, 18, 18)
                        .addComponent(btnPilih)))
                .addContainerGap()));
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 388,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPilih)
                    .addComponent(btnBatal))
                .addGap(0, 12, Short.MAX_VALUE)));

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
  private javax.swing.JButton btnPilih;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  // End of variables declaration//GEN-END:variables
}

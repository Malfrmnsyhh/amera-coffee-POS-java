package UI;

import Model.DetailTransaksi;
import java.awt.Font;
import java.awt.print.*;
import java.util.List;
import javax.swing.*;

public class StrukDialog extends javax.swing.JDialog {

  private JTextArea txaStruk;

  private String strukText;

  public StrukDialog(java.awt.Frame perent,
      String noTransaksi,
      String namaKasir,
      String namaCustomer,
      String nomorMeja,
      String tanggal,
      List<DetailTransaksi.DetailItem> itemList,
      String total,
      String bayar,
      String kembalian) {
    super(perent, "Struk Pembayaran", true);

    this.strukText = buildStrukText(
        noTransaksi, namaKasir, namaCustomer,
        nomorMeja, tanggal, itemList,
        total, bayar, kembalian);

    initComponents();
    setLocationRelativeTo(perent);
  }

  private String buildStrukText(String noTransaksi, String namaKasir,
      String namaCustomer, String nomorMeja,
      String tanggal,
      List<DetailTransaksi.DetailItem> itemList,
      String total, String bayar, String kembalian) {
    StringBuilder sb = new StringBuilder();
    String garis = "================================";
    sb.append(garis).append("\n");
    sb.append("         AMERA COFFEE           \n");
    sb.append("    Coffee shop pilihan anda!    \n");
    sb.append(garis).append("\n");
    sb.append("No Transaksi : ").append(noTransaksi).append("\n");
    sb.append("Kasir        : ").append(namaKasir).append("\n");
    sb.append("Customer     : ").append(namaCustomer).append("\n");
    sb.append("Meja         : ").append(nomorMeja).append("\n");
    sb.append("Tanggal      : ").append(tanggal).append("\n");
    sb.append(garis).append("\n");
    sb.append("PESANAN:\n");
    sb.append(garis).append("\n");
    
    for (DetailTransaksi.DetailItem item : itemList) {
      String nama = item.getNamaMenu();
      String jumlah = "x" + item.getJumlah();
      String subtotal = "Rp " + String.format("%,d", item.getSubtotal()).replace(',', '.');
      sb.append(String.format("%-18s %3s %10s\n", nama, jumlah, subtotal));
    }
    sb.append(garis).append("\n");
    sb.append(String.format("%-14s %16s\n", "Total", total));
    sb.append(String.format("%-14s %16s\n", "Bayar", bayar));
    sb.append(String.format("%-14s %16s\n", "Kembalian", kembalian));
    sb.append(garis).append("\n");
    sb.append("       Terima Kasih!            \n");
    sb.append("  Selamat menikmati Pesanan Anda!  \n");
    sb.append(garis).append("\n");
    return sb.toString();
  }

  private void initComponents() {
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setResizable(false);
    
    txaStruk = new JTextArea(strukText);
    txaStruk.setEditable(false); 
    txaStruk.setFont(new Font("Courier New", Font.PLAIN, 13));
    txaStruk.setMargin(new java.awt.Insets(10, 10, 10, 10));
    
    JScrollPane scrollPane = new JScrollPane(txaStruk);
    scrollPane.setPreferredSize(new java.awt.Dimension(390, 420));
    
    JButton btnPrint = new JButton("Print Struk");
    btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btnPrint.setBackground(new java.awt.Color(51, 51, 51));
    btnPrint.setForeground(java.awt.Color.WHITE);
    btnPrint.setFocusPainted(false);
    btnPrint.addActionListener(e -> cetakStruk());
    
    JButton btnTutup = new JButton("Tutup");
    btnTutup.setFont(new Font("Segoe UI", Font.BOLD, 13));
    btnTutup.addActionListener(e -> dispose());
    
    JPanel panelTombol = new JPanel();
    panelTombol.add(btnPrint);
    panelTombol.add(btnTutup);
    
    getContentPane().setLayout(new java.awt.BorderLayout(10, 10));
    getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
    getContentPane().add(panelTombol, java.awt.BorderLayout.SOUTH);
    pack();
  }

  private void cetakStruk() {
    PrinterJob printerJob = PrinterJob.getPrinterJob();
    
    printerJob.setPrintable((graphics, pageFormat, pageIndex) -> {
      if (pageIndex > 0)
        return Printable.NO_SUCH_PAGE;
      graphics.setFont(new Font("Courier New", Font.PLAIN, 11));
      java.awt.FontMetrics fm = graphics.getFontMetrics();
      
      double x = pageFormat.getImageableX() + 5;
      double y = pageFormat.getImageableY() + fm.getHeight();
      
      for (String baris : strukText.split("\n")) {
        graphics.drawString(baris, (int) x, (int) y);
        y += fm.getHeight(); 
      }
      return Printable.PAGE_EXISTS;
    });
    
    if (printerJob.printDialog()) {
      try {
        printerJob.print();
        JOptionPane.showMessageDialog(this,
            "Struk berhasil dicetak!",
            "Sukses", JOptionPane.INFORMATION_MESSAGE);
      } catch (PrinterException e) {
        JOptionPane.showMessageDialog(this,
            "Gagal mencetak: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}

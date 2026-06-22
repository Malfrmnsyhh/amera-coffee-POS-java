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

  private String centerText(String text, int width) {
    if (text == null) return "";
    if (text.length() >= width) {
      return text.substring(0, width);
    }
    int padding = (width - text.length()) / 2;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < padding; i++) {
      sb.append(" ");
    }
    sb.append(text);
    return sb.toString();
  }

  private String buildStrukText(String noTransaksi, String namaKasir,
      String namaCustomer, String nomorMeja,
      String tanggal,
      List<DetailTransaksi.DetailItem> itemList,
      String total, String bayar, String kembalian) {
    StringBuilder sb = new StringBuilder();
    String garis = "================================";
    sb.append(garis).append("\n");
    sb.append(centerText("AMERA COFFEE", 32)).append("\n");
    sb.append(centerText("Coffee shop pilihan anda!", 32)).append("\n");
    sb.append(garis).append("\n");
    sb.append(String.format("%-14s : %s\n", "No Transaksi", noTransaksi));
    sb.append(String.format("%-14s : %s\n", "Kasir", namaKasir));
    sb.append(String.format("%-14s : %s\n", "Customer", namaCustomer));
    sb.append(String.format("%-14s : %s\n", "Meja", nomorMeja));
    sb.append(String.format("%-14s : %s\n", "Tanggal", tanggal));
    sb.append(garis).append("\n");
    sb.append(centerText("PESANAN", 32)).append("\n");
    sb.append(garis).append("\n");
    
    for (DetailTransaksi.DetailItem item : itemList) {
      String nama = item.getNamaMenu();
      if (nama.length() > 18) {
        nama = nama.substring(0, 15) + "...";
      }
      String jumlah = "x" + item.getJumlah();
      String subtotal = "Rp " + String.format("%,d", item.getSubtotal()).replace(',', '.');
      sb.append(String.format("%-18s %3s %9s\n", nama, jumlah, subtotal));
    }
    sb.append(garis).append("\n");
    sb.append(String.format("%-14s %17s\n", "Total", total));
    sb.append(String.format("%-14s %17s\n", "Bayar", bayar));
    sb.append(String.format("%-14s %17s\n", "Kembalian", kembalian));
    sb.append(garis).append("\n");
    sb.append(centerText("Terima Kasih!", 32)).append("\n");
    sb.append(centerText("Selamat menikmati Pesanan Anda!", 32)).append("\n");
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

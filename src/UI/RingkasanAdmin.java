package UI;

import Database.RingkasanDAO;
import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class RingkasanAdmin extends JFrame {

  private RingkasanDAO dao = new RingkasanDAO();
  private JLabel pendapatanValueLabel;
  private JLabel transaksiValueLabel;
  private JComboBox<String> pendapatanFilterCombo;
  private JComboBox<String> transaksiFilterCombo;
  private NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

  public RingkasanAdmin() {
    initComponents();
  }

  private void initComponents() {
    setTitle("Ringkasan Admin");
    setResizable(false);
    getContentPane().setBackground(new Color(245, 245, 245));

    JPanel mainPanel = new JPanel(new GridLayout(2, 2, 20, 20));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

    String[] filterOptions = { "Hari Ini", "Semua" };
    pendapatanFilterCombo = new JComboBox<>(filterOptions);
    transaksiFilterCombo = new JComboBox<>(filterOptions);
    pendapatanFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    transaksiFilterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    pendapatanFilterCombo.addActionListener(e -> refreshPendapatan());
    transaksiFilterCombo.addActionListener(e -> refreshTransaksi());

    int pendapatan = dao.getTotalPendapatan("Hari Ini");
    int totalTrx = dao.getTotalTransaksi("Hari Ini");
    int totalPegawai = dao.getTotalPegawai();
    int totalProduk = dao.getTotalProduk();

    mainPanel
        .add(createCard("Pendapatan", formatRupiah.format(pendapatan), new Color(153, 153, 0), pendapatanFilterCombo));
    mainPanel
        .add(createCard("Total Transaksi", String.valueOf(totalTrx), new Color(0, 102, 204), transaksiFilterCombo));
    mainPanel.add(createCard("Total Akun Pegawai", String.valueOf(totalPegawai), new Color(204, 102, 0), null));
    mainPanel.add(createCard("Jumlah Jenis Produk", String.valueOf(totalProduk), new Color(0, 153, 51), null));

    JLabel title = new JLabel("Ringkasan Bisnis - Amera Coffee", SwingConstants.CENTER);
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

    add(title, BorderLayout.NORTH);
    add(mainPanel, BorderLayout.CENTER);
    setPreferredSize(new Dimension(800, 500));
    pack();
  }

  private void refreshPendapatan() {
    String filter = pendapatanFilterCombo.getSelectedItem().toString();
    int pendapatan = dao.getTotalPendapatan(filter);
    if (pendapatanValueLabel != null) {
      pendapatanValueLabel.setText(formatRupiah.format(pendapatan));
    }
  }

  private void refreshTransaksi() {
    String filter = transaksiFilterCombo.getSelectedItem().toString();
    int totalTrx = dao.getTotalTransaksi(filter);
    if (transaksiValueLabel != null) {
      transaksiValueLabel.setText(String.valueOf(totalTrx));
    }
  }

  private JPanel createCard(String title, String value, Color color, JComboBox<String> filterCombo) {
    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(color);
    card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));

    JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);
    headerPanel.add(titleLabel, BorderLayout.NORTH);

    if (filterCombo != null) {
      filterCombo.setFocusable(false);
      filterCombo.setPreferredSize(new Dimension(120, 28));
      headerPanel.add(filterCombo, BorderLayout.SOUTH);
    }

    JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
    valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
    valueLabel.setForeground(Color.WHITE);

    if (title.equals("Pendapatan")) {
      pendapatanValueLabel = valueLabel;
    } else if (title.equals("Total Transaksi")) {
      transaksiValueLabel = valueLabel;
    }

    card.add(headerPanel, BorderLayout.NORTH);
    card.add(valueLabel, BorderLayout.CENTER);
    return card;
  }
}

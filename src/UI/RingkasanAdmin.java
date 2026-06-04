package UI;

import Database.RingkasanDAO;
import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class RingkasanAdmin extends JFrame {
    
    private RingkasanDAO dao = new RingkasanDAO();

    public RingkasanAdmin() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Ringkasan Admin");
        setResizable(false);
        getContentPane().setBackground(new Color(245, 245, 245));

        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        int pendapatan = dao.getTotalPendapatanHariIni();
        int totalTrx = dao.getTotalTransaksiHariIni();
        int totalPegawai = dao.getTotalPegawai();
        int totalProduk = dao.getTotalProduk();

        mainPanel.add(createCard("Pendapatan Hari Ini", formatRupiah.format(pendapatan), new Color(153, 153, 0)));
        mainPanel.add(createCard("Total Transaksi Hari Ini", String.valueOf(totalTrx), new Color(0, 102, 204)));
        mainPanel.add(createCard("Total Akun Pegawai", String.valueOf(totalPegawai), new Color(204, 102, 0)));
        mainPanel.add(createCard("Jumlah Jenis Produk", String.valueOf(totalProduk), new Color(0, 153, 51)));

        JLabel title = new JLabel("Ringkasan Bisnis - Amera Coffee", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        add(title, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(800, 500));
        pack();
    }

    private JPanel createCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }
}

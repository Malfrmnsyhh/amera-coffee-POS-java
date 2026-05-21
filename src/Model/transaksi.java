package Model;

import java.time.LocalDate;

/**
 * Entity class untuk Transaksi
 * Menyimpan data transaksi secara keseluruhan
 */
public class transaksi {
  private String noTransaksi;
  private int kasirId;
  private String namaKasir;
  private int memberId;
  private String namaCustomer;
  private int nomorMeja;
  private LocalDate tanggal;
  private int total;
  private int bayar;
  private int kembalian;

  // Constructor
  public transaksi() {
  }

  public transaksi(String noTransaksi, int kasirId, String namaKasir,
      int memberId, String namaCustomer, int nomorMeja,
      LocalDate tanggal, int total, int bayar, int kembalian) {
    this.noTransaksi = noTransaksi;
    this.kasirId = kasirId;
    this.namaKasir = namaKasir;
    this.memberId = memberId;
    this.namaCustomer = namaCustomer;
    this.nomorMeja = nomorMeja;
    this.tanggal = tanggal;
    this.total = total;
    this.bayar = bayar;
    this.kembalian = kembalian;
  }

  // Getters
  public String getNoTransaksi() {
    return noTransaksi;
  }

  public int getKasirId() {
    return kasirId;
  }

  public String getNamaKasir() {
    return namaKasir;
  }

  public int getMemberId() {
    return memberId;
  }

  public String getNamaCustomer() {
    return namaCustomer;
  }

  public int getNomorMeja() {
    return nomorMeja;
  }

  public LocalDate getTanggal() {
    return tanggal;
  }

  public int getTotal() {
    return total;
  }

  public int getBayar() {
    return bayar;
  }

  public int getKembalian() {
    return kembalian;
  }

  // Setters
  public void setNoTransaksi(String noTransaksi) {
    this.noTransaksi = noTransaksi;
  }

  public void setKasirId(int kasirId) {
    this.kasirId = kasirId;
  }

  public void setNamaKasir(String namaKasir) {
    this.namaKasir = namaKasir;
  }

  public void setMemberId(int memberId) {
    this.memberId = memberId;
  }

  public void setNamaCustomer(String namaCustomer) {
    this.namaCustomer = namaCustomer;
  }

  public void setNomorMeja(int nomorMeja) {
    this.nomorMeja = nomorMeja;
  }

  public void setTanggal(LocalDate tanggal) {
    this.tanggal = tanggal;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public void setBayar(int bayar) {
    this.bayar = bayar;
  }

  public void setKembalian(int kembalian) {
    this.kembalian = kembalian;
  }

  @Override
  public String toString() {
    return "transaksi{" +
        "noTransaksi='" + noTransaksi + '\'' +
        ", kasirId=" + kasirId +
        ", namaKasir='" + namaKasir + '\'' +
        ", memberId=" + memberId +
        ", namaCustomer='" + namaCustomer + '\'' +
        ", nomorMeja=" + nomorMeja +
        ", tanggal=" + tanggal +
        ", total=" + total +
        ", bayar=" + bayar +
        ", kembalian=" + kembalian +
        '}';
  }
}

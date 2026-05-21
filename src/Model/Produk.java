package Model;

/**
 * Entity produk (menu) dari tabel produk.
 */
public class Produk {

  private int id;
  private String kodeProduk;
  private String namaProduk;
  private int harga;
  private int stok;

  public Produk() {
  }

  public Produk(int id, String kodeProduk, String namaProduk, int harga, int stok) {
    this.id = id;
    this.kodeProduk = kodeProduk;
    this.namaProduk = namaProduk;
    this.harga = harga;
    this.stok = stok;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getKodeProduk() {
    return kodeProduk;
  }

  public void setKodeProduk(String kodeProduk) {
    this.kodeProduk = kodeProduk;
  }

  public String getNamaProduk() {
    return namaProduk;
  }

  public void setNamaProduk(String namaProduk) {
    this.namaProduk = namaProduk;
  }

  public int getHarga() {
    return harga;
  }

  public void setHarga(int harga) {
    this.harga = harga;
  }

  public int getStok() {
    return stok;
  }

  public void setStok(int stok) {
    this.stok = stok;
  }
}

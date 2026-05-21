package Model;

/**
 * Entity member dari tabel member.
 */
public class Member {

  private int id;
  private String kodeMember;
  private String nama;
  private String noHp;

  public Member() {
  }

  public Member(int id, String kodeMember, String nama, String noHp) {
    this.id = id;
    this.kodeMember = kodeMember;
    this.nama = nama;
    this.noHp = noHp;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getKodeMember() {
    return kodeMember;
  }

  public void setKodeMember(String kodeMember) {
    this.kodeMember = kodeMember;
  }

  public String getNama() {
    return nama;
  }

  public void setNama(String nama) {
    this.nama = nama;
  }

  public String getNoHp() {
    return noHp;
  }

  public void setNoHp(String noHp) {
    this.noHp = noHp;
  }
}

package Database;

import Model.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Akses data tabel member.
 */
public class MemberDAO {

  /**
   * Cari member berdasarkan kode member
   */
  public Member cariByKode(String kodeMember) {
    if (kodeMember == null || kodeMember.trim().isEmpty()) {
      return null;
    }

    String sql = "SELECT id, kode_member, nama, no_hp "
        + "FROM member WHERE UPPER(kode_member) = ?";

    try (Connection conn = Koneksi.getKoneksi();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, kodeMember.trim().toUpperCase());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Member(
              rs.getInt("id"),
              rs.getString("kode_member"),
              rs.getString("nama"),
              rs.getString("no_hp"));
        }
      }
    } catch (Exception e) {
      System.err.println("MemberDAO.cariByKode: " + e.getMessage());
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Cari member berdasarkan ID member
   */
  public Member cariById(int idMember) {
    String sql = "SELECT id, kode_member, nama, no_hp FROM member WHERE id = ?";

    try (Connection conn = Koneksi.getKoneksi();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, idMember);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Member(
              rs.getInt("id"),
              rs.getString("kode_member"),
              rs.getString("nama"),
              rs.getString("no_hp"));
        }
      }
    } catch (Exception e) {
      System.err.println("MemberDAO.cariById: " + e.getMessage());
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Ambil semua data member dari database
   */
  public List<Member> getAllMembers() {
    List<Member> memberList = new ArrayList<>();
    String sql = "SELECT id, kode_member, nama, no_hp FROM member ORDER BY id ASC";

    try (Connection conn = Koneksi.getKoneksi();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Member member = new Member(
            rs.getInt("id"),
            rs.getString("kode_member"),
            rs.getString("nama"),
            rs.getString("no_hp"));
        memberList.add(member);
      }
    } catch (Exception e) {
      System.err.println("MemberDAO.getAllMembers: " + e.getMessage());
      e.printStackTrace();
    }

    return memberList;
  }

  /**
   * Cari member dengan filter nama atau kode
   */
  public List<Member> cariByKeyword(String keyword) {
    List<Member> memberList = new ArrayList<>();
    String sql = "SELECT id, kode_member, nama, no_hp FROM member " +
        "WHERE kode_member LIKE ? OR nama LIKE ? ORDER BY nama ASC";

    try (Connection conn = Koneksi.getKoneksi();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      String searchKeyword = "%" + keyword + "%";
      ps.setString(1, searchKeyword);
      ps.setString(2, searchKeyword);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Member member = new Member(
              rs.getInt("id"),
              rs.getString("kode_member"),
              rs.getString("nama"),
              rs.getString("no_hp"));
          memberList.add(member);
        }
      }
    } catch (Exception e) {
      System.err.println("MemberDAO.cariByKeyword: " + e.getMessage());
      e.printStackTrace();
    }

    return memberList;
  }
}

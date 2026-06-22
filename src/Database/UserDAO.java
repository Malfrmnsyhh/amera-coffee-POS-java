package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    // Model untuk menyimpan data User sementara
    public static class User {
        public int id;
        public String namaLengkap;
        public String username;
        public String password;
        public String role;

        public User(int id, String namaLengkap, String username, String password, String role) {
            this.id = id;
            this.namaLengkap = namaLengkap;
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";
        
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                list.add(new User(
                    rs.getInt("id"),
                    rs.getString("nama_lengkap"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean tambahUser(String namaLengkap, String username, String password, String role) {
        String sql = "INSERT INTO users (nama_lengkap, username, password, role) VALUES (?, ?, SHA2(?, 256), ?)";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaLengkap);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, role);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(int id, String namaLengkap, String username, String password, String role) {
        boolean updatePassword = password != null && !password.trim().isEmpty();
        String sql;
        if (updatePassword) {
            sql = "UPDATE users SET nama_lengkap=?, username=?, password=SHA2(?, 256), role=? WHERE id=?";
        } else {
            sql = "UPDATE users SET nama_lengkap=?, username=?, role=? WHERE id=?";
        }
        
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaLengkap);
            ps.setString(2, username);
            if (updatePassword) {
                ps.setString(3, password);
                ps.setString(4, role);
                ps.setInt(5, id);
            } else {
                ps.setString(3, role);
                ps.setInt(4, id);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hapusUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = Koneksi.getKoneksi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

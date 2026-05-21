package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Koneksi singleton ke database MySQL.
 */
public class Koneksi {

  private static Connection koneksi;

  private static final String DEFAULT_URL = "jdbc:mysql://127.0.0.1:3308/amera_coffee"
      + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
  private static final String DEFAULT_USER = "root";
  private static final String DEFAULT_PASS = "admin";

  private static String getEnvOrProp(String key, String fallback) {
    String fromProp = System.getProperty(key);
    if (fromProp != null && !fromProp.isBlank()) {
      return fromProp.trim();
    }
    String fromEnv = System.getenv(key);
    if (fromEnv != null && !fromEnv.isBlank()) {
      return fromEnv.trim();
    }
    return fallback;
  }

  private static final String URL = getEnvOrProp("AMERA_DB_URL", DEFAULT_URL);
  private static final String USER = getEnvOrProp("AMERA_DB_USER", DEFAULT_USER);
  private static final String PASS = getEnvOrProp("AMERA_DB_PASS", DEFAULT_PASS);

  public static Connection getKoneksi() throws SQLException {
    try {
      if (koneksi == null || koneksi.isClosed() || !koneksi.isValid(2)) {
        koneksi = DriverManager.getConnection(URL, USER, PASS);
        System.out.println("Koneksi Berhasil");
      }
    } catch (SQLException e) {
      koneksi = null;
      System.err.println("Koneksi Gagal: " + e.getMessage());
      throw e;
    }
    return koneksi;
  }

  /**
   * Cek koneksi tanpa melempar exception ke UI.
   */
  public static boolean isConnected() {
    try {
      Connection c = getKoneksi();
      return c != null && !c.isClosed();
    } catch (SQLException e) {
      return false;
    }
  }

  public static void main(String[] args) {
    try {
      getKoneksi();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}

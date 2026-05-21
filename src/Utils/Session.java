package Utils;

/**
 * Menyimpan data user yang sedang login (sederhana, untuk dipakai antar form).
 */
public final class Session {

  private static int userId;
  private static String userName = "";
  private static String role = "";

  private Session() {
  }

  public static void setUser(int id, String nama, String userRole) {
    userId = id;
    userName = nama != null ? nama : "";
    role = userRole != null ? userRole : "";
  }

  public static void clear() {
    userId = 0;
    userName = "";
    role = "";
  }

  public static int getUserId() {
    return userId;
  }

  public static String getUserName() {
    return userName;
  }

  public static String getRole() {
    return role;
  }

  public static boolean isLoggedIn() {
    return userId > 0;
  }
}

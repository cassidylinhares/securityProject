package SecurityProject;

public class PasswordCheck {
  // public static void main(String[] args) {
  // // replace Main with the class the password orgiginates with
  // Main passObj = new Main();
  // // replace password with the variable that saves the password
  // passCheck(passObj.password);
  // System.out.println("Password check complete!");
  // }

  public static boolean passwordChecker(String password) {
    boolean strongPassword = false;
    boolean hasLower = false;
    boolean hasUpper = false;
    boolean hasDigit = false;

    int i = 0;
    while (i < password.length()) {
      if (hasLower && hasUpper && hasDigit) {
        break;
      }
      if (Character.isLowerCase(password.charAt(i))) {
        hasLower = true;
      }
      if (Character.isUpperCase(password.charAt(i))) {
        hasUpper = true;
      }

      if (Character.isDigit(password.charAt(i))) {
        hasDigit = true;
      }
      i++;
    }

    if (password.length() < 8) {
      System.out.println("Password must be 8 or more characters long!");
    }

    else if (hasLower == false) {
      System.out.println("Please include a lower case character.");
    } else if (hasUpper == false) {
      System.out.println("Please include an upper case character.");
    } else if (hasDigit == false) {
      System.out.println("Please include a number character.");
    } else {
      System.out.println("Great Password!");
      strongPassword = true;
    }
    return strongPassword;
  }

}

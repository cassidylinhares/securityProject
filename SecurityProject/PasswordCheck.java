class PasswordCheck {
    public static void main(String[] args) {
      //replace Main with the class the password orgiginates with
      Main passObj = new Main();
      //replace password with the variable that saves the password
      passCheck(passObj.password);
      System.out.println("Password check complete!");
    }

    public static void passCheck(String pass)
    {
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        int i = 0;
        while (i < pass.length())
        {
            if (hasLower && hasUpper && hasDigit)
            {
                break;
            }
            if (Character.isLowerCase(pass.charAt(i)))
            {
              hasLower = true;
            }
            if (Character.isUpperCase(pass.charAt(i)))
            {
              hasUpper = true;
            }

            if (Character.isDigit(pass.charAt(i)))
            {
              hasDigit = true;
            }
            i++;
          }

      if (pass.length() < 8)
        {
          System.out.println("Password must be 8 or more characters long!");
        }

       else if (hasLower == false){
          System.out.println("Please include a lower case character :(");
        }
        else if (hasUpper == false){
          System.out.println("Please include an upper case character :(");
        }
        else if (hasDigit == false){
          System.out.println("Please include a number character :(");
        }
        else
        {
           System.out.println("Great Password!");
        }
    }

  }
  
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Project 5 -- Teacher
 *
 * @author CS180-PR5 Group 079
 * @version December 13, 2021
 * <p>
 * A Teacher class
 * extends User
 */

public class Teacher extends User implements Serializable {

    private int id;
    private String username;
    private String password;
    private String fullName;

    public Teacher(String username, String password, String fullName, ArrayList<User> accounts) {
        super(username, password, fullName, accounts);
    }

    public Teacher(ArrayList<User> accounts) {
        super(accounts);
    }

}

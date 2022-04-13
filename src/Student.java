import java.io.Serializable;
import java.util.ArrayList;

/**
 * Project 5 -- Student
 *
 * @author CS180-PR5 Group 079
 * @version December 13, 2021
 * <p>
 * A Student class
 * extends User
 */

public class Student extends User implements Serializable {

    private int id;
    private String username;
    private String fullName;
    private String password;

    public Student(String username, String password, String fullName, ArrayList<User> accounts) {
        super(username, password, fullName, accounts);
    }

    public Student(ArrayList<User> accounts) {
        super(accounts);
    }
}

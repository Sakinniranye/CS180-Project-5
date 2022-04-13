import java.io.Serializable;
import java.util.ArrayList;

/**
 * Project 4 -- User
 *
 * @author CS180-PR4 Group 079
 * @version November 15, 2021
 * <p>
 * A User class
 * master class of Student and Teacher
 * used for managing Darkspace users
 */

public class User implements Serializable {
    private int id;
    private String username;
    private String password;
    private String fullName;

    public User(String username, String password, String fullName, ArrayList<User> accounts) {
        this.id = generateId(accounts);
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    public User(ArrayList<User> accounts) {
        this.id = generateId(accounts);
        this.username = "%test%";
        this.password = "123";
        this.fullName = "%test%";
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getFullName() {
        return this.fullName;
    }

    public int generateId(ArrayList<User> accounts) {
        return accounts.size() + 1;
    }

    public void save(Student student) {
    }
}
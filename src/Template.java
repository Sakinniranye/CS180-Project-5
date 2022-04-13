import java.io.Serializable;
import java.util.ArrayList;

/**
 * Project 5 -- Template
 *
 * @author CS180-PR5 Group 079
 * @version December 13, 2021
 * <p>
 * A Template class
 * used for serialized communication with client
 */

public class Template implements Serializable {
    ArrayList<String> template = new ArrayList<>();

    public void add(ArrayList<String> toAdd) {
        this.template.add(toAdd.get(0));
        this.template.add(toAdd.get(1));
    }

    public ArrayList<String> get(int no) {
        ArrayList<String> output = new ArrayList<>();
        output.add(this.template.get(2 * no));
        output.add(this.template.get(2 * no + 1));
        return output;
    }

    public int size() {
        return (this.template.size() / 2);
    }

    public ArrayList<ArrayList<String>> convert() {
        ArrayList<ArrayList<String>> output = new ArrayList<>();
        for (int i = 0; i < this.size(); i++) {
            output.add(this.get(i));
        }
        return output;
    }


}

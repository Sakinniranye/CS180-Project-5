import java.io.Serializable;
import java.util.ArrayList;

/**
 * Project 5 -- Question
 *
 * @author CS180-PR5 Group 079
 * @version December 13, 2021
 * <p>
 * A Question class
 * used for managing answers
 */

public class Question implements Serializable {
    private String title;
    private ArrayList<String> choices;
    private String correctAnswer;

    public Question(String title, ArrayList<String> choices, String correctAnswer) {
        this.title = title;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public Question(String title) {
        this.title = title;
        this.choices = new ArrayList<String>();
        this.correctAnswer = "";
    }

    // most of the methods are explained by their name

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }

    public void addChoice(String choice) {
        for (String i : this.choices) {
            if (i.equals(choice)) {
                System.out.println("This choice already exists!");
                return;
            }
        }
        choices.add(choice);
        System.out.println("Added choice: " + choice);
    }

    public boolean addChoiceGUI(String choice) {
        for (String i : this.choices) {
            if (i.equals(choice)) {
                System.out.println("This choice already exists!");
                return false;
            }
        }
        choices.add(choice);
        System.out.println("Added choice: " + choice);
        return true;
    }

    public boolean removeChoice(String choice) {
        if (this.choices.contains(choice)) {
            this.choices.remove(choice);
            return true;
        } else {
            return false;
        }
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean checkAnswer(String candidate) {
        return candidate.equals(this.correctAnswer);
    }

    // checks if this question contains choice
    public boolean containsChoice(String choice) {
        for (String i : choices) {
            if (choice.equals(i)) {
                return true;
            }
        }
        return false;
    }

    public void printChoices() {
        for (String i : choices) {
            System.out.println(i);
        }
    }

    public String getChoiceByNo(int no) {
        return choices.get(no - 1);
    }

    public void printChoicesGUIButtons(Template template) {
        for (String i : choices) {
            template.add(new ArrayList<String>() {
                {
                    add("BUTTON");
                    add(i);
                }
            });
        }
    }

    public void printChoicesGUI(Template template) {
        for (String i : choices) {
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add(i);
                }
            });
        }
    }
}

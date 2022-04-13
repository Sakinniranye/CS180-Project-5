import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Project 5 -- Course
 *
 * @author CS180-PR5 Group 079
 * @version December 13, 2021
 * <p>
 * A Course class
 * used for managing quizzes
 */

public class Course implements Serializable {
    private ArrayList<Quiz> quizzes;
    private String courseName;

    public Course(String courseName) {
        this.courseName = courseName;
        this.quizzes = new ArrayList<>();
    }

    // most of the methods are explained by their name

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // add quiz
    public boolean addQuiz(Quiz quiz) {
        for (Quiz i : this.quizzes) {
            if (i.getQuizName().equals(quiz.getQuizName())) {
                System.out.println("There already is a quiz with this name! Please try again.");
                return false;
            }
        }

        quizzes.add(quiz);
        System.out.println(quiz.getQuizName() + " was added to the course!");
        return true;
    }

    // add quiz
    public boolean addQuizQUI(Quiz quiz, ArrayList<ArrayList<String>> template, ObjectOutputStream writer,
                              ObjectInputStream reader, ArrayList<String> clientResponse)
            throws IOException, ClassNotFoundException {
        for (Quiz i : this.quizzes) {
            if (i.getQuizName().equals(quiz.getQuizName())) {
                System.out.println("There already is a quiz with this name! Please try again.");
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add("There already is a quiz with this name!");
                    }
                });
                template.add(new ArrayList<String>() {
                    {
                        add("BUTTON");
                        add("OK");
                    }
                });
                writer.writeObject(template);
                writer.flush();
                template = new ArrayList<>();
                clientResponse = (ArrayList<String>) reader.readObject();
                return false;
            }
        }

        quizzes.add(quiz);
        System.out.println(quiz.getQuizName() + " was added to the course!");
        return true;
    }

    // remove quiz
    public boolean removeQuiz(Quiz quiz) {
        if (quiz != null) {
            for (Quiz i : this.quizzes) {
                if (i.getQuizName().equals(quiz.getQuizName())) {
                    this.quizzes.remove(i);
                    System.out.println("Removed Quiz: " + i.getQuizName());
                    return true;
                }
            }
        }
        System.out.println("There is no quiz with this name.");
        return false;
    }

    public boolean removeQuizByNo(int no) {
        if (this.quizzes.size() >= no) {
            this.quizzes.remove(no - 1);
            return true;
        }
        return false;
    }

    public Quiz getQuizByNo(int no) {
        if (this.quizzes.size() >= no) {
            return this.quizzes.get(no - 1);
        }
        return null;
    }

    public Quiz quizByName(String quizName) {
        if (quizName == null) {
            return null;
        }
        for (Quiz i : this.quizzes) {
            if (i.getQuizName().equals(quizName)) {
                return i;
            }
        }
        return null;
    }

    public void listQuizzes() {
        System.out.println("Available Quizzes: ");
        if (this.quizzes.size() > 0) {
            for (Quiz i : this.quizzes) {
                System.out.println(i.getQuizName());
            }
        } else {
            System.out.println("No quizzes in this course");
        }
    }

    public void listQuizzesGUI(Template template) {
        System.out.println("Available Quizzes: ");
        template.add(new ArrayList<String>() {
            {
                add("TEXT");
                add("Available Quizzes: ");
            }
        });
        if (this.quizzes.size() > 0) {
            for (Quiz i : this.quizzes) {
                System.out.println(i.getQuizName());
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(i.getQuizName());
                    }
                });
            }
        } else {
            System.out.println("No quizzes in this course");
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add("No quizzes in this course");
                }
            });
        }
    }

    public void listQuizzesGUIButtons(Template template) {
        System.out.println("Available Quizzes: ");
        template.add(new ArrayList<String>() {
            {
                add("TEXT");
                add("Available Quizzes: ");
            }
        });
        if (this.quizzes.size() > 0) {
            for (Quiz i : this.quizzes) {
                System.out.println(i.getQuizName());
                template.add(new ArrayList<String>() {
                    {
                        add("BUTTON");
                        add(i.getQuizName());
                    }
                });
            }
        } else {
            System.out.println("No quizzes in this course");
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add("No quizzes in this course");
                }
            });
        }
    }

    // edit quiz name, deadline, and/or time limit
    public String editQuiz(Quiz quiz, String newQuizName, LocalDateTime newDeadline, int newTimeLimit) {
        for (int i = 0; i < quizzes.size(); i++) {
            if (quizzes.get(i).getQuizName().equals(quiz.getQuizName())) {
                quiz.setQuizName(newQuizName);
                quiz.setDeadline(newDeadline);
                quiz.setTimeLimit(newTimeLimit);
            }
        }
        return quiz.getQuizName() + " was successfully modified!";
    }

    public int getSize() {
        return this.quizzes.size();
    }
}

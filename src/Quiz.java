import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Project 5 -- Quiz
 *
 * @author CS180-PR5 Group 079
 * @version December 13, 2021
 * <p>
 * A Quiz class
 * used for managing questions
 */

public class Quiz implements Serializable {
    private String quizName;
    private ArrayList<Question> questions;
    private LocalDateTime deadline;
    private int timeLimit;
    private int requiredQ;
    private boolean randomized;
    private boolean ready;

    public Quiz(String quizName, LocalDateTime deadline, int timeLimit, int requiredQ, boolean randomized) {
        this.quizName = quizName;
        this.questions = new ArrayList<>();
        this.deadline = deadline;
        this.timeLimit = timeLimit;
        this.requiredQ = requiredQ;
        this.randomized = randomized;
        this.ready = true;
    }

    public Quiz(String quizName) {
        this.quizName = quizName;
        this.questions = new ArrayList<>();
        this.deadline = null;
        this.timeLimit = 0;
        this.requiredQ = 0;
        this.ready = false;
    }

    // most of the methods are explained by their name

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isRandomized() {
        return randomized;
    }

    public void setRandomized(boolean randomized) {
        this.randomized = randomized;
    }

    public int getRequiredQ() {
        return requiredQ;
    }

    public void setRequiredQ(int requiredQ) {
        this.requiredQ = requiredQ;
    }

    // quiz name
    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    // quiz questions
    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    // quiz deadline
    public LocalDateTime getDeadline() {
        return deadline;
    }

    public String readDeadline() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dtf.format(this.getDeadline());
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    // quiz Time Limit
    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    // add questions
    public boolean addQuestion(Question newQuestion) {
        for (Question i : this.questions) {
            if (i.getTitle().equals(newQuestion.getTitle())) {
                System.out.println("This question already exists!");
                return false;
            }
        }
        questions.add(newQuestion);
        return true;
    }

    // remove question
    public ArrayList<Question> removeQuestion(Question oldQuestion) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getTitle().equals(oldQuestion.getTitle())) {
                questions.remove(oldQuestion);
            } else {
                System.out.println("This question does not exists!");
            }
        }
        return questions;
    }

    // edit question, options, and/or answers
    public String editQuestion(Question quizQuestions, String question) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getTitle().equals(quizQuestions.getTitle())) {
                quizQuestions.setTitle(question);
            }
        }
        return "Quiz question was successfully updated!";
    }

    public String editOptions(Question quizQuestions, ArrayList<String> options) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getTitle().equals(quizQuestions.getTitle())) {
                quizQuestions.setChoices(options);
            }
        }
        return "Quiz question was successfully updated!";
    }

    public String editAnswer(Question quizQuestions, String answer) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getTitle().equals(quizQuestions.getTitle())) {
                quizQuestions.setCorrectAnswer(answer);
            }
        }
        return "Quiz question was successfully updated!";
    }

    // grade quiz
    public void gradeQuiz() {

    }

    public void printQuiz() {
        System.out.println("Quiz: " + this.getQuizName());
        System.out.println("Deadline: " + this.readDeadline());
        System.out.println("Time Limit: " + this.getTimeLimit() + " minutes");
        System.out.println("Randomized: " + this.isRandomized());
        for (Question i : questions) {
            System.out.println("Question prompt: " + i.getTitle());
            i.printChoices();
            System.out.println("Correct answer: " + i.getCorrectAnswer());
        }
        System.out.println();
    }

    public void printQuizGUI(Template template) {
        String prompt1 = ("Quiz: " + this.getQuizName());
        String prompt2 = ("Deadline: " + this.readDeadline());
        String prompt3 = ("Time Limit: " + this.getTimeLimit() + " minutes");
        String prompt4 = ("Randomized: " + this.isRandomized());
        template.add(new ArrayList<String>() {
            {
                add("TEXT");
                add(prompt1);
            }
        });
        template.add(new ArrayList<String>() {
            {
                add("TEXT");
                add(prompt2);
            }
        });
        template.add(new ArrayList<String>() {
            {
                add("TEXT");
                add(prompt3);
            }
        });
        template.add(new ArrayList<String>() {
            {
                add("TEXT");
                add(prompt4);
            }
        });
        for (Question i : questions) {
            String prompt5 = ("Question prompt: " + i.getTitle());
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add(prompt5);
                }
            });
            i.printChoicesGUI(template);
            String prompt6 = ("Correct answer: " + i.getCorrectAnswer());
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add(prompt6);
                }
            });
        }
        System.out.println();
    }

}
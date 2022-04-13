import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Project 5 -- Submission
 *
 * @author CS180-PR5 Group 079
 * @version December 13, 2021
 * <p>
 * A Submission class
 * used for managing user taken quizzes
 */

public class Submission implements Serializable {
    private int id;
    private String qName;
    private ArrayList<Integer> grades;
    private ArrayList<Question> questions;
    private ArrayList<Question> shuffledQ;
    private ArrayList<String> studentAnswers;
    private int timeLimit;
    private LocalDateTime deadline;
    private LocalDateTime submitTime;
    private LocalDateTime startTime;
    private boolean randomized;

    public Submission(Quiz quiz, Student student) {
        this.id = student.getId();
        this.grades = new ArrayList<Integer>();
        this.questions = quiz.getQuestions();
        this.shuffledQ = new ArrayList<Question>();
        this.studentAnswers = new ArrayList<String>();
        this.timeLimit = quiz.getTimeLimit();
        this.deadline = quiz.getDeadline();
        this.submitTime = null;
        this.startTime = null;
        this.qName = quiz.getQuizName();
        this.randomized = quiz.isRandomized();
    }

    // most of the methods are explained by method name

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getqName() {
        return qName;
    }

    public void setqName(String qName) {
        this.qName = qName;
    }

    public ArrayList<Integer> getGrades() {
        return grades;
    }

    public void setGrades(ArrayList<Integer> grades) {
        this.grades = grades;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void addGrade(int grade) {
        this.grades.add(grade);
    }

    public ArrayList<Question> getShuffledQ() {
        return shuffledQ;
    }

    public void setShuffledQ(ArrayList<Question> shuffledQ) {
        this.shuffledQ = shuffledQ;
    }

    public ArrayList<String> getStudentAnswers() {
        return studentAnswers;
    }

    public void setStudentAnswers(ArrayList<String> studentAnswers) {
        this.studentAnswers = studentAnswers;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // method that allows student to take the quiz
    // the result is saved in submission object
    public void undergoSubmission(Scanner scanner) {
        this.startTime = LocalDateTime.now();
        System.out.println("Date of start: " + readDate(startTime));
        if (timeLimit == 0) {
            System.out.println("Allowed time: unlimited");
        } else {
            System.out.println("Allowed time: " + timeLimit + " minutes");
        }

        Random rng = new Random();
        int popper = 0;
        int size = questions.size();
        ArrayList<Question> notOriginal = new ArrayList<>();
        notOriginal.addAll(questions);
        if (randomized) {
            for (int i = 0; i < size; i++) {
                popper = rng.nextInt(notOriginal.size());
                shuffledQ.add(notOriginal.get(popper));
                notOriginal.remove(popper);
            }
        } else {
            shuffledQ.addAll(questions);
        }
        for (Question q : shuffledQ) {
            System.out.println("Question number: " + (shuffledQ.indexOf(q) + 1) + " / " + shuffledQ.size());
            System.out.println("Q: " + q.getTitle());
            while (true) {
                System.out.println("Possible answers: ");
                q.printChoices();
                System.out.println("Enter your answer: ");
                String answer = scanner.nextLine();
                if (q.containsChoice(answer)) {
                    studentAnswers.add(answer);
                    break;
                } else {
                    System.out.println("Choice not found, try again");
                }
            }
        }
        System.out.println("End of the quiz");
        this.submitTime = LocalDateTime.now();
        System.out.println("Quiz finished on: " + readDate(submitTime));
    }

    public void undergoSubmissionGUI(Template template,
                                     ObjectOutputStream writer, ObjectInputStream reader,
                                     ArrayList<String> clientResponse)
            throws IOException, ClassNotFoundException {
        this.startTime = LocalDateTime.now();
        String prompt1 = ("Date of start: " + readDate(startTime));
        template.add(new ArrayList<String>() {
            {
                add("TEXT");
                add(prompt1);
            }
        });
        if (timeLimit == 0) {
            String prompt2 = ("Allowed time: unlimited");
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add(prompt2);
                }
            });
        } else {
            String prompt3 = ("Allowed time: " + timeLimit + " minutes");
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add(prompt3);
                }
            });
        }
        template.add(new ArrayList<String>() {
            {
                add("BUTTON");
                add("OK");
            }
        });
        writer.writeObject(template);
        writer.flush();
        template = new Template();
        clientResponse = (ArrayList<String>) reader.readObject();

        Random rng = new Random();
        int popper = 0;
        int size = questions.size();
        ArrayList<Question> notOriginal = new ArrayList<>();
        notOriginal.addAll(questions);
        if (randomized) {
            for (int i = 0; i < size; i++) {
                popper = rng.nextInt(notOriginal.size());
                shuffledQ.add(notOriginal.get(popper));
                notOriginal.remove(popper);
            }
        } else {
            shuffledQ.addAll(questions);
        }
        for (Question q : shuffledQ) {
            String prompt3 = ("Question number: " + (shuffledQ.indexOf(q) + 1) + " / " + shuffledQ.size());
            String prompt4 = ("Q: " + q.getTitle());
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
            q.printChoicesGUIButtons(template);
            writer.writeObject(template);
            writer.flush();
            template = new Template();
            clientResponse = (ArrayList<String>) reader.readObject();

            studentAnswers.add(q.getChoiceByNo(Integer.parseInt(clientResponse.get(0)) - 1));
        }
        String prompt5 = ("End of the quiz");
        this.submitTime = LocalDateTime.now();
        String prompt6 = ("Quiz finished on: " + readDate(submitTime));
        template.add(new ArrayList<String>() {
            {
                add("TEXT");
                add(prompt5);
            }
        });
        template.add(new ArrayList<String>() {
            {
                add("TEXT");
                add(prompt6);
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
        template = new Template();
        clientResponse = (ArrayList<String>) reader.readObject();
    }

    // show submission details
    public void displaySubmission() {
        System.out.println("User id: " + id);
        System.out.println("Quiz name: " + qName);
        if (!(submitTime == null)) {
            System.out.println("Date of start: " + readDate(startTime));
            System.out.println("Date of finish: " + readDate(submitTime));
            if (deadline != null) {
                System.out.println("Deadline: " + readDate(deadline));
            } else {
                System.out.println("No deadline");
            }

            if (timeLimit == 0) {
                System.out.println("Allowed time: unlimited");
            } else {
                System.out.println("Allowed time: " + timeLimit + " minutes");
            }
            System.out.println();
            for (Question q : shuffledQ) {
                System.out.println("Q: " + q.getTitle());
                System.out.println("Possible answers: ");
                q.printChoices();
                System.out.println("Correct answer:");
                System.out.println(q.getCorrectAnswer());
                System.out.println("Student answer:");
                System.out.println(studentAnswers.get(shuffledQ.indexOf(q)));
                System.out.println();
            }
        }
        if (grades.size() > 0) {
            System.out.println("Grades:" + this.grades.toString());
        } else {
            System.out.println("Quiz not graded yet");
            System.out.println();
        }
    }

    public void displaySubmissionGUI(Template template,
                                     ObjectOutputStream writer, ObjectInputStream reader,
                                     ArrayList<String> clientResponse)
            throws IOException, ClassNotFoundException {
        String prompt1 = ("User id: " + id);
        template.add(new ArrayList<String>() {
            {
                add("TEXT");
                add(prompt1);
            }
        });
        String prompt2 = ("Quiz name: " + qName);
        template.add(new ArrayList<String>() {
            {
                add("TEXT");
                add(prompt2);
            }
        });
        if (!(submitTime == null)) {
            String prompt3 = ("Date of start: " + readDate(startTime));
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add(prompt3);
                }
            });
            String prompt4 = ("Date of finish: " + readDate(submitTime));
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add(prompt4);
                }
            });
            if (deadline != null) {
                String prompt5 = ("Deadline: " + readDate(deadline));
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(prompt5);
                    }
                });
            } else {
                String prompt6 = ("No deadline");
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(prompt6);
                    }
                });
            }

            if (timeLimit == 0) {
                String prompt7 = ("Allowed time: unlimited");
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(prompt7);
                    }
                });
            } else {
                String prompt8 = ("Allowed time: " + timeLimit + " minutes");
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(prompt8);
                    }
                });
            }
            template.add(new ArrayList<String>() {
                {
                    add("BUTTON");
                    add("OK");
                }
            });
            writer.writeObject(template);
            writer.flush();
            template = new Template();
            clientResponse = (ArrayList<String>) reader.readObject();

            for (Question q : shuffledQ) {
                String prompt9 = ("Q: " + q.getTitle());
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(prompt9);
                    }
                });
                String prompt10 = ("Possible answers: ");
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(prompt10);
                    }
                });
                q.printChoicesGUI(template);
                String prompt11 = ("Correct answer:");
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(prompt11);
                    }
                });
                String prompt12 = (q.getCorrectAnswer());
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(prompt12);
                    }
                });
                String prompt13 = ("Student answer:");
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(prompt13);
                    }
                });
                String prompt14 = (studentAnswers.get(shuffledQ.indexOf(q)));
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(prompt14);
                    }
                });
                template.add(new ArrayList<String>() {
                    {
                        add("BUTTON");
                        add("NEXT");
                    }
                });
                writer.writeObject(template);
                writer.flush();
                template = new Template();
                clientResponse = (ArrayList<String>) reader.readObject();
            }
        }
        if (grades.size() > 0) {
            String prompt15 = ("Grades:" + this.grades.toString());
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add(prompt15);
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
            template = new Template();
            clientResponse = (ArrayList<String>) reader.readObject();
        } else {
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add("Quiz not graded yet");
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
            template = new Template();
            clientResponse = (ArrayList<String>) reader.readObject();
        }
    }

    // to convert date object to string
    public String readDate(LocalDateTime t) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dtf.format(t);
    }
}

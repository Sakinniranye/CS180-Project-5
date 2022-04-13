import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Project 5 -- DarkServer
 *
 * @author CS180-PR5 Group 079
 * @version December 13, 2021
 * <p>
 * A DarkServer class
 * main server used to communicate with the client
 * uses port 1123
 */

public class DarkServer {

    // Variables for longest and most used prompts
    private static String welcomePrompt = "Welcome to the Darkspace quiz tool";
    private static String mainQuestion = "Are you a student or teacher?";
    private static String mainQuestionPrompt = "1. Student \n2. Teacher";
    private static String statusQuestion = "Are you an existing user or a new user";
    private static String statusQuestionPrompt = "1. Existing user\n2. New user";
    private static String enterUsername = "Enter your username";
    private static String enterFullName = "Enter your full name";
    private static String enterPassword = "Enter your password";
    private static String studentPrompt = "1. Select Course\n2. View course list\n3. Delete Account\n4. Logout";
    private static String teacherPrompt = "1. Add Course\n2. View course list\n3. Delete Course\n4. Select Course" +
                                          "\n5. Delete Account\n6. Logout";
    private static String teacherSelectCoursePrompt = "1. Delete Quiz\n2. Edit Quiz\n3. Create Quiz\n" +
                                                      "4. Display/grade Quiz\n5. Go back\n6. List Quizzes\n7. Print Quiz";
    private static String studentSelectCoursePrompt = "1. Take Quiz\n2. View Grades\n3. View Quiz list\n4. Go back";
    private static String invalidInput = "Invalid input! Please try again.";
    private static String goodbyePrompt = "Thanks for using Darkspace!";
    private static String addCoursePrompt = "Enter the name of the Course you would like to add: ";
    private static String deleteCoursePrompt = "Enter the name of the Course you would like to delete: ";
    private static String courseExistsMessage = "Sorry, this course exists.";
    private static String courseNotExist = "Sorry, this course does not exist.";
    private static String addedCourseMessage = "Successfully added course: ";
    private static String removedCourseMessage = "Successfully removed course: ";
    private static String idMessage = "Your unique ID you will use to log in: ";
    private static String enterId = "Enter your ID";
    private static String deleteAccountMessage = "Successfully deleted account";
    private static int delay = 200;
    private static final int PORT = 1123;
    private static ArrayList<User> accounts = new ArrayList<>();
    private static ArrayList<Course> courses = new ArrayList<>();
    private static ArrayList<Submission> submissions = new ArrayList<>();


    public static void main(String[] args) {

        accounts = readUsers();
        courses = readCourses();
        submissions = readSubmissions();

        ServerSocket server = null;

        try {
            server = new ServerSocket(PORT);
            server.setReuseAddress(true);

            while (true) {
                Socket client = server.accept();
                DarkLoop clientLoop = new DarkLoop(client);
                new Thread(clientLoop).start();
            }
        } catch (IOException e) {
            System.out.println("problem establishing connection");
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    System.out.println("problem closing connection");
                }
            }
        }
    }

    /**
     * Project 5 -- DarkLoop
     *
     * @author CS180-PR5 Group 079
     * @version December 13, 2021
     * <p>
     * A DarkLoop class
     * Handles single client
     */

    private static class DarkLoop implements Runnable {
        private final Socket client;

        // Constructor
        public DarkLoop(Socket socket) {
            this.client = socket;
        }

        public void run() {
            ObjectOutputStream writer = null;
            ObjectInputStream reader = null;
            try {

                writer = new ObjectOutputStream(client.getOutputStream());
                reader = new ObjectInputStream(client.getInputStream());


                boolean serverRunning = true;
                while (serverRunning) {
                    try {
                        do {

                            Template template = new Template();
                            ArrayList<String> clientResponse = new ArrayList<>();
                            String toServerMessage = "";

                            // Main Darkspace loop
                            // Saves data to file after user log out
                            boolean cont = true;
                            while (cont) {
                                // Print welcome prompt
                                System.out.println();
                                System.out.println(welcomePrompt);
                                template.add(new ArrayList<String>() {
                                    {
                                        add("TEXT");
                                        add(welcomePrompt);
                                    }
                                });
                                template.add(new ArrayList<String>() {
                                    {
                                        add("BUTTON");
                                        add("enter");
                                    }
                                });
                                writer.writeObject(template);
                                writer.flush();
                                template = new Template();
                                clientResponse = (ArrayList<String>) reader.readObject();
                                System.out.println(clientResponse.get(0));

                                // Ask user profession
                                System.out.println(mainQuestion);
                                System.out.println(mainQuestionPrompt);
                                template.add(new ArrayList<String>() {
                                    {
                                        add("TEXT");
                                        add(mainQuestion);
                                    }
                                });
                                template.add(new ArrayList<String>() {
                                    {
                                        add("BUTTON");
                                        add("student");
                                    }
                                });
                                template.add(new ArrayList<String>() {
                                    {
                                        add("BUTTON");
                                        add("teacher");
                                    }
                                });
                                writer.writeObject(template);
                                writer.flush();
                                template = new Template();
                                clientResponse = (ArrayList<String>) reader.readObject();

                                // Get answer from user
                                String mainAnswer = clientResponse.get(0);
                                // Go to correct user type interface


                                // STUDENT BRANCH
                                if (mainAnswer.equals("1")) {
                                    cont = false;

                                    // Ask for new / existing
                                    System.out.println(statusQuestion);
                                    System.out.println(statusQuestionPrompt);
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("TEXT");
                                            add(statusQuestion);
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("Existing user");
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("New user");
                                        }
                                    });
                                    writer.writeObject(template);
                                    writer.flush();
                                    template = new Template();
                                    clientResponse = (ArrayList<String>) reader.readObject();

                                    // New / existing response
                                    String statusAnswer = clientResponse.get(0);

                                    // Existing user
                                    boolean flag = true;
                                    Student current = null;
                                    while (flag || current == null) {
                                        // ask for id and password and check if correct
                                        if (statusAnswer.equals("1")) {
                                            System.out.println(enterId);
                                            System.out.println(enterPassword);
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(enterId);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("FIELD");
                                                    add("user input here");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(enterPassword);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("FIELD");
                                                    add("user input here");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Confirm");
                                                }
                                            });
                                            writer.writeObject(template);
                                            writer.flush();
                                            template = new Template();
                                            clientResponse = (ArrayList<String>) reader.readObject();

                                            String userId = clientResponse.get(1);
                                            String password = clientResponse.get(2);

                                            // check if user is a Student
                                            User temp = checkLoginGUI(userId, password, accounts, template);
                                            if (template.size() != 0) {
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

                                            if (temp instanceof Student) {
                                                current = (Student) checkLogin(userId, password, accounts);
                                            } else if (!invalidPassword(userId, password, accounts)) {
                                                System.out.println("ID does not belong to a student");
                                                System.out.println();
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add("ID does not belong to a student");
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
                                                current = null;
                                            } else {
                                                current = null;
                                            }
                                            if (current != null) {
                                                flag = false;
                                            }
                                        } else if (statusAnswer.equals("2")) {  // New user
                                            // Create new user with provided details
                                            System.out.println(enterUsername);
                                            System.out.println(enterFullName);
                                            System.out.println(enterPassword);
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(enterUsername);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("FIELD");
                                                    add("user input here");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(enterFullName);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("FIELD");
                                                    add("user input here");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(enterPassword);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("FIELD");
                                                    add("user input here");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Confirm");
                                                }
                                            });
                                            writer.writeObject(template);
                                            writer.flush();
                                            template = new Template();
                                            clientResponse = (ArrayList<String>) reader.readObject();

                                            String username = clientResponse.get(1);
                                            String fullName = clientResponse.get(2);
                                            String password = clientResponse.get(3);

                                            current = new Student(username, password, fullName, accounts);
                                            accounts.add(current);
                                            System.out.println(idMessage + current.getId());
                                            toServerMessage = idMessage + current.getId();
                                            String finalToServerMessage = toServerMessage;
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(finalToServerMessage);
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

                                            flag = false;
                                        }
                                        // ask again if failed to log in
                                        if (flag) {
                                            if (!statusAnswer.equals("1")) {
                                                System.out.println(invalidInput);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add(invalidInput);
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
                                            // Ask for new / existing
                                            System.out.println(statusQuestion);
                                            System.out.println(statusQuestionPrompt);
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(statusQuestion);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Existing user");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("New user");
                                                }
                                            });
                                            writer.writeObject(template);
                                            writer.flush();
                                            template = new Template();
                                            clientResponse = (ArrayList<String>) reader.readObject();

                                            // New / existing response
                                            statusAnswer = clientResponse.get(0);
                                        }
                                    }

                                    // MAIN STUDENT LOOP
                                    // Ask Student for action
                                    System.out.println();
                                    System.out.println("Student: " + current.getUsername()
                                                       + " ID: " + current.getId());
                                    System.out.println(studentPrompt);
                                    toServerMessage = "Student: " + current.getUsername()
                                                      + " ID: " + current.getId();
                                    String finalToServerMessage1 = toServerMessage;
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("TEXT");
                                            add(finalToServerMessage1);
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("Select Course");
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("View course list");
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("Delete Account");
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("Logout");
                                        }
                                    });
                                    writer.writeObject(template);
                                    writer.flush();
                                    template = new Template();
                                    clientResponse = (ArrayList<String>) reader.readObject();

                                    // Get action answer
                                    String studentPromptAnswer = clientResponse.get(0);
                                    String quizSelection = "";
                                    do {
                                        // Course selection
                                        while (studentPromptAnswer.equals("1")) {
                                            Course currentCourse = null;

                                            clientResponse = new ArrayList<>();
                                            while (clientResponse.size() == 0) {
                                                System.out.println("List of available courses: ");
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("REFRESH");
                                                        add("refresh");
                                                    }
                                                });
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add("Choose a course");
                                                    }
                                                });
                                                listCoursesGUIButtons(courses, template);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("BUTTON");
                                                        add("Cancel");
                                                    }
                                                });
                                                writer.writeObject(template);
                                                writer.flush();
                                                template = new Template();
                                                try {
                                                    Thread.sleep(delay);
                                                } catch (InterruptedException ex) {
                                                    Thread.currentThread().interrupt();
                                                }
                                                clientResponse = (ArrayList<String>) reader.readObject();
                                                System.out.println(clientResponse.toString());
                                            }
                                            reader.readObject();
                                            System.out.println(clientResponse.toString());

                                            String courseName = "\n\n";
                                            try {
                                                if (Integer.parseInt(clientResponse.get(0)) <= courses.size()) {
                                                    courseName = courses.get(Integer.parseInt(clientResponse.get(0))
                                                                             - 1).getCourseName();
                                                } else if (Integer.parseInt(clientResponse.get(0)) == courses.size()
                                                                                                      + 1) {
                                                    courseName = "\n\n\n";
                                                }
                                            } catch (Exception e) {
                                                System.out.println("defaulting course name");
                                                courseName = "\n\n";
                                            }

                                            if (courseExists(courses, courseName)) {
                                                currentCourse = findCourse(courses, courseName);
                                            } else if (!courseName.equals("\n\n\n")) {
                                                System.out.println(courseNotExist);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add(courseNotExist);
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


                                            while (currentCourse != null) {
                                                System.out.println();
                                                System.out.println(studentSelectCoursePrompt);
                                                String guiCourseName = currentCourse.getCourseName();
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add("Choose your action in " + guiCourseName);
                                                    }
                                                });
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("BUTTON");
                                                        add("Take Quiz");
                                                    }
                                                });
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("BUTTON");
                                                        add("View Grades");
                                                    }
                                                });
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("BUTTON");
                                                        add("View Quiz list");
                                                    }
                                                });
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("BUTTON");
                                                        add("Go back");
                                                    }
                                                });
                                                writer.writeObject(template);
                                                writer.flush();
                                                template = new Template();
                                                clientResponse = (ArrayList<String>) reader.readObject();
                                                System.out.println(clientResponse.toString());
                                                quizSelection = clientResponse.get(0);
                                                switch (quizSelection) {
                                                    case "1":
                                                        if (currentCourse.getSize() > 0) {
                                                            currentCourse.listQuizzes();
                                                            System.out.println("Input name of the Quiz to take");

                                                            clientResponse = new ArrayList<>();
                                                            while (clientResponse.size() == 0) {
                                                                template.add(new ArrayList<String>() {
                                                                    {
                                                                        add("REFRESH");
                                                                        add("refresh");
                                                                    }
                                                                });
                                                                currentCourse.listQuizzesGUIButtons(template);
                                                                template.add(new ArrayList<String>() {
                                                                    {
                                                                        add("BUTTON");
                                                                        add("CANCEL");
                                                                    }
                                                                });
                                                                writer.writeObject(template);
                                                                writer.flush();
                                                                template = new Template();
                                                                try {
                                                                    Thread.sleep(delay);
                                                                } catch (InterruptedException ex) {
                                                                    Thread.currentThread().interrupt();
                                                                }
                                                                clientResponse =
                                                                        (ArrayList<String>) reader.readObject();
                                                            }
                                                            reader.readObject();

                                                            String quizName = null;

                                                            try {
                                                                quizName = currentCourse.getQuizByNo(Integer.
                                                                                parseInt(clientResponse.get(0)))
                                                                        .getQuizName();
                                                            } catch (NullPointerException e) {
                                                                System.out.println("going back");
                                                            }

                                                            if (currentCourse.quizByName(quizName) != null) {
                                                                // take quiz by quizName
                                                                Quiz taken = currentCourse.quizByName(quizName);
                                                                try {
                                                                    if (!taken.isReady()) {
                                                                        throw new Exception();
                                                                    }
                                                                    Submission newSubmit = new Submission(taken,
                                                                            current);
                                                                    newSubmit.undergoSubmissionGUI(template, writer,
                                                                            reader, clientResponse);

                                                                    template = new Template();
                                                                    String submitQuestion = "";
                                                                    System.out.println("Do you want to" +
                                                                                       " submit? (Y/N)");
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add("Do you want to submit?");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("Yes");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("No");
                                                                        }
                                                                    });
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    clientResponse = (ArrayList<String>)
                                                                            reader.readObject();
                                                                    System.out.println(clientResponse.toString());

                                                                    submitQuestion = clientResponse.get(0);
                                                                    if (submitQuestion.equals("1")) {
                                                                        submissions.add(newSubmit);
                                                                    }

                                                                } catch (Exception e) {
                                                                    System.out.println("Unable to take quiz, " +
                                                                                       "quiz is incomplete");
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add("Unable to take quiz, quiz is" +
                                                                                " incomplete");
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
                                                                    clientResponse = (ArrayList<String>)
                                                                            reader.readObject();
                                                                }
                                                            } else {
                                                                System.out.println("Invalid name");
                                                            }
                                                        } else {
                                                            System.out.println("No quizzes to take");
                                                            template.add(new ArrayList<String>() {
                                                                {
                                                                    add("TEXT");
                                                                    add("No quizzes to take");
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
                                                        break;
                                                    case "2":
                                                        // view grades
                                                        if (submissions.size() > 0) {
                                                            currentCourse.listQuizzes();
                                                            System.out.println("Input name of the" +
                                                                               " Submission to view");
                                                            clientResponse = new ArrayList<>();
                                                            while (clientResponse.size() == 0) {
                                                                template.add(new ArrayList<String>() {
                                                                    {
                                                                        add("REFRESH");
                                                                        add("refresh");
                                                                    }
                                                                });
                                                                currentCourse.listQuizzesGUIButtons(template);
                                                                template.add(new ArrayList<String>() {
                                                                    {
                                                                        add("BUTTON");
                                                                        add("CANCEL");
                                                                    }
                                                                });
                                                                writer.writeObject(template);
                                                                writer.flush();
                                                                template = new Template();
                                                                try {
                                                                    Thread.sleep(delay);
                                                                } catch (InterruptedException ex) {
                                                                    Thread.currentThread().interrupt();
                                                                }
                                                                clientResponse =
                                                                        (ArrayList<String>) reader.readObject();
                                                            }
                                                            reader.readObject();

                                                            String quizName = null;
                                                            try {
                                                                quizName = currentCourse.getQuizByNo(Integer.
                                                                                parseInt(clientResponse.get(0)))
                                                                        .getQuizName();
                                                            } catch (NullPointerException e) {
                                                                System.out.println("no submissions");
                                                            }

                                                            if (findSubByName(quizName, submissions,
                                                                    current) != null) {
                                                                // take quiz by quizName
                                                                ArrayList<Submission> taken = findSubByName(quizName,
                                                                        submissions, current);
                                                                for (Submission i : taken) {
                                                                    String displayForGUI = ("press to display" +
                                                                                            " Submission " +
                                                                                            (taken.indexOf(i) + 1) + " / "
                                                                                            + taken.size());
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add(displayForGUI);
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("DISPLAY");
                                                                        }
                                                                    });
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    clientResponse = (ArrayList<String>)
                                                                            reader.readObject();

                                                                    i.displaySubmissionGUI(template,
                                                                            writer, reader, clientResponse);
                                                                    template = new Template();
                                                                }
                                                            } else {
                                                                System.out.println("No submission available");

                                                            }
                                                        } else {
                                                            System.out.println("No Submissions to view");
                                                            template.add(new ArrayList<String>() {
                                                                {
                                                                    add("TEXT");
                                                                    add("No Submissions to view");
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
                                                        break;
                                                    case "3":
                                                        // view quiz list
                                                        currentCourse.listQuizzes();
                                                        clientResponse = new ArrayList<>();
                                                        while (clientResponse.size() == 0) {
                                                            template.add(new ArrayList<String>() {
                                                                {
                                                                    add("REFRESH");
                                                                    add("refresh");
                                                                }
                                                            });
                                                            currentCourse.listQuizzesGUI(template);
                                                            template.add(new ArrayList<String>() {
                                                                {
                                                                    add("BUTTON");
                                                                    add("OK");
                                                                }
                                                            });
                                                            writer.writeObject(template);
                                                            writer.flush();
                                                            template = new Template();
                                                            try {
                                                                Thread.sleep(delay);
                                                            } catch (InterruptedException ex) {
                                                                Thread.currentThread().interrupt();
                                                            }
                                                            clientResponse = (ArrayList<String>) reader.readObject();
                                                        }
                                                        clientResponse = (ArrayList<String>) reader.readObject();
                                                        break;
                                                    case "4":
                                                        // go back to previous menu
                                                        studentPromptAnswer = "";
                                                        currentCourse = null;
                                                        break;
                                                }
                                            }
                                            if (currentCourse == null) {
                                                studentPromptAnswer = "";
                                            }
                                        }
                                        // List available courses
                                        if (studentPromptAnswer.equals("2")) {
                                            clientResponse = new ArrayList<>();
                                            while (clientResponse.size() == 0) {
                                                System.out.println("List of available courses: ");
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("REFRESH");
                                                        add("refresh");
                                                    }
                                                });
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add("List of available courses: ");
                                                    }
                                                });
                                                listCoursesGUI(courses, template);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("BUTTON");
                                                        add("OK");
                                                    }
                                                });
                                                writer.writeObject(template);
                                                writer.flush();
                                                //courses.add(new Course("test2"));
                                                template = new Template();
                                                try {
                                                    Thread.sleep(delay);
                                                } catch (InterruptedException ex) {
                                                    Thread.currentThread().interrupt();
                                                }
                                                clientResponse = (ArrayList<String>) reader.readObject();
                                            }
                                            clientResponse = (ArrayList<String>) reader.readObject();
                                        }
                                        // Delete account
                                        if (studentPromptAnswer.equals("3")) {
                                            current.setUsername("Inactive");
                                            current.setPassword(null);
                                            System.out.println(deleteAccountMessage);
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(deleteAccountMessage);
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

                                            studentPromptAnswer = "4";
                                            // redirect to log out
                                        }
                                        // log out
                                        if (!studentPromptAnswer.equals("4")) {
                                            System.out.println();
                                            System.out.println("Student: " + current.getUsername()
                                                               + " ID: " + current.getId());
                                            System.out.println(studentPrompt);
                                            toServerMessage = "Student: " + current.getUsername()
                                                              + " ID: " + current.getId();
                                            String finalToServerMessage2 = toServerMessage;
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(finalToServerMessage2);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Select Course");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("View course list");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Delete Account");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Logout");
                                                }
                                            });
                                            writer.writeObject(template);
                                            writer.flush();
                                            template = new Template();
                                            clientResponse = (ArrayList<String>) reader.readObject();

                                            // Get action answer
                                            studentPromptAnswer = clientResponse.get(0);
                                        }


                                    } while (!studentPromptAnswer.equals("4")); // repeat until user logs out


                                } else if (mainAnswer.equals("2")) {  // TEACHER BRANCH
                                    cont = false;

                                    // Ask for new / existing
                                    System.out.println(statusQuestion);
                                    System.out.println(statusQuestionPrompt);
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("TEXT");
                                            add(statusQuestion);
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("Existing user");
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("New user");
                                        }
                                    });
                                    writer.writeObject(template);
                                    writer.flush();
                                    template = new Template();
                                    clientResponse = (ArrayList<String>) reader.readObject();

                                    // New / existing response
                                    String statusAnswer = clientResponse.get(0);

                                    // Existing user
                                    boolean flag = true;
                                    Teacher current = null;
                                    while (flag || current == null) {
                                        if (statusAnswer.equals("1")) {
                                            System.out.println(enterId);
                                            System.out.println(enterPassword);
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(enterId);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("FIELD");
                                                    add("user input here");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(enterPassword);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("FIELD");
                                                    add("user input here");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Confirm");
                                                }
                                            });
                                            writer.writeObject(template);
                                            writer.flush();
                                            template = new Template();
                                            clientResponse = (ArrayList<String>) reader.readObject();

                                            String userId = clientResponse.get(1);
                                            String password = clientResponse.get(2);

                                            User temp = checkLoginGUI(userId, password, accounts, template);
                                            if (template.size() != 0) {
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

                                            if (temp instanceof Teacher) {
                                                current = (Teacher) checkLogin(userId, password, accounts);
                                            } else if (!invalidPassword(userId, password, accounts)) {
                                                System.out.println("ID does not belong to a teacher");
                                                System.out.println();
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add("ID does not belong to a teacher");
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
                                                current = null;
                                            } else {
                                                current = null;
                                            }

                                            if (current != null) {
                                                flag = false;
                                            }
                                        } else if (statusAnswer.equals("2")) {  // New user
                                            System.out.println(enterUsername);
                                            System.out.println(enterFullName);
                                            System.out.println(enterPassword);
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(enterUsername);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("FIELD");
                                                    add("user input here");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(enterFullName);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("FIELD");
                                                    add("user input here");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(enterPassword);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("FIELD");
                                                    add("user input here");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Confirm");
                                                }
                                            });
                                            writer.writeObject(template);
                                            writer.flush();
                                            template = new Template();
                                            clientResponse = (ArrayList<String>) reader.readObject();

                                            String username = clientResponse.get(1);
                                            String fullName = clientResponse.get(2);
                                            String password = clientResponse.get(3);

                                            current = new Teacher(username, password, fullName, accounts);
                                            accounts.add(current);
                                            System.out.println(idMessage + current.getId());
                                            toServerMessage = idMessage + current.getId();
                                            String finalToServerMessage = toServerMessage;
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(finalToServerMessage);
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

                                            flag = false;
                                        }
                                        if (flag) {
                                            if (!statusAnswer.equals("1")) {
                                                System.out.println(invalidInput);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add(invalidInput);
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
                                            // Ask for new / existing
                                            System.out.println(statusQuestion);
                                            System.out.println(statusQuestionPrompt);
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(statusQuestion);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Existing user");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("New user");
                                                }
                                            });
                                            writer.writeObject(template);
                                            writer.flush();
                                            template = new Template();
                                            clientResponse = (ArrayList<String>) reader.readObject();

                                            // New / existing response
                                            statusAnswer = clientResponse.get(0);
                                        }
                                    }


                                    // MAIN TEACHER LOOP
                                    // Ask teacher for action
                                    System.out.println();
                                    System.out.println("Teacher: " + current.getUsername()
                                                       + " ID: " + current.getId());
                                    System.out.println(teacherPrompt);
                                    toServerMessage = "Teacher: " + current.getUsername()
                                                      + " ID: " + current.getId();
                                    String finalToServerMessage1 = toServerMessage;
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("TEXT");
                                            add(finalToServerMessage1);
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("Add Course");
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("View course list");
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("Delete Course");
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("Select Course");
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("Delete Account");
                                        }
                                    });
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("BUTTON");
                                            add("Logout");
                                        }
                                    });
                                    writer.writeObject(template);
                                    writer.flush();
                                    template = new Template();
                                    clientResponse = (ArrayList<String>) reader.readObject();
                                    // Get action answer
                                    String teacherPromptAnswer = clientResponse.get(0);
                                    String quizSelection = "";
                                    do {
                                        // Course selection
                                        // Add course
                                        if (teacherPromptAnswer.equals("1")) {
                                            System.out.println(addCoursePrompt);
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(addCoursePrompt);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("FIELD");
                                                    add("User input here");
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
                                            String courseName = clientResponse.get(1);

                                            if (courseExists(courses, courseName)) {
                                                System.out.println(courseExistsMessage);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add(courseExistsMessage);
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
                                                courses.add(new Course(courseName));
                                                System.out.println(addedCourseMessage + courseName);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add(addedCourseMessage + courseName);
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
                                        // print courses option
                                        if (teacherPromptAnswer.equals("2")) {
                                            clientResponse = new ArrayList<>();
                                            while (clientResponse.size() == 0) {
                                                System.out.println("List of available courses: ");
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("REFRESH");
                                                        add("refresh");
                                                    }
                                                });
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add("List of available courses: ");
                                                    }
                                                });
                                                listCoursesGUI(courses, template);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("BUTTON");
                                                        add("OK");
                                                    }
                                                });
                                                writer.writeObject(template);
                                                writer.flush();
                                                //courses.add(new Course("test2"));
                                                template = new Template();
                                                try {
                                                    Thread.sleep(delay);
                                                } catch (InterruptedException ex) {
                                                    Thread.currentThread().interrupt();
                                                }
                                                clientResponse = (ArrayList<String>) reader.readObject();
                                            }
                                            clientResponse = (ArrayList<String>) reader.readObject();
                                        }

                                        //Delete Course option
                                        if (teacherPromptAnswer.equals("3")) {
                                            System.out.println(deleteCoursePrompt);
                                            clientResponse = new ArrayList<>();
                                            while (clientResponse.size() == 0) {
                                                System.out.println("List of available courses: ");
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("REFRESH");
                                                        add("refresh");
                                                    }
                                                });
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add(deleteCoursePrompt);
                                                    }
                                                });
                                                listCoursesGUIButtons(courses, template);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("BUTTON");
                                                        add("Cancel");
                                                    }
                                                });
                                                writer.writeObject(template);
                                                writer.flush();
                                                template = new Template();
                                                try {
                                                    Thread.sleep(delay);
                                                } catch (InterruptedException ex) {
                                                    Thread.currentThread().interrupt();
                                                }
                                                clientResponse = (ArrayList<String>) reader.readObject();
                                                System.out.println(clientResponse.toString());
                                            }
                                            reader.readObject();
                                            System.out.println(clientResponse.toString());

                                            String courseName = "\n\n";
                                            try {
                                                if (Integer.parseInt(clientResponse.get(0)) <= courses.size()) {
                                                    courseName = courses.get(Integer.parseInt(clientResponse.get(0))
                                                                             - 1).getCourseName();
                                                } else if (Integer.parseInt(clientResponse.get(0)) == courses.size()
                                                                                                      + 1) {
                                                    courseName = "\n\n\n";
                                                }
                                            } catch (Exception e) {
                                                System.out.println("defaulting course name");
                                                courseName = "\n\n";
                                            }

                                            if (courseExists(courses, courseName)) {
                                                courses.remove(findCourse(courses, courseName));
                                                System.out.println(removedCourseMessage + courseName);
                                                String finalCourseName = courseName;
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add(removedCourseMessage + finalCourseName);
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
                                            } else if (!courseName.equals("\n\n\n")) {
                                                System.out.println(courseNotExist);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add(courseNotExist);
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
                                        // choose course
                                        while (teacherPromptAnswer.equals("4")) {
                                            System.out.println("List of available courses: ");
                                            Course currentCourse = null;

                                            clientResponse = new ArrayList<>();
                                            while (clientResponse.size() == 0) {
                                                System.out.println("List of available courses: ");
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("REFRESH");
                                                        add("refresh");
                                                    }
                                                });
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add("Choose course");
                                                    }
                                                });
                                                listCoursesGUIButtons(courses, template);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("BUTTON");
                                                        add("Cancel");
                                                    }
                                                });
                                                writer.writeObject(template);
                                                writer.flush();
                                                template = new Template();
                                                try {
                                                    Thread.sleep(delay);
                                                } catch (InterruptedException ex) {
                                                    Thread.currentThread().interrupt();
                                                }
                                                clientResponse = (ArrayList<String>) reader.readObject();
                                                System.out.println(clientResponse.toString());
                                            }
                                            reader.readObject();
                                            System.out.println(clientResponse.toString());

                                            String courseName = "\n\n";
                                            try {
                                                if (Integer.parseInt(clientResponse.get(0)) <= courses.size()) {
                                                    courseName = courses.get(Integer.parseInt(clientResponse.get(0))
                                                                             - 1).getCourseName();
                                                } else if (Integer.parseInt(clientResponse.get(0)) == courses.size()
                                                                                                      + 1) {
                                                    courseName = "\n\n\n";
                                                }
                                            } catch (Exception e) {
                                                System.out.println("defaulting course name");
                                                courseName = "\n\n";
                                            }

                                            if (courseExists(courses, courseName)) {
                                                currentCourse = findCourse(courses, courseName);
                                            } else if (!courseName.equals("\n\n\n")) {
                                                System.out.println(courseNotExist);
                                                template.add(new ArrayList<String>() {
                                                    {
                                                        add("TEXT");
                                                        add(courseNotExist);
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

                                            if (currentCourse != null) {
                                                while (!quizSelection.equals("5")) {
                                                    System.out.println();
                                                    System.out.println(teacherSelectCoursePrompt);
                                                    String guiCourseName = currentCourse.getCourseName();
                                                    template.add(new ArrayList<String>() {
                                                        {
                                                            add("TEXT");
                                                            add("Choose your action in " + guiCourseName);
                                                        }
                                                    });
                                                    template.add(new ArrayList<String>() {
                                                        {
                                                            add("BUTTON");
                                                            add("Delete Quiz");
                                                        }
                                                    });
                                                    template.add(new ArrayList<String>() {
                                                        {
                                                            add("BUTTON");
                                                            add("Edit Quiz");
                                                        }
                                                    });
                                                    template.add(new ArrayList<String>() {
                                                        {
                                                            add("BUTTON");
                                                            add("Create Quiz");
                                                        }
                                                    });
                                                    template.add(new ArrayList<String>() {
                                                        {
                                                            add("BUTTON");
                                                            add("Display/grade Quiz");
                                                        }
                                                    });
                                                    template.add(new ArrayList<String>() {
                                                        {
                                                            add("BUTTON");
                                                            add("Go back");
                                                        }
                                                    });
                                                    template.add(new ArrayList<String>() {
                                                        {
                                                            add("BUTTON");
                                                            add("List Quizzes");
                                                        }
                                                    });
                                                    template.add(new ArrayList<String>() {
                                                        {
                                                            add("BUTTON");
                                                            add("Print Quiz");
                                                        }
                                                    });
                                                    writer.writeObject(template);
                                                    writer.flush();
                                                    template = new Template();
                                                    clientResponse = (ArrayList<String>) reader.readObject();
                                                    System.out.println(clientResponse.toString());
                                                    quizSelection = clientResponse.get(0);
                                                    switch (quizSelection) {
                                                        case "1":
                                                            // delete quiz
                                                            if (currentCourse.getSize() > 0) {
                                                                currentCourse.listQuizzes();
                                                                System.out.println("Input name of the Quiz" +
                                                                                   " to delete");

                                                                clientResponse = new ArrayList<>();
                                                                while (clientResponse.size() == 0) {
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("REFRESH");
                                                                            add("refresh");
                                                                        }
                                                                    });
                                                                    currentCourse.listQuizzesGUIButtons(template);
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("CANCEL");
                                                                        }
                                                                    });
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    try {
                                                                        Thread.sleep(delay);
                                                                    } catch (InterruptedException ex) {
                                                                        Thread.currentThread().interrupt();
                                                                    }
                                                                    clientResponse =
                                                                            (ArrayList<String>) reader.readObject();
                                                                }
                                                                reader.readObject();
                                                                System.out.println(clientResponse.toString());
                                                                try {
                                                                    currentCourse.removeQuizByNo(
                                                                            Integer.parseInt(clientResponse.get(0)));
                                                                } catch (Exception e) {
                                                                    System.out.println("unable to delete quiz");
                                                                }
                                                            } else {
                                                                System.out.println("No quizzes to delete");
                                                                template.add(new ArrayList<String>() {
                                                                    {
                                                                        add("TEXT");
                                                                        add("No quizzes to delete");
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
                                                                clientResponse = (ArrayList<String>)
                                                                        reader.readObject();
                                                            }
                                                            break;
                                                        case "2":
                                                            // edit quiz
                                                            if (currentCourse.getSize() > 0) {
                                                                currentCourse.listQuizzes();
                                                                System.out.println("Input name of the Quiz to edit");

                                                                clientResponse = new ArrayList<>();
                                                                while (clientResponse.size() == 0) {
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("REFRESH");
                                                                            add("refresh");
                                                                        }
                                                                    });
                                                                    currentCourse.listQuizzesGUIButtons(template);
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    try {
                                                                        Thread.sleep(delay);
                                                                    } catch (InterruptedException ex) {
                                                                        Thread.currentThread().interrupt();
                                                                    }
                                                                    clientResponse =
                                                                            (ArrayList<String>) reader.readObject();
                                                                }
                                                                reader.readObject();

                                                                String quizName = currentCourse.getQuizByNo(Integer.
                                                                                parseInt(clientResponse.get(0)))
                                                                        .getQuizName();
                                                                if (currentCourse.quizByName(quizName) != null) {
                                                                    Quiz edited = currentCourse.quizByName(quizName);
                                                                    System.out.println("working on: "
                                                                                       + edited.getQuizName());

                                                                    // randomize Q?
                                                                    System.out.println("Randomize this quiz (Y/N)");
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add("Randomize this quiz?");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("Yes");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("No");
                                                                        }
                                                                    });
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    clientResponse = (ArrayList<String>)
                                                                            reader.readObject();
                                                                    String randomizeA = clientResponse.get(0);
                                                                    if (randomizeA.equals("1")) {
                                                                        edited.setRandomized(true);
                                                                        System.out.println("Randomizing questions");
                                                                    } else {
                                                                        edited.setRandomized(false);
                                                                        System.out.println("Questions" +
                                                                                           " not randomized");
                                                                    }

                                                                    // Q timing
                                                                    System.out.println("How long" +
                                                                                       " should be this quiz?" +
                                                                                       " (in minutes)\n0 for unlimited");
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add("How long should be this quiz?");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add("(in minutes), 0 for unlimited");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("FIELD");
                                                                            add("user input here");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("CONFIRM");
                                                                        }
                                                                    });
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    clientResponse = (ArrayList<String>)
                                                                            reader.readObject();
                                                                    String timeLimit = clientResponse.get(1);
                                                                    int timeLimitInt = 0;
                                                                    try {
                                                                        timeLimitInt = Integer.parseInt(timeLimit);
                                                                        System.out.println("Quiz will last "
                                                                                           + timeLimit +
                                                                                           " minutes");
                                                                    } catch (NumberFormatException e) {
                                                                        System.out.println("Unable to" +
                                                                                           " distinguish time," +
                                                                                           " setting it to unlimited");
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("TEXT");
                                                                                add("Unable to distinguish time");
                                                                            }
                                                                        });
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("TEXT");
                                                                                add("Setting time  to unlimited");
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
                                                                        clientResponse = (ArrayList<String>)
                                                                                reader.readObject();
                                                                    }
                                                                    edited.setTimeLimit(timeLimitInt);

                                                                    // Q deadline
                                                                    System.out.println("In how many days is the" +
                                                                                       " deadline of this quiz?");
                                                                    System.out.println("In how many hours is the" +
                                                                                       " deadline of this quiz?");
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add("In how many days is the" +
                                                                                " deadline of this quiz?");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("FIELD");
                                                                            add("user input here");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add("In how many hours is the" +
                                                                                " deadline of this quiz?");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("FIELD");
                                                                            add("user input here");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("CONFIRM");
                                                                        }
                                                                    });
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    clientResponse = (ArrayList<String>)
                                                                            reader.readObject();
                                                                    String deadDay = clientResponse.get(1);
                                                                    String deadHour = clientResponse.get(2);

                                                                    int deadHourInt = 1;
                                                                    int deadDayInt = 1;
                                                                    int combinedInt = 25;
                                                                    try {
                                                                        deadHourInt = Integer.parseInt(deadHour);
                                                                        deadDayInt = Integer.parseInt(deadDay);
                                                                        combinedInt = 24 * deadDayInt + deadHourInt;
                                                                    } catch (NumberFormatException e) {
                                                                        System.out.println("Unable" +
                                                                                           " to distinguish time," +
                                                                                           " setting deadline in 1 day 1 hour");
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("TEXT");
                                                                                add("Unable to distinguish time");
                                                                            }
                                                                        });
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("TEXT");
                                                                                add("Setting deadline" +
                                                                                    " in 1 day 1 hour");
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
                                                                        clientResponse = (ArrayList<String>)
                                                                                reader.readObject();
                                                                    }
                                                                    int deadline = 0;
                                                                    DateTimeFormatter dtf =
                                                                            DateTimeFormatter.ofPattern(
                                                                                    "yyyy/MM/dd HH:mm:ss");
                                                                    LocalDateTime qDeadline = LocalDateTime.now()
                                                                            .plusHours(combinedInt);

                                                                    System.out.println("Quiz deadline is "
                                                                                       + combinedInt
                                                                                       + " hours\nOn: " + dtf.format(qDeadline));
                                                                    edited.setDeadline(qDeadline);
                                                                    int finalCombinedInt = combinedInt;
                                                                    String finalDate = dtf.format(qDeadline)
                                                                            .toString();
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add("Quiz deadline is " + finalCombinedInt
                                                                                + " hours");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add("On: " + finalDate);
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
                                                                    clientResponse = (ArrayList<String>)
                                                                            reader.readObject();

                                                                    // clear Q?
                                                                    System.out.println("Clear all questions? (Y/N)");
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add("Clear all questions?");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("Yes");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("No");
                                                                        }
                                                                    });
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    clientResponse = (ArrayList<String>)
                                                                            reader.readObject();

                                                                    String clearQ = clientResponse.get(0);
                                                                    if (clearQ.equals("1")) {
                                                                        edited.setQuestions(new
                                                                                ArrayList<Question>());
                                                                        edited.setReady(false);
                                                                        System.out.println("Cleared questions");
                                                                    } else {
                                                                        System.out.println("Questions not cleared");
                                                                    }

                                                                    System.out.println("Do you want to" +
                                                                                       " add a question to" +
                                                                                       " this quiz? (Y/N)");
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("TEXT");
                                                                            add("Add a question to this quiz?");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("Yes");
                                                                        }
                                                                    });
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("No");
                                                                        }
                                                                    });
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    clientResponse = (ArrayList<String>)
                                                                            reader.readObject();

                                                                    String adding = clientResponse.get(0);
                                                                    while (adding.equals("1")) {
                                                                        System.out.println("Name the question: ");
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("TEXT");
                                                                                add("Name the question");
                                                                            }
                                                                        });
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("FIELD");
                                                                                add("User input here");
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
                                                                        clientResponse = (ArrayList<String>)
                                                                                reader.readObject();

                                                                        String questionTitle = clientResponse.get(1);
                                                                        Question newQ;
                                                                        if (edited.addQuestion(newQ =
                                                                                new Question(questionTitle))) {

                                                                            System.out.println("Enter new" +
                                                                                               " possible answer or" +
                                                                                               " finish with" +
                                                                                               " empty input (enter)");
                                                                            template.add(new ArrayList<String>() {
                                                                                {
                                                                                    add("TEXT");
                                                                                    add("Enter new possible answer");
                                                                                }
                                                                            });
                                                                            template.add(new ArrayList<String>() {
                                                                                {
                                                                                    add("TEXT");
                                                                                    add("(Leave blank to finish)");
                                                                                }
                                                                            });
                                                                            template.add(new ArrayList<String>() {
                                                                                {
                                                                                    add("FIELD");
                                                                                    add("User input here");
                                                                                }
                                                                            });
                                                                            template.add(new ArrayList<String>() {
                                                                                {
                                                                                    add("BUTTON");
                                                                                    add("CONFIRM");
                                                                                }
                                                                            });
                                                                            writer.writeObject(template);
                                                                            writer.flush();
                                                                            template = new Template();
                                                                            clientResponse = (ArrayList<String>)
                                                                                    reader.readObject();
                                                                            System.out.println(clientResponse.get(1));

                                                                            String choice = clientResponse.get(1);
                                                                            while (!choice.equals("") ||
                                                                                   newQ.getChoices().size() < 1) {
                                                                                if (!newQ.addChoiceGUI(choice)) {
                                                                                    template.add
                                                                                            (new ArrayList<String>() {
                                                                                                {
                                                                                                    add("TEXT");
                                                                                                    add("This choice" +
                                                                                                        " already" +
                                                                                                        " exists");
                                                                                                }
                                                                                            });
                                                                                    template.add(
                                                                                            new ArrayList<String>() {
                                                                                                {
                                                                                                    add("BUTTON");
                                                                                                    add("OK");
                                                                                                }
                                                                                            });
                                                                                    writer.writeObject(template);
                                                                                    writer.flush();
                                                                                    template = new Template();
                                                                                    clientResponse
                                                                                            = (ArrayList<String>)
                                                                                            reader.readObject();
                                                                                }
                                                                                System.out.println("Enter new " +
                                                                                                   "possible answer or" +
                                                                                                   " finish with empty " +
                                                                                                   "input (enter)");
                                                                                template.add(new ArrayList<String>() {
                                                                                    {
                                                                                        add("TEXT");
                                                                                        add("Enter new " +
                                                                                            "possible answer");
                                                                                    }
                                                                                });
                                                                                template.add(new ArrayList<String>() {
                                                                                    {
                                                                                        add("TEXT");
                                                                                        add("(Leave blank" +
                                                                                            " to finish)");
                                                                                    }
                                                                                });
                                                                                template.add(new ArrayList<String>() {
                                                                                    {
                                                                                        add("FIELD");
                                                                                        add("User input here");
                                                                                    }
                                                                                });
                                                                                template.add(new ArrayList<String>() {
                                                                                    {
                                                                                        add("BUTTON");
                                                                                        add("CONFIRM");
                                                                                    }
                                                                                });
                                                                                writer.writeObject(template);
                                                                                writer.flush();
                                                                                template = new Template();
                                                                                clientResponse = (ArrayList<String>)
                                                                                        reader.readObject();
                                                                                System.out.println(clientResponse.
                                                                                        get(1));

                                                                                choice = clientResponse.get(1);
                                                                            }
                                                                            while (true) {
                                                                                newQ.printChoices();
                                                                                System.out.println("enter the " +
                                                                                                   "correct answer");
                                                                                template.add(new ArrayList<String>() {
                                                                                    {
                                                                                        add("TEXT");
                                                                                        add("Choose the " +
                                                                                            "correct answer");
                                                                                    }
                                                                                });
                                                                                newQ.printChoicesGUIButtons(template);
                                                                                writer.writeObject(template);
                                                                                writer.flush();
                                                                                template = new Template();
                                                                                clientResponse = (ArrayList<String>)
                                                                                        reader.readObject();

                                                                                String answer = newQ.getChoiceByNo(
                                                                                        Integer.parseInt(
                                                                                                clientResponse
                                                                                                        .get(0)));
                                                                                if (newQ.containsChoice(answer)) {
                                                                                    newQ.setCorrectAnswer(answer);
                                                                                    break;
                                                                                } else {
                                                                                    System.out.println("Try again");
                                                                                }
                                                                            }
                                                                        } else {
                                                                            template.add(new ArrayList<String>() {
                                                                                {
                                                                                    add("TEXT");
                                                                                    add("This name is already used");
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
                                                                            clientResponse = (ArrayList<String>)
                                                                                    reader.readObject();
                                                                        }

                                                                        System.out.println("Do you want to" +
                                                                                           " add a question to" +
                                                                                           " this quiz? (Y/N)");
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("TEXT");
                                                                                add("Add a question to this quiz?");
                                                                            }
                                                                        });
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("BUTTON");
                                                                                add("Yes");
                                                                            }
                                                                        });
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("BUTTON");
                                                                                add("No");
                                                                            }
                                                                        });
                                                                        writer.writeObject(template);
                                                                        writer.flush();
                                                                        template = new Template();
                                                                        clientResponse = (ArrayList<String>)
                                                                                reader.readObject();
                                                                        adding = clientResponse.get(0);
                                                                    }
                                                                    if (edited.getQuestions().size() > 0) {
                                                                        edited.setReady(true);
                                                                    }


                                                                }
                                                            } else {
                                                                System.out.println("No quizzes to edit");
                                                                template.add(new ArrayList<String>() {
                                                                    {
                                                                        add("TEXT");
                                                                        add("No quizzes to edit");
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
                                                                clientResponse = (ArrayList<String>)
                                                                        reader.readObject();
                                                            }
                                                            break;
                                                        case "3":
                                                            // create quiz
                                                            System.out.println("Input name of the quiz");
                                                            template.add(new ArrayList<String>() {
                                                                {
                                                                    add("TEXT");
                                                                    add("Input name of the quiz");
                                                                }
                                                            });
                                                            template.add(new ArrayList<String>() {
                                                                {
                                                                    add("FIELD");
                                                                    add("User input here");
                                                                }
                                                            });
                                                            template.add(new ArrayList<String>() {
                                                                {
                                                                    add("BUTTON");
                                                                    add("CONFIRM");
                                                                }
                                                            });
                                                            writer.writeObject(template);
                                                            writer.flush();
                                                            template = new Template();
                                                            clientResponse = (ArrayList<String>)
                                                                    reader.readObject();
                                                            String quizName = clientResponse.get(1);
                                                            Quiz tempQuiz = new Quiz(quizName);
                                                            if (!currentCourse.addQuiz(tempQuiz)) {
                                                                template.add(new ArrayList<String>() {
                                                                    {
                                                                        add("TEXT");
                                                                        add("This course already exists");
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
                                                                clientResponse = (ArrayList<String>)
                                                                        reader.readObject();
                                                            }
                                                            break;
                                                        case "4":
                                                            // grade quiz
                                                            if (submissions.size() > 0) {
                                                                currentCourse.listQuizzes();
                                                                System.out.println("Input name of" +
                                                                                   " the Submission to view");
                                                                clientResponse = new ArrayList<>();
                                                                while (clientResponse.size() == 0) {
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("REFRESH");
                                                                            add("refresh");
                                                                        }
                                                                    });
                                                                    currentCourse.listQuizzesGUIButtons(template);
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("OK");
                                                                        }
                                                                    });
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    try {
                                                                        Thread.sleep(delay);
                                                                    } catch (InterruptedException ex) {
                                                                        Thread.currentThread().interrupt();
                                                                    }
                                                                    clientResponse =
                                                                            (ArrayList<String>) reader.readObject();
                                                                }
                                                                reader.readObject();

                                                                quizName = null;
                                                                try {
                                                                    quizName = currentCourse.getQuizByNo(Integer.
                                                                            parseInt(clientResponse
                                                                                    .get(0))).getQuizName();
                                                                } catch (NullPointerException e) {
                                                                    System.out.println("no submissions");
                                                                }

                                                                if (findSubByName(quizName, submissions,
                                                                        null) != null) {
                                                                    // take quiz by quizName
                                                                    ArrayList<Submission> taken =
                                                                            findSubByName(quizName,
                                                                                    submissions, null);
                                                                    for (Submission i : taken) {
                                                                        String displayForGUI = ("press to display" +
                                                                                                " Submission " +
                                                                                                (taken.indexOf(i) + 1) + " / " +
                                                                                                taken.size());
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("TEXT");
                                                                                add(displayForGUI);
                                                                            }
                                                                        });
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("BUTTON");
                                                                                add("DISPLAY");
                                                                            }
                                                                        });
                                                                        writer.writeObject(template);
                                                                        writer.flush();
                                                                        template = new Template();
                                                                        clientResponse = (ArrayList<String>)
                                                                                reader.readObject();

                                                                        i.displaySubmissionGUI(template, writer,
                                                                                reader, clientResponse);
                                                                        template = new Template();

                                                                        String gradeQuestion = "";
                                                                        System.out.println("Do you want to grade" +
                                                                                           " this quiz? (Y/N)");
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("TEXT");
                                                                                add("Do you want to" +
                                                                                    " grade this quiz?");
                                                                            }
                                                                        });
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("BUTTON");
                                                                                add("Yes");
                                                                            }
                                                                        });
                                                                        template.add(new ArrayList<String>() {
                                                                            {
                                                                                add("BUTTON");
                                                                                add("No");
                                                                            }
                                                                        });
                                                                        writer.writeObject(template);
                                                                        writer.flush();
                                                                        template = new Template();
                                                                        clientResponse = (ArrayList<String>)
                                                                                reader.readObject();

                                                                        gradeQuestion = clientResponse.get(0);
                                                                        if (gradeQuestion.equals("1")) {
                                                                            try {
                                                                                i.setGrades(new ArrayList<Integer>());
                                                                                for (int a = 0; a <
                                                                                                i.getStudentAnswers().size();
                                                                                     a++) {
                                                                                    String promptGui1 = ("What is" +
                                                                                                         " the grade for question "
                                                                                                         + (a + 1) + "?");
                                                                                    try {
                                                                                        template.add(new ArrayList
                                                                                                <String>() {
                                                                                            {
                                                                                                add("TEXT");
                                                                                                add(promptGui1);
                                                                                            }
                                                                                        });
                                                                                        template.add(new ArrayList
                                                                                                <String>() {
                                                                                            {
                                                                                                add("TEXT");
                                                                                                add("(by display" +
                                                                                                    " order)");
                                                                                            }
                                                                                        });
                                                                                        template.add(new ArrayList
                                                                                                <String>() {
                                                                                            {
                                                                                                add("FIELD");
                                                                                                add("User input" +
                                                                                                    " here");
                                                                                            }
                                                                                        });
                                                                                        template.add(new ArrayList
                                                                                                <String>() {
                                                                                            {
                                                                                                add("BUTTON");
                                                                                                add("CONFIRM");
                                                                                            }
                                                                                        });
                                                                                        writer.writeObject(template);
                                                                                        writer.flush();
                                                                                        template = new Template();
                                                                                        clientResponse
                                                                                                = (ArrayList<String>)
                                                                                                reader.readObject();

                                                                                        String grade =
                                                                                                clientResponse.get(1);
                                                                                        int gradeInt =
                                                                                                Integer.parseInt(
                                                                                                        grade);
                                                                                        i.addGrade(gradeInt);
                                                                                    } catch (
                                                                                            NumberFormatException e) {
                                                                                        System.out.println(
                                                                                                "Not an number, " +
                                                                                                "defaulting" +
                                                                                                " to" +
                                                                                                " grade 0");
                                                                                        template.add(
                                                                                                new ArrayList<String>
                                                                                                        () {
                                                                                                    {
                                                                                                        add("TEXT");
                                                                                                        add("Not an " +
                                                                                                            "number, " +
                                                                                                            "defaulting" +
                                                                                                            " to " +
                                                                                                            "grade 0");
                                                                                                    }
                                                                                                });
                                                                                        template.add(
                                                                                                new ArrayList<String>
                                                                                                        () {
                                                                                                    {
                                                                                                        add("BUTTON");
                                                                                                        add("OK");
                                                                                                    }
                                                                                                });
                                                                                        writer.writeObject(template);
                                                                                        writer.flush();
                                                                                        template = new Template();
                                                                                        clientResponse = (ArrayList
                                                                                                <String>) reader.
                                                                                                readObject();

                                                                                        i.addGrade(0);
                                                                                    }
                                                                                }
                                                                            } catch (Exception e) {
                                                                                System.out.println("Unable" +
                                                                                                   " to grade quiz");
                                                                            }
                                                                        } else {
                                                                            System.out.println("Skipping grading");
                                                                        }

                                                                    }
                                                                } else {
                                                                    System.out.println("No submission available");
                                                                }
                                                            } else {
                                                                System.out.println("No Submissions to view");
                                                                template.add(new ArrayList<String>() {
                                                                    {
                                                                        add("TEXT");
                                                                        add("No Submissions to view");
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
                                                                clientResponse = (ArrayList<String>)
                                                                        reader.readObject();
                                                            }
                                                            break;
                                                        case "5":
                                                            // return to course selection
                                                            teacherPromptAnswer = "";
                                                            break;
                                                        case "6":
                                                            // list quizzes
                                                            currentCourse.listQuizzes();
                                                            clientResponse = new ArrayList<>();
                                                            while (clientResponse.size() == 0) {
                                                                template.add(new ArrayList<String>() {
                                                                    {
                                                                        add("REFRESH");
                                                                        add("refresh");
                                                                    }
                                                                });
                                                                currentCourse.listQuizzesGUI(template);
                                                                template.add(new ArrayList<String>() {
                                                                    {
                                                                        add("BUTTON");
                                                                        add("OK");
                                                                    }
                                                                });
                                                                writer.writeObject(template);
                                                                writer.flush();
                                                                template = new Template();
                                                                try {
                                                                    Thread.sleep(delay);
                                                                } catch (InterruptedException ex) {
                                                                    Thread.currentThread().interrupt();
                                                                }
                                                                clientResponse = (ArrayList<String>)
                                                                        reader.readObject();
                                                            }
                                                            clientResponse = (ArrayList<String>)
                                                                    reader.readObject();
                                                            break;
                                                        case "7":
                                                            // print quiz
                                                            if (currentCourse.getSize() > 0) {
                                                                currentCourse.listQuizzes();
                                                                System.out.println("Input name of" +
                                                                                   " the Quiz to print");

                                                                clientResponse = new ArrayList<>();
                                                                while (clientResponse.size() == 0) {
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("REFRESH");
                                                                            add("refresh");
                                                                        }
                                                                    });
                                                                    currentCourse.listQuizzesGUIButtons(template);
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("CANCEL");
                                                                        }
                                                                    });
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    try {
                                                                        Thread.sleep(delay);
                                                                    } catch (InterruptedException ex) {
                                                                        Thread.currentThread().interrupt();
                                                                    }
                                                                    clientResponse =
                                                                            (ArrayList<String>) reader.readObject();
                                                                }
                                                                reader.readObject();
                                                                System.out.println(clientResponse.toString());

                                                                quizName = null;
                                                                try {
                                                                    quizName = currentCourse.getQuizByNo(Integer.
                                                                                    parseInt(clientResponse.get(0)))
                                                                            .getQuizName();
                                                                } catch (NullPointerException e) {
                                                                    System.out.println("going back");
                                                                }

                                                                if (currentCourse.quizByName(quizName) != null) {
                                                                    currentCourse.quizByName(quizName).printQuiz();
                                                                    currentCourse.quizByName(quizName)
                                                                            .printQuizGUI(template);
                                                                    template.add(new ArrayList<String>() {
                                                                        {
                                                                            add("BUTTON");
                                                                            add("OK");
                                                                        }
                                                                    });
                                                                    writer.writeObject(template);
                                                                    writer.flush();
                                                                    template = new Template();
                                                                    clientResponse = (ArrayList<String>) reader
                                                                            .readObject();
                                                                } else {
                                                                    System.out.println("Invalid name");
                                                                }
                                                            } else {
                                                                System.out.println("No quizzes to print");
                                                                template.add(new ArrayList<String>() {
                                                                    {
                                                                        add("TEXT");
                                                                        add("No quizzes to print");
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
                                                                clientResponse = (ArrayList<String>)
                                                                        reader.readObject();
                                                            }
                                                            break;
                                                    }
                                                }
                                                quizSelection = "";
                                            } else {
                                                teacherPromptAnswer = "";
                                            }


                                        }
                                        // Delete account
                                        if (teacherPromptAnswer.equals("5")) {
                                            current.setUsername("Inactive");
                                            current.setPassword(null);
                                            System.out.println(deleteAccountMessage);
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(deleteAccountMessage);
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

                                            teacherPromptAnswer = "6";

                                        }
                                        // print menu
                                        if (!teacherPromptAnswer.equals("6")) {
                                            System.out.println();
                                            System.out.println("Teacher: " + current.getUsername()
                                                               + " ID: " + current.getId());
                                            System.out.println(teacherPrompt);
                                            toServerMessage = "Teacher: " + current.getUsername()
                                                              + " ID: " + current.getId();
                                            String finalToServerMessage3 = toServerMessage;
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("TEXT");
                                                    add(finalToServerMessage3);
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Add Course");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("View course list");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Delete Course");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Select Course");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Delete Account");
                                                }
                                            });
                                            template.add(new ArrayList<String>() {
                                                {
                                                    add("BUTTON");
                                                    add("Logout");
                                                }
                                            });
                                            writer.writeObject(template);
                                            writer.flush();
                                            template = new Template();
                                            clientResponse = (ArrayList<String>) reader.readObject();
                                            teacherPromptAnswer = clientResponse.get(0);
                                        }


                                    } while (!teacherPromptAnswer.equals("6"));
                                } else {  // Goodbye the user
                                    System.out.println(invalidInput);
                                    template.add(new ArrayList<String>() {
                                        {
                                            add("TEXT");
                                            add(invalidInput);
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
                            System.out.println(goodbyePrompt);
                            System.out.println();
                            template.add(new ArrayList<String>() {
                                {
                                    add("TEXT");
                                    add(goodbyePrompt);
                                }
                            });
                            template.add(new ArrayList<String>() {
                                {
                                    add("BUTTON");
                                    add("Goodbye");
                                }
                            });
                            writer.writeObject(template);
                            writer.flush();
                            template = new Template();
                            clientResponse = (ArrayList<String>) reader.readObject();

                            // Save accounts, courses and submissions back to file
                            if (accounts != null) {
                                saveUsers(accounts);
                            }
                            if (courses != null) {
                                saveCourses(courses);
                            }
                            if (submissions != null) {
                                saveSubmits(submissions);
                            }
                            // ask the user whether to run the program again
                            System.out.println("Do you want to run again? (Y/N)");
                            template.add(new ArrayList<String>() {
                                {
                                    add("TEXT");
                                    add("Run again?");
                                }
                            });
                            template.add(new ArrayList<String>() {
                                {
                                    add("BUTTON");
                                    add("Yes");
                                }
                            });
                            template.add(new ArrayList<String>() {
                                {
                                    add("BUTTON");
                                    add("No");
                                }
                            });
                            writer.writeObject(template);
                            writer.flush();
                            template = new Template();
                            clientResponse = (ArrayList<String>) reader.readObject();
                            String mainLoop = clientResponse.get(0);
                            if (!mainLoop.equals("1")) {
                                serverRunning = false;
                                break;
                            }

                        } while (true);
                    } catch (Exception e) {
                        System.out.println("End of communication");
                        serverRunning = false;
                    }
                }

                System.out.println("closing the connection");
                if (accounts != null) {
                    saveUsers(accounts);
                }
                if (courses != null) {
                    saveCourses(courses);
                }
                if (submissions != null) {
                    saveSubmits(submissions);
                }
                reader.close();
                writer.close();

            } catch (IOException e) {
                System.out.println("saving");
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    if (writer != null) {
                        writer.close();
                        client.close();
                    }
                    if (accounts != null) {
                        saveUsers(accounts);
                    }
                    if (courses != null) {
                        saveCourses(courses);
                    }
                    if (submissions != null) {
                        saveSubmits(submissions);
                    }
                } catch (IOException e) {
                    System.out.println("problem closing the server");
                }
            }
        }
    }


    // Takes String name of the course and array of all curses
    // Returns Course object matching this name or null if not found
    public static Course findCourse(ArrayList<Course> coursesHere, String courseName) {

        for (Course e : coursesHere) {
            if (e.getCourseName().equals(courseName)) {
                return e;
            }
        }
        return null;
    }

    // Handles user selecting the course
    public static Course selectCourse(ArrayList<Course> coursesHere, Scanner scanner) {
        listCourses(coursesHere);
        if (coursesHere.size() > 0) {
            System.out.println("Provide the name of your course");
            boolean correct = false;
            String courseName = "";
            do {
                courseName = scanner.nextLine();
                correct = courseExists(coursesHere, courseName);
                if (correct) {
                    for (Course i : coursesHere) {
                        if (i.getCourseName().equals(courseName)) {
                            return i;
                        }
                    }
                } else {
                    System.out.println("Try again");
                }
            } while (!correct);
        }
        return null;
    }

    // Check if the course with given name exists in array of courses
    public static boolean courseExists(ArrayList<Course> coursesHere, String courseName) {
        boolean exists = false;

        for (Course e : coursesHere) {
            if (e.getCourseName().equals(courseName)) {
                exists = true;
            }
        }
        return exists;
    }

    // lists the courses in the array of courses
    public static void listCourses(ArrayList<Course> coursesHere) {
        if (coursesHere.size() > 0) {
            for (Course e : coursesHere) {
                System.out.println(e.getCourseName());
            }
        } else {
            System.out.println("No Courses");
        }
    }

    // lists the courses in the array of courses
    public static void listCoursesGUI(ArrayList<Course> coursesHere, Template template) {
        if (coursesHere.size() > 0) {
            for (Course e : coursesHere) {
                System.out.println(e.getCourseName());
                template.add(new ArrayList<String>() {
                    {
                        add("TEXT");
                        add(e.getCourseName());
                    }
                });
            }
        } else {
            System.out.println("No Courses");
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add("No Courses");
                }
            });
        }

    }

    public static void listCoursesGUIButtons(ArrayList<Course> coursesHere, Template template) {
        if (coursesHere.size() > 0) {
            for (Course e : coursesHere) {
                System.out.println(e.getCourseName());
                template.add(new ArrayList<String>() {
                    {
                        add("BUTTON");
                        add(e.getCourseName());
                    }
                });
            }
        }
//        else {
//            System.out.println("No Courses");
//            template.add(new ArrayList<String>() { {
//                add("TEXT");
//                add("No Courses");
//            }  } );
//        }

    }

    // save current user objects to file in local directory
    public static void saveUsers(ArrayList<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("UserData", false))) {
            for (Object current : users) {
                oos.writeObject(current);
            }
            oos.close();
            System.out.println("The User(s) were successfully written to a file");
        } catch (Exception e) {
            System.out.println("Error saving to file");
        }
    }

    // read current User objects from file in local directory
    // resets the file in case objects are not compatible
    public static ArrayList<User> readUsers() {
        ArrayList<User> users = new ArrayList<>();

        File file = new File("UserData");
        if (file.exists()) {

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("UserData"))) {
                boolean cont = true;
                while (cont) {
                    Object current = null;
                    try {
                        current = ois.readObject();
                    } catch (ClassNotFoundException e) {
                        System.out.println("wrong class");
                    } catch (java.io.InvalidClassException e) {
                        System.out.println("invalid class");
                        file.delete();
                        users = new ArrayList<User>();
                        return users;
                    } catch (java.io.StreamCorruptedException e) {
                        System.out.println("corrupted class");
                    } catch (java.io.EOFException e) {
                        System.out.println("success reading Users from file");
                    }
                    if (current != null)
                        users.add((User) current);
                    else
                        cont = false;
                }
                ois.close();
                return users;
            } catch (Exception e) {
                System.out.println("Error reading from file");
                return users;
            }
        }
        return users;
    }

    // save current courses objects to file in local directory
    public static void saveCourses(ArrayList<Course> coursesHere) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("CourseData",
                false))) {
            for (Object current : coursesHere) {
                oos.writeObject(current);
            }
            oos.close();
            System.out.println("The Course(s) were successfully written to a file");
        } catch (Exception e) {
            System.out.println("Error saving to file");
        }
    }

    // read current Courses objects from file in local directory
    // resets the file in case objects are not compatible
    public static ArrayList<Course> readCourses() {
        ArrayList<Course> coursesHere = new ArrayList<>();

        File file = new File("UserData");
        if (file.exists()) {

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("CourseData"))) {
                boolean cont = true;
                while (cont) {
                    Object current = null;
                    try {
                        current = ois.readObject();
                    } catch (ClassNotFoundException e) {
                        System.out.println("wrong class");
                    } catch (java.io.InvalidClassException e) {
                        System.out.println("invalid class");
                        file.delete();
                        coursesHere = new ArrayList<Course>();
                        return coursesHere;
                    } catch (java.io.StreamCorruptedException e) {
                        System.out.println("corrupted class");
                    } catch (java.io.EOFException e) {
                        System.out.println("success reading Courses from file");
                    }
                    if (current != null)
                        coursesHere.add((Course) current);
                    else
                        cont = false;
                }
                ois.close();
                return coursesHere;
            } catch (Exception e) {
                System.out.println("Error reading from file");
                return coursesHere;
            }
        }
        return coursesHere;
    }

    // Finding submission by submission name and Student ID
    // if not found returns null
    public static ArrayList<Submission> findSubByName(String subName, ArrayList<Submission> submissionsHere,
                                                      Student current) {
        if (subName == null) {
            return null;
        }
        ArrayList<Submission> result = new ArrayList<>();
        if (submissionsHere.size() > 0) {
            for (Submission i : submissionsHere) {
                if (current == null && subName.equals(i.getqName())) {
                    result.add(i);
                } else if (current != null && i.getId() == current.getId() && subName.equals(i.getqName())) {
                    result.add(i);
                }
            }
            if (result.size() > 0) {
                return result;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // read current Submission objects from file in local directory
    // resets the file in case objects are not compatible
    public static ArrayList<Submission> readSubmissions() {
        ArrayList<Submission> submissionsHere = new ArrayList<>();

        File file = new File("SubmitData");
        if (file.exists()) {

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("SubmitData"))) {
                boolean cont = true;
                while (cont) {
                    Object current = null;
                    try {
                        current = ois.readObject();
                    } catch (ClassNotFoundException e) {
                        System.out.println("wrong class");
                    } catch (java.io.InvalidClassException e) {
                        System.out.println("invalid class");
                        file.delete();
                        submissionsHere = new ArrayList<Submission>();
                        return submissionsHere;
                    } catch (java.io.StreamCorruptedException e) {
                        System.out.println("corrupted class");
                    } catch (java.io.EOFException e) {
                        System.out.println("success reading Submissions from file");
                    }
                    if (current != null)
                        submissionsHere.add((Submission) current);
                    else
                        cont = false;
                }
                ois.close();
                return submissionsHere;
            } catch (Exception e) {
                System.out.println("Error reading from file");
                return submissionsHere;
            }
        }
        return submissionsHere;
    }

    // save current submission objects to file in local directory
    public static void saveSubmits(ArrayList<Submission> submissionsHere) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("SubmitData",
                false))) {
            for (Object current : submissionsHere) {
                oos.writeObject(current);
            }
            oos.close();
            System.out.println("The Submission(s) were successfully written to a file");
        } catch (Exception e) {
            System.out.println("Error saving to file");
        }
    }


    // check if user exists or not
    // takes ID password and array of users as parameters
    // handles deleted students and invalid password
    // if not found returns null
    public static User checkLogin(String userId, String password, ArrayList<User> accountsHere) {
        boolean invalid = true;
        for (User i : accountsHere) {
            if (userId.equals(String.valueOf(i.getId()))) {
                invalid = false;
                try {
                    if (i.getPassword().equals(password)) {
                        return i;
                    } else {
                        System.out.println("Invalid password");
                        System.out.println();
                    }
                } catch (NullPointerException e) {
                    System.out.println("User was deleted");
                }
            }
        }
        if (invalid) {
            System.out.println("Invalid ID");
        }
        return null;
    }

    public static User checkLoginGUI(String userId, String password, ArrayList<User> accountsHere, Template template) {
        boolean invalid = true;
        for (User i : accountsHere) {
            if (userId.equals(String.valueOf(i.getId()))) {
                invalid = false;
                try {
                    if (i.getPassword().equals(password)) {
                        return i;
                    } else {
                        System.out.println("Invalid password");
                        template.add(new ArrayList<String>() {
                            {
                                add("TEXT");
                                add("Invalid password");
                            }
                        });
                        System.out.println();
                    }
                } catch (NullPointerException e) {
                    System.out.println("User was deleted");
                    template.add(new ArrayList<String>() {
                        {
                            add("TEXT");
                            add("User was deleted");
                        }
                    });
                }
            }
        }
        if (invalid) {
            System.out.println("Invalid ID");
            template.add(new ArrayList<String>() {
                {
                    add("TEXT");
                    add("Invalid ID");
                }
            });
        }
        return null;
    }

    // checks if provided password matches provided id
    public static boolean invalidPassword(String userId, String password, ArrayList<User> accountsHere) {
        for (User i : accountsHere) {
            if (userId.equals(String.valueOf(i.getId()))) {
                try {
                    if (!i.getPassword().equals(password)) {
                        return true;
                    }
                } catch (NullPointerException e) {
                    // expected
                }
            }
        }
        return false;
    }

}


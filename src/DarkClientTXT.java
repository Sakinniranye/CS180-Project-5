import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Project 5 -- DarkClientTXT
 *
 * @author CS180-PR5 Group 079
 * @version December 13, 2021
 * <p>
 * A Client class
 * used to communicate with the server
 * by sending and receiving commands to create GUIs
 * uses port 1123
 */

public class DarkClientTXT {

    private static boolean allowClose = true;

    public static Window makeJFrame(ArrayList<ArrayList<String>> template, JFrame frame, ObjectOutputStream writer) {
        frame.setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        WindowListener listener = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    if (allowClose) {
                        writer.writeObject(null);
                    }

                } catch (IOException ex) {
                    System.out.println("Close");
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        };
        frame.addWindowListener(listener);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(template.size(), 1));
        panel.setBackground(Color.BLACK);


        ArrayList<String> string = new ArrayList<>();
        ArrayList<JTextField> text = new ArrayList<>();

        UIManager.put("Button.background", Color.BLACK);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.opaque", true);
        UIManager.put("Label.opaque", true);
        UIManager.put("Label.background", Color.BLACK);
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.getLookAndFeelDefaults().put("Panel.background", Color.BLACK);

        if (template.get(0).get(0).equals("REFRESH")) {
            template.remove(0);
            panel.setLayout(new GridLayout(template.size(), 1));
            for (int i = 0; i < template.size(); i++) {
                if (template.get(i).get(0).equals("TEXT")) {
                    JLabel label = new JLabel(" " + template.get(i).get(1) + " ");
                    label.setFont(new Font("Courier", Font.PLAIN, 30));
                    panel.add(label);
                }
                if (template.get(i).get(0).equals("FIELD")) {
                    JTextField textField = new JTextField();
                    textField.setFont(new Font("Courier", Font.PLAIN, 30));
                    panel.add(textField);
                    text.add(textField);
                }
                if (template.get(i).get(0).equals("BUTTON")) {
                    JButton button = new JButton(" " + template.get(i).get(1) + " ");
                    button.setFont(new Font("Courier", Font.PLAIN, 30));
                    String finalI = String.valueOf(i);
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            string.add(finalI);
                            for (int i = 0; i < text.size(); i++) {
                                string.add(text.get(i).getText());
                            }
                            try {
                                writer.writeObject(string);
                                writer.flush();
                            } catch (Exception ex) {
                                System.out.println("error");
                            }
                            frame.removeWindowListener(listener);
                            frame.dispose();
                        }
                    });
                    panel.add(button);
                }
                frame.add(panel, BorderLayout.CENTER);
                frame.setLocationRelativeTo(null);
                frame.pack();
            }
        } else {
            for (int i = 0; i < template.size(); i++) {
                if (template.get(i).get(0).equals("TEXT")) {
                    JLabel label = new JLabel(" " + template.get(i).get(1) + " ");
                    label.setFont(new Font("Courier", Font.PLAIN, 30));
                    panel.add(label);
                }
                if (template.get(i).get(0).equals("FIELD")) {
                    JTextField textField = new JTextField();
                    textField.setFont(new Font("Courier", Font.PLAIN, 30));
                    panel.add(textField);
                    text.add(textField);
                }
                if (template.get(i).get(0).equals("BUTTON")) {
                    JButton button = new JButton(" " + template.get(i).get(1) + " ");
                    button.setFont(new Font("Courier", Font.PLAIN, 30));
                    String finalI = String.valueOf(i);
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            string.add(finalI);
                            for (int i = 0; i < text.size(); i++) {
                                string.add(text.get(i).getText());
                            }
                            try {
                                writer.writeObject(string);
                                writer.flush();
                            } catch (Exception ex) {
                                System.out.println("error");
                            }
                            frame.removeWindowListener(listener);
                            frame.dispose();
                        }
                    });
                    panel.add(button);
                }
                frame.add(panel, BorderLayout.CENTER);
                frame.setLocationRelativeTo(null);
                frame.pack();
            }
        }
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        try {
            System.out.println("trying to connect");
            Socket socket = new Socket("localhost", 1123);
            System.out.println("connected");
            System.out.println();
            ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());

            boolean running = true;
            Object instruction;
            ArrayList<ArrayList<String>> template;
            ArrayList<ArrayList<String>> oldTemplate = new ArrayList<>();
            ArrayList<String> response = new ArrayList<>();
            Scanner scanner = new Scanner(System.in);
            ArrayList<String> input = null;

            ArrayList<java.awt.Window> windowArray = new ArrayList<>();

            while (running) {
                try {
                    instruction = reader.readObject();
                    template = ((Template) instruction).convert();
                    if (!template.toString().equals(oldTemplate.toString()) ||
                        !template.get(0).get(0).equals("REFRESH")) {
                        if (template.get(0).get(0).equals("REFRESH") ||
                            !template.toString().equals(oldTemplate.toString())) {
                            allowClose = false;
                            Thread.sleep(100);
                            for (int i = 0; i < windowArray.size(); i++) {
                                windowArray.get(i).dispose();
                            }
                            windowArray = new ArrayList<>();
                            Thread.sleep(100);
                            allowClose = true;
                        }

                        System.out.println("top");
                        oldTemplate = new ArrayList<>(template);
                        JFrame frame = new JFrame();
                        System.out.println("IN THIS GUI:");
                        for (int i = 0; i < template.size(); i++) {
                            System.out.println(i + " " + template.get(i).get(0) +
                                               " containing: " + template.get(i).get(1));
                        }
                        System.out.println("END OF GUI");
                        ArrayList<ArrayList<String>> finalTemplate = template;
                        ArrayList<Window> finalWindowArray = windowArray;

                        if (template.get(0).get(0).equals("REFRESH")) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    finalWindowArray.add(makeJFrame(finalTemplate, frame, writer));
                                }
                            });
                        } else {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    makeJFrame(finalTemplate, frame, writer);
                                }
                            });
                        }
                        if (template.get(0).get(0).equals("REFRESH")) {
                            System.out.println("refresh");
                            writer.writeObject(new ArrayList<String>());
                            writer.flush();
                        }
                    } else {
//                        System.out.println("bot");
//                        System.out.println("IN THIS GUI:");
//                        for (int i = 0; i < template.size(); i++) {
//                            System.out.println(i + " " + template.get(i).get(0) +
//                            " containing: " + template.get(i).get(1));
//                        }
//                        System.out.println("END OF GUI");
                        writer.writeObject(new ArrayList<String>());
                        writer.flush();
                    }

                } catch (EOFException e) {
                    running = false;
                }

            }

            reader.close();
            writer.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
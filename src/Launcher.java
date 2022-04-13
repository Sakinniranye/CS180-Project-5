import javax.swing.*;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Project 5 -- Launcher
 *
 * @author CS180-PR5 Group 079
 * @version December 13, 2021
 * <p>
 * A Launcher class
 * used for launching the server
 * and connecting clients
 */

public class Launcher {

    public static void main(String[] args) {

        // to enable / disable console output
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
                // NO-OP
            }
        }));

        new Thread(() -> DarkServer.main(null)).start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("problem waiting");
        }
        int quantity;
        while (true) {
            try {
                quantity = Integer.parseInt(JOptionPane.showInputDialog(null,
                        "How many clients to open?", "Client Launcher", JOptionPane.QUESTION_MESSAGE));
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(null, "Enter a number greater than 0", "Client Launcher",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Enter an integer", "Client Launcher",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        for (int i = 0; i < quantity; i++) {
            new Thread(() -> DarkClientTXT.main(null)).start();
        }
    }
}


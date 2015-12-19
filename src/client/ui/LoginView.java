package client.ui;

import client.Client;

import javax.swing.*;

public class LoginView {
    private Client client;

    public static void runUI(Client client) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Login");
            frame.setSize(300, 150);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel panel = new JPanel();
            frame.add(panel);
            placeComponents(frame, panel, client);

            frame.setVisible(true);
        });
    }

    private static void placeComponents(JFrame frame, JPanel panel, Client client) {

        panel.setLayout(null);

        JLabel userLabel = new JLabel("User");
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 10, 160, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 40, 160, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("login");
        loginButton.setBounds(100, 80, 80, 25);
        panel.add(loginButton);
        loginButton.addActionListener(e -> {
            String login = userText.getText();
            String password = String.valueOf(passwordText.getPassword());
            if (!"".equals(userText.getText()) && !"".equals(password)) {
                client.api.login(login, password);
                frame.setVisible(false);
                frame.dispose();
            }
        });

    }

}
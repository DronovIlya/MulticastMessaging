package client.ui;

import client.Client;
import commands.entity.Chat;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import java.util.HashMap;
import java.util.Map;


public class ChatView extends JFrame {
    public static Map<Long, ChatView> clientUIMap = new HashMap<>();
    //private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JTextArea textArea;
    private JTextField textField;
    private Client client;
    private Chat chat;

    public ChatView(Client client, Chat chat) {
        super("In " + chat.title + " as " + client.manager.self.name);
        this.client = client;
        this.chat = chat;
        initComponents();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    public static void runUI(Client client, Chat chat) {
        SwingUtilities.invokeLater(() -> {
            ChatView tmp = new ChatView(client, chat);
            tmp.setVisible(true);
            clientUIMap.put(chat.id, tmp);
        });
    }

    private void initComponents() {
        //jLabel1 = new JLabel("Try typing 'spectacular' or 'Swing'...");
        textField = new JTextField(20);
        textField.addActionListener(e -> {
            client.api.messageSend(chat.id, textField.getText());
            appendMessage(client.manager.self.name, textField.getText());
            textField.setText("");
        });
        textArea = new JTextArea();
        textArea.setColumns(50);
        textArea.setLineWrap(true);
        textArea.setRows(10);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        jScrollPane1 = new JScrollPane(textArea);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        //Create a parallel group for the horizontal axis
        ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        //Create a sequential and a parallel groups
        SequentialGroup h1 = layout.createSequentialGroup();
        ParallelGroup h2 = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
        //Add a scroll panel and a label to the parallel group h2
        h2.addComponent(textField, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE);
        h2.addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE);
        //h2.addComponent(jLabel1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE);

        //Add a container gap to the sequential group h1
        h1.addContainerGap();
        // Add the group h2 to the group h1
        h1.addGroup(h2);
        h1.addContainerGap();
        //Add the group h1 to hGroup
        hGroup.addGroup(Alignment.TRAILING, h1);
        //Create the horizontal group
        layout.setHorizontalGroup(hGroup);

        //Create a parallel group for the vertical axis
        ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        //Create a sequential group
        SequentialGroup v1 = layout.createSequentialGroup();
        //Add a container gap to the sequential group v1
        v1.addContainerGap();
        //Add a label to the sequential group v1
        //v1.addComponent(jLabel1);
        //v1.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        //Add scroll panel to the sequential group v1
        v1.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE);
        v1.addComponent(textField);
        v1.addContainerGap();
        //Add the group v1 to vGroup
        vGroup.addGroup(v1);
        //Create the vertical group
        layout.setVerticalGroup(vGroup);
        pack();

    }

    public void appendMessage(String sender, String message) {
        textArea.append(sender + ": " + message + "\n");
    }

}
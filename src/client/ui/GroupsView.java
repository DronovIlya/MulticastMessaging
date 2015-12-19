package client.ui;


import client.Client;
import commands.entity.Chat;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/* GroupsView.java requires no other files. */
public class GroupsView extends JPanel
        implements ListSelectionListener {
    private static final String fireString = "Join";
    private Client client;
    private List<Chat> availableChats;
    private JFrame frame;
    private JList list;
    private DefaultListModel listModel;
    private JButton fireButton;

    public GroupsView(JFrame frame, Client client, List<Chat> availableChats) {
        super(new BorderLayout());
        this.frame = frame;
        this.client = client;
        this.availableChats = availableChats;
        listModel = new DefaultListModel();
        for (Chat c : availableChats) {
            listModel.addElement(c.title);
        }

        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        fireButton = new JButton(fireString);
        fireButton.setActionCommand(fireString);
        fireButton.addActionListener(e -> {
            int index = list.getSelectedIndex();
            client.api.joinChat(availableChats.get(index).id);
            listModel.remove(index);
            availableChats.remove(index);

            int size = listModel.getSize();

            if (size == 0) { //Nobody's left, disable firing.
                fireButton.setEnabled(false);
                frame.isVisible();
                frame.dispose();

            } else { //Select an index.
                if (index == listModel.getSize()) {
                    //removed item in last position
                    index--;
                }

                list.setSelectedIndex(index);
                list.ensureIndexIsVisible(index);
            }
        });


        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                BoxLayout.LINE_AXIS));
        buttonPane.add(fireButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     *
     * @param client
     * @param availableChats
     */
    private static void createAndShowGUI(Client client, List<Chat> availableChats) {
        //Create and set up the window.
        JFrame frame = new JFrame("Available groups for " + client.manager.self.name);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(500, 500));
        //Create and set up the content pane.
        JComponent newContentPane = new GroupsView(frame, client, availableChats);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void runUI(Client client, List<Chat> availableChats) {
        SwingUtilities.invokeLater(() -> createAndShowGUI(client, availableChats));
    }

    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {

            if (list.getSelectedIndex() == -1) {
                //No selection, disable fire button.
                fireButton.setEnabled(false);

            } else {
                //Selection, enable the fire button.
                fireButton.setEnabled(true);
            }
        }
    }

    class FireListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //This method can be called only if
            //there's a valid selection
            //so go ahead and remove whatever's selected.

        }
    }
}

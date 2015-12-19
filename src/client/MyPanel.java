package client;

import commands.MessageSendCmd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class MyPanel extends JPanel implements MouseListener, MouseMotionListener {
    private Set<MLine> image;

    private static Color[] colors = new Color[]{Color.BLUE, Color.RED, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE};

    private boolean mouseIsPressed = false;

    private Point startPoint, endPoint;
    private Client client;
    private long chatId;

    public MyPanel(Client client, long chatId) {
        this.client = client;
        this.chatId = chatId;
        setBorder(BorderFactory.createLineBorder(Color.black));
        setMaximumSize(new Dimension(getHeight(), getWidth()));
        image = new HashSet<>();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        if (mouseIsPressed) {
            g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }
        for (MLine l : image) {
            g.setColor(l.color);
            g.drawLine(l.start.x, l.start.y, l.end.x, l.end.y);
        }
    }

    public void addNewLine(MessageSendCmd.Response response) {
        if (response.message.sender.id == -1) {
            image.addAll(MLine.getValues(response.message.text));
        } else {
            if (response.message.sender.equals(client.manager.self.name)) {
                return;
            }
            Color c = colors[response.message.sender.name.hashCode() % colors.length];
            image.add(MLine.valueOf(response.message.text, c));
        }
        repaint();
    }

    private void onAddLine(MLine line) {
        client.api.messageSend(chatId,line.toString());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseIsPressed = true;
        startPoint = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        MLine line = new MLine(startPoint, new Point(e.getX(), e.getY()), Color.BLACK);
        if (!line.start.equals(line.end)) {
            image.add(line);
            mouseIsPressed = false;
            startPoint = null;
            endPoint = null;
            onAddLine(line);
            repaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        endPoint = new Point(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


    private static class MLine {
        Point start, end;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MLine mLine = (MLine) o;

            if (!start.equals(mLine.start)) return false;
            if (!end.equals(mLine.end)) return false;
            return color.equals(mLine.color);

        }

        @Override
        public int hashCode() {
            int result = start.hashCode();
            result = 31 * result + end.hashCode();
            result = 31 * result + color.hashCode();
            return result;
        }

        Color color;

        public MLine(Point s, Point e, Color c) {
            start = s;
            end = e;
            color = c;
        }

        public MLine(int x1, int y1, int x2, int y2, Color c) {
            start = new Point(x1, y1);
            end = new Point(x2, y2);
            this.color = c;
        }

        @Override
        public String toString() {
            return "" + start.x  + " " + start.y + " " + end.x + " " + end.y;
        }

        public static MLine valueOf(String str, Color c) {
            String[] arr = str.split(" ");
            return new MLine(new Point(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])), new Point(Integer.parseInt(arr[2]), Integer.parseInt(arr[3])),c);
        }

        public static ArrayList<MLine> getValues(String str) {
            ArrayList<MLine> lines = new ArrayList<>();
            for (String lineStr : Arrays.asList(str.split(","))) {
                String[] lineArr = lineStr.split(" ");
                Color c = colors[lineArr[4].hashCode() % colors.length];
                lines.add(new MLine(Integer.parseInt(lineArr[0]),Integer.parseInt(lineArr[1]), Integer.parseInt(lineArr[2]), Integer.parseInt(lineArr[3]), c));
            }
            return lines;
        }
    }
}
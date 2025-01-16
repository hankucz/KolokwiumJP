import javax.swing.*;

public class Frame {
    private final JFrame frame = new JFrame("Wersja5N_HannaKuczynska281049");

    public void show() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 420);
        frame.setLocationRelativeTo(null);

        DrawingPanel drawingPanel = new DrawingPanel();
        frame.add(drawingPanel);

        frame.setVisible(true);
        drawingPanel.requestFocusInWindow();
    }
}


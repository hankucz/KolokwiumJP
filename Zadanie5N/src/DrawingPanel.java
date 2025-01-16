//Hanna Kuczyńska 281049 Informatyka techniczna - WERSJA 5N
/*Polecenie: Utworzyć aplikację okienkową (JFrame) zawierającą m.in. obszar do rysowania
(JPanel, JComponent itp.). Na tym obszarze mają się pojawiać kwadraty o jednostkowym boku
(jednostka do wyboru, nie chodzi o jeden piksel). Mają one opadać w dół aż do napotkania
przeszkody - krawędzi okna lub innego kwadratu. W trakcie tego riuchu ma być możliwe sterowanie
klawiaturą (prawo - lewo). Pełen rządek kwadratów znika (jak w tetrisie). Każdy z kwadratów
ma być sterowany nowym wątkiem
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class DrawingPanel extends JPanel implements KeyListener {
    private final java.util.List<Rectangle> squares = new ArrayList<>();
    private final int SQ_SIZE = 60;
    private Rectangle activeSquare = null;

    public DrawingPanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        int SPEED = 200;
        Timer timer = new Timer(SPEED, e -> moveFallingSquaresDown());
        timer.start();

        addNewSquare();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);

        synchronized (squares) {
            for (Rectangle rect : squares) {
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
        }
    }

    private void moveFallingSquaresDown() {
        if (activeSquare != null) {
            synchronized (squares) {
                if (checkIfSquaresCollide(activeSquare, 0, SQ_SIZE)) {
                    activeSquare.y += SQ_SIZE;
                } else {
                    activeSquare = null;
                    clearFullRows();
                    addNewSquare();
                }
            }
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (activeSquare == null) return;

        int keyCode = e.getKeyCode();
        synchronized (squares) {
            switch (keyCode) {
                case KeyEvent.VK_LEFT -> {
                    if (checkIfSquaresCollide(activeSquare, -SQ_SIZE, 0)) {
                        activeSquare.x -= SQ_SIZE;
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (checkIfSquaresCollide(activeSquare, SQ_SIZE, 0)) {
                        activeSquare.x += SQ_SIZE;
                    }
                }
            }
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
    private boolean checkIfSquaresCollide(Rectangle sq, int sqX, int sqY) {
        Rectangle futurePosition = new Rectangle(sq.x + sqX, sq.y + sqY, sq.width, sq.height);

        if (futurePosition.y + futurePosition.height > getHeight()) {
            return false;
        }

        synchronized (squares) {
            for (Rectangle other : squares) {
                if (other != sq && other.intersects(futurePosition)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void clearFullRows() {
        int rows = getHeight() / SQ_SIZE;
        int collumns = getWidth() / SQ_SIZE;
        boolean[] fullRows = new boolean[rows];

        synchronized (squares) {
            for (Rectangle sq : squares) {
                int row = sq.y / SQ_SIZE;
                fullRows[row] = fullRows[row] || (squares.stream()
                        .filter(r -> r.y / SQ_SIZE == row).count() == collumns);
            }

            for (int row = 0; row < rows; row++) {
                if (fullRows[row]) {
                    int finalRow = row;
                    squares.removeIf(sq -> sq.y / SQ_SIZE == finalRow);
                    for (Rectangle sq : squares) {
                        if (sq.y / SQ_SIZE < row) {
                            sq.y += SQ_SIZE;
                        }
                    }
                }
            }
        }
    }

    public void addNewSquare() {
        int cols = getWidth() / SQ_SIZE;
        int randomColumn = (int) (Math.random() * cols);
        int x = randomColumn * SQ_SIZE;
        Rectangle newSquare = new Rectangle(x, 0, SQ_SIZE, SQ_SIZE);
        synchronized (squares) {
            squares.add(newSquare);
        }
        activeSquare = newSquare;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Frame().show());
    }
}
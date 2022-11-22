package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final int WAITING_TIME = 10_000;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

     /**
     * Builds a new CGUI.
     */
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        up.addActionListener(e -> agent.upCounting());
        down.addActionListener(e -> agent.downCounting());
        stop.addActionListener(e -> {
            agent.stopCounting();
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        });

        new Thread(agent).start();
        new Thread(() -> {
            try {
                Thread.sleep(WAITING_TIME);
            } catch (InterruptedException ex) {
                System.out.println(ex); // NOPMD: allowed as this is just an exercise
            }
            agent.stopCounting();
        }).start();
    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean increase;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    if (increase) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    System.out.println(ex); // NOPMD: allowed as this is just an exercise
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        /**
        * increase count.
        */
        public void upCounting() {
            this.increase = true;
        }

        /**
         * deacrease count.
         */
        public void downCounting() {
            this.increase = false;
        }
    }
}

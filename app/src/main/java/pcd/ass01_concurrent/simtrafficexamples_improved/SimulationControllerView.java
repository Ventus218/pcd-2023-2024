package pcd.ass01_concurrent.simtrafficexamples_improved;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SimulationControllerView extends JFrame {

    private int stepNumber;
    private boolean isPaused = false;
    private final JButton pauseButton;

    public SimulationControllerView(int defaultStepNumber, SimulationControllerDelegate delegate) {
        super("Simulation Controller View");
        SimulationControllerView thisView = this;
        stepNumber = defaultStepNumber;

        setSize(280, 120);

        JLabel stepLabel = new JLabel();
        setStepLabel(stepLabel, stepNumber);

        JSlider stepSlider = new JSlider(1, 10000, stepNumber);
        stepSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                stepNumber = stepSlider.getValue();
                setStepLabel(stepLabel, stepNumber);
            }
        });

        pauseButton = new JButton("Pause");
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPaused) {
                    delegate.onUnpauseSimulation(thisView);
                } else {
                    delegate.onPauseSimulation(thisView);
                }
                isPaused = !isPaused;
                pauseButton.setText(isPaused ? "Unpause" : "Pause");
            }
        });
        
        JButton stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopButton.setEnabled(false);
                pauseButton.setEnabled(false);
                delegate.onStopSimulation(thisView);
            }
        });

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepSlider.setEnabled(false);
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                pauseButton.setEnabled(true);
                delegate.onStartSimulation(thisView, stepNumber);
            }
        });
        
        JPanel sliderContainer = new JPanel();
        sliderContainer.setLayout(new BoxLayout(sliderContainer, BoxLayout.Y_AXIS));
        sliderContainer.add(stepLabel);
        sliderContainer.add(stepSlider);
        
        JPanel cp = new JPanel();
        cp.add(startButton);
        cp.add(stopButton);
        cp.add(pauseButton);
        cp.add(sliderContainer);
        setContentPane(cp);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setStepLabel(JLabel label, int steps) {
        label.setText(steps + " steps");
    }

    public void display() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }

    public void close() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(false);
            this.dispose();
        });
    }

    public void disablePauseButton() {
        SwingUtilities.invokeLater(() -> {
            this.pauseButton.setEnabled(false);
        });
    }
}

package pcd.ass01_concurrent.simtrafficexamples_improved;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SimulationControllerView extends JFrame {

    public SimulationControllerView(int defaultStepNumber) {
        super("Simulation Controller View");
        setSize(200, 120);
        JPanel cp = new JPanel();

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start!");
            }
        });
        cp.add(startButton);

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Stop!");
            }
        });
        cp.add(stopButton);

        JPanel sliderContainer = new JPanel();
        sliderContainer.setLayout(new BoxLayout(sliderContainer, BoxLayout.Y_AXIS));

        JLabel stepLabel = new JLabel();
        setStepLabel(stepLabel, defaultStepNumber);
        sliderContainer.add(stepLabel);

        JSlider stepSlider = new JSlider(1, 10000, defaultStepNumber);
        stepSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setStepLabel(stepLabel, stepSlider.getValue());
            }
        });
        sliderContainer.add(stepSlider);

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
}

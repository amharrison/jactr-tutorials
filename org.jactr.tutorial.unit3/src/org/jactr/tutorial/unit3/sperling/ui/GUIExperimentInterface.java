package org.jactr.tutorial.unit3.sperling.ui;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.commonreality.reality.CommonReality;
import org.commonreality.sensors.aural.DefaultAuralSensor;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tutorial.unit3.sperling.IExperimentInterface;

public class GUIExperimentInterface implements IExperimentInterface {

	protected JFrame _window;
	protected KeyListener _keyListener;
	private Consumer<Character> _keyConsumer;
	private JLabel[][] _labels;
	private IExperiment _experiment;

	public GUIExperimentInterface(IExperiment experiment, int numberOfRows, int numberOfColumns) {
		_window = new JFrame();

		_experiment = experiment;
		_keyListener = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {

				_keyConsumer.accept(e.getKeyChar());
			}
		};

		Font font = new JLabel().getFont();
		final Font fFont = font.deriveFont(font.getSize2D() * 5f);

		_labels = new JLabel[numberOfRows][numberOfColumns];
		IntStream.range(0, numberOfRows).forEach((row) -> {
			IntStream.range(0, numberOfColumns).forEach(col -> {
				JLabel label = new JLabel("");
				label.setFont(fFont);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setHorizontalTextPosition(SwingConstants.CENTER);
				_labels[row][col] = label;
				label.setName(""+row+""+col);
			});
		});

		Container container = _window.getContentPane();
		container.setLayout(new GridLayout(numberOfRows, numberOfColumns));

		for (int row = 0; row < _labels.length; row++)
			for (int col = 0; col < _labels[0].length; col++)
				container.add(_labels[row][col]);

		/*
		 * and set the size of the window
		 */
		Toolkit tk = Toolkit.getDefaultToolkit();
		_window.setSize(tk.getScreenSize());
	}

	@Override
	public void configure(Consumer<Character> keyConsumer, char[][] rows) {
		try {
			SwingUtilities.invokeAndWait(() -> {
				_keyConsumer = keyConsumer;
				_window.addKeyListener(_keyListener);
				for (int row = 0; row < rows.length; row++)
					for (int col = 0; col < rows[0].length; col++) {
						_labels[row][col].setText("" + rows[row][col]);
						_labels[row][col].setVisible(true);
					}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clear() {
		try {
			/*
			 * note: we setVisible to false, but leave the label alone. If we changed the
			 * label and hid it, we'd see the label change
			 */
			SwingUtilities.invokeAndWait(() -> {
				for (int row = 0; row < _labels.length; row++)
					for (int col = 0; col < _labels[0].length; col++)
						_labels[row][col].setVisible(false);
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void show() {

		try {
			SwingUtilities.invokeAndWait(() -> {
				_window.setVisible(true);
				_window.requestFocus();
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// and wait until idle..
		try {
			SwingUtilities.invokeAndWait(() -> {
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void hide() {

		SwingUtilities.invokeLater(() -> {
			_window.setVisible(false);
			_window.removeKeyListener(_keyListener);
		});

	}

	@Override
	public void dispose() {
		SwingUtilities.invokeLater(() -> {
			_window.setVisible(false);
			_window.dispose();
		});

	}

	@Override
	public void beep(final int beepCount) {

		int hZ = 2000;
		if (beepCount == 2)
			hZ = 1000;
		else if (beepCount == 3)
			hZ = 500;
//		_experiment.getVariableContext().set("beep", hZ);
//		new XMLSensorAction("org/jactr/tutorial/unit3/experiment/sim/beep.xml", true, _experiment)
//				.fire(_experiment.getVariableContext());
		Optional<DefaultAuralSensor> aural = CommonReality.getSensors().stream()
				.filter(DefaultAuralSensor.class::isInstance).map(s -> {
					return (DefaultAuralSensor) s;
				}).findFirst();

		final String fContent = "" + hZ;
		aural.ifPresent(as -> {
			as.queueSound(as.newSound(new String[] { "tone" }, fContent, as.getClock().getTime(), 0.5));
		});

		/*
		 * launch in its own thread so we don't delay the ui
		 */
		new Thread(() -> {
			Toolkit tk = Toolkit.getDefaultToolkit();
			for (int i = 0; i < beepCount; i++) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tk.beep();
			}
		}).start();

	}

}

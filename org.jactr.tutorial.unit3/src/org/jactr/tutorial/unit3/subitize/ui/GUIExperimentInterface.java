package org.jactr.tutorial.unit3.subitize.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jactr.tutorial.unit3.subitize.IExperimentInterface;

public class GUIExperimentInterface implements IExperimentInterface {

	protected JFrame _window;
	protected KeyListener _keyListener;
	private Consumer<Character> _keyConsumer;
	private JLabel[] _labels;

	public GUIExperimentInterface(int maxTargets) {
		_window = new JFrame();

		_keyListener = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {

				_keyConsumer.accept(e.getKeyChar());
			}
		};

		Font font = new JLabel().getFont();
		final Font fFont = font.deriveFont(font.getSize2D() * 5f);

		_labels = new JLabel[maxTargets];
		Container container = _window.getContentPane();
		container.setLayout(null);

		IntStream.range(0, maxTargets).forEach(col -> {
			JLabel label = new JLabel("");
			label.setFont(fFont);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setHorizontalTextPosition(SwingConstants.CENTER);
			_labels[col] = label;
			label.setText("X");
			label.setVisible(false);
			label.setName("" + col);
			container.add(label);
		});

		/*
		 * and set the size of the window
		 */
		Toolkit tk = Toolkit.getDefaultToolkit();
		_window.setSize(tk.getScreenSize());
	}

	@Override
	public void configure(Consumer<Character> keyConsumer, int maxTargets) {
		try {
			SwingUtilities.invokeAndWait(() -> {
				_keyConsumer = keyConsumer;
				_window.addKeyListener(_keyListener);

				Toolkit tk = Toolkit.getDefaultToolkit();
				Dimension screen = tk.getScreenSize();

				for (int col = 0; col < maxTargets; col++) {
					int x = (int) (Math.random() * screen.width);
					int y = (int) (Math.random() * screen.height);
					Dimension preferred = _labels[col].getPreferredSize();
					_labels[col].setBounds(x, y, preferred.width, preferred.height);
					_labels[col].setVisible(true);
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
						_labels[row].setVisible(false);
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

	

}

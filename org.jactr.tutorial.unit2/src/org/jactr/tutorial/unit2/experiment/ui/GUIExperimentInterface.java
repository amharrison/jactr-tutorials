package org.jactr.tutorial.unit2.experiment.ui;

import java.awt.BorderLayout;
import java.awt.Container;
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

import org.jactr.tutorial.unit2.experiment.IExperimentInterface;

public class GUIExperimentInterface implements IExperimentInterface {

	protected JFrame _window;
	protected KeyListener _keyListener;
	private Consumer<Character> _keyConsumer;
	private JLabel[] _labels;

	public GUIExperimentInterface(int numberOfLabels) {
		_window = new JFrame();

		_keyListener = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				try
				{
				_window.removeKeyListener(_keyListener);
				_keyConsumer.accept(e.getKeyChar());
				}
				catch(Exception e1)
				{
					e1.printStackTrace(System.err);
				}
			}
		};

		_labels = new JLabel[numberOfLabels];
		IntStream.range(0, numberOfLabels).forEach((i) -> {
			JLabel label = new JLabel("");
			Font font = label.getFont();
			font = font.deriveFont(font.getSize2D() * 5f);
			label.setFont(font);
			_labels[i] = label;
		});

		Container container = _window.getContentPane();
		container.setLayout(new BorderLayout());

		String[] hints = new String[] { "Center", "East", "West" };
		int[] alignment = new int[] {SwingConstants.CENTER, SwingConstants.LEFT, SwingConstants.RIGHT};
		for (int i = 0; i < _labels.length; i++) {
			container.add(_labels[i], hints[i]);
			_labels[i].setHorizontalAlignment(alignment[i]);
			_labels[i].setHorizontalTextPosition(alignment[i]);
		}

		/*
		 * and set the size of the window
		 */
		Toolkit tk = Toolkit.getDefaultToolkit();
		_window.setSize(tk.getScreenSize());
	}

	@Override
	public void configure(Consumer<Character> keyConsumer, String... labels) {
		try {
			SwingUtilities.invokeAndWait(() -> {
				_keyConsumer = keyConsumer;
				_window.addKeyListener(_keyListener);
				for (int i = 0; i < labels.length; i++)
					_labels[i].setText(labels[i]);
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
					_window.requestFocusInWindow();
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
		
			try {
				SwingUtilities.invokeAndWait(() -> {
					_window.setVisible(false);
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
	public void dispose() {
		SwingUtilities.invokeLater(()->{
			_window.setVisible(false);
			_window.dispose();
		});
		
	}

}

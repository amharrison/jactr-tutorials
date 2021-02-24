package org.jactr.tutorial.unit4.paired.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jactr.tutorial.unit4.paired.IExperimentInterface;

public class GUIExperimentInterface implements IExperimentInterface {

	protected JFrame _window;
	protected KeyListener _keyListener;
	private Consumer<Character> _keyConsumer;
	private JLabel _label;
	

	private String _word;
	private String _digit;
	

	public GUIExperimentInterface() {
		_window = new JFrame();
		_label = new JLabel();
		Font font = _label.getFont();

		_label.setFont(font.deriveFont(font.getSize2D() * 5f));
		_label.setHorizontalAlignment(SwingConstants.CENTER);
		_label.setVerticalAlignment(SwingConstants.CENTER);
		
		_label.setVisible(false);
		_window.getContentPane().setLayout(new BorderLayout());
		_window.getContentPane().add(_label, BorderLayout.CENTER);

		_keyListener = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				_window.removeKeyListener(_keyListener);
				_keyConsumer.accept(e.getKeyChar());
			}
		};

		Toolkit tk = Toolkit.getDefaultToolkit();
		_window.setSize(tk.getScreenSize());
	}

	@Override
	public void configure(Consumer<Character> keyConsumer, int trial, String word, int digit) {
		_word = word;
		_digit = "" + digit;
		_keyConsumer = keyConsumer;
		_window.addKeyListener(_keyListener);
	}

	@Override
	public void showWord() {
		try {
			SwingUtilities.invokeAndWait(() -> {
				_label.setText(_word);
				_label.setVisible(true);
				_window.setVisible(true);
			});
			SwingUtilities.invokeAndWait(() -> {
				// noop
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showDigit() {
		try {
			SwingUtilities.invokeAndWait(() -> {
				_label.setText(_digit);
				_label.setVisible(true);
				_window.setVisible(true);
			});
			SwingUtilities.invokeAndWait(() -> {
				// noop
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void clear() {
		try {
			SwingUtilities.invokeAndWait(() -> {
				_label.setVisible(false);
			});
			SwingUtilities.invokeAndWait(() -> {
				// noop
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void hide() {
		try {
			SwingUtilities.invokeAndWait(() -> {
				_window.setVisible(false);
			});
			SwingUtilities.invokeAndWait(() -> {
				// noop
			});
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	public void dispose() {
		_window.dispose();

	}

}

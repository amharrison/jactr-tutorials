package org.jactr.tutorial.unit6.probability.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jactr.tutorial.unit6.probability.IExperimentInterface;

public class GUIExperimentInterface implements IExperimentInterface {

	private JFrame _window;
	protected KeyListener _keyListener;
	private Consumer<Character> _keyConsumer;
	private JLabel _centerLabel;

	private boolean _resultIsHeads;

	public GUIExperimentInterface() {

		_window = new JFrame();
		_centerLabel = new JLabel();
		Font font = _centerLabel.getFont();

		_centerLabel.setFont(font.deriveFont(font.getSize2D() * 5f));
		_centerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		_centerLabel.setVerticalAlignment(SwingConstants.CENTER);

		_centerLabel.setVisible(false);
		_window.getContentPane().setLayout(new BorderLayout());
		_window.getContentPane().add(_centerLabel, BorderLayout.CENTER);

		_keyListener = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				_window.removeKeyListener(_keyListener);
				//hide the label
				_centerLabel.setVisible(false);
				
				//notify
				_keyConsumer.accept(e.getKeyChar());
			}
		};

		Toolkit tk = Toolkit.getDefaultToolkit();
		_window.setSize(tk.getScreenSize());
	}

	@Override
	public void configure(Consumer<Character> response, boolean resultIsHeads) {
		_keyConsumer = response;
		_resultIsHeads = resultIsHeads;
		_centerLabel.setText("");
		_centerLabel.setVisible(false);
		_window.addKeyListener(_keyListener);
	}

	@Override
	public void showPrompt() {
		try {
			SwingUtilities.invokeAndWait(()->{
				_centerLabel.setText("Choose");
				_centerLabel.setVisible(true);
				_window.setVisible(true);			
			});
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void showFeedback() {
		try {
			SwingUtilities.invokeAndWait(()->{
				_centerLabel.setText(_resultIsHeads?"Heads":"Tails");
				_centerLabel.setVisible(true);
			});
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clear() {
		try {
			SwingUtilities.invokeAndWait(()->{
				_centerLabel.setText("");
				_centerLabel.setVisible(false);	
			});
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		_window.setVisible(false);
		_window.dispose();
	}

}

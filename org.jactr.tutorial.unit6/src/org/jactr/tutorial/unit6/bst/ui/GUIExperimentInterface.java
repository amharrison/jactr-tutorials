package org.jactr.tutorial.unit6.bst.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.jactr.tutorial.unit6.bst.IExperimentInterface;

public class GUIExperimentInterface implements IExperimentInterface {

	protected JFrame _window;

	private LineComponent[] _reference = new LineComponent[3];
	private LineComponent _goal, _current;

	private Consumer<String> _responseHandler;
	private StringBuilder _response;

	protected JLabel _done;

	public GUIExperimentInterface() {
		_window = new JFrame();

		Container container = _window.getContentPane();
		container.setLayout(new GridBagLayout());

		for (int i = 0; i < _reference.length; i++) {
			GridBagConstraints layout = new GridBagConstraints();

			JButton button = new JButton(String.format("%c", (i + 'A')));
			button.setActionCommand("" + i);
			_reference[i] = new LineComponent();
			_reference[i].setName(String.format("%c", (i + 'A')));

			button.addActionListener(this::buttonClicked);

			layout.gridx = 0;
			layout.gridy = i;
			layout.gridheight = layout.gridwidth = 1;
			container.add(button, layout);

			layout.gridx = 1;
			layout.gridwidth = 3;
			layout.fill = GridBagConstraints.HORIZONTAL;
			container.add(_reference[i], layout);
		}

		_goal = new LineComponent();
		_goal.setColor(Color.GREEN);
		_goal.setName("goal");

		GridBagConstraints layout = new GridBagConstraints();
		layout.gridx=0;
		layout.gridy=4;
		layout.gridwidth=1;
		container.add(new JLabel(" "), layout);
		
		layout.gridx = 1;
		layout.gridy = 4;
		layout.gridwidth = 3;
		layout.fill = GridBagConstraints.HORIZONTAL;
		container.add(_goal, layout);

		JButton reset = new JButton("Reset");
		reset.addActionListener(this::reset);

		_current = new LineComponent();
		_current.setColor(Color.BLUE);
		_current.setName("current");

		layout.gridx = 0;
		layout.gridy = 5;
		layout.gridwidth = 1;
		container.add(reset, layout);

		layout.gridx = 1;
		layout.gridy = 5;
		layout.gridwidth = 3;
		layout.fill = GridBagConstraints.HORIZONTAL;
		container.add(_current, layout);

		_done = new JLabel();

		layout.gridx = 3;
		layout.gridy = 6;
		layout.gridwidth = 1;
		layout.fill = GridBagConstraints.NONE;
		layout.anchor = GridBagConstraints.LINE_END;
		container.add(_done, layout);

		Toolkit tk = Toolkit.getDefaultToolkit();
		_window.setSize(tk.getScreenSize());
	}

	@Override
	public void configure(Consumer<String> actionSequence, int a, int b, int c, int goal) {
		_reference[0].setLength(a);
		_reference[1].setLength(b);
		_reference[2].setLength(c);
		_goal.setLength(goal);
		_current.setLength(0);
		_done.setText("");
		_response = new StringBuilder();
		_responseHandler = actionSequence;
	}

	@Override
	public void show() {
		try {
			SwingUtilities.invokeAndWait(() -> {
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

	private void buttonClicked(ActionEvent actionevent1) {
		JButton button = (JButton) actionevent1.getSource();
		String actionCommand = actionevent1.getActionCommand();
		int index = Integer.parseInt(actionCommand);

		_response.append(button.getText());

		if (_goal.getLength() > _current.getLength())
			_current.setLength(_current.getLength() + _reference[index].getLength());
		else
			_current.setLength(_current.getLength() - _reference[index].getLength());

		if (_current.getLength() == _goal.getLength()) {
			_responseHandler.accept(_response.toString());
			_done.setText("Done");
		}

		_done.repaint();
		_current.repaint();
	}

	private void reset(ActionEvent actionevent1) {
		_current.setLength(0);
		_current.repaint();
		_response.append("R");
	}


}

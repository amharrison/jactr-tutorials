package org.jactr.tutorial.unit6.bst.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JComponent;

public class LineComponent extends JComponent {

	private int _length = 0;
	private Color _color = Color.BLACK;
	private Stroke _stroke = new BasicStroke(5f);

	public LineComponent() {

	}

	public Dimension getPreferredSize() {
		return new Dimension(_length*2, getMinimumSize().height);
	}

	public void setColor(Color color) {
		_color = color;
	}

	public void setLength(int lineLength) {
		int old = _length;
		_length = lineLength;
		firePropertyChange("lineLength", old, lineLength); //so we can detect the change
	}

	public int getLength() {
		return _length;
	}

	protected void paintComponent(Graphics g) {
		if (_length > 0) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(_stroke);
			g2d.setColor(_color);
			int y = getHeight() / 2;
			g2d.drawLine(0, y, _length*2, y);
		}
	}
}


package org.jactr.tutorial.unit6.bst.swing;

import java.awt.Component;

import org.commonreality.object.IMutableObject;
import org.commonreality.sensors.base.impl.DefaultObjectKey;
import org.commonreality.sensors.swing.internal.Coordinates;
import org.commonreality.sensors.swing.processors.AbstractCreatorProcessor;
import org.jactr.tutorial.unit6.bst.ui.LineComponent;

public class LineComponentProcessor extends AbstractCreatorProcessor {
	
	static public final String LINE_LENGTH = "bst.lineLength";

	public LineComponentProcessor(Coordinates coordinates) {
		super(coordinates, LineComponent.class);
	}

	@Override
	protected String[] calculateTypes(Component arg0) {
		return new String[] { "line" };
	}

	@Override
	protected String getText(Component arg0) {
		return "";
	}

	public void process(DefaultObjectKey objectKey, IMutableObject mutableObject) {
		super.process(objectKey, mutableObject);
		//now set our unique property
		LineComponent lc = (LineComponent) objectKey.getObject();
		mutableObject.setProperty(LINE_LENGTH, lc.getLength());
	}
}

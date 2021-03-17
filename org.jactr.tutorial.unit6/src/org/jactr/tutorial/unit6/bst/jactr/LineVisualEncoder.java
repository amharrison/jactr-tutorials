package org.jactr.tutorial.unit6.bst.jactr;

import org.jactr.modules.pm.visual.memory.impl.encoder.ExtensibleVisualEncoder;
import org.jactr.tutorial.unit6.bst.swing.LineComponentProcessor;

public class LineVisualEncoder extends ExtensibleVisualEncoder {

	public LineVisualEncoder() {
		super("line-object", "line");
		addFeatureHandler(LineComponentProcessor.LINE_LENGTH, "length");
	}

}

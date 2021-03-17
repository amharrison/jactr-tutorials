package org.jactr.tutorial.unit6.bst.swing;

import java.util.function.Consumer;

import org.commonreality.sensors.swing.DefaultSwingSensor;

public class SwingConfigurator implements Consumer<DefaultSwingSensor> {

	@Override
	public void accept(DefaultSwingSensor dss) {
		
		dss.getSwingCenter().add(new LineComponentProcessor(dss.getCoordinateTransform()));
	}

}

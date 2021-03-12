package org.jactr.tutorial.unit5.twentyone.cards.impl;

import org.jactr.tutorial.unit5.twentyone.cards.IAction;

public class DefaultAction implements IAction {

	private final String _name;
	private final Object _parameter;
	private final Character _shortCut;
	
	public DefaultAction(String name, Character shortCut, Object parameter) {
		_name = name;
		_parameter = parameter;
		_shortCut = shortCut;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		result = prime * result + ((_parameter == null) ? 0 : _parameter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultAction other = (DefaultAction) obj;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		if (_parameter == null) {
			if (other._parameter != null)
				return false;
		} else if (!_parameter.equals(other._parameter))
			return false;
		return true;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Object getParameter() {
		return _parameter;
	}
	
	public IAction clone(Object parameter) {
		return new DefaultAction(_name, _shortCut, parameter);
	}

	@Override
	public Character getShortCut() {
		return _shortCut;
	}

}

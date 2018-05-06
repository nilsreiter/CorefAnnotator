package de.unistuttgart.ims.coref.annotator.comp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Function;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class BoundLabel extends JLabel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	Function<Object, String> convert;
	String propertyName;

	public BoundLabel(CABean bean, String propertyName, Function<Object, String> convert) {
		this.convert = convert;
		this.propertyName = propertyName;
		bean.addPropertyChangeListener(this);
		this.setHorizontalAlignment(SwingConstants.RIGHT);
	}

	public BoundLabel(CABean bean, String propertyName, Function<Object, String> convert, Object initialValue) {
		this.convert = convert;
		this.propertyName = propertyName;
		bean.addPropertyChangeListener(this);
		setText(convert.apply(initialValue));
		this.setHorizontalAlignment(SwingConstants.RIGHT);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(propertyName))
			setText(convert.apply(evt.getNewValue()));
	}

}

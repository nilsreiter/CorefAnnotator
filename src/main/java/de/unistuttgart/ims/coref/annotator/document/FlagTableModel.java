package de.unistuttgart.ims.coref.annotator.document;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;

public class FlagTableModel implements TableModel, ModelAdapter, FlagModelListener {

	FlagModel flagModel;
	MutableSet<TableModelListener> tableModelListeners = Sets.mutable.empty();

	public FlagTableModel(FlagModel fm) {
		this.flagModel = fm;
		this.flagModel.addFlagModelListener(this);
	}

	@Override
	public int getRowCount() {
		return flagModel.getFlags().size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "flagmodel.icon";
		case 1:
			return "flagmodel.key";
		case 2:
			return "flagmodel.label";
		case 3:
			return "flagmodel.targetclass";
		default:
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 3:
			return Class.class;
		case 2:
		case 1:
			return String.class;
		case 0:
			return Ikon.class;
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 1)
			return false;
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Flag f = flagModel.getFlags().get(rowIndex);
		switch (columnIndex) {
		case 0:
			return MaterialDesign.valueOf(f.getIcon());
		case 1:
			return f.getKey();
		case 2:
			return f.getLabel();
		case 3:
			try {
				return Class.forName(f.getTargetClass());
			} catch (ClassNotFoundException e) {
				return null;
			}
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Annotator.logger.entry(aValue, rowIndex, columnIndex);
		Flag f = flagModel.getFlags().get(rowIndex);
		switch (columnIndex) {
		case 1:
			f.setIcon(((MaterialDesign) aValue).name());
			break;
		case 2:
			f.setLabel((String) aValue);
			break;
		case 3:
			f.setTargetClass(((Class<?>) aValue).getCanonicalName());
			break;
		}
		flagModel.updateFlag(f);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		this.tableModelListeners.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		this.tableModelListeners.remove(l);
	}

	@Override
	public void flagEvent(FeatureStructureEvent event) {
		Annotator.logger.entry(event);

		TableModelEvent tme = null;
		switch (event.getType()) {
		case Add:
			tme = new TableModelEvent(this, getRowCount() - 1);
			break;
		case Update:
			tme = new TableModelEvent(this, flagModel.getFlags().indexOf(event.getArgument(0)));
			break;
		default:
		}
		Annotator.logger.debug(tme);
		if (tme != null)
			for (TableModelListener l : tableModelListeners)
				l.tableChanged(tme);
	}
}

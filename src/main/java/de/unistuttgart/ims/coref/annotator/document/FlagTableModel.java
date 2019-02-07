package de.unistuttgart.ims.coref.annotator.document;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateFlagProperty;

public class FlagTableModel implements TableModel, ModelAdapter, FlagModelListener {

	DocumentModel documentModel;
	MutableSet<TableModelListener> tableModelListeners = Sets.mutable.empty();

	public FlagTableModel(DocumentModel dm) {
		this.documentModel = dm;
		this.documentModel.getFlagModel().addFlagModelListener(this);
	}

	@Override
	public int getRowCount() {
		return documentModel.getFlagModel().getFlags().size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Annotator.getString(Constants.Strings.FLAG_EDITOR_ICON);
		case 1:
			return Annotator.getString(Constants.Strings.FLAG_EDITOR_KEY);
		case 2:
			return Annotator.getString(Constants.Strings.FLAG_EDITOR_LABEL);
		case 3:
			return Annotator.getString(Constants.Strings.FLAG_EDITOR_TARGETCLASS);
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
		// if (columnIndex == 1)
		// return false;
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Flag f = documentModel.getFlagModel().getFlags().get(rowIndex);
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
		Flag f = documentModel.getFlagModel().getFlags().get(rowIndex);
		UpdateFlagProperty.FlagProperty property = null;
		Object value = null;
		switch (columnIndex) {
		case 0:
			property = UpdateFlagProperty.FlagProperty.ICON;
			value = ((MaterialDesign) (aValue)).name();
			break;
		case 1:
			property = UpdateFlagProperty.FlagProperty.KEY;
			value = aValue;
			break;
		case 2:
			property = UpdateFlagProperty.FlagProperty.LABEL;
			value = aValue;
			break;
		case 3:
			property = UpdateFlagProperty.FlagProperty.TARGETCLASS;
			value = ((Class<?>) aValue).getCanonicalName();
			break;
		default:
			return;
		}
		documentModel.edit(new UpdateFlagProperty(f, property, value));
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
			tme = new TableModelEvent(this, getRowCount() - 1, getRowCount() - 1, TableModelEvent.ALL_COLUMNS,
					TableModelEvent.INSERT);
			break;
		case Update:
			tme = new TableModelEvent(this, documentModel.getFlagModel().getFlags().indexOf(event.getArgument(0)));
			break;
		case Remove:
			int row = documentModel.getFlagModel().getFlags().indexOf(event.getArgument(0));
			tme = new TableModelEvent(this, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
			break;
		default:
		}
		Annotator.logger.debug(tme);
		if (tme != null)
			for (TableModelListener l : tableModelListeners)
				l.tableChanged(tme);
	}

}

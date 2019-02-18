package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.apache.uima.jcas.cas.FSArray;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.action.AddDirectedRelationAction;
import de.unistuttgart.ims.coref.annotator.action.AddUndirectedRelationAction;
import de.unistuttgart.ims.coref.annotator.action.DeleteAction;
import de.unistuttgart.ims.coref.annotator.api.v1.DirectedEntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.SymmetricEntityRelation;
import de.unistuttgart.ims.coref.annotator.comp.DefaultTableHeaderCellRenderer;
import de.unistuttgart.ims.coref.annotator.comp.EntityPanel;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.document.RelationModelListener;
import de.unistuttgart.ims.coref.annotator.document.adapter.DirectedRelationsTableModel;
import de.unistuttgart.ims.coref.annotator.document.adapter.EntityComboBoxModel;
import de.unistuttgart.ims.coref.annotator.document.adapter.FlagComboBoxModel;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityRelation;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityRelation.EntityRelationProperty;

public class RelationEditor extends JFrame {

	private static final long serialVersionUID = 1L;

	DocumentModel documentModel;
	DocumentWindow documentWindow;

	JTable tableWithDirectedRelations;
	Component tableWithUndirectedRelations;
	JPanel toolbar;

	public RelationEditor(DocumentModel documentModel, DocumentWindow documentWindow) {
		this.documentModel = documentModel;
		this.documentWindow = documentWindow;
		this.setTitle(Annotator.getString(Constants.Strings.RELATION_EDITOR) + ": " + documentWindow.getTitle());
		this.addWindowListener(new FlagEditorWindowListener());

		// Actions
		AbstractAction addDirectedRelationAction = new AddDirectedRelationAction(documentModel);
		AbstractAction addUndirectedRelationAction = new AddUndirectedRelationAction(documentModel);

		// Table
		initDirected();
		tableWithUndirectedRelations = initUndirected();

		this.toolbar = new JPanel();
		this.toolbar.add(new JButton(addDirectedRelationAction));
		this.toolbar.add(new JButton(addUndirectedRelationAction));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tableWithDirectedRelations),
				new JScrollPane(tableWithUndirectedRelations));

		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		this.getContentPane().add(toolbar, BorderLayout.NORTH);
		this.setVisible(true);
		this.pack();
	}

	private Component initUndirected() {

		JPanel panel = new GridLayoutTable(documentModel);

		return panel;

	}

	private void initDirected() {
		EntityComboBoxModel entityComboBoxModel = new EntityComboBoxModel();
		documentModel.getCoreferenceModel().addCoreferenceModelListener(entityComboBoxModel);

		FlagComboBoxModel flagDirectedComboBoxModel = new FlagComboBoxModel(DirectedEntityRelation.class);
		documentModel.getFlagModel().addFlagModelListener(flagDirectedComboBoxModel);

		JComboBox<Entity> entityCombobox = new JComboBox<Entity>(entityComboBoxModel);
		entityCombobox.setRenderer(new EntityListCellRenderer());

		JComboBox<Flag> directedflagCombobox = new JComboBox<Flag>(flagDirectedComboBoxModel);
		directedflagCombobox.setRenderer(new FlagListCellRenderer());

		DirectedRelationsTableModel directedRelationsTableModel = new DirectedRelationsTableModel(
				documentModel.getRelationModel());

		this.tableWithDirectedRelations = new JTable(directedRelationsTableModel);
		this.tableWithDirectedRelations.setGridColor(Color.GRAY);
		this.tableWithDirectedRelations.setAutoCreateColumnsFromModel(true);
		this.tableWithDirectedRelations.setAutoCreateRowSorter(true);
		this.tableWithDirectedRelations.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.tableWithDirectedRelations.setDefaultRenderer(Entity.class, new EntityTableCellRenderer());
		this.tableWithDirectedRelations.setDefaultRenderer(Flag.class, new FlagTableCellRenderer());
		this.tableWithDirectedRelations.setDefaultEditor(Entity.class, new DefaultCellEditor(entityCombobox));
		this.tableWithDirectedRelations.setDefaultEditor(Flag.class, new DefaultCellEditor(directedflagCombobox));
		this.tableWithDirectedRelations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tableWithDirectedRelations.setDropMode(DropMode.ON);
		this.tableWithDirectedRelations.setDragEnabled(true);
		this.tableWithDirectedRelations.setTransferHandler(directedRelationsTableModel.getTransferHandler());
		this.tableWithDirectedRelations.setRowHeight(25);

		this.tableWithDirectedRelations.getColumnModel().getColumn(0)
				.setHeaderRenderer(new DefaultTableHeaderCellRenderer() {

					private static final long serialVersionUID = 1L;

					@Override
					public String getToolTipText() {
						return Annotator.getString(Constants.Strings.FLAG_EDITOR_ICON_TOOLTIP);
					}
				});
		this.tableWithDirectedRelations.getColumnModel().getColumn(1)
				.setHeaderRenderer(new DefaultTableHeaderCellRenderer() {

					private static final long serialVersionUID = 1L;

					@Override
					public String getToolTipText() {
						return Annotator.getString(Constants.Strings.FLAG_EDITOR_KEY_TOOLTIP);
					}
				});
		this.tableWithDirectedRelations.getColumnModel().getColumn(2)
				.setHeaderRenderer(new DefaultTableHeaderCellRenderer() {

					private static final long serialVersionUID = 1L;

					@Override
					public String getToolTipText() {
						return Annotator.getString(Constants.Strings.FLAG_EDITOR_LABEL_TOOLTIP);
					}
				});
	}

	class EntityListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value != null) {
				setText(((Entity) value).getLabel());
				setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT, new Color(((Entity) value).getColor())));
			}
			return this;
		}
	}

	class EntityTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		String tooltipText;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value != null) {
				setText(((Entity) value).getLabel());
				tooltipText = ((Entity) value).getLabel();
				setIcon(FontIcon.of(MaterialDesign.MDI_ACCOUNT, new Color(((Entity) value).getColor())));
			}
			return this;
		}

		@Override
		public String getToolTipText() {
			return tooltipText;
		}
	}

	class FlagListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value != null) {
				setText(((Flag) value).getLabel());
				setIcon(FontIcon.of(MaterialDesign.valueOf(((Flag) value).getIcon())));
			}
			return this;
		}
	}

	class FlagTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			Flag flag = (Flag) value;
			if (flag != null) {
				setText(flag.getLabel());
				setIcon(FontIcon.of(MaterialDesign.valueOf(flag.getIcon())));
			}
			return this;
		}
	}

	class MyTableHeader extends JTableHeader {

		private static final long serialVersionUID = 1L;
		String[] tooltips;

		MyTableHeader(TableColumnModel columnModel, String[] columnTooltips) {
			super(columnModel);// do everything a normal JTableHeader does
			this.tooltips = columnTooltips;// plus extra data
		}

		@Override
		public String getToolTipText(MouseEvent e) {
			java.awt.Point p = e.getPoint();
			int index = columnModel.getColumnIndexAtX(p.x);
			int realIndex = columnModel.getColumn(index).getModelIndex();
			return this.tooltips[realIndex];
		}
	}

	class FlagEditorWindowListener implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosed(WindowEvent e) {

		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class MyHeaderRenderer extends DefaultTableHeaderCellRenderer {

		private static final long serialVersionUID = 1L;

	}

	class GridLayoutTable extends JPanel implements RelationModelListener {

		private static final long serialVersionUID = 1L;

		MutableMap<EntityRelation, Integer> componentMap = Maps.mutable.empty();
		GridLayout layout = new GridLayout(0, 1);

		public GridLayoutTable(DocumentModel documentModel) {
			setLayout(layout);
			documentModel.getRelationModel().addRelationModelListener(this);
		}

		protected void addRow(SymmetricEntityRelation relation) {
			JPanel rowPanel = new JPanel();
			FlagComboBoxModel flagUndirectedComboBoxModel = new FlagComboBoxModel(SymmetricEntityRelation.class);
			documentModel.getFlagModel().addFlagModelListener(flagUndirectedComboBoxModel);

			JComboBox<Flag> undirectedflagCombobox = new JComboBox<Flag>(flagUndirectedComboBoxModel);
			undirectedflagCombobox.setRenderer(new FlagListCellRenderer());

			rowPanel.add(new JButton(new DeleteAction(getDocumentWindow(), relation)));
			rowPanel.add(undirectedflagCombobox);
			rowPanel.add(addFSArray(relation, relation.getEntities()));

			layout.setRows(layout.getRows() + 1);
			int idx = -1;
			if (componentMap.containsKey(relation)) {
				idx = componentMap.get(relation);
				remove(idx);
			} else
				idx = getComponentCount();
			add(rowPanel, idx);
			componentMap.put(relation, idx);

			validate();
		}

		protected Component addFSArray(SymmetricEntityRelation relation, FSArray arr) {
			JPanel panel = new JPanel();
			for (int i = 0; i < arr.size(); i++) {
				Entity entity = (Entity) arr.get(i);
				EntityPanel el = new EntityPanel(documentModel, entity);
				el.setShowText(false);
				el.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						if (SwingUtilities.isRightMouseButton(e)) {
							JPopupMenu menu = new JPopupMenu();
							menu.add(new AbstractAction("delete") {

								private static final long serialVersionUID = 1L;

								@Override
								public void actionPerformed(ActionEvent e) {
									documentModel.edit(new UpdateEntityRelation(relation,
											EntityRelationProperty.REMOVE_ENTITY, entity));
								}

							});
							menu.show(el, e.getX(), e.getY());
						}
					}

				});
				panel.add(el);
			}
			return panel;
		}

		@Override
		public void relationEvent(FeatureStructureEvent event) {
			switch (event.getType()) {
			case Update:
			case Add:
				if (event.getArgument1() instanceof SymmetricEntityRelation) {
					addRow((SymmetricEntityRelation) event.getArgument1());
				}
				break;
			case Init:
				break;
			case Merge:
				break;
			case Move:
				break;
			case Op:
				break;
			case Remove:
				break;
			default:
				break;
			}
		}

	}

	public DocumentWindow getDocumentWindow() {
		return documentWindow;
	}

}

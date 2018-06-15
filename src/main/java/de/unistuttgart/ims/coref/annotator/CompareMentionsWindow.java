package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Set;
import java.util.function.Consumer;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.StyleContext;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.action.CloseAction;
import de.unistuttgart.ims.coref.annotator.action.CopyAction;
import de.unistuttgart.ims.coref.annotator.action.FileImportAction;
import de.unistuttgart.ims.coref.annotator.action.FileSelectOpenAction;
import de.unistuttgart.ims.coref.annotator.action.SelectedFileOpenAction;
import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.comp.BoundLabel;
import de.unistuttgart.ims.coref.annotator.comp.ColorIcon;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.document.Op;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.worker.DocumentModelLoader;

public class CompareMentionsWindow extends AbstractTextWindow
		implements HasTextView, CoreferenceModelListener, PreferenceChangeListener {

	public class TextCaretListener implements CaretListener {

		@Override
		public void caretUpdate(CaretEvent e) {
			if (textPane.getSelectionStart() != textPane.getSelectionEnd()) {
				Span span = new Span(textPane.getSelectionStart(), textPane.getSelectionEnd());
				stats.setAgreementInSpan(getAgreementInSpan(span));
			}
		}
	}

	class CopyMentionAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		Span span;

		public CopyMentionAction(Span span) {
			this.span = span;
			putValue(Action.NAME, "add");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			targetModel.edit(new Op.AddMentionsToNewEntity(span));
		}

	}

	class AnnotatorStatistics {
		int mentions = 0;
		int entities = 0;
		int entityGroups = 0;
		int lastMention = 0;
		int length = 0;

		public void analyze(JCas jcas, Consumer<Mention> cons) {
			length = jcas.getDocumentText().length();
			for (Mention m : JCasUtil.select(jcas, Mention.class)) {
				mentions++;
				if (m.getEnd() > lastMention)
					lastMention = m.getEnd();
				cons.accept(m);
			}
			for (Entity e : JCasUtil.select(jcas, Entity.class)) {
				entities++;
				if (e instanceof EntityGroup)
					entityGroups++;
			}
		}
	}

	class TextMouseListener implements MouseListener {

		protected JMenu getMentionMenu(Mention mention, String source) {
			JMenu menu = new JMenu(mention.getCoveredText() + "(" + source + ")");
			menu.add(new CopyMentionAction(new Span(mention)));
			return menu;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				JPopupMenu pMenu = new JPopupMenu();
				int offset = textPane.viewToModel(e.getPoint());

				for (int i = 0; i < models.size(); i++) {
					MutableList<Annotation> localAnnotations = Lists.mutable.withAll(models.get(i).getMentions(offset));
					MutableList<Annotation> mentions = localAnnotations
							.select(m -> m instanceof Mention || m instanceof DetachedMentionPart);
					for (Annotation m : mentions) {
						Mention mention = null;
						if (m instanceof Mention) {
							mention = (Mention) m;
						} else if (m instanceof DetachedMentionPart) {
							mention = ((DetachedMentionPart) m).getMention();
						}
						if (mention != null)
							pMenu.add(getMentionMenu(mention, String.valueOf(i)));
					}

				}
				pMenu.show(e.getComponent(), e.getX(), e.getY());

			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	private static final long serialVersionUID = 1L;
	MutableList<String> annotatorIds;
	MutableList<Action> open;

	AbstractAction copyAction;

	MutableList<JCas> jcas;
	MutableList<File> files;
	int loadedJCas = 0;
	int loadedCModels = 0;
	Annotator mainApplication;
	JPanel mentionsInfoPane;
	JPanel agreementPanel = null;

	MutableList<CoreferenceModel> models;
	MutableList<AnnotatorStatistics> annotatorStats;
	MutableList<MutableSetMultimap<Entity, Mention>> entityMentionMaps;

	AgreementStatistics stats = new AgreementStatistics();

	StyleContext styleContext = new StyleContext();

	JCas targetJCas;

	CoreferenceModel targetModel;

	boolean textIsSet = false;
	int size = 0;
	Color[] colors;
	JMenu fileMenu;

	public CompareMentionsWindow(Annotator mainApplication, int size) throws UIMAException {
		this.mainApplication = mainApplication;
		this.jcas = Lists.mutable.withNValues(size, () -> null);
		this.files = Lists.mutable.withNValues(size, () -> null);
		this.annotatorIds = Lists.mutable.withNValues(size, () -> null);
		this.open = Lists.mutable.withNValues(size, () -> null);
		this.annotatorStats = Lists.mutable.withNValues(size, () -> null);
		this.models = Lists.mutable.withNValues(size, () -> null);
		this.entityMentionMaps = Lists.mutable.withNValues(size, () -> Multimaps.mutable.set.empty());
		this.colors = new Color[size];
		ColorProvider cp = new ColorProvider();
		for (int i = 0; i < colors.length; i++) {
			this.colors[i] = cp.getNextColor();
		}
		this.size = size;
		Annotator.app.getPreferences().addPreferenceChangeListener(this);
		this.initialiseMenu();
		this.initialiseWindow();
		this.targetJCas = JCasFactory.createJCas();
	}

	protected void drawAll(JCas jcas, Color color) {
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			highlightManager.underline(m, color);

		}

	}

	protected void drawAllAnnotations() {
		if (loadedJCas < size)
			return;
		MutableList<MutableSet<Span>> mapList = Lists.mutable.empty();
		MutableMap<Span, Mention> map = Maps.mutable.empty();
		Span overlapping = new Span(Integer.MIN_VALUE, Integer.MAX_VALUE);
		int index = 0;
		for (JCas jcas : jcas) {
			MutableSet<Span> map1 = Sets.mutable.empty();

			Span annotatedRange = new Span(Integer.MAX_VALUE, Integer.MIN_VALUE);
			for (Mention m : JCasUtil.select(jcas, Mention.class)) {
				if (Annotator.app.getPreferences().getBoolean(Constants.CFG_IGNORE_SINGLETONS_WHEN_COMPARING,
						Defaults.CFG_IGNORE_SINGLETONS_WHEN_COMPARING)
						&& entityMentionMaps.get(index).get(m.getEntity()).size() <= 1)
					continue;
				map1.add(new Span(m));
				map.put(new Span(m), m);

				if (m.getEnd() > annotatedRange.end)
					annotatedRange.end = m.getEnd();
				if (m.getBegin() < annotatedRange.begin)
					annotatedRange.begin = m.getBegin();
			}
			mapList.add(map1);
			if (overlapping.begin < annotatedRange.begin)
				overlapping.begin = annotatedRange.begin;
			if (overlapping.end > annotatedRange.end)
				overlapping.end = annotatedRange.end;
			index++;
		}

		MutableSet<Span> intersection = Sets.mutable.withAll(mapList.getFirst());
		for (int i = 1; i < mapList.size(); i++) {
			intersection = intersection.intersect(mapList.get(i));
		}

		for (Span s : intersection) {
			highlightManager.underline(map.get(s), Color.gray.brighter());
		}
		int agreed = intersection.size();
		int total = agreed;
		int totalInOverlappingPart = agreed;
		for (int i = 0; i < mapList.size(); i++) {
			Set<Span> spans = mapList.get(i);
			for (Span s : spans) {
				if (!intersection.contains(s)) {
					highlightManager.underline(map.get(s), colors[i]);
					total++;
					if (overlapping.contains(s))
						totalInOverlappingPart++;
				}
			}
		}
		stats.setTotal(total);
		stats.setAgreed(agreed);
		stats.setTotalInOverlappingPart(totalInOverlappingPart);
		this.mentionsInfoPane.add(getAgreementPanel(), -1);
	}

	protected double getAgreementInSpan(Span s) {
		MutableList<MutableSet<Span>> mapList = Lists.mutable.empty();

		int total = 0;
		int index = 0;
		for (JCas jcas : jcas) {
			MutableSet<Span> map1 = Sets.mutable.empty();

			Annotation sel = new Annotation(jcas);
			sel.setBegin(s.begin);
			sel.setEnd(s.end);

			for (Mention m : JCasUtil.selectCovered(Mention.class, sel)) {
				if (Annotator.app.getPreferences().getBoolean(Constants.CFG_IGNORE_SINGLETONS_WHEN_COMPARING,
						Defaults.CFG_IGNORE_SINGLETONS_WHEN_COMPARING)
						&& entityMentionMaps.get(index).get(m.getEntity()).size() <= 1)
					continue;
				map1.add(new Span(m));
				total++;
			}
			mapList.add(map1);
			index++;
		}

		MutableSet<Span> intersection = Sets.mutable.withAll(mapList.getFirst());
		for (int i = 1; i < mapList.size(); i++) {
			intersection = intersection.intersect(mapList.get(i));
		}
		int agreed = intersection.size();

		total = total - ((jcas.size() - 1) * intersection.size());

		return 100 * agreed / (double) total;

	}

	protected JPanel getAgreementPanel() {
		if (agreementPanel == null) {
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(5, 2));
			Border border = BorderFactory.createTitledBorder(Annotator.getString(Strings.STAT_AGR_TITLE));
			panel.setBorder(border);
			panel.setPreferredSize(new Dimension(200, 70));

			JLabel desc;
			desc = new JLabel(Annotator.getString(Constants.Strings.STAT_KEY_TOTAL) + ":", SwingConstants.RIGHT);
			desc.setToolTipText(Annotator.getString(Constants.Strings.STAT_KEY_TOTAL_TOOLTIP));
			panel.add(desc);
			JLabel valueLabel = new BoundLabel(stats, "total", o -> o.toString(), stats.total());
			panel.add(valueLabel);

			desc = new JLabel(Annotator.getString(Constants.Strings.STAT_KEY_AGREED) + ":", SwingConstants.RIGHT);
			desc.setToolTipText(Annotator.getString(Constants.Strings.STAT_KEY_AGREED_TOOLTIP));
			panel.add(desc);
			panel.add(new BoundLabel(stats, "agreed", o -> String.format("%1$,3d", o), stats.getAgreed()));

			desc = new JLabel(Annotator.getString(Constants.Strings.STAT_KEY_AGREED_OVERALL) + ":",
					SwingConstants.RIGHT);
			desc.setToolTipText(Annotator.getString(Constants.Strings.STAT_KEY_AGREED_OVERALL_TOOLTIP));
			panel.add(desc);
			JLabel percTotalLabel = new JLabel(String.format("%1$3.1f%%", 100 * stats.agreed / (double) stats.total),
					SwingConstants.RIGHT);

			panel.add(percTotalLabel);

			desc = new JLabel(Annotator.getString(Constants.Strings.STAT_KEY_AGREED_PARALLEL) + ":",
					SwingConstants.RIGHT);
			desc.setToolTipText(Annotator.getString(Constants.Strings.STAT_KEY_AGREED_PARALLEL_TOOLTIP));
			panel.add(desc);
			JLabel percOvrLabel = new JLabel(
					String.format("%1$3.1f%%", 100 * stats.agreed / (double) stats.totalInOverlappingPart),
					SwingConstants.RIGHT);
			panel.add(percOvrLabel);

			desc = new JLabel(Annotator.getString(Constants.Strings.STAT_KEY_AGREED_SELECTED) + ":",
					SwingConstants.RIGHT);
			desc.setToolTipText(Annotator.getString(Constants.Strings.STAT_KEY_AGREED_SELECTED_TOOLTIP));
			panel.add(desc);
			JLabel selectedAgreementLabel = new BoundLabel(stats, "agreementInSpan",
					o -> String.format("%1$3.1f%%", o));
			selectedAgreementLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			panel.add(selectedAgreementLabel);

			stats.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {

					if (evt.getPropertyName().equals("total") || evt.getPropertyName().equals("agreed")) {
						percTotalLabel.setText(
								String.format("%1$3.1f%%", 100 * stats.getAgreed() / (double) stats.getTotal()));
					}
					if (evt.getPropertyName().equals("totalInOverlappingPart")
							|| evt.getPropertyName().equals("agreed")) {
						percOvrLabel.setText(String.format("%1$3.1f%%",
								100 * stats.getAgreed() / (double) stats.getTotalInOverlappingPart()));
					}
				}

			});

			this.agreementPanel = panel;

		}
		return agreementPanel;
	}

	protected JPanel getAnnotatorPanel(int index) {

		AnnotatorStatistics stats = annotatorStats.get(index);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(5, 2));
		Border border = BorderFactory.createTitledBorder(annotatorIds.get(index));
		panel.setBorder(border);
		panel.setPreferredSize(new Dimension(200, 75));
		JLabel desc;

		// color
		desc = new JLabel(Annotator.getString(Constants.Strings.STAT_KEY_COLOR) + ":", SwingConstants.RIGHT);
		desc.setToolTipText(Annotator.getString(Constants.Strings.STAT_KEY_COLOR_TOOLTIP));
		panel.add(desc);
		panel.add(new JLabel(new ColorIcon(30, 10, colors[index]), SwingConstants.RIGHT));

		// number of mentions
		desc = new JLabel(Annotator.getString(Constants.Strings.STAT_KEY_MENTIONS) + ":", SwingConstants.RIGHT);
		desc.setToolTipText(Annotator.getString(Constants.Strings.STAT_KEY_MENTIONS_TOOLTIP));
		panel.add(desc);
		panel.add(new JLabel(String.valueOf(stats.mentions), SwingConstants.RIGHT));

		// number of entities
		desc = new JLabel(Annotator.getString(Constants.Strings.STAT_KEY_ENTITIES) + ":", SwingConstants.RIGHT);
		desc.setToolTipText(Annotator.getString(Constants.Strings.STAT_KEY_ENTITIES_TOOLTIP));
		panel.add(desc);
		panel.add(new JLabel(String.valueOf(stats.entities), SwingConstants.RIGHT));

		// annotation position
		desc = new JLabel(Annotator.getString(Constants.Strings.STAT_KEY_POSITION) + ":", SwingConstants.RIGHT);
		desc.setToolTipText(Annotator.getString(Constants.Strings.STAT_KEY_POSITION_TOOLTIP));
		panel.add(desc);
		panel.add(new JLabel(
				String.format("%1$,3d (%2$3.1f%%)", stats.lastMention, 100 * stats.lastMention / (double) stats.length),
				SwingConstants.RIGHT));

		panel.add(new JLabel(Annotator.getString(Constants.Strings.ACTION_OPEN) + ":", SwingConstants.RIGHT));
		panel.add(new JButton(open.get(index)));

		return panel;
	}

	@Override
	public Span getSelection() {
		return new Span(textPane.getSelectionStart(), textPane.getSelectionEnd());
	}

	@Override
	public String getText() {
		return textPane.getText();
	}

	protected void initialiseActions() {
		copyAction = new CopyAction(this);
	};

	protected void initialiseMenu() {

		JMenu helpMenu = new JMenu(Annotator.getString(Strings.MENU_HELP));
		helpMenu.add(mainApplication.helpAction);

		menuBar.add(initialiseMenuFile());
		menuBar.add(initialiseMenuSettings());
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		Annotator.logger.info("Initialised menus");
	}

	protected synchronized void initialiseText(JCas jcas2) {
		if (textIsSet)
			return;
		textPane.setText(jcas2.getDocumentText().replaceAll("\r", " "));
		textPane.setCaretPosition(0);
		textIsSet = true;

		StyleManager.styleCharacter(textPane.getStyledDocument(), StyleManager.getDefaultCharacterStyle());
		StyleManager.styleParagraph(textPane.getStyledDocument(), StyleManager.getDefaultParagraphStyle());

		drawAllAnnotations();
	}

	protected void initialiseWindow() {

		super.initializeWindow();
		Caret caret = new Caret();

		// JTabbedPane tabbedPane = new JTabbedPane();
		textPane = new JTextPane();
		textPane.setPreferredSize(new Dimension(500, 800));
		textPane.setDragEnabled(false);
		textPane.setEditable(false);
		textPane.setCaret(caret);
		textPane.getCaret().setVisible(true);
		textPane.addFocusListener(caret);
		textPane.setCaretPosition(0);
		// mentionsTextPane.addMouseListener(new TextMouseListener());
		textPane.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
				copyAction);
		textPane.addCaretListener(new TextCaretListener());

		mentionsInfoPane = new JPanel();
		mentionsInfoPane.setLayout(new BoxLayout(mentionsInfoPane, BoxLayout.Y_AXIS));
		mentionsInfoPane.setPreferredSize(new Dimension(210, 750));
		mentionsInfoPane.setMaximumSize(new Dimension(250, 750));
		mentionsInfoPane.add(new JLabel());
		mentionsInfoPane.add(new JLabel());
		mentionsInfoPane.add(new JLabel());

		mentionsInfoPane.add(Box.createVerticalGlue());
		mentionsInfoPane.add(Box.createVerticalGlue());
		mentionsInfoPane.add(Box.createVerticalGlue());
		mentionsInfoPane.add(Box.createVerticalGlue());

		JSplitPane mentionsPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(textPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), mentionsInfoPane);
		mentionsPane.setDividerLocation(500);

		// tabbedPane.add("Mentions", mentionsPane);
		// add(tabbedPane, BorderLayout.CENTER);
		add(mentionsPane, BorderLayout.CENTER);
		highlightManager = new HighlightManager(textPane);
		setPreferredSize(new Dimension(800, 800));
		pack();
	}

	private void finishLoading() {
		if (loadedCModels >= files.size()) {
			super.stopIndeterminateProgress();
		}
	}

	public void setCoreferenceModel(DocumentModel cm, int index) {
		models.set(index, cm.getCoreferenceModel());
		loadedCModels++;
		finishLoading();
	}

	public void setJCas(JCas jcas, String annotatorId, int index) {
		this.jcas.set(index, jcas);
		this.annotatorIds.set(index, annotatorId);
		this.annotatorStats.set(index, new AnnotatorStatistics());
		this.annotatorStats.get(index).analyze(jcas, m -> {
			entityMentionMaps.get(index).put(m.getEntity(), m);
		});
		loadedJCas++;
		if (!textIsSet)
			initialiseText(jcas);
		mentionsInfoPane.add(getAnnotatorPanel(index), index);
		drawAllAnnotations();
		mentionsInfoPane.add(getAgreementPanel(), -1);
		new DocumentModelLoader(cm -> setCoreferenceModel(cm, index), jcas).execute();
		revalidate();
	}

	@Override
	protected void entityEventMove(FeatureStructureEvent event) {

	}

	@Override
	protected void entityEventMerge(FeatureStructureEvent event) {

	}

	@Override
	protected void entityEventOp(FeatureStructureEvent event) {

	}

	protected JMenu initialiseMenuFile() {
		JMenu fileImportMenu = new JMenu(Annotator.getString(Strings.MENU_FILE_IMPORT_FROM));

		PluginManager pm = mainApplication.getPluginManager();
		for (Class<? extends IOPlugin> pluginClass : pm.getIOPlugins()) {
			try {
				IOPlugin plugin = pm.getIOPlugin(pluginClass);
				if (plugin.getImporter() != null)
					fileImportMenu.add(new FileImportAction(mainApplication, plugin));
			} catch (ResourceInitializationException e) {
				Annotator.logger.catching(e);
			}

		}

		fileMenu = new JMenu(Annotator.getString(Strings.MENU_FILE));
		fileMenu.add(new FileSelectOpenAction(mainApplication));
		fileMenu.add(mainApplication.getRecentFilesMenu());
		fileMenu.add(fileImportMenu);
		fileMenu.add(new CloseAction());
		fileMenu.add(mainApplication.quitAction);

		return fileMenu;
	}

	public void setFile(File file, int index) {
		this.files.set(index, file);
		this.open.set(index, new SelectedFileOpenAction(Annotator.app, file));

	}

	public void setFiles(Iterable<File> files) {
		this.files = Lists.mutable.withAll(files);
		this.open = this.files.collect(f -> new SelectedFileOpenAction(Annotator.app, f));
		JMenu currentFilesMenu = new JMenu(Annotator.getString(Constants.Strings.ACTION_OPEN));
		this.open.forEach(a -> currentFilesMenu.add(a));
		fileMenu.add(currentFilesMenu, 1);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey() == Constants.CFG_IGNORE_SINGLETONS_WHEN_COMPARING) {
			highlightManager.hilit.removeAllHighlights();
			drawAllAnnotations();
		}
	}

}

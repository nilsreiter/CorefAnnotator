package de.unistuttgart.ims.coref.annotator.plugin.xlsx;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.RangedHashSetValuedHashMap;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.v2.Flag;
import de.unistuttgart.ims.coref.annotator.api.v2.Line;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.Segment;
import de.unistuttgart.ims.coref.annotator.plugin.csv.CsvExportPlugin.ContextUnit;
import de.unistuttgart.ims.coref.annotator.plugins.SingleFileStream;
import de.unistuttgart.ims.coref.annotator.uima.AnnotationLengthComparator;
import de.unistuttgart.ims.coref.annotator.uima.MentionComparator;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

public class XLSXWriter extends SingleFileStream {

	private static final String ENTITY_GROUP = "entityGroup";
	private static final String ENTITY_LABEL = "entityLabel";
	private static final String ENTITY_NUM = "entityNum";
	private static final String SURFACE = "surface";
	private static final String END = "end";
	private static final String END_LINE = "end_line";
	private static final String END_SEGMENT = "end_segment";
	private static final String BEGIN = "begin";
	private static final String BEGIN_LINE = "begin_line";
	private static final String BEGIN_SEGMENT = "begin_segment";
	private static final String CONTEXT_LEFT = "leftContext";
	private static final String CONTEXT_RIGHT = "rightContext";

	public static final String PARAM_CONTEXTWIDTH = "context width";
	@ConfigurationParameter(name = PARAM_CONTEXTWIDTH, defaultValue = "0", mandatory = false)
	int optionContextWidth = 0;

	public static final String PARAM_TRIM_WHITESPACE = "trim whitespace";
	@ConfigurationParameter(name = PARAM_TRIM_WHITESPACE, defaultValue = "true", mandatory = false)
	boolean optionTrimWhitespace = true;

	public static final String PARAM_REPLACE_NEWLINES = "replace newlines";
	@ConfigurationParameter(name = PARAM_REPLACE_NEWLINES, defaultValue = "false", mandatory = false)
	boolean optionReplaceNewlines = false;

	public static final String PARAM_CONTEXT_UNIT = "PARAM_CONTEXT_UNIT";
	@ConfigurationParameter(name = PARAM_CONTEXT_UNIT, defaultValue = "CHARACTER")
	Plugin.ContextUnit optionContextUnit = ContextUnit.CHARACTER;

	public static final String PARAM_INCLUDE_LINE_NUMBERS = "PARAM_INCLUDE_LINE_NUMBERS";
	@ConfigurationParameter(name = PARAM_INCLUDE_LINE_NUMBERS, defaultValue = "false")
	boolean optionIncludeLineNumbers = false;

	public static final String PARAM_SEPARATE_SHEETS_FOR_ENTITIES = "PARAM_SEPARATE_SHEETS_FOR_ENTITIES";
	@ConfigurationParameter(name = PARAM_SEPARATE_SHEETS_FOR_ENTITIES, defaultValue = "false")
	boolean optionSeparateSheetsForEntities = false;

	Iterable<Entity> entities = null;
	String replacementForNewlines = " ";

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		configure();
	}

	public void configure() {
		if (optionContextUnit == ContextUnit.LINE)
			replacementForNewlines = " // ";
	}

	@Override
	public void write(JCas jcas, OutputStream os) throws IOException {

		ImmutableList<Mention> allMentions = Lists.mutable.withAll(JCasUtil.select(jcas, Mention.class))
				.sortThis(new MentionComparator()).toImmutable();
		ImmutableList<Flag> allFlags = Lists.immutable.withAll(JCasUtil.select(jcas, Flag.class));

		ImmutableList<Flag> mentionFlags = allFlags
				.select(f -> f.getTargetClass().equalsIgnoreCase(Mention.class.getName()));
		ImmutableList<Flag> entityFlags = allFlags
				.select(f -> f.getTargetClass().equalsIgnoreCase(Entity.class.getName()));

		RangedHashSetValuedHashMap<Segment> segmentIndex = new RangedHashSetValuedHashMap<Segment>();
		RangedHashSetValuedHashMap<Line> lineIndex = new RangedHashSetValuedHashMap<Line>();
		if (optionIncludeLineNumbers) {
			for (Line line : JCasUtil.select(jcas, Line.class))
				lineIndex.add(line);
			for (Segment segment : JCasUtil.select(jcas, Segment.class))
				segmentIndex.add(segment);
		}

		if (entities == null)
			entities = JCasUtil.select(jcas, Entity.class);

		@SuppressWarnings("resource")
		Workbook wb = new XSSFWorkbook();
		CellStyle cs = wb.createCellStyle();
		cs.setWrapText(true);

		Sheet sheet = null;

		int cellNum = 0;
		if (!optionSeparateSheetsForEntities) {
			sheet = wb.createSheet("Export");
			printHeader(sheet, entityFlags, mentionFlags, segmentIndex.notEmpty());
		}

		int rowNum = 1;
		Cell cell;
		Row row;
		int entityNum = 0;
		int surfaceColumn = Integer.MIN_VALUE;
		for (Entity entity : entities) {
			if (optionSeparateSheetsForEntities) {
				sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(entity.getLabel()));
				printHeader(sheet, entityFlags, mentionFlags, segmentIndex.notEmpty());
				rowNum = 1;
			}
			for (Mention mention : allMentions.select(m -> m.getEntity() == entity)) {
				row = sheet.createRow(rowNum++);
				cellNum = 0;
				String surface = UimaUtil.getCoveredText(mention);
				if (mention.getDiscontinuous() != null)
					surface += " " + mention.getDiscontinuous().getCoveredText();
				if (optionReplaceNewlines)
					surface = surface.replaceAll(" ?[\n\r\f]+ ?", replacementForNewlines);
				row.createCell(cellNum++).setCellValue(UimaUtil.getBegin(mention));
				row.createCell(cellNum++).setCellValue(UimaUtil.getEnd(mention));
				if (optionIncludeLineNumbers) {
					if (segmentIndex.notEmpty()) {
						Segment segment = Lists.mutable.ofAll(segmentIndex.get(UimaUtil.getBegin(mention)))
								.min(new AnnotationLengthComparator<Segment>());
						row.createCell(cellNum++).setCellValue(segment.getLabel());
						segment = Lists.mutable.ofAll(segmentIndex.get(UimaUtil.getEnd(mention)))
								.min(new AnnotationLengthComparator<Segment>());
						row.createCell(cellNum++).setCellValue(segment.getLabel());
					}
					try {
						row.createCell(cellNum++).setCellValue(
								JCasUtil.selectPreceding(Line.class, mention.getSurface(0), 1).get(0).getNumber());
					} catch (Exception e) {
						row.createCell(cellNum++).setCellValue(-1);
					}
					try {
						Annotation a = new Annotation(jcas);
						a.setBegin(UimaUtil.getEnd(mention));
						a.setEnd(UimaUtil.getEnd(mention));

						row.createCell(cellNum++)
								.setCellValue(JCasUtil.selectPreceding(Line.class, a, 1).get(0).getNumber());
					} catch (Exception e) {
						row.createCell(cellNum++).setCellValue(-1);
					}
				}
				if (optionContextWidth > 0) {
					try {

						String contextString = getContext(jcas, mention, true);
						if (optionReplaceNewlines)
							contextString = contextString.replaceAll(" ?[\n\r\f]+ ?", replacementForNewlines);
						cell = row.createCell(cellNum++);
						cell.setCellValue(contextString);
						cell.setCellStyle(cs);
					} catch (NoSuchElementException e) {
						row.createCell(cellNum++).setCellValue("");
					}
				}
				surfaceColumn = cellNum;
				row.createCell(cellNum++).setCellValue((optionTrimWhitespace ? surface.trim() : surface));
				if (optionContextWidth > 0) {
					try {
						String contextString = getContext(jcas, mention, false);
						if (optionReplaceNewlines)
							contextString = contextString.replaceAll(" ?[\n\r\f]+ ?", replacementForNewlines);
						cell = row.createCell(cellNum++);
						cell.setCellValue(contextString);
						cell.setCellStyle(cs);
					} catch (NoSuchElementException e) {
						row.createCell(cellNum++).setCellValue("");
					}
				}
				row.createCell(cellNum++).setCellValue(entityNum);
				row.createCell(cellNum++).setCellValue(entity.getLabel());
				row.createCell(cellNum++).setCellValue((entity instanceof EntityGroup));
				for (Flag flag : entityFlags) {
					row.createCell(cellNum++).setCellValue(Util.isX(entity, flag.getKey()));
				}
				for (Flag flag : mentionFlags) {
					row.createCell(cellNum++).setCellValue(Util.isX(mention, flag.getKey()));
				}
			}
			entityNum++;
			sheet.setColumnWidth(surfaceColumn - 1, 40 * 256);
			sheet.setColumnWidth(surfaceColumn, 40 * 256);
			sheet.setColumnWidth(surfaceColumn + 1, 40 * 256);
			sheet.createFreezePane(0, 1, 0, 1);
		}
		wb.write(os);
	}

	protected void printHeader(Sheet sheet, Iterable<Flag> flags1, Iterable<Flag> flags2, boolean includeSegments) {
		Font ft = sheet.getWorkbook().createFont();
		ft.setBold(true);
		CellStyle cs = sheet.getWorkbook().createCellStyle();
		cs.setFont(ft);

		int cellNum = 0;
		Row row = sheet.createRow(0);
		Cell cell;

		cell = row.createCell(cellNum++);
		cell.setCellValue(BEGIN);

		cell = row.createCell(cellNum++);
		cell.setCellValue(END);

		if (optionIncludeLineNumbers) {
			if (includeSegments) {
				row.createCell(cellNum++).setCellValue(BEGIN_SEGMENT);
				row.createCell(cellNum++).setCellValue(END_SEGMENT);
			}

			row.createCell(cellNum++).setCellValue(BEGIN_LINE);
			row.createCell(cellNum++).setCellValue(END_LINE);
		}
		if (optionContextWidth > 0) {
			row.createCell(cellNum++).setCellValue(CONTEXT_LEFT);
		}
		row.createCell(cellNum++).setCellValue(SURFACE);
		if (optionContextWidth > 0) {
			row.createCell(cellNum++).setCellValue(CONTEXT_RIGHT);
		}
		row.createCell(cellNum++).setCellValue(ENTITY_NUM);
		row.createCell(cellNum++).setCellValue(ENTITY_LABEL);
		row.createCell(cellNum++).setCellValue(ENTITY_GROUP);

		for (Flag flag : flags1) {
			row.createCell(cellNum++).setCellValue(Annotator.getStringWithDefault(flag.getLabel(), flag.getLabel()));
		}
		for (Flag flag : flags2) {
			row.createCell(cellNum++).setCellValue(Annotator.getStringWithDefault(flag.getLabel(), flag.getLabel()));
		}

		Iterator<Cell> ci = row.cellIterator();
		while (ci.hasNext())
			ci.next().setCellStyle(cs);
	}

	protected String getContext(JCas jcas, Mention mention, boolean backward) {
		String text = jcas.getDocumentText();
		switch (optionContextUnit) {
		case LINE:
			if (backward) {
				int leftEnd = Lists.immutable
						.withAll(JCasUtil.selectPreceding(Line.class, mention.getSurface(0), optionContextWidth))
						.collect(l -> l.getBegin()).min();
				if (optionTrimWhitespace) {
					return text.substring(leftEnd, UimaUtil.getBegin(mention)).trim();
				} else {
					return text.substring(leftEnd, UimaUtil.getBegin(mention));
				}
			} else {
				int rightEnd = Lists.immutable
						.withAll(JCasUtil.selectFollowing(Line.class, UimaUtil.getLast(mention), optionContextWidth))
						.collect(l -> l.getEnd()).max();
				if (optionTrimWhitespace) {
					return text.substring(UimaUtil.getEnd(mention), rightEnd).trim();
				} else {
					return text.substring(UimaUtil.getEnd(mention), rightEnd);
				}
			}
		default:
			if (backward)
				if (optionTrimWhitespace) {
					return StringUtils.right(text.substring(0, UimaUtil.getBegin(mention)).trim(), optionContextWidth);
				} else {
					return StringUtils.right(text, optionContextWidth);
				}
			else if (optionTrimWhitespace) {
				return StringUtils.left(text.substring(UimaUtil.getEnd(mention)).trim(), optionContextWidth);
			} else {
				return StringUtils.left(text, optionContextWidth);
			}
		}
	}

	public Iterable<Entity> getEntities() {
		return entities;
	}

	public void setEntities(Iterable<Entity> entities) {
		this.entities = entities;
	}

	public int getOptionContextWidth() {
		return optionContextWidth;
	}

	public void setOptionContextWidth(int optionContextWidth) {
		this.optionContextWidth = optionContextWidth;
	}

	public boolean isOptionTrimWhitespace() {
		return optionTrimWhitespace;
	}

	public void setOptionTrimWhitespace(boolean optionTrimWhitespace) {
		this.optionTrimWhitespace = optionTrimWhitespace;
	}

	public boolean isOptionReplaceNewlines() {
		return optionReplaceNewlines;
	}

	public void setOptionReplaceNewlines(boolean optionReplaceNewlines) {
		this.optionReplaceNewlines = optionReplaceNewlines;
	}

	public Plugin.ContextUnit getOptionContextUnit() {
		return optionContextUnit;
	}

	public void setOptionContextUnit(Plugin.ContextUnit optionContextUnit) {
		this.optionContextUnit = optionContextUnit;
	}

	public boolean isOptionIncludeLineNumbers() {
		return optionIncludeLineNumbers;
	}

	public void setOptionIncludeLineNumbers(boolean optionIncludeLineNumbers) {
		this.optionIncludeLineNumbers = optionIncludeLineNumbers;
	}

}

# Changelog

Issue numbers (e.g., #43) refer to GitHub issues:
https://github.com/nilsreiter/CorefAnnotator/issues

## 1.5.0

- Searching in the tree view now behaves slightly different: If a new 
  mention is dragged in (or added a key) while the tree view is ranked 
  according to the search, the ranking remains after having added the 
  mention. If the user presses enter, the ranking (and search) is 
  removed. #100
- Added a new command to automatically remove all singletons. Empty
  entities are also removed at the same time. #103
- If a text segment has been selected, clicking on the context menu 
  shows the option "copy formatted example". This copies the selection 
  with their markdown or plain text formatted annotations onto the 
  clipboard. Currently, the markdown format uses <sub></sub> to 
  subscript, but other formats can be included. #110
- **New type system**: This version introduces a modified type system, 
  which entails that the file format has to change. At this point, 
  the changes are not significant, but provide the stage for later 
  changes. When an old file is opened for the first time, it gets 
  converted automatically. #101
- The user can now control whether singletons are ignored for comparing
  two annotation files. #106

## 1.4.3

- Fixed an issue with the TEI/XML handling. The base TEI IO plugin now
  *only* handles elements with `@ref` attribute, and does not read or 
  write `@xml:id` attributes. This is in line with the documentation 
  of the `@ref` attribute, which is not restricted to XML IDs. #118

## 1.4.2

- Fixed that detached mention parts were not included in the tree
  after loading a file. #111
- Fixed that warning dialogs were displayed even if all changes were 
  saved. The downside is that saving now clears the history. #109
- Delete everything now deletes everything. #97
- Fixed that the settings menu would disappear on Windows if multiple
  windows are opened. #113

## 1.4.1

- App now launches on a Mac with Java 9 and higher. #71
- Mentions can now be moved into an entity group properly #108


## 1.4

- Selecting text in the compare view now displays agreement calculated 
  in this section on the right #79 
- Annotated files can now be merged. This can be used if different 
  annotators have worked on different parts of the text. The merge is 
  done as is -- entities need to be merged by hand after the documents 
  have been merged. #73
- Duplicate mentions (covering the same span) can now be deleted 
  automatically. This is done via the context menu for entities and 
  should be useful after files have been merged. #81 
- Mention export: Mentions of selected entities can now be exported as a 
  CSV file. The CSV file contains the surface, the span (character 
  positions), the mention label and number, all mention and entity 
  properties (ambiguous, generic etc.). This can be used for quick
  and dirty statistical analysis of an entity (R-script available). #94
- Entity groups can now be formed from more than two selected 
  entities. Only the first two will be used to create the label. The 
  'and' in the label is now translatable. #96
- Automatic support: The tools menu now contains a sub menu with 
  automatic helpers. At the moment, StanfordNER can be run on a number
  of languages. See help window for details. #89
- Fixes that the first mention of a chain was missing when importing 
  from TEI #92

## 1.3.3

- Fixed a bug that caused loading to crash if in previous runs, entities
  had been removed under certain conditions. Files can now be opened, but
  group members may be missing. #93 
- Fixed a bug that could cause subsequent loading issues. If an entity 
  was part of a group, and then removed entirely, a null-reference would
  appear in the group and causing loading to crash. #95

## 1.3.2

- Fixed a bug that caused saved files to be written in another file name after export #90

## 1.3.1

- Fixed a bug that caused multiple removal actions to be individual undos #78
- Fixed that the TEI import did not include the header #80
- Fixed a bug that prevented the removal of an entity from an entity group to be undoable #82
- Fixed: Add buttons in search panels are now inactive when now findings are selected #83
- Attempt at fixing a crash bug in the search panel #70

## 1.3

- Add TEI import plugin. In TEI files, everything that as a `ref` 
  attribute is considered a mention. Elements with `who` attribute are 
  treated specially, because dramas (i.e., the sub-ordinate `speaker`-
  element is considered the mention). #72
- Undo support. Pressing ctrl/cmd-z undos the last action. The history 
  is saved as long as the document is left open. #65
- Generation of candidates. Clicking on a selected text shows the 
  context menu, which now contains a list of candidate entities (as well
  as the option to create a new entity) #58
- Compare view. A new view allows comparing the works of two annotators. 
  This view only compares the spans of the mentions -- there is 
  currently no way of comparing the actual entity assignments. #69. 


## 1.2.1

- Application now also starts on Windows and Linux #68

## 1.2 

- Findings can be dragged from search window and dropped onto the entity tree #49
- Main window informs if new version is available #60
- Annotation also works by dropping on any already annotated mention within the text view #1
- Document language can now be set before or after importing
- Post-correction of automatic CR: #43
- We can now import CoNLL 2012, which is generated by IMS HotCoref DE
- English texts can be run through Stanford Core NLP CR via an import plugin
- Selected text can now be copied to system clipboard #61
- Groups can be created via context menu
- Groups have a better icon
- Arbitrary segments of the document can be selected and commented (via the menu bar). Existing comments are visible in the text and in a comment viewer, where they can be edited and deleted. #25
- bug fixes
  - First/last token can now be properly annotated #67

## 1.1.2

- Fixed issue that files were not saved properly because overwriting didn't work #63
- Fixed issue that button in search panel didn't became active after selecting an entity in tree #59

## 1.1.1

- Fixed shortcut window #56
- Add possibility to mark mentions as non-nominal #55
- Fixed merging of multiple entities #54

## 1.1

- If multiple things are now selected in the tree, they can be deleted at once
- Entities can now be merged. All mentions of the smaller entity are moved to the larger one, and the smaller one gets deleted #36
- Increased line spacing to improve readability #35
- (re-)Sorting the tree while searching is now much faster #40
- Export to CoNLL 2012 #37
- It is now possible to mark findings in the search panel and annotate them at once, either to an existing or a new entity #41
- Multiple things can be moved at once #33
- Entities can be hidden, such that their mentions are no longer underlined #46

## 1.0.4

- Fixed that multiple entities could be unintentionally merged when exporting to CRETA/Adorno #48
- Fixed that original annotations (that have been imported) are still present in the export when using CRETA/Adorno #47

## 1.0.3

- Fixed that the export dialog was not shown #44
- Fixed a bug preventing application launch #45

## 1.0.2

- Fixed a bug in which some underlines were printed on top of each other #38
- Fixed a bug preventing loading of texts if a title was not set for the document #39

## 1.0.1

- Fixed a bug when loading DKpro formatted files (including WebAnno) #32


## 1.0
- First release
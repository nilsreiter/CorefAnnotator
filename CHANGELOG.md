# Changelog

Issue numbers (e.g., #43) refer to GitHub issues:
https://github.com/nilsreiter/CorefAnnotator/issues

## 1.12.0

- The app now includes a command line tool to convert multiple 
  files at once. #242
- Added a SFB1391 specific importer than interprets `<lb>` tags 
  properly #255
- Export mention statistics now has some configuration options. 
  Import and export plugins can have that as well now. #76
- Sorting of entities in the tree is now case insensitive #257
- Shortcut keys are now displayed in brackets #256
- We can now specify a profile when loading the application #249. 
  The profile is stored in an XML file called `profile.xml` in the same
  directory as the XMI file to be loaded. The profile can
  - pre-define flags and entities
  - disallow operations
  - set configuration options
  

## 1.11.2

- Using the QuaDramA/TEI importer no longer generates files in the
  old format #246
- Flags can now be deleted properly #247

## 1.11.1

- Entities in tree are now sortable again #241

## 1.11.0

- JavaFX is now optional. If JavaFX classes can't be loaded, the application
  uses file open/save windows from classic swing. #220
- The app now compiles with OpenJDK properly. 
  Tested using OpenJDK 12 #237
- Updated guava from 28.0-jre to 28.1-jre #236
- Updated log4j-core from 2.12.0 to 2.12.1 #234
- Updated log4j-api from 2.12.0 to 2.12.1 #233
- Updated mockito-core from 2.27.0 to 3.0.0 #229

## 1.10.0

- With a new toggle setting, assigning a mention over an already 
  existing mention replaces the annotation #149
- The status bar at the bottom now shows the entity name if the cursor
  is within a mention of an entity. Thanks @andreasvc for the initial 
  code contribution! #189
- Line spacing can now be set explicitly in the View menu #226
- Adjacent mentions that belong to the same entity can now be merged
  with a single action. Two mentions are considered adjacent, if there is
  only whitespace between them. #218
- Entity label in context menu now has limited width #232

## 1.9.6

- Improved painting of underlinings for long annotations and a lot of 
  overlapping annotations #225


## 1.9.5

- Fixed the loading of GerDraCor files #219
- Fixed that the entity tree is not updated when mentions are moved #217
- Entities can be hidden again properly #216

## 1.9.4

- Improved stability of the grouping mechanism. #210

## 1.9.3

- Fixed a bug that prevented the display of the file open dialog #207
- Deleting many empty entities no longer creates an infinite loop 
  on OpenJDK #192
- Less console output and file logging (but can be activated if needed)

## 1.9.2

- CoNLL export now also works if no entities have been annotated. #191
- Fixed a bug that prevented mentions with the same begin position to be 
  underlined. #186
- Exporting to CoNLL format now shows the proper file extensions and 
  allows overwriting. #187

## 1.9.1

- The shortcut key to jump to mentions has been changed to alt-arrow, 
  because shift arrow collided with extending the selection by keyboard.
  #185

## 1.9.0

- The CoNLL 2012 importer now always reads coreference information from
  the last column. Thanks @andreasvc. #127
- The README now contains a section on requirements and installation, 
  specifying the JavaFX dependency explicitly. Thanks @andreasvc. #174
- When searching an entity in the tree view, the tree now scrolls to 
  top automatically. #178
- Using shift-arrow, the user can now jump from one annotated piece to 
  the next. This function does not entirely iterate over mentions. If 
  two mentions have the exact same boundaries, pressing the combo again 
  jumps to the next mention with different boundaries. #182
- Entities can now be automatically renamed. The new name is derived 
  from the first, last, or longest mention. This is decided ad hoc in 
  a dialog. The renaiming is fully undoable, so users can try with 
  different options. #173
- Pressing backspace while a mention is selected removes it. Multiple 
  mentions with the same span are deleted as well. ctrl-backspace 
  deletes *all* mentions in the selected area. #175
- The label of the entity of a selected mention is now displayed in the 
  status bar #176
- Some formatting information can now be read from TEI/XML files and 
  will be displayed accordingly. All `<head>` elements are shown large,
  everything with a `rend=italic` or `rend=bold` will be display as 
  such. In addition `<emph>` is rendered as italic. `<lg>` and `<div>`
  elements will be marked as segments (and if they have `<head>`, 
  they're displayed in the scrollbar). #181

## 1.8.0

- It's now possible to create new flags that can be assigned to entities, 
  mentions, and detached mention parts. The flags can be created with
  the flag editor (in the tools menu). #126
- The text view can now display line numbers. See 
  [the wiki](https://github.com/nilsreiter/CorefAnnotator/wiki/Line-Numbers) for 
  details. #169 
- The text field in the search dialog is now automatically in focus when
  the window is opened #159
- The text field now maintains previous searches #161
- The search can now be restricted to mentions only #162
- We now use a JavaFX-based file dialog, which should look more native 
  to the OS (no issue)

## 1.7.4

- If dragging, the entity mention tree no longer reacts on moving over 
  mentions, i.e., the text no longer jumps to the mention if something 
  is dragged #170
- Moving a mention into an entity group was leading to file corruption.
  Files could no longer be opened. This is fixed, and corrupted files
  can be opened again. #171

## 1.7.3

- Renamed an entity is now properly undoable #167
- Setting the language now actually works #168

## 1.7.2

- Fixed a bug that prevented loading files when run on OpenJDK #166

## 1.7.1

- Fixed a bug that changed the entity name to null if its' properties
  are changed #163
- Fixed a bug that showed a wrong icon when editing the entity name 
  #164
- It's no longer possible to rename mentions 

## 1.7.0

- If a group contains more than two entities, the number of additional 
  entities is now displayed in the label #156
- Long entity names are now abbreviated, but tooltips show the full 
  name
- Entity names can be edited by double clicking on it in the tree view
  #152
- Deleting a mention no longer collapses the entity tree, even if the 
  tree is automatically sorted by mention count (in this case, the 
  expanded sub tree may be moved). #155

## 1.6.1

- Fixed a bug with the export to TEI. XML ids may now include special 
  characters #157

## 1.6.0

- Removed commenting system #130
- Context menu in the text view is now flatter #146
- Clicking on an entity now highlights all mentions of it #142
- If segments are annotated in the XMI file, they can now be 
  displayed in the scroll bar. This is a preparation for segment 
  support, and not yet directly usable. #84
- The search panel now has a second tab that allows searching for 
  mention flags #42

## 1.5.6

- Fixed an issue that prevented merging compressed files #150

## 1.5.5

- Fixed an issue that resulted in copied mentions #141

## 1.5.4

- Fixed that creating new documents from plain text (and others) did
  not set the correct file version number #137
- Fixed that the style is not correctly set when importing QuaDramA 
  files #139
- Fixed that hidden entities also have their properties in gray #133

## 1.5.3

- Fixed a bug that caused detached mention parts to disappear 
  after conversion #131
- Fixed an issue that caused entities to be disrupted after 
  conversion #129

## 1.5.2

- Fixed an issue that prevented loading of the correct style

## 1.5.1

- Fixes that hiding an entity didn't de-underline its mentions #127

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
- Compressed files: If the file name entered in the Save As dialog ends 
  on ".xmi.gz", the file will be gzipped and therefore take much less 
  space on disk (and is more email-friendly). Files ending on ".xmi.gz" 
  can be loaded directly as usual. #125
- QuaDramA/CRETA-specific importers that we used to import files from 
  WebAnno have been removed. Import (of coreference annotations) from 
  WebAnno can still be done using the dkpro importer. #124
- JSON export: Coreference annotations can now be exported as JSON
  files. The file contains a list of tokens, and each token can be 
  associated with one or more entities. This JSON format will be 
  importable by [rCat](http://www.rcat-ims.de:5000/) in the future. #120

## 1.4.4

- Fixed an issue in the compare view that led to displaced underlines.
  Root cause was the handling of windows-style newlines `\r\n` #114
- Fixed an issue in the TEI importer that caused the document title to 
  be wrong #119

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
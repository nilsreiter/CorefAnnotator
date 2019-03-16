[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.1228106.svg)](https://doi.org/10.5281/zenodo.1228106)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Github All Releases](https://img.shields.io/github/downloads/nilsreiter/CorefAnnotator/total.svg)
![GitHub (pre-)release](https://img.shields.io/github/release/nilsreiter/CorefAnnotator/all.svg)
[![Build Status](https://travis-ci.org/nilsreiter/CorefAnnotator.svg?branch=master)](https://travis-ci.org/nilsreiter/CorefAnnotator)
[![CodeFactor](https://www.codefactor.io/repository/github/nilsreiter/corefannotator/badge)](https://www.codefactor.io/repository/github/nilsreiter/corefannotator)
# Coref Annotator

This is an annotation tool for coreference. It's built on top of [Apache's UIMA](https://uima.apache.org), and works with long documents and long coreference chains.

## Features

- Supports annotation of long texts with many discourse entities
- Intuitive drag and drop operations
- Fully operable by keyboard for fast annotation
- Annotation texts can be formatted
- Search function to navigate in long texts
- Localisable in other languages (currently: English and German)
- Import and export in a few file formats
- Automatically generated candidates

## Requirements

- Java 8 or higher; both Oracle Java and OpenJDK are supported.
- JavaFX: https://openjfx.io/openjfx-docs/#install-javafx
- [Oracle Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) includes JavaFX;
  if you use OpenJDK or Oracle Java 11, you need to install JavaFX separately.

# Installation

## Requirements 

- Java 8 or higher; both Oracle Java and [OpenJDK](https://jdk.java.net) are supported. Due to licensing fees for Oracle JDK, I recommend using OpenJDK.

### Optional: JavaFX

The core functionality of CorefAnnotator runs fine without JavaFX. What you get from using the JavaFX version is a better integration into the operating systems (mostly file dialogs). And better integration means happier annotators means more productive annotators :-)


[Oracle Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) includes JavaFX. 

If you use OpenJDK or Oracle Java 11 (or higher), you need to [install JavaFX separately](https://openjfx.io/openjfx-docs/#install-javafx).

If JavaFX has been installed, the application can be started with the following snippet on the command line:

```bash
java --module-path PATH_TO_JAVAFX --add-modules javafx.swing,javafx.controls -jar PATH_TO_COREFANNOTATOR_JAR
```
# zeus-access-400

Müheloser Zugriff auf IBM DB2/400-Datenbanken aus Java heraus mit jt400. Erkunden Sie die Welt der Datenbankintegration in Ihren Legacy-RPG-Projekten.

**zeus-access-400** ist eine kontinuierlich wachsende Sammlung von Beispielen, die aufzeigen, wie Java nahtlos mit RPG-Anwendungen auf AS/400 und IBM i verbunden werden kann. Entwickelt als Bildungsressource für Entwickler und als Begleitung zum Blog [tiny-tool.de](https://tiny-tool.de/).

Effortlessly access IBM DB2/400 databases from Java using jt400. Explore the world of database integration in your legacy RPG projects.

**zeus-access-400** is a continuously evolving collection of examples showcasing how to seamlessly connect Java with RPG applications on AS/400 and IBM i. Designed as an educational resource for developers and a companion to the [tiny-tool.de](https://tiny-tool.de/) blog.

## Voraussetzungen / Prerequisites

Um die Beispiele ausführen zu können, müssen folgende Dateien im selben Verzeichnis wie das ausführbare JAR vorhanden sein:
To run the examples, the following files need to be present in the same directory as the executable JAR:

- `zeus-access-400-1.0-SNAPSHOT.jar`
- `jt400.jar`
- `application.properties`

## Ausführung / Execution

### Auf einem Windows-System / On a Windows System

```cmd
java -cp "zeus-access-400-1.0-SNAPSHOT.jar;jt400.jar" de.zeus.hermes.AppInitializer application.properties
```
### Auf einem Windows-System / On a Windows System

```cmd
java -cp "zeus-access-400-1.0-SNAPSHOT.jar:jt400.jar" de.zeus.hermes.AppInitializer application.properties
```


[APACHE 2.0](LICENSE)

# Visit / Besuchen Sie
[tiny-tool.de](https://tiny-tool.de/).

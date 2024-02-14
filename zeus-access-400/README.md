## zeus-access-400

Müheloser Z ugriff auf IBM DB2/400-Datenbanken aus Java heraus mit jt400. Erkunden Sie die Welt der Datenbankintegration in Ihren Legacy-RPG-Projekten.

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
### Linux/Unix-System/System-i 

```cmd
java -cp "zeus-access-400-1.0-SNAPSHOT.jar:jt400.jar" de.zeus.hermes.AppInitializer application.properties
```

## Unterstützte Export-Datenformate / Supported Export Data Formats

**zeus-access-400** unterstützt eine Vielzahl von Datenformaten für den Export Ihrer Datenbankabfragen. Diese Flexibilität ermöglicht es Entwicklern, Daten in dem Format zu exportieren, das am besten zu ihren Bedürfnissen passt. Aktuell unterstützte Formate umfassen:

- `XML`: Ideal für datenintensive Anwendungen, bei denen die Dokumentstruktur wichtig ist.
- `HTML`: Perfekt für die Erstellung von Berichten oder Datenvisualisierungen, die im Webbrowser angezeigt werden sollen.
- `JSON`: Ein leichtgewichtiges Daten-Austauschformat, das sich gut für Webanwendungen und APIs eignet.
- `JSONL`: Eine Variante von JSON, die sich besonders für den Export großer Datensätze eignet, wobei jedes Objekt in einer neuen Zeile steht.
- `CSV`: Ein klassisches Format für den Export tabellarischer Daten, das sich leicht in Tabellenkalkulationsprogramme wie Microsoft Excel oder Google Sheets importieren lässt.

**zeus-access-400** supports a variety of data formats for exporting your database queries. This flexibility allows developers to export data in the format that best suits their needs. Currently supported formats include:

- `XML`: Ideal for data-intensive applications where document structure is important.
- `HTML`: Perfect for creating reports or data visualizations to be displayed in a web browser.
- `JSON`: A lightweight data interchange format well-suited for web applications and APIs.
- `JSONL`: A variant of JSON, particularly useful for exporting large datasets where each object is on a new line.
- `CSV`: A classic format for exporting tabular data, easily importable into spreadsheet programs like Microsoft Excel or Google Sheets.


[APACHE 2.0](LICENSE)

# Visit / Besuchen Sie
[tiny-tool.de](https://tiny-tool.de/).

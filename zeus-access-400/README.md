## zeus-access-400

Müheloser Zugriff auf IBM DB2/400-Datenbanken aus Java heraus mit jt400. Erkunden Sie die Welt der Datenbankintegration in Ihren Legacy-RPG-Projekten.

**zeus-access-400** ist eine kontinuierlich wachsende Sammlung von Beispielen, die aufzeigen, wie Java nahtlos mit RPG-Anwendungen auf AS/400 und IBM i verbunden werden kann. Entwickelt als Bildungsressource für Entwickler und als Begleitung zum Blog [tiny-tool.de](https://tiny-tool.de/).

Effortlessly access IBM DB2/400 databases from Java using jt400. Explore the world of database integration in your legacy RPG projects.

**zeus-access-400** is a continuously evolving collection of examples showcasing how to seamlessly connect Java with RPG applications on AS/400 and IBM i. Designed as an educational resource for developers and a companion to the [tiny-tool.de](https://tiny-tool.de/) blog.

### Voraussetzungen / Prerequisites

### Deutsch
- **Neu:** Laden Sie die `zeus-access-400.zip` herunter und entpacken Sie sie.
- Fügen Sie `jt400.jar` zum Ordner `zeus-access-400` hinzu.
- Passen Sie `application.properties` nach Ihren Bedürfnissen an.

### English 
- New:** Download the `zeus-access-400.zip` and unzip it.
- Add `jt400.jar` to the `zeus-access-400` folder.
- Customize `application.properties` according to your needs.

### Ausführung / Execution

Starten Sie die Anwendung einfach mit `runexport.cmd` auf Windows oder `runexport.sh` auf Linux/Unix. Diese Skripte erleichtern die Ausführung und sorgen für eine reibungslose Integration.

Simply start the application with `runexport.cmd` on Windows or `runexport.sh` on Linux/Unix.

### Auf einem Windows-System / On a Windows System `runexport.cmd`

```cmd
java -cp "zeus-access-400-1.0-SNAPSHOT.jar;jt400.jar" de.zeus.hermes.AppInitializer application.properties
```
### Linux/Unix-System/System-i `runexport.sh`

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
- `MD`: Ideal für die Erstellung dokumentierter Berichte oder Anleitungen, die direkt auf GitHub, GitLab oder anderen Versionierungssystemen verwendet werden können.

**zeus-access-400** supports a variety of data formats for exporting your database queries. This flexibility allows developers to export data in the format that best suits their needs. Currently supported formats include:

- `XML`: Ideal for data-intensive applications where document structure is important.
- `HTML`: Perfect for creating reports or data visualizations to be displayed in a web browser.
- `JSON`: A lightweight data interchange format well-suited for web applications and APIs.
- `JSONL`: A variant of JSON, particularly useful for exporting large datasets where each object is on a new line.
- `CSV`: A classic format for exporting tabular data, easily importable into spreadsheet programs like Microsoft Excel or Google Sheets.
- `MD`: Ideal for creating documented reports or instructions that can be directly used on GitHub, GitLab, or other versioning systems.


[APACHE 2.0](LICENSE)

# Visit / Besuchen Sie
[tiny-tool.de](https://tiny-tool.de/).

# ENVPRINTER

Ein Beispielprojekt, um Umgebungsvariablen von einer IBM i Maschine mit der Hilfe von Java auszulesen und darzustellen.
Es zeigt die Integration von verschiedenen Technologien und Sprachen, um ein bestimmtes Ziel zu erreichen.

### Java-Klasse: EnvVariablesPrinter

Dies ist ein einfaches Java-Programm, das Umgebungsvariablen des Betriebssystems ausliest. Es kann entweder alle
Umgebungsvariablen ausgeben oder spezifische, die als Argumente übergeben wurden. So bekommt man einen Überblick über
die aktuelle Umgebung und ihre Einstellungen.

### Shell-Skript: startEnvPrinter.sh

Dieses Skript dient als Wrapper für das Java-Programm. Es prüft, ob Java installiert ist und ruft dann das Java-Programm
mit den übergebenen Argumenten auf. Durch dieses Skript kann man das Java-Programm bequem vom Shell-Prompt aus aufrufen.

### ILE-RPGLE-Programm

Das ILE-RPGLE-Programm dient als Brücke zwischen der IBM i (oder AS/400) Umgebung und dem Java-Programm. Es benutzt die
QCMDEXC-API, um einen QSH-Befehl (Qshell-Befehl) abzusetzen, der das Shell-Skript aufruft. Dabei werden eventuelle
Parameter an das Java-Programm weitergeleitet.

[MIT](LICENSE)
## Besuchen Sie
[tiny-tool.de](https://tiny-tool.de/).


### EnvPrinter: An Example Project
An example project for reading and displaying environment variables from an IBM i machine using Java. It showcases the integration of different technologies and languages to achieve a specific goal.

#### Java Class: EnvVariablesPrinter
This is a simple Java program that reads operating system environment variables. It can either output all environment variables or specific ones that were passed as arguments. This provides an overview of the current environment and its settings.

#### Shell Script: startEnvPrinter.sh
This script serves as a wrapper for the Java program. It checks whether Java is installed and then calls the Java program with the passed arguments. The script allows the Java program to be conveniently called from the shell prompt.

#### ILE-RPGLE Program
The ILE-RPGLE program serves as a bridge between the IBM i (or AS/400) environment and the Java program. It uses the QCMDEXC API to issue a QSH command (Qshell command) that invokes the shell script. Any parameters are then forwarded to the Java program.

[MIT](LICENSE)
## Visit
[tiny-tool.de](https://tiny-tool.de/).

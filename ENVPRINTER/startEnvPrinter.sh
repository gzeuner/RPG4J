#!/bin/sh

# Überprüfen, ob die Java-Umgebung vorhanden ist
if [ -z "$(which java)" ]; then
  echo "Java ist nicht installiert oder der Pfad zu Java ist nicht gesetzt."
  exit 1
fi

# Startet das Java-Programm $@ überträgt alle Argumente, die an das Skript gegeben wurden, an das Java-Programm.
java -jar /home/ZEUS/EnvVariablesPrinter.jar $@



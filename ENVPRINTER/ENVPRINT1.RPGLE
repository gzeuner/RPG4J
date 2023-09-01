**free
ctl-opt DftActGrp(*No) ActGrp(*Caller);

// Prozedur-Prototyp mit Parameterliste zum Aufruf dieses Programmes
dcl-pr entry extPgm('ENVPRINT1');
  paKey char(30) options(*nopass);
end-pr;

dcl-pi entry;
  paKey char(30) options(*nopass);
end-pi;

// Globale Variablen
dcl-s QSHCommand varchar(1024);
dcl-s ScriptPath varchar(255) inz('/home/YOURPATH/');
dcl-s ScriptName varchar(50) inz('startEnvPrinter.sh');
dcl-s JobQueue varchar(50) inz('YOURQUEUE');

// Prozedur-Prototyp für Kommandozeile QCMDEXC
dcl-pr ExecCmd extPgm('QCMDEXC');
  Cmd char(2048) const;
  CmdLen packed(15:5) const;
end-pr;

// Variablen für Command-Ausgaben über QCMDEXC
dcl-s CmdString char(2048);
dcl-s CmdLength packed(15:5);

execQSH();

*inlr = *on;
return;

// Subprozedur zum Ausführen des QSH Kommandos
dcl-proc execQSH;
  QSHCommand = 'QSH CMD(''' + ScriptPath + ScriptName;

  if (%parms() > 0 and %trim(paKey) <> '');
    QSHCommand += ' ' + %trim(paKey);
  endif;

  QSHCommand += ''')';

  CmdString = 'SBMJOB CMD(' + QSHCommand + ') JOBQ(' + JobQueue + ')';
  CmdLength = %len(%trim(CmdString));
  ExecCmd(CmdString:CmdLength);
end-proc;

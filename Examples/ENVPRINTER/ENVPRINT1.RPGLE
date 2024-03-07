**free
// MIT License
// Copyright (c) 2023 Guido Zeuner
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

ctl-opt DftActGrp(*No) ActGrp(*Caller);

// Prozedur-Prototyp mit Parameterliste zum Aufruf dieses Programmes (main)
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

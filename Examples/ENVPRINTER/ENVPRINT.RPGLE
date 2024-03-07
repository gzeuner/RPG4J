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
     H DFTACTGRP(*NO)
     H ACTGRP(*CALLER)

      //Prozedur-Prototyp mit Parameterliste zum Aufruf dieses Programmes
     D entry           PR                  ExtPgm('ENVPRINT')
     D  paKey                        30a   options(*nopass)

     D entry           PI
     D  paKey                        30a   options(*nopass)

      //Globale Variablen
     D QSHCommand      S           1024A   varying
     D ScriptPath      S            255A   varying inz('/home/YOURPATH/')
     D ScriptName      S             50A   varying inz('startEnvPrinter.sh')
     D JobQueue        S             50A   varying inz('YOURQUEUE')

      //Prozedur-Prototyp für Kommandozeile QCMDEXC
     D ExecCmd         Pr                  ExtPgm('QCMDEXC')
     D  Cmd                        2048a   Const
     D  CmdLen                       15  5 Const

      //Variablen für Command-Ausgaben über QCMDEXC
     D CmdString       s           2048a
     D CmdLength       s             15  5

      /free

       exSr execQSH;
       *Inlr = *On;

       //Java-Prozess über QCMDEXC / QSH aufrufen
       begsr execQSH;

         QSHCommand = 'QSH CMD(''' + ScriptPath + ScriptName;

         if (%Parms() > 0 and %Trim(paKey) <> '');
           QSHCommand += ' ' + %Trim(paKey);
         endif;

         QSHCommand += ''')';

         CmdString = 'SBMJOB CMD(' + QSHCommand + ') JOBQ(' + JobQueue + ')';
         CmdLength = %Len(%Trim(CmdString));
         ExecCmd(CmdString:cmdLength);
       endsr;

      /end-free

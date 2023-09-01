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

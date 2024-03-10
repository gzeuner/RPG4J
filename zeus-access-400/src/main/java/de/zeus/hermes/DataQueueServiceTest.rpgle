**FREE
ctl-opt dftactgrp(*no) actgrp(*new) option(*srcstmt: *nodebugio);

// Includes and definitions
/include qsysinc/qrpglesrc,qrcvdtaq
/include qsysinc/qrpglesrc,qsnddtaq

// Prototypes
dcl-pr WriteToQueue extproc('QSNDDTAQ');
  *n char(10) const; // Data queue name
  *n char(10) const; // Data queue library
  *n char(256) const; // Data to send
  *n int(10) const; // Length of data to send
  *n char(10); // Error data structure
end-pr;

dcl-pr ReadFromQueue extproc('QRCVDTAQ');
  *n char(10) const; // Data queue name
  *n char(10) const; // Data queue library
  *n char(256); // Variable to receive data
  *n int(10) const; // Length of data to receive
  *n int(10) const; // Wait time
  *n char(10); // Error data structure
end-pr;

// Variables declaration and Main pgm
dcl-s queueName char(10) inz('RP2JQ');
dcl-s queueLib char(10) inz('YOURLIB');
dcl-s dataToSend char(256) inz('https://wttr.in/Berlin?format=%C+%t+%w');
dcl-s dataReceived char(256);
dcl-s waitTime int(10) inz(30);
dcl-s errds char(10) inz(*blanks);
dcl-s keyPress char(1);

// Write to queue RP2JQ
WriteToQueue(queueName: queueLib: dataToSend: %len(%trim(dataToSend)): errds);

// Wait and read from queue J2RPQ
ReadFromQueue('J2RPQ': queueLib: dataReceived: 256: waitTime: errds);

// Show the result
dsply ('Ergebnis des REST-Calls: ' + %trim(dataReceived));

// Wait for Space key to exit
dsply ('Drücke Space zum Beenden') ' ' wait(*input) keyPress;

*inlr = *on;

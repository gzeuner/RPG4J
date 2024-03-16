**FREE
ctl-opt dftactgrp(*no) actgrp(*new) option(*srcstmt: *nodebugio);

// Error Data Structure
dcl-ds errds qualified template;
  bytesProv int(10) inz(0);
  bytesAvail int(10) inz(0);
  msgId char(7) inz(*blanks);
  reserved char(1) inz(*blanks);
  errorMsg char(256) inz(*blanks);
end-ds;

dcl-ds errdsVar likeDS(errds) inz(*likeDS);

// Prototypes
dcl-pr WriteToQueue extpgm('QSNDDTAQ');
  *n char(10) const; // Data queue name
  *n char(10) const; // Data queue library
  *n pointer; // Data to send
  *n int(10) const; // Length of data to send
  *n likeDS(errds); // Error data Structure
end-pr;

dcl-pr ReadFromQueue extpgm('QRCVDTAQ');
  *n char(10) const; // Data queue name
  *n char(10) const; // Data queue library
  *n pointer; // Variable to receive data (als Zeiger)
  *n int(10) const; // Length of data to receive
  *n int(10) const; // Wait time
  *n likeDS(errds); // Error data Structure
end-pr;

// Variables declaration and Main pgm
dcl-s queueLib char(10) inz('ZEUS1');
dcl-s dataToSend char(256) inz('https://wttr.in/Berlin?format=%C+%t+%w');
dcl-s dataReceived char(256);
dcl-s waitTime int(10) inz(30);
dcl-s keyPress char(1);

// Convert Data to send
dcl-s dataToSendPtr pointer inz(%addr(dataToSend));
// Prepare for reading from the queue
dcl-s dataReceivedPtr pointer inz(%addr(dataReceived));

// Write to queue RP2JQ
WriteToQueue('RP2JQ': queueLib: dataToSendPtr: %len(%trim(dataToSend)): errdsVar);

// Display error if any
if errdsVar.msgId <> *blanks;
  dsply ('Error: ' + errdsVar.msgId + ' ' + %subst(errdsVar.errorMsg: 1: 30));
endif;


// Wait and read from queue J2RPQ
ReadFromQueue('J2RPQ': queueLib: dataReceivedPtr: 256: waitTime: errdsVar);

// Display the word "Result" on its own line
dsply 'Result:';

// Then, display the first 52 characters of dataReceived on the next line
dsply %trim(%subst(dataReceived: 1: 52));

// Wait for Space key to exit
dsply ('Press Enter to exit.');

*inlr = *on;

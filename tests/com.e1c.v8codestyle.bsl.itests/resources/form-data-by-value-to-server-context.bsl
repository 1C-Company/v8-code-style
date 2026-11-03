&AtServer
Procedure ProcessSpreadsheet(Spreadsheet)
EndProcedure

&AtClient
Procedure Compliant()
    Spreadsheet = New SpreadsheetDocument;
	ProcessSpreadsheet(Spreadsheet);
EndProcedure
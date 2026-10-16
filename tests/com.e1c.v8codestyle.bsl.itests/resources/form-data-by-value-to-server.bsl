&AtServerNoContext
Procedure ProcessSpreadsheet(Spreadsheet)
EndProcedure

&AtClient
Procedure NonCompliant()
    Spreadsheet = New SpreadsheetDocument;
	ProcessSpreadsheet(Spreadsheet);
EndProcedure

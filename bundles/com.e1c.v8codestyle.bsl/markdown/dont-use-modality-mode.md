# Checks dont use modality call in dont use modality mode.

## Noncompliant Code Example

Procedure NonComplaint(Parameters) Export
	DoMessageBox("Message");
EndProcedure


## Compliant Solution

Procedure Complaint(Parameters) Export
	ShowMessageBox("Message");
EndProcedure


## See
[Limitations on the use of modal windows and synchronous calls](https://its.1c.ru/db/v8std#content:703:hdoc)


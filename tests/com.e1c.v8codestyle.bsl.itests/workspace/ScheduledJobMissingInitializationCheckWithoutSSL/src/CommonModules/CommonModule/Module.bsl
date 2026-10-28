Procedure SecondPosCall() Export // ERROR

	A = 1;
	CommonUse.OnStartScheduledJobExecution();

EndProcedure

Procedure FirstPosCall() Export

	CommonUse.OnStartScheduledJobExecution();

EndProcedure

Procedure NoInitCall() Export // ERROR

EndProcedure

Procedure NoScheduleReference() Export

EndProcedure

Procedure VariableToArgs() Export

	A = 1;
	B = 1;
	CommonUse.OnStartScheduledJobExecution(A, B);

EndProcedure

Procedure DeclaredVariableNotPassed() Export // ERROR

	Some1 = Metadata.ScheduledJobs.ScheduledJobEmptyMethod;
	Some2 = 1;
	CommonUse.OnStartScheduledJobExecution(Some1);

EndProcedure

Procedure CallInsideIf() Export // ERROR

	If True Then
		CommonUse.OnStartScheduledJobExecution();
	EndIf;

EndProcedure
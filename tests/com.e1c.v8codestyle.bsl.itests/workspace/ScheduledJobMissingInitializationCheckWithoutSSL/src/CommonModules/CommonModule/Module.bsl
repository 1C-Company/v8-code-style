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

Procedure DynamicAccessVariableToArgs() Export

	Some1 = Metadata.ScheduledJobs;
	Some2 = 1;
	CommonUse.OnStartScheduledJobExecution(Some1.ScheduledJobEmptyMethod, Some2);

EndProcedure

Procedure IndexAccessVariableToArgs() Export

	Some2 = 1;
	Some1 = new Array(1, 2);
	CommonUse.OnStartScheduledJobExecution(Some1[0], Some2);

EndProcedure

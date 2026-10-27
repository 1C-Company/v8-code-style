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

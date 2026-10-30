# Calling the BackgroundJobs.Execute procedure

It is not recommended to call the BackgroundJobs.Execute procedure if the SSL (Standard Subsystems Library) is used in the configuration.
Instead, it is recommended to use the SSL methods: TimeConsumingOperations.ExecuteInBackground, TimeConsumingOperations.ExecuteFunction, TimeConsumingOperations.ExecuteProcedure, etc.

## Incorrect 

```bsl
BackgroundJob = BackgroundJobs.Execute(
	"MyServerProcedures.ExecuteLongCalculation", 
	LaunchParameters, 
	JobKey, 
	"Calculation of document data"
);
```

## Correct

```bsl
FunctionResult = TimeConsumingOperations.StartExecutionInBackground(
    "MyServerProcedures.ExecuteLongCalculation", 
    ProcedureParameters, 
    ExecutionParameters
);
```

## See also

[Standard, clause 6.2](https://its.1c.ru/db/v8std#content:642:hdoc)
[Standard](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Designing_user_interfaces/Long-running_operations_on_the_server/?language=en)
# Calling the Initialization Procedure in a Scheduled Job Method

If the SSL (Standard Subsystems Library) is used in the configuration, it is recommended to call the CommonUse.OnStartScheduledJobExecution() procedure at the beginning of the scheduled job method.

## Incorrect

```bsl
Procedure Incorrect()
	
	// Missing CommonUse.OnStartExecuteScheduledJob() call
	
	// some code ...
	
EndProcedure
```

## Correct

```bsl
Procedure Correct()
	
	CommonUse.OnStartScheduledJobExecution(Metadata.ScheduledJobs.MyScheduledExchange);
	
	// some code ...
	
EndProcedure
```

## See more

[Standart](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/Creating_and_modifying_metadata_objects/Scheduled_jobs/Scheduled_jobs_____Starting/?language=en)
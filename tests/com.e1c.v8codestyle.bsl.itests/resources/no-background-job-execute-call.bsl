Procedure Some()

	// Same module, other real methods - module name alone must not trigger
	BackgroundJobs.GetBackgroundJobs();
	BackgroundJobs.FindByUUID(JobID);
	BackgroundJobs.WaitForExecutionCompletion(BackgroundJobsArray, 10);

	// Other module, same method name - method name alone must not trigger
	SomeService.Execute();

	// Recommended SSL alternative - must not trigger
	TimeConsumingOperations.ExecuteInBackground("CommonModule.RunLongCalculation", Parameters, ExecutionParameters);

EndProcedure

# The minimum job interval is 1 minute

The minimum job interval is 1 minute.

## Noncompliant Code Example

## Compliant Solution

- A scheduled job must be executed just frequently enough to properly serve its objectives.
- For optimal server performance, the default scheduled job interval is 24 hours. 
- Some job can be executed more often to keep important data up-to-date.
- The minimum job interval is 1 minute.
- The interval of a scheduled job has to be reasonably balanced with the job run time. For example, if the run time is 20 seconds, running the job every minute is unreasonably often.
- Whenever possible, schedule resource-intensive scheduled jobs during the server low-load hours. For example, during off-hours or off days.
- Schedule resource-intensive jobs in sequence. Consider the expected run time to avoid the job overlap.

## See

[Configuring scheduled job schedules](https://support.1ci.com/hc/en-us/articles/360011001440-Configuring-scheduled-job-schedules)

# Check handler has assigned to a single event

Check handler has assigned to a single event

## Noncompliant Code Example

If you mix two event in a single procedure, its logic gets complicated and decreases its stability.

## Compliant Solution

Assign a handler for each event. If different actions are required in case of events in different form items:
- Create a separate procedure or a function that executes the required action.
- Сreate a separate handler for each form item.
- Call the required procedure or function from each handler.

## See

[Module structure](https://support.1ci.com/hc/en-us/articles/360011002360-Module-structure#2.4.3)

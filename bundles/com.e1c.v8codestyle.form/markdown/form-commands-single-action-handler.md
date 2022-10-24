# Check handler has assigned to a single command

Check handler has assigned to a single command.

## Noncompliant Code Example

If you mix two event in a single procedure, its logic gets complicated and decreases its stability.

## Compliant Solution

Assign a handler for each event. If different actions are required in case of events in different form commands:
- Create a separate procedure or a function that executes the required action.
- Сreate a separate handler for each form item.
- Call the required procedure or function from each handler.

## See

[Module structure](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_Development_Standards/)

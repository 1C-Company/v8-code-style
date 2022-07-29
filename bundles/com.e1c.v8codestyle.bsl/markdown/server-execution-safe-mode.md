# Safe mode is not enabled before "Execute" or "Eval" call

To avoid vulnerabilities, in server procedures and functions, 
enable safe mode before calling the Execute or Eval methods.

In service mode, data separation is to be considered upon 
enabling safe mode.

## Noncompliant Code Example

```bsl
Execute(Algorithm);
```

```bsl
Eval(Algorithm);
```

## Compliant Solution

```bsl
SetSafeMode(True);
Execute(Algorithm);
```

```bsl
SetSafeMode(True);
Eval(Algorithm);
```

In service mode:

```bsl
SetSafeMode(True);

For each SeparatorName in ConfigurationSeparators() Do
    SetDataSeparationSafeMode(SeparatorName, True);
EndDo;

Execute Algorithm;
```

```bsl
SetSafeMode(True);

For each SeparatorName in ConfigurationSeparators() Do
    SetDataSeparationSafeMode(SeparatorName, True);
EndDo;

Eval(Algorithm);
```

If the Standard Subsystems Library is used in the configuration, use the following:

```bsl
Common.ExecuteInSafeMode()
```

```bsl
Common.CalculateInSafeMode()
```

Instead of generating a string calling the module method and passing it to Execute:
```bsl
Common.ExecuteConfigurationMethod()
```

```bsl
Common.ExecuteObjectMethod()
```

If the Standard Subsystems Library version is earlier than 2.4.1, use the following:


```bsl
SafeModeManager.ExecuteInSafeMode()
```

```bsl
SafeModeManager.CalculateInSafeMode()
```

Instead of generating a string calling the module method and passing it to Execute:
```bsl
SafeModeManager.ExecuteConfigurationMethod()
```

```bsl
SafeModeManager.ExecuteObjectMethod()
```

## See
[Restrictions on the use of Run and Eval on the server](https://support.1ci.com/hc/en-us/articles/360011122479-Restrictions-on-the-use-of-Run-and-Eval-on-the-server)

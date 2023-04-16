# The asynchronous method is not followed by lines of code

In the asynchronous approach the method is called as usual, but control returns to the caller before the asynchronous 
method is completed. After that, execution of the caller continues. 

## Noncompliant Code Example

```bsl
Text = "Warning text";
ShowMessageBox( , Text);
Message("Warning is closed");
```

## Compliant Solution

```bsl
Text = "Warning text";
Await DoMessageBoxAsync(Text);
Message("Warning is closed");
```

## See

- [Synchronous and asynchronous operations](https://kb.1ci.com/1C_Enterprise_Platform/Guides/Developer_Guides/1C_Enterprise_8.3.19_Developer_Guide/Chapter_4._1C_Enterprise_language/4.7._Queries/4.7.9._Synchronous_and_asynchronous_operations/)
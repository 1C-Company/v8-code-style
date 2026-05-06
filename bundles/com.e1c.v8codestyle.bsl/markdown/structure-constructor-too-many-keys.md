# Structure constructor has too many keys

Check structure constructor has more then 3 keys


## Noncompliant Code Example


```bsl

Parameters = new Structure("Key1, Key2, Key3, Key4");

```

## Compliant Solution

```bsl
Parameters = new Structure();
Parameters.Insert("Key1");
Parameters.Insert("Key2");
Parameters.Insert("Key3");
Parameters.Insert("Key3");

```

## See


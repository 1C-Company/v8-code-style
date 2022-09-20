// @strict-types

Procedure NonComplaint() Export

    result = New Array(); // Array of Number

    result.Add(42);
    result.Insert(0, 42);
    result.Set(0, 42);
    result.Add();
    result.Insert(0);

EndProcedure

Procedure Complaint() Export

    result = New Array();

    result.Add(42);
    result.Insert(0, 42);
    result.Set(0, 42);
    result.Add();
    result.Insert(0);

EndProcedure

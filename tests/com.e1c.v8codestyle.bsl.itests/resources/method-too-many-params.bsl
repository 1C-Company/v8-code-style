
Procedure Noncompliant1(Name,
      Heading = Undefined,
      OnChangeHandler = "",
      OnBeginSelectionHandler = "",
      MarginWidth,
      Backcolor = Undefined,
      TitleBackColor = Undefined,
      Parent = Undefined,
      HeaderPicture = Undefined,
      DataPath = Undefined,
      FieldReadOnly = False,
      ChoiceParameterLinks = Undefined)

EndProcedure

Procedure Noncompliant2(Name,
      Heading,
      OnChangeHandler,
      OnBeginSelectionHandler,
      MarginWidth,
      Backcolor,
      TitleBackColor,
      Parent)

EndProcedure

Procedure Noncompliant3ButErrorYet(Name,
      Heading = Undefined,
      OnChangeHandler = "",
      OnBeginSelectionHandler = "",
      MarginWidth)

EndProcedure

Procedure Noncompliant4(Name,
      Heading = Undefined,
      OnChangeHandler = "",
      OnBeginSelectionHandler = "",
      Backcolor = Undefined)

EndProcedure


Procedure Compliant(FieldName) 

EndProcedure

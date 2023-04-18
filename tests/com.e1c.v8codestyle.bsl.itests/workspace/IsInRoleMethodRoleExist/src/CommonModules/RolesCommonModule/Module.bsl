Procedure TestIsInRole()
	If IsInRole("TestRole3") Then
	EndIf;
	If IsInRole("TestRole1") Then
	EndIf;
EndProcedure

Процедура ТестРольДоступна()
	Если РольДоступна("TestRole3") Тогда
	КонецЕсли;
	Если РольДоступна("TestRole1") Тогда
	КонецЕсли;
КонецПроцедуры

Procedure TestStaff()
	If Users.RolesAvailable("TestRole1,TestRole2,TestRole3") Then
		Message("Test message");
	EndIf;
EndProcedure

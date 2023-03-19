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

Procedure TestRolesAvailable()
	If Users.RolesAvailable("TestRole1,TestRole2,TestRole3") Then
	EndIf;
	If Users.RolesAvailable("TestRole1,TestRole2") Then
	EndIf;
EndProcedure

Процедура ТестПользователиРолиДоступны()
	Если Пользователи.РолиДоступны("TestRole1,TestRole2,TestRole3") Then
	EndIf;
	Если Пользователи.РолиДоступны("TestRole1,TestRole2") Then
	EndIf;
КонецПроцедуры
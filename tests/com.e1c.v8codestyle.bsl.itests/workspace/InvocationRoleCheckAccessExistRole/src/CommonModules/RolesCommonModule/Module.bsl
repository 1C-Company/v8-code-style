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

Procedure TestStaff()
	If Users.IsFullUser() Then
		Message("Test message");
	EndIf;
EndProcedure

Процедура ТестПрочее()
	Если Пользователи.ПолноправныйПользователь() Тогда
		Сообщить("Тестовое сообщение");
	КонецЕсли;
КонецПроцедуры
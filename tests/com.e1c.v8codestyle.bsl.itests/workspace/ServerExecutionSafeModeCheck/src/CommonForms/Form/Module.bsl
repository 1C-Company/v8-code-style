&НаСервере
Процедура Тест1()
	Код = "Рез = 1 + 1";
	Выполнить(Код);
	а = Вычислить(Код);
	УстановитьБезопасныйРежим(Истина);
	Выполнить(Код);
	а = Вычислить(Код);
КонецПроцедуры

&AtServer
Функция Тест2()
	Код = "Рез = 1 + 1";
	Execute(Код);
	а = Eval(Код);
	SetSafeMode(True);
	Execute(Код);
	а = Eval(Код);
КонецФункции
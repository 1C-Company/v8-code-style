&НаКлиенте 
Перем МойРеквизит экспорт;

&НаКлиенте 
Функция МояФункция() экспорт
	Возврат 1;
КонецФункции

&НаКлиенте
Процедура Тест()
	ЭтаФорма.МойРеквизит = ЭтотОбъект.МояФункция();
	ThisForm.МойРеквизит = ThisObject.МояФункция();	
	Notification = New NotifyDescription("ShowQueryEnding", ThisForm);
КонецПроцедуры
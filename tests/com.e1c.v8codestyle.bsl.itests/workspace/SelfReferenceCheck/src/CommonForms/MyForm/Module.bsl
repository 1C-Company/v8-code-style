&НаКлиенте 
Перем МойРеквизит экспорт;

&НаКлиенте 
Функция МояФункция() экспорт
	Возврат 1;
КонецФункции

&НаКлиенте
Процедура Тест()
	ЭтотОбъект.МойРеквизит = ЭтотОбъект.МояФункция();
	ThisObject.МойРеквизит = ThisObject.МояФункция();	
	ThisObject.MyAttrubute = "Value";
КонецПроцедуры
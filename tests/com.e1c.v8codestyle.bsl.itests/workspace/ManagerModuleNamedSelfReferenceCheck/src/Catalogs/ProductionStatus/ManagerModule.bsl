Функция МояФункция() экспорт
	Возврат 1;
КонецФункции

Процедура Тест()
	Catalogs.ProductionStatus.МойРеквизит = Catalogs.ProductionStatus.МояФункция();
	Справочники.ProductionStatus.МойРеквизит = Справочники.ProductionStatus.МояФункция();
	Переменная1 = Enums.ProductionStatus.EmptyRef();
	Переменная1 = Перечисления.ProductionStatus.ПустаяСсылка();
КонецПроцедуры
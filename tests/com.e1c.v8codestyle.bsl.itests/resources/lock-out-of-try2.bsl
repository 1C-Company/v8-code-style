Процедура Тест()
    
    Блокировка = Новый БлокировкаДанных;
    ЭлементБлокировкиДанных = Блокировка.Добавить("Документ.ПриходнаяНакладная");
    ЭлементБлокировкиДанных.Режим = РежимБлокировкиДанных.Исключительный;
    Блокировка.Заблокировать();
    
КонецПроцедуры
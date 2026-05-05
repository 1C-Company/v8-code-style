Процедура Тест1(Параметры)
    
Тест = Новый COMОбъект("Word.Application");
Тест.WordBasic.DisableAutoMacros(1);
Документ = Тест.Documents.Open(ИмяФайла);
    
КонецПроцедуры
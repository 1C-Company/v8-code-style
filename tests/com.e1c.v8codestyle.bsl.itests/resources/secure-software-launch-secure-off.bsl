Процедура Тест1(Параметры)
    
Тест = Новый COMОбъект("Word.Application");
Тест.WordBasic.DisableAutoMacros(0);
Документ = Тест.Documents.Open(ИмяФайла);
    
КонецПроцедуры
Процедура ПравильныйМетод()
    
    ИмяПромежуточногоФайла = ПолучитьИмяВременногоФайла("xml");
    Данные.Записать(ИмяПромежуточногоФайла);
    
    Попытка
       МоеУдалениеФайла(ИмяПромежуточногоФайла);
    Исключение
       ЗаписьЖурналаРегистрации(НСтр("ru = 'Мой механизм.Действие'"), УровеньЖурналаРегистрации.Ошибка, , , ПодробноеПредставлениеОшибки(ИнформацияОбОшибке()));
    КонецПопытки;
    
КонецПроцедуры

Procedure Compliant()
    
    IntermediateFileName = GetTempFileName("xml");
    Data.Write(IntermediateFileName);
    
    Try
        MyFileDeletion(IntermediateFileName);
    Exception
        ShowMessageBox(,NStr("en = 'Delete file error'"));
    EndTry;

EndProcedure



Процедура ПравильныйМетод()
    
    ИмяПромежуточногоФайла = ПолучитьИмяВременногоФайла("xml");
    Данные.Записать(ИмяПромежуточногоФайла);
    
    Попытка
       УдалитьФайлы(ИмяПромежуточногоФайла);
    Исключение
       ЗаписьЖурналаРегистрации(НСтр("ru = 'Мой механизм.Действие'"), УровеньЖурналаРегистрации.Ошибка, , , ПодробноеПредставлениеОшибки(ИнформацияОбОшибке()));
    КонецПопытки;
    
КонецПроцедуры

Procedure Comliant()
    
    IntermediateFileName = GetTempFileName("xml");
    Data.Write(IntermediateFileName);
    
    Try
        DeleteFiles(IntermediateFileName);
    Exception
        ShowMessageBox(,NStr("en = 'Delete file error'"));
    EndTry;

EndProcedure

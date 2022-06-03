Процедура ПравильныйМетод()

    МойОбщийМодуль.ИмяПромежуточногоФайла = ПолучитьИмяВременногоФайла("xml");
    Данные.Записать(МойОбщийМодуль.ИмяПромежуточногоФайла);

    Попытка
       МойОбщийМодуль.МоеУдалениеФайла(МойОбщийМодуль.ИмяПромежуточногоФайла);
    Исключение
       ЗаписьЖурналаРегистрации(НСтр("ru = 'Мой механизм.Действие'"), УровеньЖурналаРегистрации.Ошибка, , , ПодробноеПредставлениеОшибки(ИнформацияОбОшибке()));
    КонецПопытки;
    
КонецПроцедуры

Procedure Compliant()
    
    MyCommonModule.IntermediateFileName = GetTempFileName("xml");
    Data.Write(MyCommonModule.IntermediateFileName);
    
    Try
        MyCommonModule.MyFileDeletion(MyCommonModule.IntermediateFileName);
    Exception
        ShowMessageBox(,NStr("en = 'Delete file error'"));
    EndTry;

EndProcedure



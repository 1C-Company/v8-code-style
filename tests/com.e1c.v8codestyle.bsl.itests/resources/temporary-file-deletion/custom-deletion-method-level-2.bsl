Процедура ПравильныйМетод()

    Справочники.Товары.ИмяПромежуточногоФайла = ПолучитьИмяВременногоФайла("xml");
    Данные.Записать(Справочники.Товары.ИмяПромежуточногоФайла);
    
    Попытка
       Справочники.Товары.МоеУдалениеФайла(Справочники.Товары.ИмяПромежуточногоФайла);
    Исключение
       ЗаписьЖурналаРегистрации(НСтр("ru = 'Мой механизм.Действие'"), УровеньЖурналаРегистрации.Ошибка, , , ПодробноеПредставлениеОшибки(ИнформацияОбОшибке()));
    КонецПопытки;
    
КонецПроцедуры

Procedure Compliant()
    
    Catalog.Goods.IntermediateFileName = GetTempFileName("xml");
    Data.Write(Catalog.Goods.IntermediateFileName);
    
    Try
        Catalog.Goods.MyFileDeletion(Catalog.Goods.IntermediateFileName);
    Exception
        ShowMessageBox(,NStr("en = 'Delete file error'"));
    EndTry;

EndProcedure



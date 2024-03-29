# Чтение отдельного реквизита объекта из базы данных

При чтении отдельных реквизитов объекта из базы данных следует иметь в виду, что вызов метода ПолучитьОбъект или обращение к реквизитам объекта через точку от ссылки приводит к загрузке объекта из базы целиком, вместе с его табличными частями.

Поэтому для чтения значений отдельных реквизитов из базы данных следует использовать запрос. 

## Неправильно

```bsl

Процедура ЗаполнитьКодИНаименованиеСтраны()
 
 СтранаСсылка = … // получаем ссылку на элемент справочника 
 КодСтраны = СтранаСсылка.Код; // первое обращение загружает объект целиком
 НаименованиеСтраны = СтранаСсылка.Наименование;
 
КонецПроцедуры

```

## Правильно

```bsl

Процедура ЗаполнитьКодИНаименованиеСтраны()
 
 Запрос = Новый Запрос(
  "ВЫБРАТЬ
  | СтраныМира.Код,
  | СтраныМира.Наименование
  |ИЗ
  | Справочник.СтраныМира КАК СтраныМира
  |ГДЕ
  | СтраныМира.Ссылка = &Ссылка");
 Запрос.УстановитьПараметр("Ссылка", Ссылка);
 
 Выборка = Запрос.Выполнить().Выбрать();
 Выборка.Следующий();

 КодСтраны = Выборка.Код;
 НаименованиеСтраны = Выборка.Наименование;

КонецПроцедуры

```

```bsl
Для упрощения синтаксиса рекомендуется также использовать специальные функции ЗначенияРеквизитовОбъекта или ЗначениеРеквизитаОбъекта (входят в состав Библиотеки стандартных подсистем).
В этом случае исходный пример будет выглядеть так:

Процедура ЗаполнитьКодИНаименованиеСтраны()

 ЗначенияРеквизитов = ОбщегоНазначения.ЗначенияРеквизитовОбъекта(СтранаСсылка, "Код, Наименование");
 КодСтраны = ЗначенияРеквизитов.Код;
 НаименованиеСтраны = ЗначенияРеквизитов.Наименование;
 
КонецПроцедуры
```

## См.


- [Чтение отдельных реквизитов объекта из базы данных](https://its.1c.ru/db/v8std/content/496/hdoc/)

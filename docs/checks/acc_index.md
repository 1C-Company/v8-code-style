# АПК Ред.1 - Индекс соответствия


## Новые проверки, реализованные в 1C:EDT

Проверки поставляемые 1C:EDT и плагинами.


| № | Код проверки | Наименование |
|---|--------------|--------------|
| 1 | module-unused-method | module-unused-method |
| 2 | query-in-loop | query-in-loop |
| 3 | right-active-users | right-active-users |
| 4 | right-administration | right-administration |
| 5 | right-all-functions-mode | right-all-functions-mode |
| 6 | right-configuration-extensions-administration | right-configuration-extensions-administration |
| 7 | right-data-administration | right-data-administration |
| 8 | right-exclusive-mode | right-exclusive-mode |
| 9 | right-interactive-open-external-data-processors | right-interactive-open-external-data-processors |
| 10 | right-interactive-open-external-reports | right-interactive-open-external-reports |
| 11 | right-output-to-printer-file-clipboard | right-output-to-printer-file-clipboard |
| 12 | right-save-user-data | right-save-user-data |
| 13 | right-start-automation | right-start-automation |
| 14 | right-start-external-connection | right-start-external-connection |
| 15 | right-start-thick-client | right-start-thick-client |
| 16 | right-start-thin-client | right-start-thin-client |
| 17 | right-start-web-client | right-start-web-client |
| 18 | right-update-database-configuration | right-update-database-configuration |
| 19 | right-view-event-log | right-view-event-log |



## Соотвествие кодов АПК и кодов проерок 1C:EDT


| № | Код проверки EDT | Код АПК | Наименование | Стандарт |
|---|------------------|---------|--------------|----------|
| 1 | configuration-data-lock-mode | 67 | [Для конфигурации не установлен управляемый режим блокировки данных.](https://github.com/1C-Company/v8-code-style/issues/177) | [460](https://its.1c.ru/db/v8std#content:460:hdoc) |
| 2 | data-exchange-load | 75 | [Отсутствует обязательная конструкция "Если ОбменДанными.Загрузка Тогда ...".](https://github.com/1C-Company/v8-code-style/issues/356) | [752](https://its.1c.ru/db/v8std#content:752:hdoc) |
| 3 | common-module-name-client | 80 | [Общий модуль, доступный только на клиенте, должен именоваться с постфиксом "Клиент".](https://github.com/1C-Company/v8-code-style/issues/471) | [469](https://its.1c.ru/db/v8std#content:469:hdoc) |
| 4 | common-module-name-global | 83 | [Глобальный общий модуль должен именоваться с постфиксом "Глобальный".](https://github.com/1C-Company/v8-code-style/issues/472) | [469](https://its.1c.ru/db/v8std#content:469:hdoc) |
| 5 | md-list-object-presentation | 93 | [Не заполнено ни представление объекта, ни представление списка.](https://github.com/1C-Company/v8-code-style/issues/437) | [468](https://its.1c.ru/db/v8std#content:468:hdoc) |
| 6 | common-module-type | 125 | [Общий модуль недопустимого типа.](https://github.com/1C-Company/v8-code-style/issues/469) | [469](https://its.1c.ru/db/v8std#content:469:hdoc) |
| 7 | right-interactive-delete-predefined-data | 192 | [Установлено право "ИнтерактивноеУдалениеПредопределенныхДанных".](https://github.com/1C-Company/v8-code-style/issues/513) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 8 | right-interactive-set-deletion-mark-predefined-data | 193 | [Установлено право "ИнтерактивнаяПометкаУдаленияПредопределенныхДанных".](https://github.com/1C-Company/v8-code-style/issues/514) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 9 | right-interactive-clear-deletion-mark-predefined-data | 194 | [Установлено право "ИнтерактивноеСнятиеПометкиУдаленияПредопределенныхДанных".](https://github.com/1C-Company/v8-code-style/issues/515) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 10 | right-interactive-delete-marked-predefined-data | 195 | [Установлено право "ИнтерактивноеУдалениеПомеченныхПредопределенныхДанных".](https://github.com/1C-Company/v8-code-style/issues/351) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 11 | empty-except-statement | 280 | [Конструкция "Попытка...Исключение...КонецПопытки" не содержит кода в исключении.](https://github.com/1C-Company/v8-code-style/issues/394) | [499](https://its.1c.ru/db/v8std#content:499:hdoc) |
| 12 | structure-consructor-too-many-keys | 293 | [В конструкторе объекта типа "Структура" указано более 3-х значений свойств.](https://github.com/1C-Company/v8-code-style/issues/553) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 13 | mdo-name-length | 381 | [Длина имени объекта метаданных превышает 80 символов.](https://github.com/1C-Company/v8-code-style/issues/124) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 14 | role-right-has-rls | 419 | [В правах роли установлены ограничения (RLS) для объекта метаданных.](https://github.com/1C-Company/v8-code-style/issues/426) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 15 | module-unused-local-variable | 433 | [Неэкспортная переменная не используется в модуле.](https://github.com/1C-Company/v8-code-style/issues/369) | [456](https://its.1c.ru/db/v8std#content:456:hdoc) |
| 16 | input-field-list-choice-mode | 448 | [У элемента формы с заполненным списком выбора отключено свойство "Режим выбора из списка".](https://github.com/1C-Company/v8-code-style/issues/251) | [765](https://its.1c.ru/db/v8std#content:765:hdoc) |
| 17 | ql-camel-case-string-literal | 463 | [Строковая константа в запросе СКД не соответствует правилам образования имен переменных.](https://github.com/1C-Company/v8-code-style/issues/243) | [762](https://its.1c.ru/db/v8std#content:762:hdoc) |
| 18 | ql-cast-to-max-number | 470 | [Превышена максимальная длина числовых данных в запросе (31 знак).](https://github.com/1C-Company/v8-code-style/issues/89) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 19 | ql-join-to-sub-query | 494 | [Использование запроса, выполняющего соединение с вложенным запросом.](https://github.com/1C-Company/v8-code-style/issues/168) | [655](https://its.1c.ru/db/v8std#content:655:hdoc) |
| 20 | module-empty-method | 573 | [Ошибка платформенной проверки конфигурации: Пустой обработчик.](https://github.com/1C-Company/v8-code-style/issues/686) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 21 | right-interactive-delete | 1149 | [Установлено право "Интерактивное удаление".](https://github.com/1C-Company/v8-code-style/issues/580) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 22 | common-module-name-client-server | 1245 | [Общий модуль, доступный на сервере и на клиенте, должен именоваться с постфиксом "КлиентСервер".](https://github.com/1C-Company/v8-code-style/issues/743) | [469](https://its.1c.ru/db/v8std#content:469:hdoc) |
| 23 | ql-camel-case-string-literal | 1300 | [В тексте запроса находится строковый литерал.](https://github.com/1C-Company/v8-code-style/issues/748) | [762](https://its.1c.ru/db/v8std#content:762:hdoc) |



## Не реализованные проверки в 1C:EDT


Присоединяйтесь к проекту  [https://github.com/1C-Company/v8-code-style](https://github.com/1C-Company/v8-code-style)! Будем благодарны Вам за помощь!

Можно помочь:
- улучшить документации по проверкам и инструментам см. [задачи](https://github.com/1C-Company/v8-code-style/labels/documentation),  см. [правила](https://github.com/1C-Company/v8-code-style/blob/master/docs/contributing/documentation.md)
- написать проверку на Java см. [задачи для новичка](https://github.com/1C-Company/v8-code-style/labels/good%20first%20issue),  см. [правила](https://github.com/1C-Company/v8-code-style/blob/master/docs/contributing/readme.md)
- написать любую проверку из списка ниже или любой другой инструмент улучшающий разработку по стандартам
- cообщить нам о [ложном срабатывании проверки](https://github.com/1C-Company/v8-code-style/issues/new?assignees=&labels=standards,bug&template=check_false.md&title=Ложное+срабатывание+проверки%3A+%3Cкод+проверки%3E) или о [не нахождении существующей ошибки](https://github.com/1C-Company/v8-code-style/issues/new?assignees=&labels=standards,bug&template=check_not_found.md&title=Проверка%3A+%3Cкод+проверки%3E+не+находит+ошибку).



| № | Код АПК | Наименование | Стандарт |
|---|---------|--------------|----------|
| 1 | 5 | [Комментарий должен начинаться с прописной буквы.](https://github.com/1C-Company/v8-code-style/issues/114) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 2 | 6 | [Комментарий содержит букву "ё".](https://github.com/1C-Company/v8-code-style/issues/461) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 3 | 7 | [Синоним содержит букву "ё".](https://github.com/1C-Company/v8-code-style/issues/462) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 4 | 8 | [Имя содержит букву "ё".](https://github.com/1C-Company/v8-code-style/issues/116) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 5 | 58 | [Имя неверно образовано из синонима.](https://github.com/1C-Company/v8-code-style/issues/332) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 6 | 59 | [Подсказка совпадает с синонимом.](https://github.com/1C-Company/v8-code-style/issues/338) | [478](https://its.1c.ru/db/v8std#content:478:hdoc) |
| 7 | 60 | [Не заполнен синоним конфигурации.](https://github.com/1C-Company/v8-code-style/issues/460) | [482](https://its.1c.ru/db/v8std#content:482:hdoc) |
| 8 | 61 | [В имени конфигурации запрещено использовать слова "редакция" или "подредакция".](https://github.com/1C-Company/v8-code-style/issues/463) | [482](https://its.1c.ru/db/v8std#content:482:hdoc) |
| 9 | 62 | [Неверно указан адрес информации о поставщике. Должен быть "http://www.1c.ru".](https://github.com/1C-Company/v8-code-style/issues/464) | [482](https://its.1c.ru/db/v8std#content:482:hdoc) |
| 10 | 63 | [Неверно указан адрес информации о конфигурации. Должен начинаться с "http://v8.1c.ru/".](https://github.com/1C-Company/v8-code-style/issues/465) | [482](https://its.1c.ru/db/v8std#content:482:hdoc) |
| 11 | 64 | [Неверно указан адрес каталога обновлений. Должен быть "http://downloads.v8.1c.ru/tmplts/".](https://github.com/1C-Company/v8-code-style/issues/466) | [482](https://its.1c.ru/db/v8std#content:482:hdoc) |
| 12 | 65 | [Использован метод "ПолучитьФорму()".](https://github.com/1C-Company/v8-code-style/issues/260) | [404](https://its.1c.ru/db/v8std#content:404:hdoc) |
| 13 | 66 | [Использована конструкция "ДЛЯ ИЗМЕНЕНИЯ".](https://github.com/1C-Company/v8-code-style/issues/178) | [460](https://its.1c.ru/db/v8std#content:460:hdoc) |
| 14 | 68 | [Использована конструкция "ДанныеФормыВЗначение()".](https://github.com/1C-Company/v8-code-style/issues/403) | [409](https://its.1c.ru/db/v8std#content:409:hdoc) |
| 15 | 69 | [Использован метод "Сообщить()".](https://github.com/1C-Company/v8-code-style/issues/268) | [418](https://its.1c.ru/db/v8std#content:418:hdoc) |
| 16 | 70 | [Использована конструкция "ПОЛНОЕ ВНЕШНЕЕ СОЕДИНЕНИЕ".](https://github.com/1C-Company/v8-code-style/issues/157) | [435](https://its.1c.ru/db/v8std#content:435:hdoc) |
| 17 | 71 | [Неверно установлен номер версии. Он не должен быть вида 0.0.0.0, N.0.0.0, N.0.N.0 или N.0.0.N.](https://github.com/1C-Company/v8-code-style/issues/109) | [483](https://its.1c.ru/db/v8std#content:483:hdoc) |
| 18 | 72 | [Синоним должен оканчиваться на номер редакции.подредакции.](https://github.com/1C-Company/v8-code-style/issues/287) | [482](https://its.1c.ru/db/v8std#content:482:hdoc) |
| 19 | 73 | [Рекомендуется избегать в названии общего модуля таких общих слов как "Процедуры", "Функции", "Обработчики", "Модуль", "Функциональность" и т.п.](https://github.com/1C-Company/v8-code-style/issues/468) | [469](https://its.1c.ru/db/v8std#content:469:hdoc) |
| 20 | 76 | [Неверно указана информация об авторских правах. Должно начинаться с "Copyright © ООО "1С-Софт"".](https://github.com/1C-Company/v8-code-style/issues/111) | [482](https://its.1c.ru/db/v8std#content:482:hdoc) |
| 21 | 78 | [Найдена экспортная процедура или функция в модуле формы.](https://github.com/1C-Company/v8-code-style/issues/261) | acc_125 |
| 22 | 82 | [Не установлено свойство "Использовать управляемые формы в обычном приложении" для конфигурации.](https://github.com/1C-Company/v8-code-style/issues/67) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 23 | 84 | [Привилегированный общий модуль должен именоваться с постфиксом "ПолныеПрава".](https://github.com/1C-Company/v8-code-style/issues/473) | [469](https://its.1c.ru/db/v8std#content:469:hdoc) |
| 24 | 85 | [Общий модуль с повторно используемыми значениями должен именоваться с постфиксом "ПовтИсп".](https://github.com/1C-Company/v8-code-style/issues/467) | [469](https://its.1c.ru/db/v8std#content:469:hdoc) |
| 25 | 86 | [Не задан синоним стандартного реквизита "Владелец".](https://github.com/1C-Company/v8-code-style/issues/119) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 26 | 87 | [Орфографическая ошибка в подсказке объекта метаданных.](https://github.com/1C-Company/v8-code-style/issues/69) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 27 | 89 | [Использован оператор "Перейти".](https://github.com/1C-Company/v8-code-style/issues/398) | [547](https://its.1c.ru/db/v8std#content:547:hdoc) |
| 28 | 90 | [Общий модуль, для которого предусмотрен вызов сервера, должен именоваться с постфиксом "ВызовСервера".](https://github.com/1C-Company/v8-code-style/issues/318) | [469](https://its.1c.ru/db/v8std#content:469:hdoc) |
| 29 | 96 | [Использована конструкция "ОБЪЕДИНИТЬ".](https://github.com/1C-Company/v8-code-style/issues/158) | [434](https://its.1c.ru/db/v8std#content:434:hdoc) |
| 30 | 99 | [Прикладной объект создан с помощью оператора Новый.](https://github.com/1C-Company/v8-code-style/issues/401) | [451](https://its.1c.ru/db/v8std#content:451:hdoc) |
| 31 | 100 | [Использован обработчик событий, подключаемый из кода и не содержащий префикса "Подключаемый_".](https://github.com/1C-Company/v8-code-style/issues/392) | [492](https://its.1c.ru/db/v8std#content:492:hdoc) |
| 32 | 101 | [Длина выражения превышает 120 символов.](https://github.com/1C-Company/v8-code-style/issues/482) | [444](https://its.1c.ru/db/v8std#content:444:hdoc) |
| 33 | 102 | [Неправильный перенос текста в выражении.](https://github.com/1C-Company/v8-code-style/issues/483) | [444](https://its.1c.ru/db/v8std#content:444:hdoc) |
| 34 | 103 | [Строка должна находиться в конце предыдущей.](https://github.com/1C-Company/v8-code-style/issues/389) | [444](https://its.1c.ru/db/v8std#content:444:hdoc) |
| 35 | 104 | [Использована директива компиляции.](https://github.com/1C-Company/v8-code-style/issues/390) | [439](https://its.1c.ru/db/v8std#content:439:hdoc) |
| 36 | 105 | [Использована явная запись наборов записей регистров (с помощью метода Записать) в процедуре обработки проведения.](https://github.com/1C-Company/v8-code-style/issues/400) | [450](https://its.1c.ru/db/v8std#content:450:hdoc) |
| 37 | 108 | [Документ не имеет реквизита "Комментарий".](https://github.com/1C-Company/v8-code-style/issues/487) | [531](https://its.1c.ru/db/v8std#content:531:hdoc) |
| 38 | 109 | [Реквизит "Комментарий" имеет недопустимый тип.](https://github.com/1C-Company/v8-code-style/issues/133) | [531](https://its.1c.ru/db/v8std#content:531:hdoc) |
| 39 | 113 | [Размер элемента управления типа "Кнопка" меньше 60х19.](https://github.com/1C-Company/v8-code-style/issues/491) | [516](https://its.1c.ru/db/v8std#content:516:hdoc) |
| 40 | 114 | [Элемент управления типа "Кнопка" содержит многострочный заголовок.](https://github.com/1C-Company/v8-code-style/issues/492) | [516](https://its.1c.ru/db/v8std#content:516:hdoc) |
| 41 | 115 | [Элемент управления типа "Кнопка" находится на командной панели.](https://github.com/1C-Company/v8-code-style/issues/284) | [516](https://its.1c.ru/db/v8std#content:516:hdoc) |
| 42 | 117 | [Ширина вертикального разделителя не соответствует требованиям.](https://github.com/1C-Company/v8-code-style/issues/494) | [515](https://its.1c.ru/db/v8std#content:515:hdoc) |
| 43 | 118 | [Высота горизонтального разделителя не соответствует требованиям.](https://github.com/1C-Company/v8-code-style/issues/283) | [515](https://its.1c.ru/db/v8std#content:515:hdoc) |
| 44 | 120 | [Расположение закладок у панели не соответствует требованиям.](https://github.com/1C-Company/v8-code-style/issues/282) | [511](https://its.1c.ru/db/v8std#content:511:hdoc) |
| 45 | 123 | [Обращение к полям регистратора.](https://github.com/1C-Company/v8-code-style/issues/131) | [477](https://its.1c.ru/db/v8std#content:477:hdoc) |
| 46 | 127 | [Отсутствует описание у экспортной переменной.](https://github.com/1C-Company/v8-code-style/issues/470) | acc_118 |
| 47 | 128 | [Отсутствует описание у переменной.](https://github.com/1C-Company/v8-code-style/issues/291) | acc_118 |
| 48 | 134 | [Количество параметров более 7.](https://github.com/1C-Company/v8-code-style/issues/474) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 49 | 141 | [Необязательные параметры расположены перед обязательными.](https://github.com/1C-Company/v8-code-style/issues/498) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 50 | 142 | [Количество необязательных параметров более 3.](https://github.com/1C-Company/v8-code-style/issues/206) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 51 | 143 | [Использование функции "ТекущаяДата()".](https://github.com/1C-Company/v8-code-style/issues/100) | [643](https://its.1c.ru/db/v8std#content:643:hdoc) |
| 52 | 144 | [Присвоение параметру "Отказ" значения, отличного от "Истина".](https://github.com/1C-Company/v8-code-style/issues/386) | [686](https://its.1c.ru/db/v8std#content:686:hdoc) |
| 53 | 145 | [Выключен флаг "Устанавливать права для новых объектов" или "Устанавливать права для реквизитов и табличных частей по умолчанию" у роли "Полные права".](https://github.com/1C-Company/v8-code-style/issues/475) | [532](https://its.1c.ru/db/v8std#content:532:hdoc) |
| 54 | 146 | [Флаг "Устанавливать права для новых объектов" должен быть включен только у роли "ПолныеПрава".](https://github.com/1C-Company/v8-code-style/issues/218) | [532](https://its.1c.ru/db/v8std#content:532:hdoc) |
| 55 | 148 | [Реквизит "Ссылка" динамического списка не выведен в таблицу на форме.](https://github.com/1C-Company/v8-code-style/issues/503) | [702](https://its.1c.ru/db/v8std#content:702:hdoc) |
| 56 | 149 | [У поля "Ссылка" таблицы динамического списка не отключена пользовательская видимость.](https://github.com/1C-Company/v8-code-style/issues/504) | [702](https://its.1c.ru/db/v8std#content:702:hdoc) |
| 57 | 150 | [Использована неправильная конструкция при установке внешней компоненты.](https://github.com/1C-Company/v8-code-style/issues/269) | [700](https://its.1c.ru/db/v8std#content:700:hdoc) |
| 58 | 152 | [В параметре "ИмяСобытия" метода "ЗаписьЖурналаРегистрации()" имеется лишний пробел после точки.](https://github.com/1C-Company/v8-code-style/issues/505) | [498](https://its.1c.ru/db/v8std#content:498:hdoc) |
| 59 | 153 | [Не локализован параметр "Комментарий" метода "ЗаписьЖурналаРегистрации()".](https://github.com/1C-Company/v8-code-style/issues/506) | [498](https://its.1c.ru/db/v8std#content:498:hdoc) |
| 60 | 154 | [Если в параметре "Комментарий" метода "ЗаписьЖурналаРегистрации()" указано подробное описание ошибки, то уровень журнала должен быть "Ошибка".](https://github.com/1C-Company/v8-code-style/issues/507) | [498](https://its.1c.ru/db/v8std#content:498:hdoc) |
| 61 | 156 | [Не локализован параметр "ИмяСобытия" метода "ЗаписьЖурналаРегистрации()".](https://github.com/1C-Company/v8-code-style/issues/476) | [498](https://its.1c.ru/db/v8std#content:498:hdoc) |
| 62 | 157 | [Для параметра "ИмяСобытия" метода "ЗаписьЖурналаРегистрации()" не задан основной язык конфигурации.](https://github.com/1C-Company/v8-code-style/issues/508) | [498](https://its.1c.ru/db/v8std#content:498:hdoc) |
| 63 | 160 | [У реквизита "Ссылка" динамического списка выключен признак "Использовать всегда".](https://github.com/1C-Company/v8-code-style/issues/266) | [702](https://its.1c.ru/db/v8std#content:702:hdoc) |
| 64 | 161 | [Параметр "ИмяСобытия" метода "ЗаписьЖурналаРегистрации()" инициализируется функцией, возращающей нелокализованную строку.](https://github.com/1C-Company/v8-code-style/issues/477) | [498](https://its.1c.ru/db/v8std#content:498:hdoc) |
| 65 | 162 | [Не установлено право.](https://github.com/1C-Company/v8-code-style/issues/509) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 66 | 163 | [Строка текста модуля содержит букву "ё".](https://github.com/1C-Company/v8-code-style/issues/193) | [456](https://its.1c.ru/db/v8std#content:456:hdoc) |
| 67 | 164 | [Установлено право "Удаление".](https://github.com/1C-Company/v8-code-style/issues/215) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 68 | 165 | [Установлено лишнее право.](https://github.com/1C-Company/v8-code-style/issues/216) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 69 | 216 | [Слово содержит кириллицу и латиницу.](https://github.com/1C-Company/v8-code-style/issues/341) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 70 | 217 | [Неправильная кодировка символа "минус".](https://github.com/1C-Company/v8-code-style/issues/518) | [456](https://its.1c.ru/db/v8std#content:456:hdoc) |
| 71 | 218 | [Установлены все права в роли "ПолныеПрава" для объекта, не входящего в состав разделителя.](https://github.com/1C-Company/v8-code-style/issues/519) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 72 | 219 | [Установлены все права в роли "АдминистраторСистемы" для объекта, входящего в состав разделителя.](https://github.com/1C-Company/v8-code-style/issues/520) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 73 | 220 | [Не установлено право в роли "АдминистраторСистемы".](https://github.com/1C-Company/v8-code-style/issues/430) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 74 | 222 | [Использование устаревшей процедуры.](https://github.com/1C-Company/v8-code-style/issues/522) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 75 | 223 | [Использование устаревшей функции.](https://github.com/1C-Company/v8-code-style/issues/379) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 76 | 224 | [Для обязательной роли установлен неправильный синоним.](https://github.com/1C-Company/v8-code-style/issues/429) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 77 | 226 | [В документе, предполагающем проведение, не установлен флаг "Привилегированный режим при проведении".](https://github.com/1C-Company/v8-code-style/issues/525) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 78 | 227 | [В документе, предполагающем проведение, не установлен флаг "Привилегированный режим при отмене проведения".](https://github.com/1C-Company/v8-code-style/issues/420) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 79 | 228 | [В функциональной опции не установлен флаг "Привилегированный режим при получении".](https://github.com/1C-Company/v8-code-style/issues/207) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 80 | 229 | [В роли есть право на изменение регистра, подчиненного регистратору.](https://github.com/1C-Company/v8-code-style/issues/421) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 81 | 232 | [Неверно установлены права на константу.](https://github.com/1C-Company/v8-code-style/issues/528) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 82 | 233 | [Для подсистемы верхнего уровня, отображаемой в командном интерфейсе, не найдено роли.](https://github.com/1C-Company/v8-code-style/issues/529) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 83 | 234 | [В роли не установлено право просмотра для подсистемы.](https://github.com/1C-Company/v8-code-style/issues/210) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 84 | 235 | [Орфографическая ошибка в имени элемента формы.](https://github.com/1C-Company/v8-code-style/issues/71) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 85 | 236 | [Неверно образован синоним объекта с префиксом "Удалить".](https://github.com/1C-Company/v8-code-style/issues/478) | [534](https://its.1c.ru/db/v8std#content:534:hdoc) |
| 86 | 239 | [В модуле должны быть определены стандартные области.](https://github.com/1C-Company/v8-code-style/issues/532) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 87 | 240 | [Код в модуле с (возможным) программным интерфейсом размещен вне стандартных областей.](https://github.com/1C-Company/v8-code-style/issues/533) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 88 | 241 | [Код размещен вне стандартных областей.](https://github.com/1C-Company/v8-code-style/issues/373) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 89 | 242 | [Не установлен признак переключения для интерфейса.](https://github.com/1C-Company/v8-code-style/issues/274) | acc_21 |
| 90 | 244 | [Длинные комментарии должны начинаться с большой буквы.](https://github.com/1C-Company/v8-code-style/issues/536) | [456](https://its.1c.ru/db/v8std#content:456:hdoc) |
| 91 | 246 | [Нет пробела в начале комментария.](https://github.com/1C-Company/v8-code-style/issues/368) | [456](https://its.1c.ru/db/v8std#content:456:hdoc) |
| 92 | 247 | [Имена переменных не должны состоять из одного символа.](https://github.com/1C-Company/v8-code-style/issues/538) | [454](https://its.1c.ru/db/v8std#content:454:hdoc) |
| 93 | 248 | [Имена переменных не должны начинаться с подчеркивания.](https://github.com/1C-Company/v8-code-style/issues/385) | [454](https://its.1c.ru/db/v8std#content:454:hdoc) |
| 94 | 249 | [Оператор "Перейти" не поддерживается платформой "1С:Предприятие" в режиме веб-клиента.](https://github.com/1C-Company/v8-code-style/issues/399) | [547](https://its.1c.ru/db/v8std#content:547:hdoc) |
| 95 | 250 | [Найдена экспортная процедура или функция в модуле команды.](https://github.com/1C-Company/v8-code-style/issues/402) | [544](https://its.1c.ru/db/v8std#content:544:hdoc) |
| 96 | 251 | [Обязательная роль не установлена как основная роль конфигурации.](https://github.com/1C-Company/v8-code-style/issues/542) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 97 | 254 | [Ключевое слово запроса написано не канонически.](https://github.com/1C-Company/v8-code-style/issues/152) | [437](https://its.1c.ru/db/v8std#content:437:hdoc) |
| 98 | 256 | [В объекте "Подписка на событие" использован обработчик из общего модуля, не являющегося клиент-серверным.](https://github.com/1C-Company/v8-code-style/issues/544) | [680](https://its.1c.ru/db/v8std#content:680:hdoc) |
| 99 | 259 | [Имя неверно образовано из синонима с префиксом "(не используется)".](https://github.com/1C-Company/v8-code-style/issues/137) | [534](https://its.1c.ru/db/v8std#content:534:hdoc) |
| 100 | 263 | [Неверно снят флажок "Включать в командный интерфейс". Для подсистемы, включенной в пользовательский командный интерфейс, он должен быть установлен.](https://github.com/1C-Company/v8-code-style/issues/479) | [543](https://its.1c.ru/db/v8std#content:543:hdoc) |
| 101 | 264 | [Неверно установлен флажок "Включать в командный интерфейс". Для функциональной подсистемы он должен быть снят.](https://github.com/1C-Company/v8-code-style/issues/480) | [543](https://its.1c.ru/db/v8std#content:543:hdoc) |
| 102 | 265 | [Орфографическая ошибка в имени объекта метаданных.](https://github.com/1C-Company/v8-code-style/issues/79) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 103 | 267 | [Количество параметров вызова процедуры или функции не соответствует количеству параметров ее определения.](https://github.com/1C-Company/v8-code-style/issues/481) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 104 | 269 | [Обращение к несуществующей подсистеме.](https://github.com/1C-Company/v8-code-style/issues/484) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 105 | 271 | [Обращение к несуществующему общему модулю.](https://github.com/1C-Company/v8-code-style/issues/485) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 106 | 273 | [В структуре модуля присутствуют пустые области.](https://github.com/1C-Company/v8-code-style/issues/486) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 107 | 274 | [Неправильный порядок стандартных областей в коде.](https://github.com/1C-Company/v8-code-style/issues/488) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 108 | 275 | [Обращение к несуществующему элементу формы.](https://github.com/1C-Company/v8-code-style/issues/264) | acc_3 |
| 109 | 277 | [Недопустимый вызов служебной процедуры или функции другой подсистемы.](https://github.com/1C-Company/v8-code-style/issues/489) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 110 | 278 | [Недопустимый вызов служебного программного интерфейса.](https://github.com/1C-Company/v8-code-style/issues/98) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 111 | 282 | [Закомментированный код или отсутствие пробела в комментарии после знака "//".](https://github.com/1C-Company/v8-code-style/issues/490) | [456](https://its.1c.ru/db/v8std#content:456:hdoc) |
| 112 | 283 | [Обращение к несуществующей роли.](https://github.com/1C-Company/v8-code-style/issues/546) | acc_2 |
| 113 | 284 | [В тексте модуля содержатся служебные комментарии.](https://github.com/1C-Company/v8-code-style/issues/195) | [456](https://its.1c.ru/db/v8std#content:456:hdoc) |
| 114 | 285 | [Закомментированный код содержит запрещенный символ.](https://github.com/1C-Company/v8-code-style/issues/197) | [456](https://its.1c.ru/db/v8std#content:456:hdoc) |
| 115 | 286 | [Стандартная область является вложенной.](https://github.com/1C-Company/v8-code-style/issues/374) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 116 | 287 | [Обращение к несуществующей процедуре (функции).](https://github.com/1C-Company/v8-code-style/issues/380) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 117 | 288 | [Устаревшая функция содержит код.](https://github.com/1C-Company/v8-code-style/issues/549) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 118 | 289 | [Устаревшая процедура содержит код.](https://github.com/1C-Company/v8-code-style/issues/205) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 119 | 290 | [Неверно установлены права в роли для чтения.](https://github.com/1C-Company/v8-code-style/issues/551) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 120 | 291 | [Неверно установлены права в роли для изменения.](https://github.com/1C-Company/v8-code-style/issues/422) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 121 | 294 | [В конструкторе объекта типа "Структура" использован конструктор другого объекта с параметрами.](https://github.com/1C-Company/v8-code-style/issues/554) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 122 | 295 | [В конструкторе объекта типа "Структура" использован вызов функции с количеством параметров более 3-х.](https://github.com/1C-Company/v8-code-style/issues/382) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 123 | 299 | [Возможно, неиспользуемая экспортная процедура (функция).](https://github.com/1C-Company/v8-code-style/issues/493) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 124 | 302 | [В начале процедуры обработки регламентного задания отсутствует вызов процедуры "ОбщегоНазначения.ПриНачалеВыполненияРегламентногоЗадания();".](https://github.com/1C-Company/v8-code-style/issues/557) | [540](https://its.1c.ru/db/v8std#content:540:hdoc) |
| 125 | 304 | [Неверно установлено значение свойства "ОбновлениеПредопределенныхДанных".](https://github.com/1C-Company/v8-code-style/issues/495) | [697](https://its.1c.ru/db/v8std#content:697:hdoc) |
| 126 | 305 | [Программный вызов метода "УстановитьОбновлениеПредопределенныхДанных" используется для переключения режима "ОбновлениеПредопределенныхДанных".](https://github.com/1C-Company/v8-code-style/issues/141) | [697](https://its.1c.ru/db/v8std#content:697:hdoc) |
| 127 | 307 | [Название процедуры (функции) содержит описание типов принимаемых параметров или возвращаемых значений.](https://github.com/1C-Company/v8-code-style/issues/203) | [647](https://its.1c.ru/db/v8std#content:647:hdoc) |
| 128 | 311 | [Длина названия раздела превышает 35 символов.](https://github.com/1C-Company/v8-code-style/issues/432) | [712](https://its.1c.ru/db/v8std#content:712:hdoc) |
| 129 | 313 | [Длина названия команды превышает 38 символов.](https://github.com/1C-Company/v8-code-style/issues/256) | [714](https://its.1c.ru/db/v8std#content:714:hdoc) |
| 130 | 319 | [Периодичность выполнения регламентного задания меньше одной минуты.](https://github.com/1C-Company/v8-code-style/issues/151) | [402](https://its.1c.ru/db/v8std#content:402:hdoc) |
| 131 | 320 | [НСтр() в выражении параметра макета. Значение параметра нужно задавать с помощью НСтр() не в колонке "Выражение", а в модуле отчета.](https://github.com/1C-Company/v8-code-style/issues/242) | [762](https://its.1c.ru/db/v8std#content:762:hdoc) |
| 132 | 321 | [Термин "1C:Предприятие" ошибочно задан с латинской "C".](https://github.com/1C-Company/v8-code-style/issues/560) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 133 | 323 | [Орфографическая ошибка в заголовке команды формы.](https://github.com/1C-Company/v8-code-style/issues/561) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 134 | 324 | [Орфографическая ошибка в подсказке команды формы.](https://github.com/1C-Company/v8-code-style/issues/562) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 135 | 325 | [Нарушена схема работы с транзакциями: отсутствует вызов "НачатьТранзакцию()", хотя вызываются "ЗафиксироватьТранзакцию()"/"ОтменитьТранзакцию()".](https://github.com/1C-Company/v8-code-style/issues/563) | [783](https://its.1c.ru/db/v8std#content:783:hdoc) |
| 136 | 326 | [Нарушена схема работы с транзакциями: для вызова "НачатьТранзакцию()" отсутствует парный вызов "ЗафиксироватьТранзакцию()".](https://github.com/1C-Company/v8-code-style/issues/564) | [783](https://its.1c.ru/db/v8std#content:783:hdoc) |
| 137 | 327 | [Нарушена схема работы с транзакциями: для вызова "НачатьТранзакцию()" отсутствует парный вызов "ОтменитьТранзакцию()".](https://github.com/1C-Company/v8-code-style/issues/565) | [783](https://its.1c.ru/db/v8std#content:783:hdoc) |
| 138 | 328 | [Нарушена схема работы с транзакциями: не найден оператор "Попытка" после вызова "НачатьТранзакцию()".](https://github.com/1C-Company/v8-code-style/issues/566) | [783](https://its.1c.ru/db/v8std#content:783:hdoc) |
| 139 | 329 | [Нарушена схема работы с транзакциями: вызов "ЗафиксироватьТранзакцию()" находится вне конструкции "Попытка... Исключение".](https://github.com/1C-Company/v8-code-style/issues/567) | [783](https://its.1c.ru/db/v8std#content:783:hdoc) |
| 140 | 330 | [Нарушена схема работы с транзакциями: между "ЗафиксироватьТранзакцию()" и "Исключение" есть исполняемый код, который может вызвать исключение.](https://github.com/1C-Company/v8-code-style/issues/568) | [783](https://its.1c.ru/db/v8std#content:783:hdoc) |
| 141 | 331 | [Нарушена схема работы с транзакциями: между "НачатьТранзакцию()" и "Попытка" есть исполняемый код.](https://github.com/1C-Company/v8-code-style/issues/569) | [783](https://its.1c.ru/db/v8std#content:783:hdoc) |
| 142 | 332 | [Нарушена схема работы с транзакциями: вызов "ОтменитьТранзакцию()" отсутствует в конструкции "Исключение... КонецПопытки".](https://github.com/1C-Company/v8-code-style/issues/570) | [783](https://its.1c.ru/db/v8std#content:783:hdoc) |
| 143 | 334 | [Функция НСтр() использована для локализации внутренних идентификаторов.](https://github.com/1C-Company/v8-code-style/issues/248) | [764](https://its.1c.ru/db/v8std#content:764:hdoc) |
| 144 | 335 | [Обнаружена нелокализованная дата.](https://github.com/1C-Company/v8-code-style/issues/247) | [763](https://its.1c.ru/db/v8std#content:763:hdoc) |
| 145 | 336 | [Использован метод "РольДоступна()".](https://github.com/1C-Company/v8-code-style/issues/427) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 146 | 341 | [Использована ролевая настройка видимости для элемента формы.](https://github.com/1C-Company/v8-code-style/issues/219) | [737](https://its.1c.ru/db/v8std#content:737:hdoc) |
| 147 | 345 | [Небезопасное хранение паролей в информационной базе.](https://github.com/1C-Company/v8-code-style/issues/412) | [740](https://its.1c.ru/db/v8std#content:740:hdoc) |
| 148 | 346 | [Обращение к несуществующему параметру формы.](https://github.com/1C-Company/v8-code-style/issues/439) | [404](https://its.1c.ru/db/v8std#content:404:hdoc) |
| 149 | 347 | [Не задан синоним стандартного реквизита "Родитель".](https://github.com/1C-Company/v8-code-style/issues/576) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 150 | 349 | [Синоним стандартного реквизита "Владелец" совпадает с наименованием.](https://github.com/1C-Company/v8-code-style/issues/577) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 151 | 350 | [Синоним стандартного реквизита "Родитель" совпадает с наименованием.](https://github.com/1C-Company/v8-code-style/issues/496) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 152 | 351 | [Бессмысленное (автосгенерированное) имя элемента формы.](https://github.com/1C-Company/v8-code-style/issues/121) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 153 | 352 | [Бессмысленное (автосгенерированное) имя реквизита формы.](https://github.com/1C-Company/v8-code-style/issues/123) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 154 | 353 | [Бессмысленное (автосгенерированное) имя команды формы.](https://github.com/1C-Company/v8-code-style/issues/335) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 155 | 354 | [Бессмысленное (автосгенерированное) имя параметра формы.](https://github.com/1C-Company/v8-code-style/issues/336) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 156 | 355 | [Орфографическая ошибка в имени команды формы.](https://github.com/1C-Company/v8-code-style/issues/70) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 157 | 356 | [Орфографическая ошибка в имени реквизита формы.](https://github.com/1C-Company/v8-code-style/issues/583) | acc_73 |
| 158 | 357 | [Орфографическая ошибка в заголовке реквизита формы.](https://github.com/1C-Company/v8-code-style/issues/285) | acc_73 |
| 159 | 358 | [Орфографическая ошибка в имени параметра формы.](https://github.com/1C-Company/v8-code-style/issues/286) | acc_73 |
| 160 | 359 | [Ограничения на уровне записей в разных ролях не совпадает для той же таблицы и права.](https://github.com/1C-Company/v8-code-style/issues/586) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 161 | 360 | [Ограничения для права "Добавление" не совпадает с ограничением права "Изменение".](https://github.com/1C-Company/v8-code-style/issues/587) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 162 | 361 | [Серверный код не заключен в инструкцию препроцессора: "#Если Сервер Или ТолстыйКлиентОбычноеПриложение Или ВнешнееСоединение Тогда...".](https://github.com/1C-Company/v8-code-style/issues/497) | [680](https://its.1c.ru/db/v8std#content:680:hdoc) |
| 163 | 362 | [Обработчик события заключен в инструкцию препроцессора: "#Если Сервер Или ТолстыйКлиентОбычноеПриложение Или ВнешнееСоединение Тогда...".](https://github.com/1C-Company/v8-code-style/issues/319) | [680](https://its.1c.ru/db/v8std#content:680:hdoc) |
| 164 | 363 | [Не следует добавлять постфикс "Клиент" в наименование глобального общего модуля с постфиксом "Глобальный".](https://github.com/1C-Company/v8-code-style/issues/499) | [469](https://its.1c.ru/db/v8std#content:469:hdoc) |
| 165 | 370 | [Параметр "ИмяСобытия" метода "ЗаписьЖурналаРегистрации()" инициализируется переменной, содержащей нелокализованную строку.](https://github.com/1C-Company/v8-code-style/issues/589) | [498](https://its.1c.ru/db/v8std#content:498:hdoc) |
| 166 | 371 | [При локализации параметра "ИмяСобытия" для получения кода языка следует использовать функцию ОбщегоНазначенияКлиентСервер.КодОсновногоЯзыка().](https://github.com/1C-Company/v8-code-style/issues/393) | [498](https://its.1c.ru/db/v8std#content:498:hdoc) |
| 167 | 374 | [Сообщение содержит восклицательный знак.](https://github.com/1C-Company/v8-code-style/issues/270) | [585](https://its.1c.ru/db/v8std#content:585:hdoc) |
| 168 | 375 | [Избыточно установлены права на устаревший объект метаданных (с префиксом "Удалить").](https://github.com/1C-Company/v8-code-style/issues/425) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 169 | 376 | [Превышена максимальная длина числовых данных (31 знак).](https://github.com/1C-Company/v8-code-style/issues/82) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 170 | 377 | [Превышена максимальная длина ресурса регистра накопления или бухгалтерии (25 знаков).](https://github.com/1C-Company/v8-code-style/issues/84) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 171 | 379 | [В качестве правого операнда операции сравнения "ПОДОБНО" указано поле таблицы.](https://github.com/1C-Company/v8-code-style/issues/86) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 172 | 380 | [Длина индекса составляет больше 900 байт.](https://github.com/1C-Company/v8-code-style/issues/87) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 173 | 384 | [Установлено нулевое поле у табличного документа](https://github.com/1C-Company/v8-code-style/issues/438) | [548](https://its.1c.ru/db/v8std#content:548:hdoc) |
| 174 | 386 | [В свойствах формы настроено условное оформление.](https://github.com/1C-Company/v8-code-style/issues/593) | [710](https://its.1c.ru/db/v8std#content:710:hdoc) |
| 175 | 387 | [В свойствах динамического списка настроено условное оформление.](https://github.com/1C-Company/v8-code-style/issues/262) | [710](https://its.1c.ru/db/v8std#content:710:hdoc) |
| 176 | 388 | [Неэкспортная процедура (функция) в разделе "ПрограммныйИнтерфейс".](https://github.com/1C-Company/v8-code-style/issues/595) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 177 | 389 | [Неэкспортная процедура (функция) в разделе "СлужебныйПрограммныйИнтерфейс".](https://github.com/1C-Company/v8-code-style/issues/378) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 178 | 393 | [Использована ролевая настройка просмотра для реквизита формы.](https://github.com/1C-Company/v8-code-style/issues/596) | [737](https://its.1c.ru/db/v8std#content:737:hdoc) |
| 179 | 394 | [Использована ролевая настройка редактирования для реквизита формы.](https://github.com/1C-Company/v8-code-style/issues/220) | [737](https://its.1c.ru/db/v8std#content:737:hdoc) |
| 180 | 395 | [Использована ролевая настройка использования для команды формы.](https://github.com/1C-Company/v8-code-style/issues/221) | [737](https://its.1c.ru/db/v8std#content:737:hdoc) |
| 181 | 397 | [Не задан таймаут для объекта при работе с внешними ресурсами.](https://github.com/1C-Company/v8-code-style/issues/500) | [748](https://its.1c.ru/db/v8std#content:748:hdoc) |
| 182 | 398 | [Задан нулевой таймаут для объекта при работе с внешними ресурсами.](https://github.com/1C-Company/v8-code-style/issues/410) | [748](https://its.1c.ru/db/v8std#content:748:hdoc) |
| 183 | 399 | [Отсутствует или неверно описана секция "Параметры" в комментарии к экспортной процедуре (функции).](https://github.com/1C-Company/v8-code-style/issues/501) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 184 | 400 | [Отсутствует или неверно описана секция "Возвращаемое значение" в комментарии к экспортной функции.](https://github.com/1C-Company/v8-code-style/issues/598) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 185 | 401 | [Не описаны некоторые параметры в секции "Параметры" в комментарии к экспортной процедуре (функции).](https://github.com/1C-Company/v8-code-style/issues/599) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 186 | 402 | [Секция "Возвращаемое значение" находится перед секцией "Параметры" в комментарии к экспортной функции.](https://github.com/1C-Company/v8-code-style/issues/600) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 187 | 403 | [Некорректно оформлена гиперссылка "См. ..." в комментарии к экспортной процедуре (функции).](https://github.com/1C-Company/v8-code-style/issues/601) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 188 | 404 | [Не найден объект переадресации из гиперссылки "См. ..." в комментарии к экспортной процедуре (функции).](https://github.com/1C-Company/v8-code-style/issues/502) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 189 | 405 | [Описаны лишние параметры в секции "Параметры" в комментарии к экспортной процедуре (функции).](https://github.com/1C-Company/v8-code-style/issues/510) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 190 | 406 | [Некорректно описаны некоторые параметры в секции "Параметры" в комментарии к экспортной процедуре (функции).](https://github.com/1C-Company/v8-code-style/issues/511) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 191 | 407 | [Некорректно описан тип некоторых параметров в секции "Параметры" в комментарии к экспортной процедуре (функции).](https://github.com/1C-Company/v8-code-style/issues/512) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 192 | 408 | [Описание параметров в секции "Параметры" должно начинаться с новой строки в комментарии к экспортной процедуре (функции).](https://github.com/1C-Company/v8-code-style/issues/516) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 193 | 409 | [Описание возвращаемого значения в секции "Возвращаемое значение" должно начинаться с новой строки в комментарии к экспортной функции.](https://github.com/1C-Company/v8-code-style/issues/517) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 194 | 410 | [Описана секция "Параметры" в комментарии к экспортной процедуре (функции), не имеющей параметров.](https://github.com/1C-Company/v8-code-style/issues/521) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 195 | 412 | [Значение свойства "История выбора при вводе" у документа не равно "Не использовать".](https://github.com/1C-Company/v8-code-style/issues/523) | [744](https://its.1c.ru/db/v8std#content:744:hdoc) |
| 196 | 413 | [Присутствует обработчик "ОбработкаПолученияДанныхВыбора", а свойство "История выбора при вводе" не равно "Не использовать".](https://github.com/1C-Company/v8-code-style/issues/263) | [744](https://its.1c.ru/db/v8std#content:744:hdoc) |
| 197 | 414 | [Неверно установлены свойства поля формы, которое ссылается на объект метаданных с отключенной историей выбора при вводе.](https://github.com/1C-Company/v8-code-style/issues/440) | [744](https://its.1c.ru/db/v8std#content:744:hdoc) |
| 198 | 415 | [Нарушена схема работы с транзакциями: отличаются условия "Если ... Тогда" у методов работы с транзакциями.](https://github.com/1C-Company/v8-code-style/issues/602) | [783](https://its.1c.ru/db/v8std#content:783:hdoc) |
| 199 | 416 | [Отсутствует или некорректно описан тип возвращаемого значения в комментарии к экспортной функции.](https://github.com/1C-Company/v8-code-style/issues/524) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 200 | 417 | [Некорректно описан тип некоторых свойств возвращаемого значения в комментарии к экспортной функции.](https://github.com/1C-Company/v8-code-style/issues/526) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 201 | 420 | [В праве "Удаление" роли установлены ограничения (RLS) для объекта метаданных.](https://github.com/1C-Company/v8-code-style/issues/211) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 202 | 421 | [Права на константу установлены в обеих ролях: "Полные права" и "Администратор системы".](https://github.com/1C-Company/v8-code-style/issues/527) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 203 | 422 | [Права на константу не установлены ни в одной из ролей: "Полные права" и "Администратор системы".](https://github.com/1C-Company/v8-code-style/issues/423) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 204 | 423 | [Для роли "Изменение<ИмяКонстанты>" установлены права на другой объект метаданных.](https://github.com/1C-Company/v8-code-style/issues/530) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 205 | 424 | [Для роли "Чтение<ИмяКонстанты>" установлены права на другой объект метаданных.](https://github.com/1C-Company/v8-code-style/issues/209) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 206 | 425 | [Отсутствует область "ОписаниеПеременных" в тексте модуля.](https://github.com/1C-Company/v8-code-style/issues/531) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 207 | 426 | [Переменная объявлена в области, содержащей процедуру или функцию.](https://github.com/1C-Company/v8-code-style/issues/534) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 208 | 427 | [Отсутствует область "Инициализация" в тексте модуля.](https://github.com/1C-Company/v8-code-style/issues/535) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 209 | 428 | [Операторы раздела инициализации расположены в области, содержащей процедуры или функции.](https://github.com/1C-Company/v8-code-style/issues/603) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 210 | 429 | [Переменная объявлена вне области "ОписаниеПеременных".](https://github.com/1C-Company/v8-code-style/issues/200) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 211 | 430 | [Операторы раздела инициализации расположены вне области "Инициализация".](https://github.com/1C-Company/v8-code-style/issues/375) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 212 | 435 | [Возврат константы типа Строка в модуле с повторным использованием.](https://github.com/1C-Company/v8-code-style/issues/607) | [724](https://its.1c.ru/db/v8std#content:724:hdoc) |
| 213 | 436 | [Возврат константы типа Число в модуле с повторным использованием.](https://github.com/1C-Company/v8-code-style/issues/608) | [724](https://its.1c.ru/db/v8std#content:724:hdoc) |
| 214 | 437 | [Возврат константы типа Дата в модуле с повторным использованием.](https://github.com/1C-Company/v8-code-style/issues/609) | [724](https://its.1c.ru/db/v8std#content:724:hdoc) |
| 215 | 438 | [Возврат константы типа Булево в модуле с повторным использованием.](https://github.com/1C-Company/v8-code-style/issues/610) | [724](https://its.1c.ru/db/v8std#content:724:hdoc) |
| 216 | 439 | [Возврат предопределенного элемента в модуле с повторным использованием.](https://github.com/1C-Company/v8-code-style/issues/537) | [724](https://its.1c.ru/db/v8std#content:724:hdoc) |
| 217 | 441 | [Отсутствует удаление временного файла после использования.](https://github.com/1C-Company/v8-code-style/issues/409) | [542](https://its.1c.ru/db/v8std#content:542:hdoc) |
| 218 | 442 | [Ошибка выполнения проверки: не удалось получить версию БСП.](https://github.com/1C-Company/v8-code-style/issues/612) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 219 | 443 | [Ошибка выполнения проверки: не удалось получить назначение ролей пользователей.](https://github.com/1C-Company/v8-code-style/issues/424) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 220 | 444 | [Экспортная процедура в модуле с повторным использованием.](https://github.com/1C-Company/v8-code-style/issues/407) | [724](https://its.1c.ru/db/v8std#content:724:hdoc) |
| 221 | 445 | [В реквизит формы присвоена нелокализованная строка.](https://github.com/1C-Company/v8-code-style/issues/249) | [765](https://its.1c.ru/db/v8std#content:765:hdoc) |
| 222 | 446 | [У элемента формы не заполнен заголовок.](https://github.com/1C-Company/v8-code-style/issues/250) | [765](https://its.1c.ru/db/v8std#content:765:hdoc) |
| 223 | 447 | [У элемента формы бессмысленная подсказка.](https://github.com/1C-Company/v8-code-style/issues/125) | [478](https://its.1c.ru/db/v8std#content:478:hdoc) |
| 224 | 449 | [Задано наименование предопределенного регламентного задания.](https://github.com/1C-Company/v8-code-style/issues/431) | [767](https://its.1c.ru/db/v8std#content:767:hdoc) |
| 225 | 450 | [В макете используется кодировка, отличная от "UTF-8".](https://github.com/1C-Company/v8-code-style/issues/253) | [766](https://its.1c.ru/db/v8std#content:766:hdoc) |
| 226 | 453 | [Обращение к менеджеру регламентных заданий при наличии подсистемы "Технология сервиса".](https://github.com/1C-Company/v8-code-style/issues/360) | [760](https://its.1c.ru/db/v8std#content:760:hdoc) |
| 227 | 454 | [Регламентное задание включено в состав разделителя.](https://github.com/1C-Company/v8-code-style/issues/622) | [760](https://its.1c.ru/db/v8std#content:760:hdoc) |
| 228 | 455 | [Регламентное задание с включенным использованием отсутствует в процедуре "ОчередьЗаданийПереопределяемый.ПриПолученииСпискаШаблонов".](https://github.com/1C-Company/v8-code-style/issues/361) | [760](https://its.1c.ru/db/v8std#content:760:hdoc) |
| 229 | 456 | [Использование метода "СтрШаблон()".](https://github.com/1C-Company/v8-code-style/issues/254) |  |
| 230 | 457 | [Использована ролевая настройка видимости в командном интерфейсе конфигурации.](https://github.com/1C-Company/v8-code-style/issues/222) | [737](https://its.1c.ru/db/v8std#content:737:hdoc) |
| 231 | 458 | [Устаревшая процедура (функция) расположена вне области "УстаревшиеПроцедурыИФункции".](https://github.com/1C-Company/v8-code-style/issues/232) | [644](https://its.1c.ru/db/v8std#content:644:hdoc) |
| 232 | 459 | [Описание процедуры (функции) размещено в середине, а не в начале комментария; либо описание параметра без отступа.](https://github.com/1C-Company/v8-code-style/issues/626) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 233 | 460 | [Использована ролевая настройка видимости в рабочей области начальной страницы.](https://github.com/1C-Company/v8-code-style/issues/223) | [737](https://its.1c.ru/db/v8std#content:737:hdoc) |
| 234 | 461 | [Использована ролевая настройка видимости в командном интерфейсе основного раздела.](https://github.com/1C-Company/v8-code-style/issues/224) | [737](https://its.1c.ru/db/v8std#content:737:hdoc) |
| 235 | 462 | [Для строковой константы запроса СКД не установлено представление доступного значения.](https://github.com/1C-Company/v8-code-style/issues/539) | [762](https://its.1c.ru/db/v8std#content:762:hdoc) |
| 236 | 464 | [Не заполнен заголовок поля динамического списка.](https://github.com/1C-Company/v8-code-style/issues/244) | [765](https://its.1c.ru/db/v8std#content:765:hdoc) |
| 237 | 465 | [Не заполнен заголовок поля выборки в запросе СКД.](https://github.com/1C-Company/v8-code-style/issues/245) | [762](https://its.1c.ru/db/v8std#content:762:hdoc) |
| 238 | 466 | [Нестандартные секции в описании экспортной процедуры (функции).](https://github.com/1C-Company/v8-code-style/issues/204) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 239 | 467 | [Использование монопольного или оперативного обработчика обновления.](https://github.com/1C-Company/v8-code-style/issues/238) | acc_77 |
| 240 | 468 | [Экспортная процедура (функция) в области "ПрограммныйИнтерфейс" в общем модуле с повторным использованием.](https://github.com/1C-Company/v8-code-style/issues/234) | [644](https://its.1c.ru/db/v8std#content:644:hdoc) |
| 241 | 469 | [В служебном общем модуле присутствует область "ПрограммныйИнтерфейс".](https://github.com/1C-Company/v8-code-style/issues/233) | [644](https://its.1c.ru/db/v8std#content:644:hdoc) |
| 242 | 472 | [Небезопасное подключение внешних компонент.](https://github.com/1C-Company/v8-code-style/issues/413) | [669](https://its.1c.ru/db/v8std#content:669:hdoc) |
| 243 | 473 | [Область "ДляВызоваИзДругихПодсистем" не входит в область "ПрограммныйИнтерфейс".](https://github.com/1C-Company/v8-code-style/issues/632) | [644](https://its.1c.ru/db/v8std#content:644:hdoc) |
| 244 | 474 | [В комментарии внутри области "ДляВызоваИзДругихПодсистем" не указана подсистема-потребитель.](https://github.com/1C-Company/v8-code-style/issues/633) | [644](https://its.1c.ru/db/v8std#content:644:hdoc) |
| 245 | 475 | [В комментарии внутри области "ДляВызоваИзДругихПодсистем" указана несуществующая подсистема.](https://github.com/1C-Company/v8-code-style/issues/634) | [644](https://its.1c.ru/db/v8std#content:644:hdoc) |
| 246 | 476 | [Внутри области "ДляВызоваИзДругихПодсистем" не найден закрывающий комментарий "// Конец ... <имя подсистемы-потребителя>".](https://github.com/1C-Company/v8-code-style/issues/235) | [644](https://its.1c.ru/db/v8std#content:644:hdoc) |
| 247 | 478 | [Нарушена схема работы с транзакциями: между "Исключение" и "ОтменитьТранзакцию()" есть исполняемый код.](https://github.com/1C-Company/v8-code-style/issues/540) | [783](https://its.1c.ru/db/v8std#content:783:hdoc) |
| 248 | 482 | [Запрос динамического списка, не содержащий секции "ИЗ", не переопределен в модуле формы.](https://github.com/1C-Company/v8-code-style/issues/636) | [768](https://its.1c.ru/db/v8std#content:768:hdoc) |
| 249 | 483 | [Псевдоним таблицы запроса динамического списка, переопределяемого программно, не заканчивается постфиксом "Переопределяемый".](https://github.com/1C-Company/v8-code-style/issues/637) | [768](https://its.1c.ru/db/v8std#content:768:hdoc) |
| 250 | 484 | [Программная установка свойств динамического списка выполняется без помощи процедуры "ОбщегоНазначения.УстановитьСвойстваДинамическогоСписка()".](https://github.com/1C-Company/v8-code-style/issues/267) | [768](https://its.1c.ru/db/v8std#content:768:hdoc) |
| 251 | 486 | [Отсутствует включение безопасного режима перед вызовом метода "Выполнить" или "Вычислить".](https://github.com/1C-Company/v8-code-style/issues/639) | [770](https://its.1c.ru/db/v8std#content:770:hdoc) |
| 252 | 487 | [Обнаружен вызов метода "Выполнить" вместо "ОбщегоНазначения.ВыполнитьВБезопасномРежиме()".](https://github.com/1C-Company/v8-code-style/issues/640) | [770](https://its.1c.ru/db/v8std#content:770:hdoc) |
| 253 | 488 | [Обнаружен вызов метода "Вычислить" вместо "ОбщегоНазначения.ВычислитьВБезопасномРежиме()".](https://github.com/1C-Company/v8-code-style/issues/641) | [770](https://its.1c.ru/db/v8std#content:770:hdoc) |
| 254 | 489 | [Обнаружен вызов метода "Выполнить" вместо "РаботаВБезопасномРежиме.ВыполнитьВБезопасномРежиме()".](https://github.com/1C-Company/v8-code-style/issues/541) | [770](https://its.1c.ru/db/v8std#content:770:hdoc) |
| 255 | 490 | [Обнаружен вызов метода "Вычислить" вместо "РаботаВБезопасномРежиме.ВычислитьВБезопасномРежиме()".](https://github.com/1C-Company/v8-code-style/issues/417) | [770](https://its.1c.ru/db/v8std#content:770:hdoc) |
| 256 | 491 | [Некорректная локализация фрагмента текста запроса.](https://github.com/1C-Company/v8-code-style/issues/241) | [762](https://its.1c.ru/db/v8std#content:762:hdoc) |
| 257 | 492 | [Неверно задан именованный параметр подстановки.](https://github.com/1C-Company/v8-code-style/issues/240) | [761](https://its.1c.ru/db/v8std#content:761:hdoc) |
| 258 | 495 | [Вызов функции "КаталогВременныхФайлов()".](https://github.com/1C-Company/v8-code-style/issues/408) | [542](https://its.1c.ru/db/v8std#content:542:hdoc) |
| 259 | 496 | [Не заполнен параметр "РегламентноеЗадание" при вызове процедуры "ОбщегоНазначения.ПриНачалеВыполненияРегламентногоЗадания();".](https://github.com/1C-Company/v8-code-style/issues/150) | [540](https://its.1c.ru/db/v8std#content:540:hdoc) |
| 260 | 497 | [В имени макета отсутствует суффикс (например, "_ru").](https://github.com/1C-Company/v8-code-style/issues/252) | [766](https://its.1c.ru/db/v8std#content:766:hdoc) |
| 261 | 499 | [Процедура переопределяемого общего модуля содержит лишний код (должен быть только код вида "<ИмяМодуля>.<ИмяПроцедуры>(<Параметры>);").](https://github.com/1C-Company/v8-code-style/issues/543) | [554](https://its.1c.ru/db/v8std#content:554:hdoc) |
| 262 | 500 | [Имя вызываемой процедуры отличается от имени переопределяемой процедуры.](https://github.com/1C-Company/v8-code-style/issues/545) | [554](https://its.1c.ru/db/v8std#content:554:hdoc) |
| 263 | 501 | [Отличается состав или порядок параметров вызываемой и переопределяемой процедуры.](https://github.com/1C-Company/v8-code-style/issues/547) | [554](https://its.1c.ru/db/v8std#content:554:hdoc) |
| 264 | 502 | [Неверный комментарий у процедуры, вызываемой в переопределяемом модуле (должен быть "См. <ПереопределяемыйМодуль>.<ПереопределяемаяПроцедура>.").](https://github.com/1C-Company/v8-code-style/issues/548) | [554](https://its.1c.ru/db/v8std#content:554:hdoc) |
| 265 | 503 | [В переопределяемом общем модуле описана функция.](https://github.com/1C-Company/v8-code-style/issues/550) | [554](https://its.1c.ru/db/v8std#content:554:hdoc) |
| 266 | 505 | [В переопределяемом общем модуле описана неэкспортная процедура (функция).](https://github.com/1C-Company/v8-code-style/issues/552) | [554](https://its.1c.ru/db/v8std#content:554:hdoc) |
| 267 | 506 | [В переопределяемом общем модуле описана лишняя внешняя область (должна быть только область "ПрограммныйИнтерфейс").](https://github.com/1C-Company/v8-code-style/issues/231) | [554](https://its.1c.ru/db/v8std#content:554:hdoc) |
| 268 | 507 | [Роль дает права на объекты других подсистем.](https://github.com/1C-Company/v8-code-style/issues/212) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 269 | 508 | [Неверно заданы права на обработку (должны быть у ИспользованиеОбработки<ИмяОбработки>, БазовыеПрава<ИмяБиблиотеки> или Подсистема<ИмяПодсистемы>).](https://github.com/1C-Company/v8-code-style/issues/213) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 270 | 510 | [Ни в одной роли нет прав на просмотр команды.](https://github.com/1C-Company/v8-code-style/issues/644) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 271 | 511 | [Неверно заданы права на команду (есть права на просмотр команды, но нет прав на чтение или просмотр объекта).](https://github.com/1C-Company/v8-code-style/issues/645) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 272 | 512 | [Неверно заданы права на команду (есть права на чтение или просмотр объекта, но нет прав на просмотр команды).](https://github.com/1C-Company/v8-code-style/issues/646) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 273 | 513 | [Неверно заданы права на команду (права на команды печати должны быть назначены роли "БазовыеПрава<ИмяБиблиотеки>").](https://github.com/1C-Company/v8-code-style/issues/214) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 274 | 514 | [Роль не дает прав ни на один объект метаданных, и к ней нет обращения в коде модулей.](https://github.com/1C-Company/v8-code-style/issues/225) | [737](https://its.1c.ru/db/v8std#content:737:hdoc) |
| 275 | 515 | [Использована функция "Пользователи.РолиДоступны()".](https://github.com/1C-Company/v8-code-style/issues/226) | [737](https://its.1c.ru/db/v8std#content:737:hdoc) |
| 276 | 518 | [Планом обмена поддерживается версия формата обмена EnterpriseData, не входящая в состав конфигурации.](https://github.com/1C-Company/v8-code-style/issues/650) | [771](https://its.1c.ru/db/v8std#content:771:hdoc) |
| 277 | 519 | [Планом обмена не поддерживается более высокая версия формата обмена EnterpriseData.](https://github.com/1C-Company/v8-code-style/issues/651) | [771](https://its.1c.ru/db/v8std#content:771:hdoc) |
| 278 | 520 | [Планом обмена не поддерживается более низкая версия формата обмена EnterpriseData.](https://github.com/1C-Company/v8-code-style/issues/229) | [771](https://its.1c.ru/db/v8std#content:771:hdoc) |
| 279 | 521 | [Нарушена схема работы с транзакциями: преждевременный выход из блока "Попытка..Исключение" без завершения или отмены транзакции.](https://github.com/1C-Company/v8-code-style/issues/395) | [783](https://its.1c.ru/db/v8std#content:783:hdoc) |
| 280 | 524 | [Некорректно заполнено свойство "Путь к данным" у поля динамического списка.](https://github.com/1C-Company/v8-code-style/issues/555) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 281 | 525 | [Некорректно заполнено свойство "Данные" у кнопки, связанной с полем динамического списка.](https://github.com/1C-Company/v8-code-style/issues/90) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 282 | 526 | [У процедуры (функции) в модуле формы отсутствует директива компиляции.](https://github.com/1C-Company/v8-code-style/issues/92) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 283 | 527 | [В качестве параметра обработчика оповещения указана серверная процедура.](https://github.com/1C-Company/v8-code-style/issues/94) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 284 | 529 | [Для объекта метаданных назначено несколько отложенных обработчиков обновления в параллельном режиме.](https://github.com/1C-Company/v8-code-style/issues/657) | acc_72 |
| 285 | 530 | [Отложенный обработчик обновления в параллельном режиме для объекта одной подсистемы обрабатывает данные другой подсистемы.](https://github.com/1C-Company/v8-code-style/issues/239) | acc_72 |
| 286 | 531 | [Избыточное обращение внутри модуля через его имя или псевдоним ЭтотОбъект (к методу, свойству или реквизиту).](https://github.com/1C-Company/v8-code-style/issues/310) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 287 | 534 | [Небезопасный запуск приложения.](https://github.com/1C-Company/v8-code-style/issues/418) | [774](https://its.1c.ru/db/v8std#content:774:hdoc) |
| 288 | 536 | [Отсутствует отключение макросов при работе с документом Microsoft Word.](https://github.com/1C-Company/v8-code-style/issues/661) | [775](https://its.1c.ru/db/v8std#content:775:hdoc) |
| 289 | 537 | [Отсутствует отключение макросов при работе с документом Microsoft Excel.](https://github.com/1C-Company/v8-code-style/issues/419) | [775](https://its.1c.ru/db/v8std#content:775:hdoc) |
| 290 | 538 | [Объект, не имеющий визуального представления, входит в состав подсистемы, включенной в командный интерфейс.](https://github.com/1C-Company/v8-code-style/issues/317) | [543](https://its.1c.ru/db/v8std#content:543:hdoc) |
| 291 | 540 | [В запросе отсутствует проверка на NULL для поля, которое может потенциально содержать NULL.](https://github.com/1C-Company/v8-code-style/issues/163) | [412](https://its.1c.ru/db/v8std#content:412:hdoc) |
| 292 | 541 | [Неверно установлены права базовой роли на объект метаданных.](https://github.com/1C-Company/v8-code-style/issues/428) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 293 | 543 | [В отложенном обработчике обновления не указан идентификатор.](https://github.com/1C-Company/v8-code-style/issues/666) | acc_67 |
| 294 | 544 | [В отложенном обработчике обновления не указан комментарий.](https://github.com/1C-Company/v8-code-style/issues/667) | acc_67 |
| 295 | 545 | [В отложенном обработчике обновления обнаружен неуникальный идентификатор.](https://github.com/1C-Company/v8-code-style/issues/668) | acc_67 |
| 296 | 546 | [В отложенном обработчике обновления обнаружен неуникальный комментарий.](https://github.com/1C-Company/v8-code-style/issues/236) | acc_67 |
| 297 | 547 | [Использована инструкция препроцессора в клиент-серверном общем модуле.](https://github.com/1C-Company/v8-code-style/issues/391) | [439](https://its.1c.ru/db/v8std#content:439:hdoc) |
| 298 | 548 | [Параметры вызова функции скопированы из определения вызываемой процедуры (функции).](https://github.com/1C-Company/v8-code-style/issues/671) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 299 | 549 | [Выполняется неявная передача обязательного параметра.](https://github.com/1C-Company/v8-code-style/issues/383) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 300 | 552 | [Небезопасное подключение внешней обработки.](https://github.com/1C-Company/v8-code-style/issues/673) | [669](https://its.1c.ru/db/v8std#content:669:hdoc) |
| 301 | 553 | [Небезопасное подключение внешнего отчета.](https://github.com/1C-Company/v8-code-style/issues/674) | [669](https://its.1c.ru/db/v8std#content:669:hdoc) |
| 302 | 554 | [Небезопасное подключение расширения конфигурации.](https://github.com/1C-Company/v8-code-style/issues/675) | [669](https://its.1c.ru/db/v8std#content:669:hdoc) |
| 303 | 555 | [Создание объекта типа "ОписаниеЗащитыОтОпасныхДействий".](https://github.com/1C-Company/v8-code-style/issues/414) | [669](https://its.1c.ru/db/v8std#content:669:hdoc) |
| 304 | 556 | [Вызов функции БСП "ОбщегоНазначения.ОписаниеЗащитыБезПредупреждений()".](https://github.com/1C-Company/v8-code-style/issues/415) | [669](https://its.1c.ru/db/v8std#content:669:hdoc) |
| 305 | 557 | [Экспортная процедура (функция) из модуля с признаком "ВызовСервера" не вызывается на клиенте.](https://github.com/1C-Company/v8-code-style/issues/312) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 306 | 558 | [Экспортная процедура (функция) из модуля с признаком "КлиентСервер" не вызывается на клиенте.](https://github.com/1C-Company/v8-code-style/issues/679) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 307 | 559 | [Экспортная процедура (функция) из модуля с признаком "КлиентСервер" не вызывается на сервере.](https://github.com/1C-Company/v8-code-style/issues/680) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 308 | 561 | [Ошибка платформенной проверки конфигурации.](https://github.com/1C-Company/v8-code-style/issues/681) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 309 | 562 | [Ошибка платформенной проверки конфигурации: Возможно ошибочное свойство.](https://github.com/1C-Company/v8-code-style/issues/556) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 310 | 563 | [Ошибка платформенной проверки конфигурации: Возможно ошибочный метод.](https://github.com/1C-Company/v8-code-style/issues/558) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 311 | 564 | [Ошибка платформенной проверки конфигурации: Возможно ошибочный параметр.](https://github.com/1C-Company/v8-code-style/issues/559) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 312 | 565 | [Ошибка платформенной проверки конфигурации: Использование модального вызова.](https://github.com/1C-Company/v8-code-style/issues/571) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 313 | 566 | [Ошибка платформенной проверки конфигурации: Использование синхронного вызова.](https://github.com/1C-Company/v8-code-style/issues/572) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 314 | 567 | [Ошибка платформенной проверки конфигурации: Не обнаружено ссылок на процедуру.](https://github.com/1C-Company/v8-code-style/issues/573) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 315 | 568 | [Ошибка платформенной проверки конфигурации: Не обнаружено ссылок на функцию.](https://github.com/1C-Company/v8-code-style/issues/682) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 316 | 569 | [Ошибка платформенной проверки конфигурации: Неразрешимые ссылки на объекты метаданных.](https://github.com/1C-Company/v8-code-style/issues/574) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 317 | 570 | [Ошибка платформенной проверки конфигурации: Отсутствует обработчик.](https://github.com/1C-Company/v8-code-style/issues/683) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 318 | 571 | [Ошибка платформенной проверки конфигурации: Переменная не определена.](https://github.com/1C-Company/v8-code-style/issues/684) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 319 | 572 | [Ошибка платформенной проверки конфигурации: Процедура или функция с указанным именем не определена.](https://github.com/1C-Company/v8-code-style/issues/685) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 320 | 574 | [Ошибка платформенной проверки конфигурации: (Проверка: Мобильный клиент).](https://github.com/1C-Company/v8-code-style/issues/66) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 321 | 580 | [Нет вызовов служебной экспортной процедуры (функции).](https://github.com/1C-Company/v8-code-style/issues/314) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 322 | 581 | [Избыточное ключевое слово "Экспорт".](https://github.com/1C-Company/v8-code-style/issues/311) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 323 | 1026 | [Орфографическая ошибка в заголовке формы.](https://github.com/1C-Company/v8-code-style/issues/68) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 324 | 1027 | [Орфографическая ошибка в заголовке элемента формы.](https://github.com/1C-Company/v8-code-style/issues/691) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 325 | 1028 | [Орфографическая ошибка в колонке табличного поля.](https://github.com/1C-Company/v8-code-style/issues/692) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 326 | 1030 | [Орфографическая ошибка в подсказке элемента управления.](https://github.com/1C-Company/v8-code-style/issues/75) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 327 | 1032 | [Орфографическая ошибка в синониме объекта метаданных.](https://github.com/1C-Company/v8-code-style/issues/694) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 328 | 1033 | [Орфографическая ошибка в комментарии объекта метаданных.](https://github.com/1C-Company/v8-code-style/issues/695) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 329 | 1034 | [Орфографическая ошибка в тексте встроенной справки.](https://github.com/1C-Company/v8-code-style/issues/76) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 330 | 1035 | [Орфографическая ошибка в тексте макета.](https://github.com/1C-Company/v8-code-style/issues/73) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 331 | 1036 | [Орфографическая ошибка в тексте модуля.](https://github.com/1C-Company/v8-code-style/issues/74) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 332 | 1037 | [Орфографическая ошибка в видимой колонке табличного поля.](https://github.com/1C-Company/v8-code-style/issues/699) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 333 | 1038 | [Орфографическая ошибка в тексте видимого элемента формы.](https://github.com/1C-Company/v8-code-style/issues/72) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 334 | 1046 | [Отсутствует обязательная роль.](https://github.com/1C-Company/v8-code-style/issues/701) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 335 | 1108 | [Нарушена сортировка объектов метаданных верхнего уровня по имени по возрастанию в дереве метаданных.](https://github.com/1C-Company/v8-code-style/issues/78) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 336 | 1125 | [В форме неверно установлен стиль.](https://github.com/1C-Company/v8-code-style/issues/278) | [524](https://its.1c.ru/db/v8std#content:524:hdoc) |
| 337 | 1126 | [Не определен обязательный элемент стиля "Цвет: ТекстПредупреждающейНадписи".](https://github.com/1C-Company/v8-code-style/issues/704) | [524](https://its.1c.ru/db/v8std#content:524:hdoc) |
| 338 | 1127 | [Не определен обязательный элемент стиля "Шрифт: ШрифтВажнойНадписи".](https://github.com/1C-Company/v8-code-style/issues/575) | [524](https://its.1c.ru/db/v8std#content:524:hdoc) |
| 339 | 1128 | [Не определен обязательный элемент стиля "Цвет: ФонГруппировкиВерхнегоУровня".](https://github.com/1C-Company/v8-code-style/issues/578) | [524](https://its.1c.ru/db/v8std#content:524:hdoc) |
| 340 | 1129 | [Не определен обязательный элемент стиля "Цвет: ФонГруппировкиПромежуточногоУровня".](https://github.com/1C-Company/v8-code-style/issues/579) | [524](https://its.1c.ru/db/v8std#content:524:hdoc) |
| 341 | 1130 | [Не определен обязательный элемент стиля "Цвет: ТекстИнформационнойНадписи".](https://github.com/1C-Company/v8-code-style/issues/276) | [524](https://its.1c.ru/db/v8std#content:524:hdoc) |
| 342 | 1131 | [Неверно определен основной стиль для конфигурации.](https://github.com/1C-Company/v8-code-style/issues/705) | [524](https://its.1c.ru/db/v8std#content:524:hdoc) |
| 343 | 1132 | [Не установлен основной стиль для конфигурации.](https://github.com/1C-Company/v8-code-style/issues/275) | [524](https://its.1c.ru/db/v8std#content:524:hdoc) |
| 344 | 1133 | [Установлен признак переключения для интерфейса "Общий".](https://github.com/1C-Company/v8-code-style/issues/707) | acc_21 |
| 345 | 1134 | [В конфигурации отсутствует обязательный интерфейс "Общий".](https://github.com/1C-Company/v8-code-style/issues/708) | acc_21 |
| 346 | 1135 | [В конфигурации отсутствует обязательный интерфейс "Полный".](https://github.com/1C-Company/v8-code-style/issues/273) | acc_21 |
| 347 | 1136 | [Не заполнена всплывающая подсказка.](https://github.com/1C-Company/v8-code-style/issues/710) | [506](https://its.1c.ru/db/v8std#content:506:hdoc) |
| 348 | 1143 | [Длина комментария превышает 120 символов.](https://github.com/1C-Company/v8-code-style/issues/292) | acc_119 |
| 349 | 1145 | [Отсутствует справочная информация.](https://github.com/1C-Company/v8-code-style/issues/290) | acc_107 |
| 350 | 1146 | [У основного объекта справка не включена в содержание.](https://github.com/1C-Company/v8-code-style/issues/288) | acc_107 |
| 351 | 1147 | [Не задана принадлежность объекта к подсистемам.](https://github.com/1C-Company/v8-code-style/issues/230) | [705](https://its.1c.ru/db/v8std#content:705:hdoc) |
| 352 | 1150 | [Не установлено право в роли "ПолныеПрава".](https://github.com/1C-Company/v8-code-style/issues/581) | [488](https://its.1c.ru/db/v8std#content:488:hdoc) |
| 353 | 1151 | [Не задан синоним объекта метаданных.](https://github.com/1C-Company/v8-code-style/issues/582) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 354 | 1200 | [Неверно установлен номер версии. Правильный формат "РР.ПП.ЗЗ.СС".](https://github.com/1C-Company/v8-code-style/issues/712) | [483](https://its.1c.ru/db/v8std#content:483:hdoc) |
| 355 | 1201 | [Краткая информация отличается от синонима.](https://github.com/1C-Company/v8-code-style/issues/713) | [482](https://its.1c.ru/db/v8std#content:482:hdoc) |
| 356 | 1202 | [Подробная информация отличается от синонима.](https://github.com/1C-Company/v8-code-style/issues/714) | [482](https://its.1c.ru/db/v8std#content:482:hdoc) |
| 357 | 1203 | [Неверно указан поставщик. Должен быть "Фирма "1С"".](https://github.com/1C-Company/v8-code-style/issues/715) | [482](https://its.1c.ru/db/v8std#content:482:hdoc) |
| 358 | 1205 | [Реквизит имеет тип фиксированной строки.](https://github.com/1C-Company/v8-code-style/issues/343) | [432](https://its.1c.ru/db/v8std#content:432:hdoc) |
| 359 | 1206 | [Нестандартная длина кода (номера), проверьте оправданность использования такой длины.](https://github.com/1C-Company/v8-code-style/issues/340) | [473](https://its.1c.ru/db/v8std#content:473:hdoc) |
| 360 | 1207 | [Совпадают имена у объекта метаданных и его составляющей.](https://github.com/1C-Company/v8-code-style/issues/718) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 361 | 1208 | [Присвоено нерекомендуемое имя.](https://github.com/1C-Company/v8-code-style/issues/719) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 362 | 1209 | [Нестандартная длина номера, проверьте оправданность использования такой длины.](https://github.com/1C-Company/v8-code-style/issues/127) | [473](https://its.1c.ru/db/v8std#content:473:hdoc) |
| 363 | 1210 | [Представление объекта совпадает с синонимом. В этом случае оно не заполняется.](https://github.com/1C-Company/v8-code-style/issues/433) | [468](https://its.1c.ru/db/v8std#content:468:hdoc) |
| 364 | 1211 | [Расширенное представление объекта совпадает с представлением объекта. В этом случае оно не заполняется.](https://github.com/1C-Company/v8-code-style/issues/722) | [468](https://its.1c.ru/db/v8std#content:468:hdoc) |
| 365 | 1213 | [Расширенное представление объекта совпадает с синонимом при незаполненном представлении объекта. В этом случае оно не заполняется.](https://github.com/1C-Company/v8-code-style/issues/434) | [468](https://its.1c.ru/db/v8std#content:468:hdoc) |
| 366 | 1214 | [Представление списка совпадает с синонимом. В этом случае оно не заполняется.](https://github.com/1C-Company/v8-code-style/issues/435) | [468](https://its.1c.ru/db/v8std#content:468:hdoc) |
| 367 | 1215 | [Расширенное представление списка совпадает с представлением списка. В этом случае оно не заполняется.](https://github.com/1C-Company/v8-code-style/issues/725) | [468](https://its.1c.ru/db/v8std#content:468:hdoc) |
| 368 | 1216 | [Расширенное представление списка совпадает с синонимом при незаполненном представлении списка. В этом случае оно не заполняется.](https://github.com/1C-Company/v8-code-style/issues/436) | [468](https://its.1c.ru/db/v8std#content:468:hdoc) |
| 369 | 1217 | [Расширенное представление совпадает с синонимом. В этом случае оно не заполняется.](https://github.com/1C-Company/v8-code-style/issues/257) | [468](https://its.1c.ru/db/v8std#content:468:hdoc) |
| 370 | 1218 | [Объект метаданных является демонстрационным.](https://github.com/1C-Company/v8-code-style/issues/118) | [474](https://its.1c.ru/db/v8std#content:474:hdoc) |
| 371 | 1219 | [Неверно сброшен флажок "Включать в содержание справки". Для основной формы его нужно включить.](https://github.com/1C-Company/v8-code-style/issues/729) | acc_107 |
| 372 | 1220 | [Неверно установлен флажок "Включать в содержание справки". Для не основной формы его нужно выключить.](https://github.com/1C-Company/v8-code-style/issues/289) | acc_107 |
| 373 | 1221 | [Не определен обязательный элемент стиля "Цвет: ТекстВторостепеннойНадписи".](https://github.com/1C-Company/v8-code-style/issues/731) | [524](https://its.1c.ru/db/v8std#content:524:hdoc) |
| 374 | 1222 | [Не определен обязательный элемент стиля "Цвет: ЦветГиперссылки".](https://github.com/1C-Company/v8-code-style/issues/277) | [524](https://its.1c.ru/db/v8std#content:524:hdoc) |
| 375 | 1223 | [Сообщение содержит нерекомендуемое местоимение ("Вы", "Вас" и пр.).](https://github.com/1C-Company/v8-code-style/issues/441) | acc_83 |
| 376 | 1224 | [Превышена максимально допустимая ширина формы 1256.](https://github.com/1C-Company/v8-code-style/issues/734) | [505](https://its.1c.ru/db/v8std#content:505:hdoc) |
| 377 | 1225 | [Превышена максимально допустимая высота формы 580.](https://github.com/1C-Company/v8-code-style/issues/280) | [505](https://its.1c.ru/db/v8std#content:505:hdoc) |
| 378 | 1238 | [Не заполнена всплывающая подсказка в шапке колонки табличного поля.](https://github.com/1C-Company/v8-code-style/issues/444) | [506](https://its.1c.ru/db/v8std#content:506:hdoc) |
| 379 | 1239 | [Для полей ввода с установленным флагом "Автоотметка незаполненного", необходимо устанавливать флаг "Автовыбор незаполненного".](https://github.com/1C-Company/v8-code-style/issues/281) | [507](https://its.1c.ru/db/v8std#content:507:hdoc) |
| 380 | 1240 | [Колонки с заведомо известной требуемой шириной не должны изменять размер.](https://github.com/1C-Company/v8-code-style/issues/279) | [504](https://its.1c.ru/db/v8std#content:504:hdoc) |
| 381 | 1241 | [У элемента формы неверное имя.](https://github.com/1C-Company/v8-code-style/issues/443) | [503](https://its.1c.ru/db/v8std#content:503:hdoc) |
| 382 | 1242 | [Отсутствует комментарий к экспортной процедуре (функции).](https://github.com/1C-Company/v8-code-style/issues/740) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 383 | 1243 | [Отсутствует или неверно описана секция "Описание" в комментарии к экспортной процедуре (функции).](https://github.com/1C-Company/v8-code-style/issues/741) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 384 | 1244 | [Обращение к несуществующему объекту метаданных.](https://github.com/1C-Company/v8-code-style/issues/366) | [456](https://its.1c.ru/db/v8std#content:456:hdoc) |
| 385 | 1248 | [Ключевое слово написано не канонически.](https://github.com/1C-Company/v8-code-style/issues/387) | [441](https://its.1c.ru/db/v8std#content:441:hdoc) |
| 386 | 1297 | [Строка не локализована. Возможно, она видна пользователю.](https://github.com/1C-Company/v8-code-style/issues/745) | [761](https://its.1c.ru/db/v8std#content:761:hdoc) |
| 387 | 1298 | [Нарушен синтаксис описания локализованной строки. Должен быть "НСтр("ru='...'")"  или "NStr("en='...'")".](https://github.com/1C-Company/v8-code-style/issues/746) | [761](https://its.1c.ru/db/v8std#content:761:hdoc) |
| 388 | 1299 | [Локализуемая строка начинается или заканчивается непечатаемым символом (например: пробел, таб, перенос).](https://github.com/1C-Company/v8-code-style/issues/747) | [761](https://its.1c.ru/db/v8std#content:761:hdoc) |
| 389 | 1308 | [Назначение расширения имеет ошибочное значение.](https://github.com/1C-Company/v8-code-style/issues/323) | acc_16 |
| 390 | 1310 | [Наименование объекта расширения не содержит префикса, соответствующего префиксу самого расширения.](https://github.com/1C-Company/v8-code-style/issues/324) | acc_28 |
| 391 | 1311 | [Процедура (функция) в модуле объекта расширения не имеет префикса, соответствующего префиксу самого расширения.](https://github.com/1C-Company/v8-code-style/issues/751) | acc_28 |
| 392 | 1312 | [Переменная в модуле объекта расширения не имеет префикса, соответствующего префиксу самого расширения.](https://github.com/1C-Company/v8-code-style/issues/107) | acc_28 |
| 393 | 1314 | [Описание расширяющего метода в модуле объекта расширения отличается от расширяемого метода в модуле объекта основной конфигурации.](https://github.com/1C-Company/v8-code-style/issues/108) | acc_59 |
| 394 | 1316 | [Ошибка проверки возможности применения расширения.](https://github.com/1C-Company/v8-code-style/issues/110) | acc_60 |
| 395 | 1317 | [Для объекта метаданных назначено несколько отложенных обработчиков обновления в последовательном режиме.](https://github.com/1C-Company/v8-code-style/issues/755) | acc_67 |
| 396 | 1318 | [Отложенный обработчик обновления в последовательном режиме для объекта одной подсистемы обрабатывает данные другой подсистемы.](https://github.com/1C-Company/v8-code-style/issues/237) | acc_67 |
| 397 | 1319 | [После инициализации блокировки отсутствует вызов "Заблокировать()".](https://github.com/1C-Company/v8-code-style/issues/757) | [499](https://its.1c.ru/db/v8std#content:499:hdoc) |
| 398 | 1320 | [Вызов "Заблокировать()" находится вне попытки.](https://github.com/1C-Company/v8-code-style/issues/396) | [499](https://its.1c.ru/db/v8std#content:499:hdoc) |
| 399 | 1324 | [Использован конструктор "Новый ЗащищенноеСоединениеOpenSSL".](https://github.com/1C-Company/v8-code-style/issues/416) | [669](https://its.1c.ru/db/v8std#content:669:hdoc) |
| 400 | 1326 | [Неверно установлено значение стиля](https://github.com/1C-Company/v8-code-style/issues/272) | [667](https://its.1c.ru/db/v8std#content:667:hdoc) |
| 401 | 1327 | [Отсутствует исключительная управляемая блокировка на записываемые (удаляемые) данные.](https://github.com/1C-Company/v8-code-style/issues/761) | [648](https://its.1c.ru/db/v8std#content:648:hdoc) |
| 402 | 1328 | [Отсутствует разделяемая управляемая блокировка на читаемые данные.](https://github.com/1C-Company/v8-code-style/issues/397) | [648](https://its.1c.ru/db/v8std#content:648:hdoc) |
| 403 | 1329 | [Недопустимое одновременное использование ссылочных и нессылочных типов в составном типе.](https://github.com/1C-Company/v8-code-style/issues/763) | [728](https://its.1c.ru/db/v8std#content:728:hdoc) |
| 404 | 1330 | [Недопустимое использование универсального составного типа (ЛюбаяСсылка, СправочникСсылка и т.п.).](https://github.com/1C-Company/v8-code-style/issues/346) | [728](https://its.1c.ru/db/v8std#content:728:hdoc) |
| 405 | 1331 | [Задано конкретное значение цвета для элемента управления формы.](https://github.com/1C-Company/v8-code-style/issues/765) | [667](https://its.1c.ru/db/v8std#content:667:hdoc) |
| 406 | 1332 | [Задано конкретное значение шрифта (либо изменен параметр шрифта из стиля) для элемента управления формы.](https://github.com/1C-Company/v8-code-style/issues/326) | [667](https://its.1c.ru/db/v8std#content:667:hdoc) |
| 407 | 1333 | [Для элемента стиля задано конкретное значение шрифта, цвета или рамки.](https://github.com/1C-Company/v8-code-style/issues/327) | [667](https://its.1c.ru/db/v8std#content:667:hdoc) |
| 408 | 1334 | [Устаревшая процедура (функция) ссылается на несуществующую процедуру (функцию).](https://github.com/1C-Company/v8-code-style/issues/768) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 409 | 1335 | [Устаревшая процедура (функция) ссылается на другую устаревшую процедуру (функцию).](https://github.com/1C-Company/v8-code-style/issues/769) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 410 | 1336 | [Устаревшая процедура (функция) ссылается на процедуру (функцию), расположенную вне области "ПрограммныйИнтерфейс".](https://github.com/1C-Company/v8-code-style/issues/112) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 411 | 1338 | [Неиспользуемый (с префиксом "Удалить") объект метаданных содержит подчиненные объекты, не относящиеся к переносу данных.](https://github.com/1C-Company/v8-code-style/issues/328) | [534](https://its.1c.ru/db/v8std#content:534:hdoc) |
| 412 | 1339 | [Избыточная проверка параметра "АвтоТест".](https://github.com/1C-Company/v8-code-style/issues/329) | [456](https://its.1c.ru/db/v8std#content:456:hdoc) |
| 413 | 1340 | [Процедура (функция), не являющаяся обработчиком события, расположена в стандартной области обработчиков событий.](https://github.com/1C-Company/v8-code-style/issues/773) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 414 | 1341 | [Процедура (функция), являющаяся обработчиком события, расположена вне стандартной области обработчиков событий](https://github.com/1C-Company/v8-code-style/issues/330) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |
| 415 | 1343 | [Дублируется картинка.](https://github.com/1C-Company/v8-code-style/issues/775) | [440](https://its.1c.ru/db/v8std#content:440:hdoc) |
| 416 | 1344 | [Дублируется наименование картинки.](https://github.com/1C-Company/v8-code-style/issues/113) | [440](https://its.1c.ru/db/v8std#content:440:hdoc) |
| 417 | 1345 | [Использование конструкции "Новый Шрифт"](https://github.com/1C-Company/v8-code-style/issues/777) | [667](https://its.1c.ru/db/v8std#content:667:hdoc) |
| 418 | 1346 | [Использование конструкции "Новый Цвет"](https://github.com/1C-Company/v8-code-style/issues/778) | [667](https://its.1c.ru/db/v8std#content:667:hdoc) |
| 419 | 1347 | [Использование конструкции "Новый Рамка"](https://github.com/1C-Company/v8-code-style/issues/331) | [667](https://its.1c.ru/db/v8std#content:667:hdoc) |
| 420 | 1348 | [Использован метод глобального контекста вместо процедуры общего модуля "ФайловаяСистемаКлиент"](https://github.com/1C-Company/v8-code-style/issues/333) | [700](https://its.1c.ru/db/v8std#content:700:hdoc) |
| 421 | 1349 | [В качестве параметра обработчика оповещения указана несуществующая процедура.](https://github.com/1C-Company/v8-code-style/issues/781) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 422 | 1350 | [В качестве параметра обработчика оповещения указана функция.](https://github.com/1C-Company/v8-code-style/issues/584) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 423 | 1351 | [В качестве параметра обработчика оповещения указана неэкспортная процедура.](https://github.com/1C-Company/v8-code-style/issues/585) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 424 | 1352 | [В качестве параметра обработчика оповещения указана процедура без параметров.](https://github.com/1C-Company/v8-code-style/issues/588) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 425 | 1353 | [Английский идентификатор в коде модуля на русском языке.](https://github.com/1C-Company/v8-code-style/issues/115) | [456](https://its.1c.ru/db/v8std#content:456:hdoc) |
| 426 | 1354 | [Локализуемая строка состоит из нелокализуемых символов.](https://github.com/1C-Company/v8-code-style/issues/246) | [761](https://its.1c.ru/db/v8std#content:761:hdoc) |
| 427 | 1355 | [Конкатенация локализуемых строк.](https://github.com/1C-Company/v8-code-style/issues/117) | [761](https://its.1c.ru/db/v8std#content:761:hdoc) |
| 428 | 1356 | [В качестве параметра конструктора "Новый ФорматированнаяСтрока" использована составная форматированная строка.](https://github.com/1C-Company/v8-code-style/issues/342) | [761](https://its.1c.ru/db/v8std#content:761:hdoc) |
| 429 | 1357 | [В качестве параметра метода "ЧислоПрописью" используется форматированная строка с параметром "Л="("L=") .](https://github.com/1C-Company/v8-code-style/issues/590) | [761](https://its.1c.ru/db/v8std#content:761:hdoc) |
| 430 | 1358 | [В качестве параметра метода "ПредставлениеПериода" используется форматированная строка с параметром "Л="("L=") .](https://github.com/1C-Company/v8-code-style/issues/591) | [761](https://its.1c.ru/db/v8std#content:761:hdoc) |
| 431 | 1359 | [В качестве параметра метода "СтрокаСЧислом" используется форматированная строка с параметром "Л="("L=") .](https://github.com/1C-Company/v8-code-style/issues/128) | [761](https://its.1c.ru/db/v8std#content:761:hdoc) |
| 432 | 1360 | [Некорректно описан тип элементов массива.](https://github.com/1C-Company/v8-code-style/issues/592) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 433 | 1361 | [В макете с типом "ВнешняяКомпонента" размешен файл с другим типом.](https://github.com/1C-Company/v8-code-style/issues/129) | [766](https://its.1c.ru/db/v8std#content:766:hdoc) |
| 434 | 1363 | [В обработчике обновления информационной базы при записи объекта не используется метод "Библиотеки стандартных подсистем".](https://github.com/1C-Company/v8-code-style/issues/344) | [690](https://its.1c.ru/db/v8std#content:690:hdoc) |
| 435 | 1365 | [Разделитель пути файла указан вручную (необходимо использовать метод "ПолучитьРазделительПути()").](https://github.com/1C-Company/v8-code-style/issues/594) | [723](https://its.1c.ru/db/v8std#content:723:hdoc) |
| 436 | 1366 | [Маска всех файлов указана вручную (необходимо использовать метод "ПолучитьМаскуВсеФайлы()").](https://github.com/1C-Company/v8-code-style/issues/345) | [723](https://its.1c.ru/db/v8std#content:723:hdoc) |
| 437 | 1367 | [Отсутствует локализация при форматировании даты.](https://github.com/1C-Company/v8-code-style/issues/597) | [763](https://its.1c.ru/db/v8std#content:763:hdoc) |
| 438 | 1368 | [Отсутствует локализация при форматировании числа.](https://github.com/1C-Company/v8-code-style/issues/604) | [763](https://its.1c.ru/db/v8std#content:763:hdoc) |
| 439 | 1369 | [Отсутствует локализация при форматировании Булево.](https://github.com/1C-Company/v8-code-style/issues/134) | [763](https://its.1c.ru/db/v8std#content:763:hdoc) |
| 440 | 1370 | [В модуле устаревшего объекта содержится код.](https://github.com/1C-Company/v8-code-style/issues/135) | [534](https://its.1c.ru/db/v8std#content:534:hdoc) |
| 441 | 1371 | [Устаревший объект включен в свойство другого объекта.](https://github.com/1C-Company/v8-code-style/issues/136) | [534](https://its.1c.ru/db/v8std#content:534:hdoc) |
| 442 | 1374 | [Некорректное использование платформенного метода "Тип()".](https://github.com/1C-Company/v8-code-style/issues/350) | [467](https://its.1c.ru/db/v8std#content:467:hdoc) |
| 443 | 1375 | [В правах роли установлены ограничения (RLS) для устаревшего объекта метаданных.](https://github.com/1C-Company/v8-code-style/issues/139) | [689](https://its.1c.ru/db/v8std#content:689:hdoc) |
| 444 | 1377 | [Разыменование ссылочного поля составного типа](https://github.com/1C-Company/v8-code-style/issues/142) | [654](https://its.1c.ru/db/v8std#content:654:hdoc) |
| 445 | 1381 | [В качестве объекта переадресации из гиперссылки "См. ..." указана устаревшая процедура (функция).](https://github.com/1C-Company/v8-code-style/issues/605) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 446 | 1382 | [В качестве объекта переадресации из гиперссылки "См. ..." указана процедура (функция), расположенная вне области "ПрограммныйИнтерфейс".](https://github.com/1C-Company/v8-code-style/issues/606) | [453](https://its.1c.ru/db/v8std#content:453:hdoc) |
| 447 | 1383 | [Имя переменной, содержащей модуль, не соответствует формату "Модуль<ИмяОбъекта>".](https://github.com/1C-Company/v8-code-style/issues/611) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 448 | 1385 | [Отсутствует обработчик начального заполнения предопределенных элементов (ПриНачальномЗаполненииЭлементов и др.).](https://github.com/1C-Company/v8-code-style/issues/353) | [784](https://its.1c.ru/db/v8std#content:784:hdoc) |
| 449 | 1386 | [Модуль не входит в подсистему, указанную в функции "ПодсистемаСуществует()".](https://github.com/1C-Company/v8-code-style/issues/613) | [640](https://its.1c.ru/db/v8std#content:640:hdoc) |
| 450 | 1387 | [Обнаружено дублирование стандартной области.](https://github.com/1C-Company/v8-code-style/issues/614) | [455](https://its.1c.ru/db/v8std#content:455:hdoc) |

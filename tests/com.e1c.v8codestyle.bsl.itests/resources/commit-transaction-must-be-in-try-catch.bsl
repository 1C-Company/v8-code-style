Процедура Тест()
    
    НачатьТранзакцию();
    ЗафиксироватьТранзакцию();
    Попытка
    // 2. Вся логика блокировки и обработки данных размещается в блоке Попытка-Исключение
    // 3. В самом конце обработки данных выполняется попытка зафиксировать транзакцию
    Исключение
    // 4. В случае любых проблем с СУБД, транзакция сначала отменяется...
    ОтменитьТранзакцию();
    // 5. ...затем проблема фиксируется в журнале регистрации...
    // 6. ... после чего, проблема передается дальше вызывающему коду.
    ВызватьИсключение;
    КонецПопытки;
    
КонецПроцедуры
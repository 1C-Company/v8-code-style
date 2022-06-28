Процедура Тест()
    
    НачатьТранзакцию();
    Попытка
    // 2. Вся логика блокировки и обработки данных размещается в блоке Попытка-Исключение
    // 3. В самом конце обработки данных выполняется попытка зафиксировать транзакцию
    ЗафиксироватьТранзакцию();
    Исключение
    // 4. В случае любых проблем с СУБД, транзакция сначала отменяется...
    
    // 5. ...затем проблема фиксируется в журнале регистрации...
    // 6. ... после чего, проблема передается дальше вызывающему коду.
    ВызватьИсключение;
    КонецПопытки;
    
    ОтменитьТранзакцию();
    
КонецПроцедуры
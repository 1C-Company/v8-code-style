Процедура Тест1(Параметры)
    
Тест = Новый COMОбъект("Excel.Application");
Тест.AutomationSecurity = 0; // msoAutomationSecurityForceDisable = 3
Документ = Тест.Workbooks.Open(ИмяФайла);
    
КонецПроцедуры
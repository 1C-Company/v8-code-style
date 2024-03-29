# Вызов "Заблокировать()" находится вне попытки

Правило проверяет наличие инициализации блокировки данных. В случае если
найдено создание блокировки, проверяется вызов метода "Заблокировать()",
при этом вызов должен быть в попытке.

## Неправильно

```bsl
БлокировкаДанных = Новый БлокировкаДанных;
ЭлементБлокировкиДанных = БлокировкаДанных.Добавить("Документ.ПриходнаяНакладная");
ЭлементБлокировкиДанных.Режим = РежимБлокировкиДанных.Исключительный;
БлокировкаДанных.Заблокировать();
```

## Правильно

```bsl
НачатьТранзакцию();
Попытка
    
    БлокировкаДанных = Новый БлокировкаДанных;
    ЭлементБлокировкиДанных = БлокировкаДанных.Добавить("Документ.ПриходнаяНакладная");
    ЭлементБлокировкиДанных.Режим = РежимБлокировкиДанных.Исключительный;
    БлокировкаДанных.Заблокировать();
    // чтение или запись данных
    
   ЗафиксироватьТранзакцию();
   
Исключение
    ОтменитьТранзакцию();

ВызватьИсключение;

КонецПопытки;
```

## См.

- [Перехват исключений в коде](https://its.1c.ru/db/v8std#content:499:hdoc:3.6)
- [Транзакции: правила использования](https://its.1c.ru/db/v8std#content:783:hdoc:1.3)
# Запрос содержит конструкцию "ДЛЯ ИЗМЕНЕНИЯ"

Конструкция ДЛЯ ИЗМЕНЕНИЯ позволяет заблаговременно заблокировать некоторые данные (которые могут читаться транзакцией другого соединения) 
уже при считывании, чтобы исключить взаимные блокировки при записи. Однако, при использовании в конфигурации управляемого режима блокировок, данная конструкция игнорируется и 
следовательно, не имеет смысла.

## Неправильно

```bsl
ВЫБРАТЬ 
  Док.Ссылка, 
ИЗ 
  Документ.РеализацияТоваров Док
ГДЕ 
  Док.Ссылка = &ДокументСсылка
ДЛЯ ИЗМЕНЕНИЯ РегистрНакопления.КонтрагентыВзаиморасчетыКомпании.Остатки // Блокирующие чтение таблицы остатков регистра для разрешения 
```

## Правильно

```bsl
ВЫБРАТЬ 
  Док.Ссылка, 
ИЗ 
  Документ.РеализацияТоваров Док
ГДЕ 
  Док.Ссылка = &ДокументСсылка
```

## См.

- [Использование управляемого режима блокировки](https://its.1c.ru/db/v8std#content:460:hdoc)
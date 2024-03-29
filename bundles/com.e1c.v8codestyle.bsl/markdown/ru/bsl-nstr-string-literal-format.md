# НСтр формат строкового литерала

## Неправильно

В первый параметр функции НСтр необходимо передавать только строковый литерал.

```bsl
Процедура Неправильно1(Сообщение) Экспорт
	
	Сообщение = НСтр("ru = 'Сообщение пользователю'" + Символы.ПС);
	
КонецПроцедуры
```

Строковый литерал в первом параметре функции не должен быть пустым.

```bsl
Процедура Неправильно2(Сообщение) Экспорт
	
	Сообщение = НСтр("");
	
КонецПроцедуры
```

Формат строкового литерала должен быть правильным: `"Ключ1 = 'значение 1'; Ключ2 = 'значение 2';"`.

```bsl
Процедура Неправильно3(Сообщение) Экспорт
	
	Сообщение = НСтр("ru = Сообщение пользователю");
	
КонецПроцедуры
```

Должен использоваться существующий код языка из списка языков конфигурации.

```bsl
Процедура Неправильно4(Сообщение) Экспорт
	
	Сообщение = НСтр("ru2 = 'Сообщение пользователю'");
	
КонецПроцедуры
```

Сообщение для кода языка не должно быть пустым.

```bsl
Процедура Неправильно5(Сообщение) Экспорт
	
	Сообщение = НСтр("ru = ''");
	
КонецПроцедуры
```

Сообщение для кода языка не должно оканчиваться пробелом.

```bsl
Процедура Неправильно6(Сообщение) Экспорт
	
	Сообщение = НСтр("ru = 'Сообщение пользователю '");
	
КонецПроцедуры
```

Сообщение для кода языка не должно оканчиваться новой строкой.

```bsl
Процедура Неправильно7(Сообщение) Экспорт
	
	Сообщение = НСтр("ru = 'Сообщение пользователю
	|'");
	
КонецПроцедуры
```

## Правильно


```bsl

Процедура Правильно(Сообщение) Экспорт
	
	Сообщение = НСтр("ru = 'Сообщение пользователю'");
	
КонецПроцедуры
```

## См.

[Интерфейсные тексты в коде: требования по локализации](https://its.1c.ru/db/v8std#contrut:761:hdoc)

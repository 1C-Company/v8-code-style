# Используется оператор Перейти

В коде на встроенном языке не рекомендуется использовать оператор Перейти, 
так как необдуманное использование данного оператора приводит к получению запутанных, 
плохо структурированных модулей, по тексту которых затруднительно понять порядок 
исполнения и взаимозависимость фрагментов. Вместо оператора Перейти рекомендуется использовать 
другие конструкции встроенного языка. 

## Неправильно

```bsl
Если ПланВидовРасчета = Объект.ПланВидовРасчета Тогда
  
  Перейти ~ПланВидовРасчета;
  
КонецЕсли;
```

## Правильно

```bsl
Если ПланВидовРасчета = Объект.ПланВидовРасчета Тогда
  
  ОбработатьПланВидовРасчета();
  
КонецЕсли;
```

## См.

- [Ограничение на использование оператора Перейти](https://its.1c.ru/db/v8std#content:547:hdoc:1)
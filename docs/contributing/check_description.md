# Описание проверок


1. Создайте файл описания проверки на английском c идентификатором в имени файла
   - Например: `com.e1c.v8codestyle.bsl/markdown/my-check-id.md`
2. Создайте файл описания проверки на русском
   - Например: `com.e1c.v8codestyle.bsl/markdown/ru/my-check-id.md`
3. Укажите
   - Краткое название проверки
   - Полное описание проблемной ситуации в коде или метаданных, того как работает проверка
   - Добавьте пример неправильного кода
   - Пояснение почему так делать не стоит
   - Добавьте пример правильного кода
   
## Шаблоны файлов

Английский `com.e1c.v8codestyle.bsl/markdown/my-check-id.md`

```
# Title of the ckeck

Description of the check

## Noncompliant Code Example


```bsl

Noncompliant = code example;

```

## Compliant Solution

```bsl

Compliant = code example;

```

## See

- [Related article](https://its.1c.ru/...)
- [Related article 2](https://its.1c.ru/...)

```

Русский `com.e1c.v8codestyle.bsl/markdown/ru/my-check-id.md`


```
# Заголовок проверки

Описание проверки

## Неправильно

```bsl

Пирмер = не правильного кода;

```

## Правильно

```bsl

Пирмер = правильного кода;

```

## См.

- [Стандрат поясняющий проблему](https://its.1c.ru/db/pubqlang#content:150:hdoc)
- [Стандрат поясняющий проблему 2](https://its.1c.ru/db/pubqlang#content:150:hdoc)

```


# Периодичность выполнения регламентного задания меньше одной минуты.

Периодичность выполнения регламентного задания меньше одной минуты.

## Неправильно

## Правильно

- регламентное задание не должно выполняться чаще, чем это нужно с прикладной точки зрения;
- с точки зрения оптимальной загрузки сервера приложений для большинства регламентных заданий нормальным является интервал выполнения заданий в 1 день; 
- исключения могут составлять случаи, когда критичным является частое выполнение заданий с прикладной точки зрения, например, для поддержания актуальности данных за короткий период;
- ни в каких случаях не следует задавать периодичность выполнения регламентных заданий меньше одной минуты;
- периодичность выполнения частых (с периодичностью менее одного дня) регламентных заданий должна быть сбалансирована со временем выполнения задания: например, если типичное время выполнения 20 секунд, то периодичность раз в минуту, скорее всего, избыточна;
- выполнение ресурсоемких регламентных операций необходимо по возможности переносить на время минимальной загрузки сервера приложений 1С:Предприятие. Например, в нерабочее время или на выходные дни;
- несколько различных ресурсоемких регламентных заданий лучше "разносить" по времени, исходя из ожидаемого времени их выполнения.

## См.

[Настройка расписания регламентных заданий](https://its.1c.ru/db/v8std#content:402:hdoc)


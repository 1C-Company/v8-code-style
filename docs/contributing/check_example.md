# Пример разработки простой проверки

Большинство проверок заключается в том, чтобы:

1. Найти определенный класс объектов в метаданных или в коде (подписаться на проверку этих объектов).
2. Затем проверить состояние объекта (свойства объекта)
3. Добавить текст ошибки

Система валидации 1C:EDT позволяет реализовывать намного более сложные сценарии. 
Обо всех возможностях читайте подробней [в документации](https://edt.1c.ru/dev/ru/docs/plugins/dev/checks/checksdoc.pdf).

## Подробнее


Создайте класс проверки `FindTheProblemCheck` наследуемый от абстрактной реализации простой (типичной) 
проверки `com.e1c.g5.v8.dt.check.components.BasicCheck`, позволяющей подписаться на объект и реагировать на изменения его свойств.


```java
public class FindTheProblemCheck
    extends BasicCheck
{
}
```

Далее придумайте подходящий идентификатор проверки, соответствующий ее идеологии, лаконичный, понятный и уникальный.
[Подробнее](Check_Convention.md#код-проверки)


```java
public class FindTheProblemCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "find-the-problem"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

}
```

### 1. Конфигурация подписки на объект и его свойства

Для проверки метаданны, форм, СКД, Табличный документ, Прав ролей, укажите верхнеуровневый объект (Top object) - тот объект который хранится в файле: Каталог, Форма, СхемаКомпоновкиДанных и т.д.

Укажите - необходимо ли проверять сам верхнеуровневый объект (`.checkTop()`) или необходимо найти его подчиненные объекты: реквизиты, элементы и т.д. (`.containment(CATALOG_ATTRIBUTE)`).

Далее укажите, все свойства объекта от которых зависит код проверки (`.features(BASIC_DB_OBJECT__OBJECT_PRESENTATION, BASIC_DB_OBJECT__LIST_PRESENTATION)`). 

Помните, что если свойство указано, при его изменении будет запущена эта проверка!


```java
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG_ATTRIBUTE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_DB_OBJECT__LIST_PRESENTATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_DB_OBJECT__OBJECT_PRESENTATION;

public class FindTheProblemCheck
    extends BasicCheck
{

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title("Заголовок проверки")
            .description("Описание проверки")
            .severity(IssueSeverity.MINOR) // строгость/критичность проверки - старайтесь быть менее критичнми :)
            .issueType(IssueType.UI_STYLE); // Укажите тип проблемы
            .topObject(CATALOG)
            .checkTop()
            .features(BASIC_DB_OBJECT__OBJECT_PRESENTATION, BASIC_DB_OBJECT__LIST_PRESENTATION);
    }
}
```

[Подробнее про критичность.](Check_Convention.md#критичность-severity)

[Подробнее про тип проблемы.](Check_Convention.md#тип-issuetype)

Для проверки кода модулей существует специальная конфигурация (упрощение)  `.module()` и укажите класс объекта на который необходимо подписаться `.checkedObjectType(OPERATOR_STYLE_CREATOR)`.


```java
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.OPERATOR_STYLE_CREATOR;

public class FindTheProblemCheck
    extends BasicCheck
{

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title("Заголовок проверки")
            .description("Описание проверки")
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .module() // проверка модуля
            .checkedObjectType(OPERATOR_STYLE_CREATOR); // Подписка на класс объекта модели модуля
    }
}
```


### 2. Реализация кода проверки


Если подписка на один тип объекта - безопасно выполнять приведение к типу объекта.

```java
public class FindTheProblemCheck
    extends BasicCheck
{

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        OperatorStyleCreator osc = (OperatorStyleCreator)object;
        
        // Реализуйте код проверки
        
        if (errorFound)
        {
        	// Добавьте сообщение об ошибке
            String message = MessageFormat.format("Сообщение об ошибке что значение {} не правильное", maxKeys);
            resultAceptor.addIssue(message, literal, STRING_LITERAL__LINES);
        }
    }
}
```


#### Проверка монитора

В метод проверки передается монитор - чтобы проверку сделать менее атомарной - в алгоритме необходимо чаще проверять отмену.


```java
public class FindTheProblemCheck
    extends BasicCheck
{

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        // Тяжелые вычисления...
        
        if (monitor.isCanceled())
        {
        	return;
        }
        
        // Продолжаем вычисления...
    }
}
```

Нет необходимости проверять монитор в самом начале процедуры т.к. система валидации проверяет его перед запуском проверки.


### 3. Сообщение об ошибке

Добавьте сообщение об ошибке, по умолчанию сообщение добавляется для объекта на который подписана проверка.

```java
resultAceptor.addIssue(message);
```

При необходимости укажите свойство объект объекта в котором ошибка.

Есть возможность добавить сообщение на другой объект и его свойство. 
Например, подписываемся на объект `OperatorStyleCreator` (`Новый Структура("Ключ1, Ключ2");`) но ошибка строковом литерале передаваемом в конструктор.


```java
resultAceptor.addIssue(message, literal, STRING_LITERAL__LINES);
```

### 4. Регистрация проверки в точке расширения

Добавьте в точку расширения `com.e1c.g5.v8.dt.check.checks` класс проверки так, чтобы он создавался на основе DI (dependency injection) фабрики.

Укажите категорию проверки (идентификатор), по типам проверяемых метаданных. [Подробнее](Check_Convention.md#категория-проверки)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?eclipse version="3.4"?>
<plugin>
   <extension
         point="com.e1c.g5.v8.dt.check.checks">
      <check
            category="com.e1c.v8codestyle.md"
            class="com.e1c.v8codestyle.internal.md.ExecutableExtensionFactory:com.e1c.v8codestyle.md.check.FindTheProblemCheck">
      </check>
```

## Дополнительно

Если более сложная - могут потребоваться дополнительные возможности. Абстрактная реализация `BasicCheck` позволяет некоторые простые расширения функциональности.

### Параметры

Если проверка сравнивает какое-либо значение с эталонным - это повод вынести такое эталонное значение в параметры, чтобы разаработчик 1С мог переопределить или дополнить значения для своего проекта.
[Подробнее...](Check_Convention.md#параметры)

### Общие расширения

Одинаковую логику предварительной фильтрации объектов можно реализовать как внутри проверки так и с помощью общего компонента расширения `com._1c.g5.v8.dt.check.components.IBasicCheckExtension`.
[Подробнее...](Check_Convention.md#компоненты-расширяющие-функциональность)

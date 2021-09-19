# Пример разработки простой проверки

Большинство проверок состо заключается в том, чтобы:

1. Найти определенный класс объектов  (подписаться на проверку этих объектов). 
2. Затем проверить состояние объекта (свойства объекта)
3. Добавить текст ошибки.

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

Для проверки метаданны, форм, СКД, Моксель, Прав ролей, укажите верхнеуровневый объект (Top object) - тот объект который хранится в файле: Каталог, Форма, СхемаКомпоновкиДанных и т.д.

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


### Проверка монитора

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

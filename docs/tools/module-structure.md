# Автоматическое создание структуры модуля

При создании объектов метаданных для всех модулей автоматически создается структура модуля.

Для каждого из типов модулей используется своя структура модуля. Подробнее см. стандарт

## Отключение авто-создания

Для проекта можно принудительно отключить или включить создание структуры модуля.

Отрокройте свйства проекта: `Properties -> V8 -> Built-in language -> Module structure -> Automatically create module structure`.

Альтернативный способ: создать файл настроек `ProjectName/.settings/com.e1c.v8codestyle.bsl.prefs` с ключем управления созданием структуры модуля:

```

eclipse.preferences.version=1
createModuleStructure=false

```

Для отключения создания структуры модуля в текущем воркспейсе откройте:

Меню Window или 1C:EDT (в macOS): `Preferences -> V8 -> Built-in language -> Module structure -> Automatically create module structure`.

Общие настройки могут быть заданы для всей инсталяции 1C:EDT или поствлятся через 1С:Стартер.

## Переопределение шаблонов

Отрокройте свйства проекта: `Properties -> V8 -> Built-in language -> Module structure`. 
Установите флажок для тех типов модулей, для которых в текущем проекте следует изменить шаблон структуры модуля.

При этом будет создан файл с типом модуля для которого необходимо переопределение. Например, для модуля менеджера: `ProjectName/.settings/templates/manager_module.bsl` 

Имена файлов модулей по типам:

- `manager_module.bsl` - для модуля менеджера
- `object_module.bsl` - для модуля объекта
- `recordset_module.bsl` - для модуля набора записей регистра
- `command_module.bsl` - для модуля команды
- `form_module.bsl` - для модуля формы
- `common_module.bsl` - для общего модуля
- `value_manager_module.bsl` - для модуля менеджера значения константы
- `external_conn_module.bsl` - для модуля внешнего соединения
- `session_module.bsl` - для модуля сессии
- `managed_app_module.bsl` - для модуля приложения
- `ordinary_app_module.bsl` - для модуля обычного приложения
- `web_service_module.bsl` - для модуля веб-сервиса
- `http_service_module.bsl` - для модуля http-сервиса
- `integration_service_module.bsl` - для модуля сервиса интеграции
- `bot_module.bsl` - для модуля бота

## См. также

- [Стандарт 455: Структура модуля](https://its.1c.ru/db/v8std#content:455:hdoc)

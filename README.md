[![Build](https://github.com/1C-Company/v8-code-style/workflows/CI/badge.svg)](https://github.com/1C-Company/v8-code-style/actions)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=1C-Company_v8-code-style&metric=coverage)](https://sonarcloud.io/dashboard?id=1C-Company_v8-code-style)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=1C-Company_v8-code-style&metric=ncloc)](https://sonarcloud.io/dashboard?id=1C-Company_v8-code-style)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=1C-Company_v8-code-style&metric=bugs)](https://sonarcloud.io/dashboard?id=1C-Company_v8-code-style)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=1C-Company_v8-code-style&metric=code_smells)](https://sonarcloud.io/dashboard?id=1C-Company_v8-code-style)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=1C-Company_v8-code-style&metric=sqale_index)](https://sonarcloud.io/dashboard?id=1C-Company_v8-code-style)

# 1С:Стандарты разработки V8

Расширение для 1C:EDT, которое помогает разрабатывать конфигурации/приложения по стандартам 1С для платформы "1С:Предприятие 8".

## Основные возможности

- [Проверки кода и метаданных](docs/checks/readme.md) по [стандартам 1С](https://its.1c.ru/db/v8std)
   - [Проверки метаданных](docs/checks/md.md)
   - [Проверки Форм](docs/checks/form.md)
   - [Проверки прав ролей](docs/checks/right.md)
   - [Проверки модулей](docs/checks/bsl.md)
   - [Проверки языка запросов](docs/checks/ql.md)
- Дополнительные инструменты, улучшающие и ускоряющие работу по стандартам 1С
   - [Авто-сортировка метаданных](docs/tools/autosort.md)
   - [Создание общих модулей по типам](docs/tools/common-module-types.md)
   - [Панель "Bsl Документирующий комментарий"](docs/tools/bsl-doc-comment-view.md)



## Установка

Плагин `1С:Стандарты разработки V8` поставляется в виде репозитория Eclipse. Установка расширения может выполняться следующими способами:

- непосредственно из p2-репозитория, опубликованного на серверах фирмы 1С.
- из локальной копии p2-репозитория, распакованного в локальную папку из предварительно скачанного zip-архива.

В строку выбора репозитория  для установки (`Work with`) вставьте адрес репозитория:

| Версия | P2-репозиторий | ZIP-архив репозитория |
|--------|----------------|-----------------------|
| 0.1.0 для 1C:EDT 2021.2 | https://edt.1c.ru/downloads/releases/plugins/v8-code-style/edt-2021.2/0.1.0/repo/ | https://edt.1c.ru/downloads/releases/plugins/v8-code-style/edt-2021.2/0.1.0/repo.zip |


Далее для установки нужно выполнить следующие действия:

- В среде разработки 1C:Enterprise Development Tools (EDT) выберите пункт меню Help – Install New Software (Справка – Установить новое ПО).
- В открывшемся окне мастера установки в строке Work with воспользуйтесь кнопкой Add… и укажите расположение репозитория.
- Если установка производится непосредственно из репозитория, опубликованного на серверах фирмы 1С, то скопируйте указанный адрес репозитория
- Если установка производится из локальной папки, то воспользуйтесь кнопкой Local.. и далее по кнопке Local укажите папку, в которую распакован репозиторий.
- Отметьте компонент `1C:Code style V8` и нажмите кнопку Next>
- На следующем шаге система определит зависимости и сформирует окончательный список библиотек к установке, после этого нажмите кнопку Next>
- Прочитайте и примите условия лицензионного соглашения и нажмите кнопку Finish
- Дождитесь окончания установки и перезапустите среду 1C:Enterprise Development Tools. Установка завершена.


## Участие в проекте

Добро пожаловать! [См. правила](CONTRIBUTING.md) в соответствующем разделе.
- [Помочь с документацией](docs/contributing/documentation.md) см. [задачи](https://github.com/1C-Company/v8-code-style/labels/documentation)
- [Добавить свою проверку](docs/contributing/readme.md) см. [задачи](https://github.com/1C-Company/v8-code-style/labels/good%20first%20issue)
- Сообщить нам о [ложном срабатывании проверки](https://github.com/1C-Company/v8-code-style/issues/new?assignees=&labels=standards,bug&template=check_false.md&title=Ложное+срабатывание+проверки%3A+%3Cкод+проверки%3E) или о [не нахождении существующей ошибки](https://github.com/1C-Company/v8-code-style/issues/new?assignees=&labels=standards,bug&template=check_not_found.md&title=Проверка%3A+%3Cкод+проверки%3E+не+находит+ошибку).


## Лицензия

[Лицензирование расширений размещенных в данном проекте осуществляется на условиях свободной (открытой) лицензии Eclipse Public License - v 2.0 (полный текст лицензии - https://www.eclipse.org/legal/epl-2.0/)](docs/contributing/licensing.md)

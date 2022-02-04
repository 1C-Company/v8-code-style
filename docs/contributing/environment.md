# Настройка окружения

## Необходимое ПО

Быстрый и легкий способ настроить среду - установите:

1. Java Development Kit 11 с JavaFX необходимые для работы JDT и 1C:EDT
2. [1С:Starter](https://releases.1c.ru/project/DevelopmentTools10) дистрибутив 1C:EDT online, без установки самой 1C:EDT.
3. Установите дистрибутив `Eclipse for 1C:EDT Plug-ins Developers`, версия 2020-12 (см. актуальную версию в таргет-платформе для ветки `master`) - это стандартный дистрибутив Eclipse JDT с дополнительными плагинами и некоторыми настройками по умолчанию.
4. Установите плагины из Eclipse-Marketplace:
    - `JAutodoc` - генерация "рыбы" java-doc на английском - по `CTRL+ALT+J` ускоряет написание документации
    - `SonarLint` - подсказывает очень много проблем в коде и плохих практик
    - `ResourceBundle Editor` - редактирование интерфейсных локализируемых файлов `*.properties` на нескольких языках
    - `Enhanced Class Decompiler` - удобный просмотр классов без исходного кода
    - `LiClipseText` - редактор поддерживающий множество синтаксиса, например Markdown
    - `EclEmma Java Code Covarage` - Запуск тестов со снятием покрытия кода
    - `PDE Source Lookup` - Автоматическая подгрузка исходников для бандлов из целевой платформы из открытых источников

## Настройки JDT и проекта

- Откройте 1C:EDT Start и добавьте новый проект, например v8-code-style (без использования Стартера, откройте JDT и укажите адрес к воркспейсу/проекту).
- ПКМ в навигаторе - Import или меню File - Import - выполните импорт "проектов из Git" указав адрес `https://github.com/1C-Company/v8-code-style.git` в визарде клонирования
- Импортируйте все проекты в репозитории начинающиеся, проекты тестовых конфигураций можно будет потом закрыть или удалить из воркспейса
- Откройте файл целевой платформы (Target platform): `com.e1c.v8codestyle.targets -> default -> default.target` скачайте всю целевую платформу и нажмите `Set active target platform` для активации.
- Выполните активацию настроек в текущем воркспейсе (при использовании 1C:EDT Start) в меню: `Help -> Perform setup tasks...` - Finish...

## Настройка дополнительных плагинов

- JAutodoc
    - Использование встроенного форматера Eclipse `Window -> Preferences -> Java -> JAutodoc -> Use Eclipse formatter`
    - Использование комментария от поля для Getter/Setter `Window -> Preferences -> Java -> JAutodoc -> [G,S]etter from field comment`
- ResourceBundle Editor
    - Кодировка `*.properties` файлов UTF-8 `Window -> Preferences -> General -> Content types -> Text -> Java properties File` измените `Default encoditng = UTF-8`
    - Отключить конвертацию юникода в ХХХ `Window -> Preferences -> ResourceBundle Editor -> Formatting -> Convert unicode values to \uXXXX`
- Enhanced Class Decompiler
    - Выбор алгоритма по умолчанию `Window -> Preferences -> Java -> Decompiler -> Default class decompiler` = JD-Core (например)
    - Установка просмотрщика классов по умолчанию `Window -> Preferences -> Java -> Decompiler -> Set Class Decompiler Viewer as the default...`

## (Опционально) Список плагинов JDT в поставке от 1С

Здесь представлен список инструментов (фич и репозитории) устовленный в `Eclipse for 1C:EDT Plug-ins Developers`, т.о. можно доустановить отсутствующий инструментарий в свою существующую JDT инсталяцию.

Рекомендуем вам воспользоваться установкой JDT, описанной выше из 1C:EDT Start.

Более подробно состав плагинов и поставляемых настроек см. в меню `Navigate -> Open Setup -> ...`

- JDT 2020-12

- Core:
    - org.eclipse.platform.ide
    - org.eclipse.platform.feature.group
    - org.eclipse.platform.source.feature.group
    - org.eclipse.rcp.feature.group
    - org.eclipse.rcp.source.feature.group
    - org.eclipse.wst.xml_ui.feature.feature.group
    - org.eclipse.wst.xsl.feature.feature.group
    - org.eclipse.m2e.feature.feature.group
    - org.eclipse.m2e.logback.feature.feature.group
    - org.eclipse.pde.feature.group
    - org.eclipse.pde.source.feature.group
    - org.eclipse.epp.mpc.feature.group
    - https://download.eclipse.org/releases/2020-12/
    - https://download.eclipse.org/tools/orbit/downloads/drops/R20201130205003/repository/

- 1C:EDT Start Auth:
    - com.e1c.g5.dt.cloud.auth.client.feature.feature.group
    - com.e1c.g5.dt.cloud.auth.client.ui.feature.feature.group
    - com.e1c.g5.dt.cloud.auth.client.third_party.feature.feature.group
    - https://services.1c.dev/repository/auth-client-p2/0.3/ - установка из Стартера

- SpotBugs:
    - com.github.spotbugs.plugin.eclipse.feature.group
    - https://spotbugs.github.io/eclipse/

- AJDT:
    - org.aspectj.feature.group
    - org.aspectj.source.feature.group
    - org.eclipse.ajdt.feature.group
    - org.eclipse.ajdt.source.feature.group
    - org.eclipse.contribution.xref.feature.group
    - org.eclipse.contribution.xref.source.feature.group
    - org.eclipse.contribution.weaving.feature.group
    - org.eclipse.contribution.weaving.source.feature.group
    - org.eclipse.equinox.weaving.sdk.feature.group
    - https://download.eclipse.org/tools/ajdt/410/dev/update/

- Gef and Gmf:
    - org.eclipse.gef.sdk.feature.group
    - org.eclipse.gmf.runtime.notation.sdk.feature.group
    - org.eclipse.gmf.runtime.sdk.feature.group
    - org.eclipse.gmf.feature.group
    - https://download.eclipse.org/releases/2020-12/

- Egit:
    - org.eclipse.jgit.feature.group
    - org.eclipse.jgit.source.feature.group
    - org.eclipse.jgit.pgm.feature.group
    - org.eclipse.egit.feature.group
    - org.eclipse.egit.source.feature.group
    - org.eclipse.egit.mylyn.feature.group
    - org.eclipse.jgit.ssh.apache.feature.group
    - org.eclipse.jgit.http.apache.feature.group
    - org.eclipse.jgit.lfs.feature.group
    - https://download.eclipse.org/releases/2020-12/

- Recommenders:
    - org.eclipse.recommenders.mylyn.rcp.feature.feature.group
    - org.eclipse.recommenders.mylyn.rcp.feature.source.feature.group
    - org.eclipse.recommenders.news.rcp.feature.feature.group
    - org.eclipse.recommenders.news.rcp.feature.source.feature.group
    - org.eclipse.recommenders.rcp.feature.feature.group
    - org.eclipse.recommenders.rcp.feature.source.feature.group
    - org.eclipse.recommenders.snipmatch.rcp.feature.feature.group
    - org.eclipse.recommenders.snipmatch.rcp.feature.source.feature.group
    - org.eclipse.recommenders.3rd.feature.feature.group
    - org.eclipse.recommenders.3rd.feature.source.feature.group
    - https://repo.eclipse.org/content/shadows/releases.unzip/org/eclipse/recommenders/stable/2.5.4/stable-2.5.4.zip-unzip/

- Xtext:
    - org.eclipse.xtext.sdk.feature.group
    - org.eclipse.xtend.sdk.feature.group
    - https://download.eclipse.org/releases/2020-12/

- EMF:
    - org.eclipse.emf.sdk.feature.group
    - org.eclipse.emf.ecore.xcore.sdk.feature.group
    - org.eclipse.emf.compare.source.feature.group
    - org.eclipse.emf.compare.feature.group
    - org.eclipse.emf.compare.diagram.gmf.feature.group
    - org.eclipse.emf.compare.diagram.gmf.source.feature.group
    - org.eclipse.emf.compare.ide.ui.feature.group
    - org.eclipse.emf.compare.ide.ui.source.feature.group
    - org.eclipse.emf.diffmerge.sdk.feature.feature.group
    - org.eclipse.emf.ecp.emfforms.sdk.feature.feature.group
    - org.eclipse.emf.query.sdk.feature.group
    - org.eclipse.emf.transaction.sdk.feature.group
    - org.eclipse.emf.eef.sdk-feature.feature.group
    - org.eclipse.xsd.sdk.feature.group
    - org.eclipse.uml2.sdk.feature.group
    - org.eclipse.ocl.all.sdk.feature.group
    - https://download.eclipse.org/releases/2020-12/

- EMF DiffMerge:
    - org.eclipse.emf.diffmerge.sdk.feature.feature.group
    - https://download.eclipse.org/diffmerge/releases/0.12.0/emf-diffmerge-site/

- MWE:
    - org.eclipse.emf.mwe2.language.sdk.feature.group
    - org.eclipse.emf.mwe2.runtime.sdk.feature.group
    - org.eclipse.emf.mwe.sdk.feature.group
    - https://download.eclipse.org/releases/2020-12/

- Xpand:
    - org.eclipse.xpand.sdk.feature.group
    - https://download.eclipse.org/releases/2020-12/

- Releng Tools
    - org.eclipse.releng.tools.feature.group
    - https://download.eclipse.org/eclipse/updates/4.18/


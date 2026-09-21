## Incorrect Service Name

It is recommended to create Web service names in English using nouns that provide a brief description of their purpose. Using Cyrillic characters is not recommended because third-party information systems might not support them. It is also advised to avoid words that do not change the core meaning if removed, such as "Service" or "WebService".Names of Web service operations and their parameters should also ideally be written in English.

The system checks for non-English characters in the name of the Web services, operations and parameters. Using the substring "Service" in a Web service name is also considered an error.

# Incorrect
- `ВебСервисОбмена` (Cyrillic)
- `DataService` (redundant word)
- `GetUserDataService` (redundant word)

# Correct
- `DataExchange`
- `Users`
- `GetCurrencyRate`

# See also

(Names of metadata objects in configurations. Section 21 Web services)[https://its.1c.ru/db/v8std#content:550:hdoc]

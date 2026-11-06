# Creating a picture from a file in a form module

When developing forms for standard configurations, only standard images and images contained within the configuration itself are used. Images sourced from external files (or file collections) are not permitted. For actions and objects for which standard images are provided, only those corresponding standard images may be used.

## Noncompliant Code Example

```bsl
Picture = New Picture("C:\Images\Print.png");
Button.Picture = Picture;
```

```bsl
Picture = New Picture(BinaryData, PictureFormat.PNG);
Button.Picture = Picture;
```

## Compliant Solution

```bsl
Button.Picture = PictureLib.Print;
```

```bsl
Button.Picture = PredefinedValue("CommonPicture.Print");
```

## See

- [Standard 517: Pictures](https://its.1c.ru/db/v8std#content:517:hdoc)

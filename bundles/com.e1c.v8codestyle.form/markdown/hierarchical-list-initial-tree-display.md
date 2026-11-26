## Restrictions on Using Dynamic Lists

For hierarchical lists, it is not recommended to set the InitialTreeView property to ExpandAllLevels, as this will cause a critical slowdown when opening large lists. Instead, use the values NotExpand or ExpandTopLevel.

## Incorrect

Setting the InitialTreeView property to ExpandAllLevels in a hierarchical list.

## Correct

Setting the InitialTreeView property to NotExpand or ExpandTopLevel in a hierarchical list.

## See also

[Restrictions on using dynamic lists, see item 2](https://its.1c.ru/db/v8std#content:489:hdoc)

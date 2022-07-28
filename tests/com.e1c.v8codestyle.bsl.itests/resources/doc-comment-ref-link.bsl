
// Non complaint.
// 
// Parameters:
//  LinkToMethodParameter - See Complaint.UnknownParameter
//  LinkToMethod - See NonComplaint()
//  LinkToExtMethod - See Catalogs.Products.NonComplaint()
// 
// Returns:
//  AnyRef - any ref
Function NonComplaint(LinkToMethodParameter, LinkToMethod, LinkToExtMethod) Export
    // empty
EndFunction

// See this description
//
// Parameters:
//  WebLink - String - Here valid web-link See https://1c.ru
//  LinkToMethod - See NonComplaint
//  LinkToExtMethod - See Catalogs.Products.NonComplaint
//  LinkToParameter - See NonComplaint.LinkToExtMethod
Procedure Complaint(WebLink, LinkToMethod, LinkToExtMethod, LinkToParameter) Export
    // empty
EndProcedure

// See Catalogs.Products.Complaint.
Procedure Complaint2(WebLink, LinkToMethod, LinkToExtMethod)
    // empty
EndProcedure

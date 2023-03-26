#Region Public

// Defines the availability of RoleNames roles to the current user,
// as well as the availability of administrator rights. 
//
// Parameters:
// RoleNames - String - comma-separated names of roles whose availability is checked.
//
// Returns:
// Boolean - True if at least one of the passed roles is available to the current user or the
//           current user has administrative rights.
//
// Example:
// If RolesAvailable("UseReportMailingLists,SendMail") Then ...
//
Function RolesAvailable(RoleNames) Export
  // code here
EndFunction

#EndRegion
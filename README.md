# ETaskify
* use this link for swagger documentation
http://localhost:8090/swagger-ui/index.html#/
### Signup
* for adding organization and default admin. 
(also adds required system roles ADMIN and USER) 
### Auth
* login - for signin. get token and refresh token. 
* refresh - for get new access token(new refresh token if old refresh expires) 
if access token expires. 
### User 
* only admins can execute user operations
* admins only make operations on the user in their organization.
### Task 
* 


#### P.S. I fulfilled the given task one by one. But I understand that you deliberately give some parts of the assignment incomplete. So I didn't touch on the logical gaps I thought. For example, there had to be general admins in the system, and these admins had to be able to delete the organization, for example. If common system user logic is added, then role operations will be opened.

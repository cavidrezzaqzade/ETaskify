# ETaskify
* use this link for swagger documentation
http://localhost:8090/swagger-ui/index.html#/
### Signup
* for adding organization and default admin. 
(also adds required system roles ADMIN and USER)
  http://localhost:8090/swagger-ui/index.html#/Signup
### Auth
* login - for signin. get token and refresh token. 
  http://localhost:8090/swagger-ui/index.html#/Auth/login
* refresh - for get new access token(new refresh token if old refresh expires) 
if access token expires.
  http://localhost:8090/swagger-ui/index.html#/Auth/getNewAccessToken
### User 
* only admins can execute user operations
* admins only make operations on the user in their organization.
### Task 
* http://localhost:8090/swagger-ui/index.html#/Task/getMyTasks every user gets their own tasks. 
* http://localhost:8090/swagger-ui/index.html#/Task/getAllTasks for admins. get all tasks
* http://localhost:8090/swagger-ui/index.html#/Task/addNewTask for admins. add new task.
* http://localhost:8090/swagger-ui/index.html#/Task/getProgresses get all statuses.
* http://localhost:8090/swagger-ui/index.html#/Task/changeTaksProgress users can change task status.

#### P.S. I fulfilled the given task one by one. But I understand that you deliberately give some parts of the assignment incomplete. So I didn't touch on the logical gaps I thought. For example, there had to be general admins in the system, and these admins had to be able to delete the organization, for example. If common system user logic is added, then role operations will be opened.

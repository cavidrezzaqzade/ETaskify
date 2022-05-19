# BookStoreApp
demo project for bootcamp

instructions:
1. run docker-compose up
2. open swagger http://localhost:8090/swagger-ui/index.html#/Auth/login and login with login=cavid password=12345(default admin) and copy token to authorize button
3. create specific role(PUBLISHER or/and USER) http://localhost:8090/swagger-ui/index.html#/Role/addNewRole
4. add user with USER and/or PUBLISHER roles. http://localhost:8090/swagger-ui/index.html#/User/addNewUser
5. login with this user again and get token. then check endpoints. 

Thank you for your attention...

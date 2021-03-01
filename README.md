## Auth

View `WebSecurityConfig.java` for security configuration. All endpoints are protected by default unless specified in the file. To access endpoints, POST request to `/api/user/login` with email and password in JSON body. A token will be returned in a cookie for authentication for future requests. Endpoints can be protected by Role either through `WebSecurityConfig.java` or `@PreAuthorize` annotation.


On frontend, page routes are protected by checking if there is a `token` cookie (super hacky).Since issued cookies expire after one hour, user will automatically be logged out after one hour. Probably need to add a way to refresh tokens.


## Documentation

View API docs at http://localhost:8080/api/swagger-ui/index.html?configUrl=/api/v3/api-docs/swagger-config

## Running the Database
Port number: 3307
`mysql -P 3307 --protocol=tcp -u root -p`
Using this command on terminal, you can go into your `mysql` on terminal
`use is4103db` - after this you can run sql queries straight to our mysql db
`SELECT * FROM user;` - example query

You can also run connect to it via mysql workbench or DataGrip to visually see all the tables and rows


You can also run connect to it via mysql workbench or DataGrip to visually see all the tables and rows


## Setup

### Database

Install `docker` and `docker-compose`

From root directory
```
docker-compose up
```

### Server

Install JDK 11

Open folder in IDE (VS Code with Spring extensions recommended)

Run application from sidebar or from `src/main/java/com/is4103/backend/BackendApplication.java`

### Githooks

Run
```
git config core.hooksPath .githooks
```
# spring-booter

[![CircleCI](https://circleci.com/gh/mn-io/spring-booter.svg?style=svg)](https://circleci.com/gh/mn-io/spring-booter)


Spring Boot setup with demo user account handling.
Supports DB versioning, unit-, e2e and concurrency tests. 

## Get started

__NB__: Inside `makefile` we define most relevant commands. 


### Setup

This project relies on MariaDB. A simple instance can be started using vagrant and this setup at https://github.com/steveswinsburg/mariadb-vagrant.

In case of database problems, check spring log output as it writes where it tries to connect to.
By default it needs two databases:

- springbooter: to run the app (`pom.xml`, `dev` profile as default) 
- springbooter_test: to run e2e tests against (`application-test.yml`)

Run `make db-update` to create table structure.


### Run

Execute `make mvn-run` to start the application.

### Play

The application output is very expressive and self explaining. Take a few minutes for reading it.
We are providing SwaggerUI, therefore open http://localhost:8080/swagger-ui.html to explore all endpoints.

Some endpoints are mapped within `makefile` - feel free to run them.
Use cases are executed by integration tests in [UserUseCaseIntegrationTest.java](https://github.com/mn-io/spring-booter/blob/master/src/test/java/net/mnio/springbooter/controller/UserUseCaseIntegrationTest.java)

IntelliJ works well with Spring and Spring Actuator.


## FAQ
 
 Why not spring security:
 - overall complex with servlets, auto configuration, several filters (count: 12)
 - it feels like to have unknown code/endpoints/configuration is more error prone instead of adding simple code myself
 - Configuration is way to complex: https://docs.spring.io/spring-security/site/docs/current/reference/html/jc.html
 - CSRF is simple to implement if authentication is used
 - Spring violates the simple REST API paradigm.
 
 ... but do not do crypto yourself!
- of course not: this project is using Apache Shiro


## Testing

As a demo project we are going to demonstrate how different tests can be written. 


In general test coverage is quite high as seen below. (Sorry, but I was too lazy to setup SureFire with SonarCube, etc. )

| Element       | Class %     | Method %     | Line %       |
| ------------- | ----------- | ------------ | ------------ |
| bootstrap     | 80% (8/10)  | 75% (15/20)  | 77% (80/103) |
| controller    | 83% (15/18) | 84% (39/46)  | 79% (99/125) |
| persistence   | 100% (3/3)  | 75% (15/20)  | 64% (27/42)  |
| services      | 100% (3/3)  | 87% (7/8)    | 93% (30/32)  |
| util          | 100% (4/4)  | 100% (10/10) | 100% (32/32) |
| Application   | 100% (1/1)  | 0% (0/1)     | 20% (1/5)    |


Tests can be seen in different ways, ignoring UI as we have a (micro)service oriented backend.


1. Unit tests: They take up the most space as they test happy paths 
  (what is indented to happen) and edge cases (kind of fuzzy testing for weird parameters).

2. Integration or E2E tests: They run against a real, but still non production, environent (e.g. including database) 
   and check whether the orchestration of different components work as desired. For example to check if a email 
   address is unique - it does not matter if this constraint is considered by a service, transaction, db constraint 
   or whatsoever. An interesting discussion about the terms can be read here: https://martinfowler.com/bliki/IntegrationTest.html
  
   
Practical speaking it can be hard to place tests in the correct box. In this project we are covering some of the test case several times.
As test writing should be fast and convenient (TDD, Clean Code!), this should not be a pain. 


When tests start to fail it can be easily tracked down what went wrong by analysing what started to fail first. 
Another advantage of double coverage on different test level is that we 
can be sure that code behaves correctly all the time, even if by accident for instsance mock component is replacing a 
working one and we forgot about it. 


### Concurrency

One thing while testing is usually not considered. How does our code behaves on race conditions? 
As a matter of fact most Spring code runs in a single thread and is designed to be stateless. But in the end we need to
save our data in the single point of truth, called database. But how can we sure that an exception triggers a rollback or 
a rollback cleans up everything within a transaction?


Therefore [jConcurrencyOrchestra](https://github.com/mn-io/jConcurrencyOrchestra) was created and used here.


A implementation can be seen in [UserServiceConcurrencyTest.java](https://github.com/mn-io/spring-booter/blob/master/src/test/java/net/mnio/springbooter/services/user/UserServiceConcurrencyTest.java)
This code in combination with the test library should be self explaining. 


__Use case__: Insert different users but with same email address.


What happens is explained along the test output:

#### Setup 
Two tasks are created and put up in a certain [running order](https://github.com/mn-io/spring-booter/blob/master/src/test/java/net/mnio/springbooter/services/user/UserServiceConcurrencyTest.java#L56)
`Thread-4-Task-9` (`task1ToBeInterrupted`) is designed to be interrupted and checks whether an exception occurs because the user email is not unique anymore even if it has started first.
`Thread-5-Task-6` (`task2CreatingUserSuccessfully`) shall continue and insert the user. As `interrupt()` is called at the same place it has to continue by adding the task again in the running order. 

`[main]            INFO : net.mnio.jConcurrencyOrchestra.test.TaskSchedule - Running order: Thread-4-Task-9, Thread-5-Task-6, Thread-5-Task-6`


#### Run test scenario

`task1ToBeInterrupted` starts and interrupts.

`[Thread-4-Task-9] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1. time for thread - Interruption 'Before saving user 'user1'' called`


Same for `task2CreatingUserSuccessfully`, but this is also third in running order and can continue.

`[Thread-5-Task-6] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1. time for thread - Interruption 'Before saving user 'user2'' called`

`[Thread-5-Task-6] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1. time for thread - Continue from interruption 'Before saving user 'user2''`


Now `task1ToBeInterrupted` can continue as running order has completed and it will finish all tasks in the order as first mentioned in running order.

`[Thread-4-Task-9] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1. time for thread - Continue from interruption 'Before saving user 'user1''`


#### Verify

__Boom__: DB constraint triggers exception and we can now verify the app behavior, its state and exception cleanup.

`[Thread-4-Task-9] WARN : org.hibernate.engine.jdbc.spi.SqlExceptionHelper - SQL Error: 1062, SQLState: 23000`

`[Thread-4-Task-9] ERROR: org.hibernate.engine.jdbc.spi.SqlExceptionHelper - (conn=1358) Duplicate entry 'email' for key 'UC_USER_EMAIL_COL'`

`[Thread-4-Task-9] ERROR: org.hibernate.internal.ExceptionMapperStandardImpl - HHH000346: Error during managed flush [org.hibernate.exception.ConstraintViolationException: could not execute statement]`

`[main]            INFO : org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor - Shutting down ExecutorService 'applicationTaskExecutor'`

`[main]            INFO : org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean - Closing JPA EntityManagerFactory for persistence unit 'default'`

`[main]            INFO : com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Shutdown initiated...`


## Open points

- liquibase diff (see makefile) fails with NPE.

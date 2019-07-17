# spring-booter

[![CircleCI](https://circleci.com/gh/mn-io/spring-booter.svg?style=svg)](https://circleci.com/gh/mn-io/spring-booter)

This repository provides a Spring Boot setup with demo user account handling, 
including DB versioning and test coverage for units, e2e and concurrency. 

## Get started

__NB__: Inside `makefile` we define most relevant commands, e.g. how to start the application and request endpoints.
But they also described later here. 


### Setup

This project relies on MariaDB. 
For example, a simple instance can be started using vagrant and this setup at https://github.com/steveswinsburg/mariadb-vagrant.

In case of database problems, please check the spring log output as it writes where it tries to connect to.
By default it needs two databases:

- springbooter: app run, parameters in `pom.xml` (`dev` profile as default) 
- springbooter_test: e2e tests, parameters in `application-test.yml`

__NB__: Before the first app run (not tests), please run `make db-update` to create table structure.
DB migration is __not__ done automatically at start up as any DB modification, maybe for production, shall happen intentionally.


### Run

Execute `make mvn-run` to start the application.


### Play

The application output is intend to be expressive and mostly self explaining. Please take a few minutes for reading it.
We are providing SwaggerUI, therefore open http://localhost:8080/swagger-ui.html to explore all endpoints.

Some endpoints are mapped within `makefile` - feel free to run them. For a basic understanding what use cases are covered 
take a look at the integration tests in [UserUseCaseIntegrationTest.java](https://github.com/mn-io/spring-booter/blob/master/src/test/java/net/mnio/springbooter/controller/UserUseCaseIntegrationTest.java).

IntelliJ works well with Spring and Spring Actuator, therefore you can check out the 'Run Dashbord'.


## Testing

As a demo project we are going to demonstrate how different tests can be written. 

In general test coverage is quite high. (Sorry, but I was too lazy to setup SureFire with SonarCube, etc.)

| Element       | Class %     | Method %     | Line %       |
| ------------- | ----------- | ------------ | ------------ |
| bootstrap     | 80% (8/10)  | 75% (15/20)  | 77% (80/103) |
| controller    | 83% (15/18) | 84% (39/46)  | 79% (99/125) |
| persistence   | 100% (3/3)  | 75% (15/20)  | 64% (27/42)  |
| services      | 100% (3/3)  | 87% (7/8)    | 93% (30/32)  |
| util          | 100% (4/4)  | 100% (10/10) | 100% (32/32) |
| Application   | 100% (1/1)  | 0% (0/1)     | 20% (1/5)    |


Tests can be seen in different ways, ignoring UI tests as we have a (micro)service oriented backend.


1. Unit tests: They take up the most space as they test happy paths 
  (what is indented to happen) and edge cases (kind of fuzzy testing for weird parameters).

2. Integration or E2E tests: They run against a real, but still non production, environment (e.g. including database) 
   and check whether the orchestration of different components work as desired. For example to check if an email 
   address is unique - it does not matter if this constraint is ensured by a service, transaction, db constraint 
   or whatsoever. An interesting discussion about the terms can be found here: https://martinfowler.com/bliki/IntegrationTest.html
  
   
Practical speaking it can be hard to place tests in the correct box. In this project we are covering some of the test case several times.
As test writing should be fast and convenient (TDD, Clean Code!), this should not be a pain. 


When tests start to fail it can be easily tracked down what went wrong by analysing what started to fail logical first. 
Another advantage of double coverage on different test level is that we 
can be sure that code behaves correctly all the time, even if for instance a mock component is replaced by a 
working one by accident and we commited the code. 


### Concurrency

One thing is usually not considered while testing. How does our code behaves on race conditions? 
As a matter of fact most Spring endpoint calls run in a single thread and is designed to be stateless. But in the end we need to
save our data in the single point of truth, which is our database. But how can we sure that an exception triggers a rollback or 
a rollback cleans up everything within a transaction?

Therefore [jConcurrencyOrchestra](https://github.com/mn-io/jConcurrencyOrchestra) was created and is used here.


A implementation can be seen in [UserServiceConcurrencyTest.java](https://github.com/mn-io/spring-booter/blob/master/src/test/java/net/mnio/springbooter/services/user/UserServiceConcurrencyTest.java).
This code in combination with the test library documentation should be self explaining. 


__Test case__: How does the system behaves if the same user is updated at the same time?
It can either overwrite the first change (last write wins), throw an exception (first write wins) or maybe even corrupt the data.
What will happen?


#### Where to interrupt

In [UserService.java#L61](https://github.com/mn-io/spring-booter/blob/master/src/main/java/net/mnio/springbooter/services/user/UserService.java#L61)
the relevant interruption is located. In production environment this line simple does nothing, 
in test environment `InterruptService` is injected to be an instance of `OrchestratedInterruptServiceImpl`.

We will interrupt just before saving an user. In sum the overall flow is as follows:

<pre>
| AuthFilter loads User from DB if Session token is present
| UserController receives HttpRequest
|\
| | UserService::updateUser(user, data) is called 
| |
| |> Interruption is called
| | User is saved to DB and new entity is returned
|/
| 
| UserController builds Http Response and finishes HttpRequest
</pre>

__NB__: The interruption is triggered by each time as POST and PUT (create and update) will call UserService::updateUser. 
Therefore if we plan to continue there, the task has to be called next again, as seen below.

### How to create tasks and orchestrate running order.

Test output starts as follows:

`[main] INFO : net.mnio.jConcurrencyOrchestra.test.TaskSchedule - Running order: Thread-4-Task-bd, Thread-4-Task-bd, Thread-5-Task-32, Thread-6-Task-20, Thread-6-Task-20`

First we see the initial [running order](https://github.com/mn-io/spring-booter/blob/master/src/test/java/net/mnio/springbooter/controller/UserControllerConcurrencyTest.java#L54).
As lambda expressions are used for creating our tasks, we have to match the Thread/Task name to the variables.

| Thread and Task (run time) | Variable name                 |
| -------------------------- | ----------------------------- |
| Thread-4-Task-bd           | task0SetupUser                |
| Thread-5-Task-32           | task1ToBeInterrupted          |
| Thread-6-Task-20           | task2CreatingUserSuccessfully |


As `UserService` interrupts any time and `task0SetupUser` has to be placed twice at the beginning.

According to the flow above, the orchestration looks like this:

<pre>
o 1.) Thread-4-Task-bd/task0SetupUser: start
| > Interrupts in UserService
| continue
x finish 

  o 2.)  Thread-5-Task-32/task1ToBeInterrupted: start
  | > Interrupts in UserService
  . waiting...
  .
  . o 3.) Thread-6-Task-20/task2CreatingUserSuccessfully: start
  . | > Interrupts in UserService
  . | continue
  . x finish
  .
  | 4.) continue
  x finish with db execption
</pre>

1.)
 - `[Thread-4-Task-bd] DEBUG: net.mnio.springbooter.bootstrap.filter.LogFilter - Incoming request: http://localhost/users (POST)`

 - `[Thread-4-Task-bd] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1. time for thread - Interruption 'Before saving user 'created'' called`

 - `[Thread-4-Task-bd] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1. time for thread - Continue from interruption 'Before saving user 'created''`

2.) 
 - `[Thread-5-Task-32] DEBUG: net.mnio.springbooter.bootstrap.filter.LogFilter - Incoming request: http://localhost/users/me (PUT)`

 - `[Thread-5-Task-32] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1. time for thread - Interruption 'Before saving user 'update1'' called`


3.) 
 - `[Thread-6-Task-20] DEBUG: net.mnio.springbooter.bootstrap.filter.LogFilter - Incoming request: http://localhost/users/me (PUT)`

 - `[Thread-6-Task-20] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1. time for thread - Interruption 'Before saving user 'update2'' called`

 - `[Thread-6-Task-20] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1. time for thread - Continue from interruption 'Before saving user 'update2''`

4.) 
 - `[Thread-5-Task-32] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1. time for thread - Continue from interruption 'Before saving user 'update1''`

 - `[Thread-5-Task-32] ERROR: net.mnio.springbooter.controller.error.ErrorHandlerComponent - ObjectOptimisticLockingFailureException (exception id: GORQaSGR, http status: 500 Internal Server Error)`

 - `org.springframework.orm.ObjectOptimisticLockingFailureException: Object of class [net.mnio.springbooter.persistence.model.User] with identifier [ac3ecb81-b141-4ae9-a81d-67231210c817]: optimistic locking failed; nested exception is org.hibernate.StaleObjectStateException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [net.mnio.springbooter.persistence.model.User#ac3ecb81-b141-4ae9-a81d-67231210c817]`


__NB__: After the running order is done the first time the tasks will be finished in the order as they first occurred. 
`task0SetupUser` has finished, therefore `task1ToBeInterrupted` continues without being placed in running order again.


## FAQ
 
 Why not using spring security:
 - overall complex with servlets, auto configuration, adds several filters (count: 12)
 - it feels like to have unknown code/endpoints/configuration is more error prone instead of adding simple code myself
 - Configuration is way to complex: https://docs.spring.io/spring-security/site/docs/current/reference/html/jc.html
 - CSRF is simple to implement if authentication is used
 - Spring violates the simple REST API paradigm.
 
 ... but do not do crypto yourself!
- of course not: this project is using Apache Shiro


## Open points

- liquibase diff (see makefile) fails with NPE.

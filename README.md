# spring-booter

[![CircleCI](https://circleci.com/gh/mn-io/spring-booter.svg?style=svg)](https://circleci.com/gh/mn-io/spring-booter)

## TODO

- liquibase diff (see makefile) fails with NPE.
- list all endpoints and filters

- Get started
  - create db, e.g. vagrant
  - run liquibase by makefile
  - hint: local tests and local run db requirements

## INFO
actuator, see actuator/mappings


## FAQ
 
 Why not spring security:
 - overall complex with servlets, auto configuration, several filters (12)
 - it feels like to have unknown code/endpoints/configuration is more error prone instead of adding simple code myself
 
 ... but dot do crypto yourself:
- i dont: using apache shiro etc.

CSRF etc, is kind of simple to implement
I like to say: simple is good, simple avoids bugs

And spring will violete the SPA, simple REST API, paradigm.

Configuration is way to complex: https://docs.spring.io/spring-security/site/docs/current/reference/html/jc.html






2019-07-03 14:47:29,329 [main] INFO : net.mnio.jConcurrencyOrchestra.test.TaskSchedule - Running order: Thread-4-Task-206386d6, Thread-5-Task-5a81222b, Thread-5-Task-5a81222b
2019-07-03 14:47:29,739 [Thread-4-Task-206386d6] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1 - Interruption 'Before saving user 'user1'' called
2019-07-03 14:47:29,820 [Thread-5-Task-5a81222b] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1 - Interruption 'Before saving user 'user2'' called
2019-07-03 14:47:29,941 [Thread-5-Task-5a81222b] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1 - Continue from interruption 'Before saving user 'user2''
2019-07-03 14:47:30,141 [Thread-4-Task-206386d6] INFO : net.mnio.jConcurrencyOrchestra.test.OrchestratedInterruptServiceImpl - 1 - Continue from interruption 'Before saving user 'user1''
2019-07-03 14:47:30,146 [Thread-4-Task-206386d6] WARN : org.hibernate.engine.jdbc.spi.SqlExceptionHelper - SQL Error: 1062, SQLState: 23000
2019-07-03 14:47:30,147 [Thread-4-Task-206386d6] ERROR: org.hibernate.engine.jdbc.spi.SqlExceptionHelper - (conn=838) Duplicate entry 'email' for key 'UC_USER_EMAIL_COL'
2019-07-03 14:47:30,149 [Thread-4-Task-206386d6] ERROR: org.hibernate.internal.ExceptionMapperStandardImpl - HHH000346: Error during managed flush [org.hibernate.exception.Constra
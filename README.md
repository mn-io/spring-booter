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


# NB; install maven first

# Create diff.sql from java code
# steps:
#  1.) Create hibernate entities and run command below
#  2.) Review changes and append them to `00-master.sql`
#  3.) Run db-update command from makefile to apply changes

db-create-diff:
	mvn -e -X clean compile liquibase:diff

# Applies master.sql to database
db-update:
	mvn liquibase:update

mvn-tree:
	mvn dependency:tree -Dverbose

mvn-run:
	mvn spring-boot:run


# NB: install curl and jq

HOST = http://localhost:8080

SESSION_TOKEN = "alice-session-token"

HEADER_SESSION = "X-Session-Token: ${SESSION_TOKEN}"
HEADER_JSON = "Content-Type: application/json"

api-hello-world:
	curl -s ${HOST}/hello/world | jq

api-users-list:
	curl -s ${HOST}/users | jq

api-user-signup:
	curl -s -d '{"email":"bob@gmail.com", "name":"Bob", "password": "bob"}' -H ${HEADER_JSON} -X POST ${HOST}/users | jq

api-user-delete:
	curl -s -i -H ${HEADER_SESSION} -X DELETE ${HOST}/users

api-session-login:
	@RESPONSE=`curl -s -i -d '{"email":"alice@alice.com", "password": "alice"}' -H ${HEADER_JSON} -X POST ${HOST}/session`; \
	echo "$$RESPONSE" | grep "X-Session-Token:"; \
	echo "$$RESPONSE" | tail -n1 | jq

api-session-logout:
	curl -s -i -H ${HEADER_SESSION} -X DELETE ${HOST}/session

# use case: an exception is thrown which is not catched or foreseen by developers or business analysts
api-test-unknown-exception-thrown:
	curl -s ${HOST}/hello/exceptionUnknown | jq

# use case: an eception is thrown which is known and part of the controll flow
api-test-mapped-exception-thrown:
	curl -s ${HOST}/hello/exceptionMapped | jq

# use case: spring has some internal error handling which should be controlled by our handling
api-test-spring-exception-thrown:
	curl -s ${HOST}/hello/exceptionBySpring | jq

# use-case: special case
api-test-not-found:
	curl -s ${HOST}/hello/notFound | jq
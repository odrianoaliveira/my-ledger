.PHONY: create-account list-accounts

create-account:
	@OWNER_ID=$$(uuidgen); \
	echo "Generated ownerId: $$OWNER_ID"; \
	curl -v -X POST http://localhost:8080/account \
	  -H "Content-Type: application/json" \
	  -d '{"name": "Cash Account", "ownerId": "'$$OWNER_ID'"}'

list-accounts:
	curl -v http://localhost:8080/account \
	  -H "Accept: application/json"

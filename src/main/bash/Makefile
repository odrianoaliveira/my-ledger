# -----------------------------------------
# Makefile for My Ledger API endpoint calls
# -----------------------------------------

SHELL := /bin/bash
BASE_URL ?= http://localhost:8080

# ----------------------------------------
# ACCOUNTS
# ----------------------------------------

## Create a new account with a random owner ID
create-account:
	@OWNER_ID=$$(uuidgen); \
	echo "Generated ownerId: $$OWNER_ID"; \
	curl --request POST \
	  --url $(BASE_URL)/account \
	  --header 'accept: application/json' \
	  --header 'content-type: application/json' \
	  --data '{"name":"Test Account", "ownerId":"'"$$OWNER_ID"'"}'

## Get list of all accounts
get-accounts:
	curl --request GET \
 		--url $(BASE_URL)/account \
		--header 'accept: application/json'

## Get account by ID
get-account:
ifndef id
	$(error Usage: make get-account id=<UUID>)
endif
	curl --request GET \
		--url $(BASE_URL)/account/$(id) \
		--header 'accept: application/json'

## Get account balance
get-account-balance:
ifndef id
	$(error Usage: make get-account-balance id=<UUID>)
endif
	curl --request GET \
    	--url $(BASE_URL)/account/$(id)/balance \
		--header 'accept: application/json'

# ----------------------------------------
# TRANSACTIONS
# ----------------------------------------

## Create a new transaction between two accounts
create-transaction:
ifndef acc1
	$(error Usage: make create-transaction acc1=<DEBIT_ACCOUNT_ID> acc2=<CREDIT_ACCOUNT_ID>)
endif
ifndef acc2
	$(error Usage: make create-transaction acc1=<DEBIT_ACCOUNT_ID> acc2=<CREDIT_ACCOUNT_ID>)
endif
	curl --request POST \
	  --url $(BASE_URL)/transaction \
	  --header 'accept: application/json' \
	  --header 'content-type: application/json' \
	  --data '{ \
		"description": "Sample Tx", \
		"timestamp": "2024-01-01T12:00:00Z", \
		"entries": [ \
			{"accountId": "$(acc1)", "amount": 1000, "direction": "debit"}, \
			{"accountId": "$(acc2)", "amount": 1000, "direction": "credit"} \
		] \
	  }'

## Get list of all transactions
get-transactions:
	curl --request GET \
		--url $(BASE_URL)/transaction \
		--header 'accept: application/json'

## Get transaction by ID
get-transaction:
ifndef id
	$(error Usage: make get-transaction id=<UUID>)
endif
	curl --request GET \
		--url $(BASE_URL)/transaction/$(id) \
		--header 'accept: application/json'

# ----------------------------------------
# Declaring phony targets
# ----------------------------------------
.PHONY: create-account get-accounts get-account get-account-balance \
        create-transaction get-transactions get-transaction

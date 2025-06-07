# My Ledger

A simple ledger application.

## Functional Requirements

1. Record transaction: log each money movement.
2. Calculate current balance: compute the current state of accounts.
3. Transactions history: provide access to historical transaction records.

## Non-functional Requirements

1. Use in-memory data store
2. Do not use external software

## Assumptions

1. The service should track all transactions individually per accounts
2. Only money movements (debit or credit) are tracked; no complex financial calculations are performed.
3. All movements are timestamped and immutable once recorded.
4. Balance is computed based on the sum of historical transactions.
5. All transactions follow the double-entry bookkeeping model to ensure accuracy and traceability. You can read more here: [Books, an immutable double-entry accounting database service](https://developer.squareup.com/blog/books-an-immutable-double-entry-accounting-database-service/)

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
|-------------------------------|----------------------------------------------------------------------|
| `./gradlew check`             | Build and run the tests                                              |
| `run`                         | Run the server                                                       |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```


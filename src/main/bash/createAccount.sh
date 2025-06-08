#!/usr/bin/env bash

curl -v -X POST http://localhost:8080/account \
  -H "Content-Type: application/json" \
  -d '{"name": "Cash Account", "ownerId": "user-123"}'

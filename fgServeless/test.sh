#!/usr/bin/env bash

URL="https://hj78qgqk09.execute-api.us-east-1.amazonaws.com/event/resolveExpr"


# curl -XGET $URL
curl -XPOST $URL\
	 -H "Content-Type: application/json"\
	 -d '{"expr": "5 + 5"}' 

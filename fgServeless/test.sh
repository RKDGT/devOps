#!/usr/bin/env bash

URL="https://s0crtgpmsi.execute-api.us-east-1.amazonaws.com/dev/event/resolveExpr"


# curl -XGET $URL
curl -XPOST $URL\
	 -H "Content-Type: application/json"\
	 -d '{"expr": "5 + 5"}' 
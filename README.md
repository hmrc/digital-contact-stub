
# digital-contact-stub

Common stub for all digital-contact related projects

# IMI stub
All the endpoints for imi are listed under imi.routes

## Endpoints

### Send emails using imi

```http
POST http://localhost8185/v2/messages
Content-Type: application/json

{
  "channel": "email",
  "from": "test@from",
  "to": [{"email": ["mike@gmail.com"], "correlationId": "correlationID"}],
  "callbackData": "callbackdata",
  "options": {"trackOpens": true, "trackClicks": true, "fromName": "fromname"},
  "contactPolicy": {"contactPolicyGroup": "", "channelCheckConsent": true, "channelApplyFrequencyCap": true},
  "requestedReceipts": [],
  "content": {"type": "", "subject": "subject", "text": "text", "html": "html"},
  "notifyUrl": "notify url"
}
```


### View sent queue
```http
GET http://localhost:8185/digital-contact-stub/viewMessages

```


### reset queue
```http
GET http://localhost:8185/digital-contact-stub/reset

```

### using mailgun for example fset service

### View sent queue
```http
GET http://localhost:8185/digital-contact-stub/mailgun/viewMessages

```


### reset queue
```http
GET http://localhost:8185/digital-contact-stub/mailgun/reset

```

# External Message Adapter stub

## Endpoints

### Process email bounce event over HIP

```http
POST http://localhost:<port>/ccmp/emailBounceback

{
"reason":"EMAIL_BOUNCE",
"sourceData":"SGVsbG8gd29ybGQ=",
"emailAddress":"test@test.com",
"formId":"CH(A)1700",
"externalRefId":"9d7f1d675d544b009d6c3a8f7fb2e1c4"
}
```
Responds with status code:

- 200 if request is successfully processed
- 400 (Bad Request) if either correlationid header is not in correct format or request field is not as per schema
- 401 (Unauthorized) if any of mandatory headers (Authorization and correlationid) is missing or Authorization header
   value is not of correct format
- 403 (Forbidden)
- 404 (NotFound)
- 500 (InternalServerError)
- 503 (ServiceUnavailable)

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
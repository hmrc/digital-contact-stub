
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
GET http://localhost:8185/digital-contact-stub/imi/viewMessages

```


### reset queue
```http
GET http://localhost:8185/digital-contact-stub/imi/reset

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

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
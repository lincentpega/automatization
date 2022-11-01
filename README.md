# java-wildberries-self-buy
New Java version of Telegram bot for Wildberries
## How to deploy application locally?
### Configuring webhook consumer via ngrok

You could install ngrok to expose a local development server to the Internet with minimal effort: \
HowTo: https://ngrok.com/download \
\
After installation you should start ngrok via http protocol on arbitrary port, ex: 8000 \
```
$ ngrok http 8000
```

After running you'll be able to see your external address to webhook recieval on this console in "Forwarding" section: \
![image](https://user-images.githubusercontent.com/99477948/199225393-9296bf44-c5b9-4579-bb76-7815f18a0616.png) \
Now every connection and requiest to this external address will be redirected to `http://localhost:{your-port}`

### Running redis server
To run redis server execute:
```
$ redis-server
```
It will be runned on default port, application configured to use this default port

### Running Spring Boot application
```
git clone https://github.com/lincentpega/java-wildberries-self-buy.git
cd java-wildberries-self-buy
./mvnw spring-boot:run
```
Finally bot is deployed

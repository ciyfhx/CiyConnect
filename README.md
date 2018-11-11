# CiyConnect
A lightweight and powerful Java library for creating TCP server and client

# Setup
## Gradle
```groovy
repositories { 
    maven { 
     url "https://dl.bintray.com/ciyfhx/maven" 
    } 
}

dependencies {
    compile "com.ciyfhx:ciyconnect:1.2.0"
}
```
## Jar file
[Download Jar file](https://bintray.com/ciyfhx/maven/CiyConnect/_latestVersion)

## Documentation
### 1. Registering packet id
### Java
```java
    
public class PacketIDs {
    
    public static int MESSAGING = 0x01;

}
```
```java
PacketsFactory factory = new PacketsFactory();

factory.registerIds(Arrays.asList(PacketIDs.MESSAGING));
SubmissionPublisher<PacketEvent<Packet>> publisher = factory.getPublisher(PacketIDs.MESSAGING);
```
### Kotlin
```kotlin
const val MESSAGING = 0x01
```
```kotlin
val factory = PacketsFactory()

val publisher = factory.registerId(MESSAGING)
```
Packets send using CiyConnect has to be first registered with the corresponding packet's id

### 2. Creating packet
### Java
```java

public class MessagingPacket extends Packet {
    public MessagingPacket(String message) {
        super(PacketIDs.MESSAGING, ByteBuffer.wrap(message.getBytes()));
    }
}
```
### Kotlin
```kotlin
data class MessagePacket(val message: String) : Packet(MESSAGING, ByteBuffer.wrap(message.toByteArray()))
```
This class will encode the message into a ByteBuffer that will be send to the network interface

### 3. Setting up the decoding process
### Java
```java
TransformProcessor<PacketEvent, String> stringTransformProcessor = Processors.ToStringProcessor;

publisher.subscribe(stringTransformProcessor);

```
We will be using the Flow API to create our decoding process.
TransformProcessor is used to translate the item that the publisher push, into another type.

```java
public static TransformProcessor<PacketEvent, String> ToStringProcessor = new TransformProcessor<PacketEvent, String>(p -> new String(p.getPacket().getData().array()));
```
### Kotlin
```kotlin
val toStringProcessor = Processors.ToStringProcessor

publisher.subscribe(toStringProcessor)

```
Here we translate the PacketEvent into a String object
#### Note: Processors.ToStringProcessor is a build class that is already been defined

Then we can just create a Flow.Subscriber which will receive the object that it translate.
### Java
```java

public class PrintLineSubscriber implements Flow.Subscriber<String>{

    private Flow.Subscription subscription;
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        System.out.format("Subscribe to %s\n", subscription.toString());
        subscription.request(1);
    }

    @Override
    public void onNext(String item) {
        System.out.format("Message: %s\n", item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        subscription.cancel();
    }

    @Override
    public void onComplete() {
        System.out.println("Done");
    }
}

stringTransformProcessor.subscribe(new PrintLineSubscriber());
```
### Kotlin
```kotlin
class PrintLineSubscriber : Flow.Subscriber<String> {

    lateinit var subscription: Flow.Subscription
    override fun onSubscribe(subscription: Flow.Subscription) {
        this.subscription = subscription
        println("Subscribe to $subscription")
        subscription.request(1)
    }

    override fun onNext(item: String) {
        println("Message: $item")
        subscription.request(1)
    }

    override fun onError(throwable: Throwable) =
        subscription.cancel()

    override fun onComplete() =
        println("Done")
}


toStringProcessor.subscribe(PrintLineSubscriber())
```
This creates a Flow.Subscriber which will print the message when the MessagePacket is received.

# 4. Creating Server and Clients
## Server
### Java
```java
Server server = ServerBuilder.newInstance().withPort(5555).withPacketsFactory(factory).build();

server.acceptIncomingConnection();
```
or
```java
server.acceptIncomingConnectionAsync();
```
for Asynchronous call which returns a CompletableFuture
### Kotlin
```kotlin
val server = ServerBuilder.newInstance().build(
            port = 5555, packetsFactory = factory)
server.acceptIncomingConnectionAsync()
```
This create a server instance with port configured to 5555 and with the packets factory that we created earlier.
<br>Calling server.acceptIncomingConnectionAsync() will tell the server to start listening for the first incoming connection.
## Client
### Java
```java
Client client = ClientBuilder.newInstance().withPacketsFactory(factory).build();

client.connect("localhost", 5555);
System.out.println("Connected");
```
or
```java
client.connectAsync("localhost", 5555).thenAccept((b) -> {

    System.out.println("Connected");
});
```
### Kotlin
```kotlin
val client = ClientBuilder.newInstance().build(packetsFactory = factory)
client.connectAsync("localhost", 5555).thenAccept {
    println("Connected")
}
```

# 5. Sending Packets
Server -> Client
### Java
```java
server.stream().forEach(n -> {
    try {
        n.getNetworkInterface().sendPacket(new MessagingPacket("test"));
    } catch (Exception e) {
        e.printStackTrace();
    }

});
```
### Kotlin
```kotlin
server.stream().forEach {
        it.networkInterface.sendPacket(MessagingPacket("test"))
}
```
#### Note: This will send the packet to call connected network interfaces/clients
Client -> Server
```java
client.sendPacket(new MessagingPacket("Hello"));
```



### Kotlin
```kotlin
client.sendPacket(MessagingPacket("Hello"))
```

# Advanced
## Dispatcher
Dispatcher defines how the server handle connections and threads

FixedServerConnectionDispatcher - Create a fixed thread pool, can only handle limit request. (Default) maxConnection = 3
CachedServerConnectionDispatcher - Create a cached thread pool, will reuse thread if available, if not create new thread

```java

ServerBuilder.newInstance().withPort(5555).withPacketsFactory(factory)
				.withServerConnectionDispatcher(new FixedServerConnectionDispatcher(3)).build();

ServerBuilder.newInstance().withPort(5555).withPacketsFactory(factory)
				.withServerConnectionDispatcher(new CachedServerConnectionDispatcher()).build();
```

## Pipeline
Pipeline is useful for doing pre-processing of data before its sent or receive by the subscriber


Build-in piplines:<br>
1. CompressionPipeLine<br>
2. AESPipeLine<br>
3. MACValidator<br>
4. HMACValidator<br>

<br>
Custom pipeline can be define by implementing the PipeLine interface.

To add a pipeline,
Example:
### Java
```java
//Server
server.acceptIncomingConnectionAsync().thenAccept(con -> {
    con.getPipeLineStream().addPipeLine(new CompressionPipeLine());
});

//Client
client.connectAsync("localhost", 5555).thenAccept(con -> {
    con.getPipeLineStream().addPipeLine(new CompressionPipeLine());
});
```

## Authentication Manager
AuthenticationManager class handles server-client authentication before the connection is added to the list or accepted
Default AuthenticationManager: RSAWithAESAuthenticationWithValidator

RSAWithAESAuthentication creates a secure network by using an asymmetric key to authenticate connection and
automatically add an AESPipeLine once the authentication succeed

RSAWithAESAuthenticationWithValidator extends the function of RSAWithAESAuthentication by adding a MACValidator 
once the authentication succeed

Custom authentication manager can be define by extending the AuthenticationManager class
## Session
Session can be used to store a connection specific object

## Dependencies

| Github        | Maven           | 
| ------------- |:-------------:| 
|https://github.com/stefan-zobel/streamsupport|[![Maven Central](https://img.shields.io/maven-central/v/net.sourceforge.streamsupport/streamsupport.svg)](http://mvnrepository.com/artifact/net.sourceforge.streamsupport/streamsupport)|

## License

GNU General Public License, version 2, [with the Classpath Exception](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)
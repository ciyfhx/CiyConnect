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
###Java
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
###Kotlin
```kotlin
const val MESSAGING = 0x01
```
```kotlin
val factory = PacketsFactory()

val publisher = factory.registerId(MESSAGING)
```
Packets send using CiyConnect has to be first registered with the corresponding packet's id

### 2. Creating packet
###Java
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
We will be using the Flow API to create our decoding process,
TransformProcessor is used to translate the item that the publisher pushed into another type

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

Then we can just create a Flow.Subscriber which will receive the object that is translates
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
This creates a Flow.Subscriber which will print the message when the MessagePacket is received

## Dependencies

| Github        | Maven           | 
| ------------- |:-------------:| 
|https://github.com/stefan-zobel/streamsupport|[![Maven Central](https://img.shields.io/maven-central/v/net.sourceforge.streamsupport/streamsupport.svg)](http://mvnrepository.com/artifact/net.sourceforge.streamsupport/streamsupport)|

## License

GNU General Public License, version 2, [with the Classpath Exception](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)
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
### Registering packet id
####Java
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
####Kotlin
```kotlin
const val MESSAGING = 0x01
```
```kotlin
val factory = PacketsFactory()

val publisher = factory.registerId(MESSAGING)
```
Packets send using CiyConnect has to be first registered with the corresponding packet's id

### Creating packet

## Dependencies

| Github        | Maven           | 
| ------------- |:-------------:| 
|https://github.com/stefan-zobel/streamsupport|[![Maven Central](https://img.shields.io/maven-central/v/net.sourceforge.streamsupport/streamsupport.svg)](http://mvnrepository.com/artifact/net.sourceforge.streamsupport/streamsupport)|

## License

GNU General Public License, version 2, [with the Classpath Exception](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)
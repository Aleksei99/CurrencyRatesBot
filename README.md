# Currency Monitoring Telegram Bot

This bot was developed as a pet project to gain practical experience in the areas of microservices, application architecture, deployment in Docker, and as a cheat sheet for future projects :smile:.

## Maven + Docker Setup

To configure automatic project build and conversion of jar archives to Docker images, you need to insert the following code into the main pom.xml file:

In the properties section, specify:

```xml
<image>[your Docker Hub login]/${project.artifactId}</image>
```
For example:
```xml
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <spring.version>2.6.7</spring.version>
        <image>alexiandr99/${project.artifactId}</image>
    </properties>
```

Next, in the same file, configure the build configuration:

```xml
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring.version}</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>repackage</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>com.google.cloud.tools</groupId>
                    <artifactId>jib-maven-plugin</artifactId>
                    <version>3.1.4</version>
                    <configuration>
                        <from>
                            <image>openjdk:17-jdk-slim</image>
                        </from>
                        <to>
                            <tags>
                                <tag>latest</tag>
                            </tags>
                        </to>
                    </configuration>
                    <executions>
                        <execution>
                            <phase>package</phase>
                            <goals>
                                <goal>build</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
```

To automatically create an image from a jar file in the pom.xml of each service, insert the following code:

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

    <profiles>
        <profile>
            <id>build-docker-image</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.google.cloud.tools</groupId>
                        <artifactId>jib-maven-plugin</artifactId>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
```

### Running the Bot

To run the bot using Docker, you need a .env file located in the same folder as the Docker-compose.yml file. This .env file should contain the necessary variables for the bot's execution. Then, execute the following command :computer::

```shell
docker-compose up
```
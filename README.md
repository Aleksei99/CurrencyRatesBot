##Настройка Maven + Docker
Для того чтобы настроить автоматическую сборку проекта и преобразование jar
архивов в docker image в главном pom.xml файле нужно вставить следующий код:

В пропертях нужно указать 
```xml
<image>[здесь ваш логин с докер хаба]/${project.artifactId}</image>
```
например
```xml
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <spring.version>2.6.7</spring.version>
        <image>alexiandr99/${project.artifactId}</image>
    </properties>
```

Дальше в этом же файле нужно настроить конфигурацию билда:

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

Для того чтобы из jar файла автоматически был создан image в pom.xml
каждого из сервисов нужно вставить следующий код:
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
# Handling jsp resources into fat jar with Spring Boot and embedded Tomcat 

Hello!

This article about handling static jsp resources into fat jar with Spring boot and embedded Tomcat.

Firstly, you should create Spring Boot project. [SPRING INITIALIZR](http://start.spring.io) is convenient service to create project. Select maven project with java and spring boot 2.0.2 and add spring boot starter web dependency. And click to generate project 

Further,  open new project in your favorite IDE.

I use embedded Tomcat as Java Servlet Container which ships with spring boot starter web. Tomcat require some dependency to render jsp view. Add this dependency to pom.xml:

   		<!-- JSP support in Tomcat-->
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-jasper</artifactId>
		</dependency>
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>jstl</artifactId>
		</dependency>

I am going to use fat jar and I would like Tomcat handle correctly my jsp files. So I have to put *.jsp files into `${project.basedir}/src/main/resources/META-INF/resources/WEB-INF`

Here is example hello.jsp file. It just print "Hello world!"

    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>  
    <html>  
    <head>  
        <title>Home</title>  
    </head>  
    <body>  
        <h1>Hello world!</h1>  
    </body>  
    </html>

Next, add view controller with `hello` request mapping which return `hello` view name.

    @SpringBootApplication  
    public class JspinjarApplication implements WebMvcConfigurer {  
      // other class method is omitted
      
      @Override  
      public void addViewControllers(ViewControllerRegistry registry) {  
          registry.addViewController("/hello").setViewName("hello");  
       }  
    }

And add some properties in application.yml to resolve view name correctly:

    spring:  
      mvc: 
        view: 
          prefix: WEB-INF/  
          suffix: .jsp

If you run project with IDE it would run fine. But if you run project packaged as jar file you get 404 error. How to fix it? I spent long hour to resolve this issue. I have found only one way.

```
app.jar
├── BOOT-INF
│   └── lib
│       └── resources.jar
│           └── META-INF
│               └── resources
│                   └── nested-meta-inf-resource.txt
├── META-INF
│   └── resources
│       └── meta-inf-resource.txt
└── resource.txt
```
Spring boot serve jsp resource only from `nested-meta-inf-resources` Therefore, we should put all jsp files into jar:

    <plugin>  
       <groupId>org.apache.maven.plugins</groupId>  
       <artifactId>maven-jar-plugin</artifactId>  
       <version>3.0.2</version>  
       <executions>  
          <execution>  
             <id>create-jsp-archive</id>  
             <phase>prepare-package</phase>  
             <goals>  
                <goal>jar</goal>  
             </goals>  
             <configuration>  
                <classifier>jsp-resources</classifier>  
                <classesDirectory>${project.basedir}/src/main/resources/</classesDirectory>  
                <includes>  
                   <include>**/*.jsp</include>  
                </includes>  
                <outputDirectory>${project.build.directory}/classes/BOOT-INF/lib</outputDirectory>  
             </configuration>  
          </execution>  
       </executions>  
    </plugin>

Finally, you run project packaged as jar you watch `Hello World!` in your browser.

Complete source code you can find in [DarrMirr/jsp-in-jar](https://github.com/DarrMirr/jsp-in-jar)
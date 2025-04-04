# Bean Tester

Automated JavaBean Testing

## Welcome

### What is it?

BeanTester is an open source Java test library that helps you rapidly and reliably test fundamental objects within your
software system, namely your domain and data objects. Mean Bean:

  - Tests that the getter and setter method pairs of a JavaBean/POJO function correctly.
  - Verifies that the equals and hashCode methods of a class comply with the Equals Contract and HashCode Contract respectively.
  - Verifies property significance in object equality.

### Why should I use it?

Mean Bean helps you rapidly and reliably test fundamental objects within your project, namely your domain and data objects.
With just a single line of code, you can be confident that your beans are well-behaved…

```java
    // Verify bean getters/setters, equals, hashCode and toString for a single bean type
    BeanVerifier.verifyBean(Company.class);
	
    // Create an instance of the bean
    Company company = TestContext.get().create(Company.class);

    // Create an instance of an interface
    CompanyInterface companyInterface = TestContext.get().create(CompanyInterface.class);

    // Create an instance of a class with a specific constructor. The parameter names are matched.
    Company company = TestContext.get()
          .addDescription(Company.class, Specs.beanConstructur("name","industryType"))
          .setRepeatable(585654000L)
          .create(Company.class); 
```

A more detailed specification of a bean may be provided by using the `SpecFilter` method. Create a class called `Company$SpecFilter` which implements the
`SpecFilter` interface. This class will be used to tune the configuration for creating beans, and customise the created beans themselves. 


### Where do I get it?

BeanTester can be acquired from the <a href="https://central.sonatype.com/artifact/com.pippsford/beantester">Maven Central</a>:

    <dependency>
        <groupId>com.pippsford</groupId>
        <artifactId>beantester</artifactId>
        <version>0.1</version>
    </dependency>
	
### License

BeanTester is released under the Apache 2.0 license.

```
Copyright 2010-2020.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

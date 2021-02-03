Steps to add Resilience4j :

1. Add dependencies :


        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot2</artifactId>
            <version>1.4.0</version>
        </dependency>

2. Now there are two ways to configure :
    i) By code : Refer MovieInfoCBThruCode.java class
    ii) By config : Refer application.yml for config & MovieInfo, UserRatingInfo classes for usages

NOTE: Few configs are there in application.yml for actuator which will be required to actually see the statuses of circuitbreaker, bulkhead etc.
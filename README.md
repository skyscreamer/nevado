Nevado JMS
==========

A JMS driver for Amazon Web Services' queue and notification services (SQS/SNS).

Getting started is easy.  Download the jar or add the following to your pom.xml:

    <dependency>
        <groupId>org.skyscreamer</groupId>
        <artifactId>nevado-jms</artifactId>
        <version>1.0.0-Beta2</version>
    </dependency>

Initializing in Spring is a piece of cake.

    <!-- Define the SDK to talk to AWS -->
    <bean id="sqsConnectorFactory" class="org.skyscreamer.nevado.jms.connector.typica.TypicaSQSConnectorFactory" />

    <!-- And this is an implementation of javax.jms.ConnectionFactory -->
    <bean id="connectionFactory" class="org.skyscreamer.nevado.jms.NevadoConnectionFactory">
        <property name="sqsConnectorFactory" ref="sqsConnectorFactory" />
        <property name="awsAccessKey" value="${aws.accessKey}" /> <!-- Set this -->
        <property name="awsSecretKey" value="${aws.secretKey}" /> <!-- And this -->
    </bean>

And now you've got a working JMS 1.1 implementation in your application.

Most of the JMS 1.1 spec is covered.  A complete [coverage map with unit tests](https://github.com/skyscreamer/nevado/wiki/Master-Feature-Grid) provides more details to satisfy geeky curiosity.

We welcome feedback at nevado-dev@skyscreamer.org!

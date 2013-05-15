Nevado JMS
==========

A JMS driver for Amazon Web Services' queue and notification services (SQS/SNS).

Getting started is easy.  Download the jar or add the following to your pom.xml:

    <dependency>
        <groupId>org.skyscreamer</groupId>
        <artifactId>nevado-jms</artifactId>
        <version>1.2.0</version>
    </dependency>

Initializing Spring is a piece of cake.

    <!-- Pick your AWS SDK.  Typica is pretty fast. -->
    <bean id="sqsConnectorFactory" class="org.skyscreamer.nevado.jms.connector.amazonaws.AmazonAwsSQSConnectorFactory" />

    <!-- And this is an implementation of javax.jms.ConnectionFactory -->
    <bean id="connectionFactory" class="org.skyscreamer.nevado.jms.NevadoConnectionFactory">
        <property name="sqsConnectorFactory" ref="sqsConnectorFactory" />
        <property name="awsAccessKey" value="${aws.accessKey}" /> <!-- Set this -->
        <property name="awsSecretKey" value="${aws.secretKey}" /> <!-- And this -->
    </bean>

And now you've got a working JMS client.

Most of the JMS 1.1 spec is covered.  A complete [coverage map with unit tests](https://github.com/skyscreamer/nevado/wiki/Master-Feature-Grid) provides more details to satisfy geeky curiosity.

We welcome feedback at nevado-dev@skyscreamer.org!

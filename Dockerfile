# Extending java:8 to execute fat-jar
FROM java:8

ENV VERTICLE_FILE omnipush-server-1.0.0-SNAPSHOT-fat.jar
ENV APP_CONF_FILE application_conf.json

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles
# Set the location of the configs
ENV CONF_HOME /usr/verticles/.conf

EXPOSE 8080

# Copy your fat jar to the container
COPY target/$VERTICLE_FILE $VERTICLE_HOME/
COPY src/main/conf/* $CONF_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar $VERTICLE_FILE -conf $CONF_HOME/$APP_CONF_FILE"]

#!/usr/bin/env bash

printf "" > $ACTIVEMQ_HOME/conf/credentials.properties
echo "activemq.username=$BROKER_USERNAME" >> $ACTIVEMQ_HOME/conf/credentials.properties
echo "activemq.password=$BROKER_PASSWORD" >> $ACTIVEMQ_HOME/conf/credentials.properties

printf "" > $ACTIVEMQ_HOME/conf/jetty-realm.properties
echo "$ADMIN_USERNAME: $ADMIN_PASSWORD, admin" >> $ACTIVEMQ_HOME/conf/jetty-realm.properties

sed -i "s/^\(ACTIVEMQ_OPTS_MEMORY\s*=\s*\).*\$/\1\"-Xms$MIN_JVM_HEAP -Xmx$MAX_JVM_HEAP\"/" $ACTIVEMQ_HOME/bin/env

/bin/bash -c "bin/activemq console"

#!/bin/bash
JVM=java
$JVM -cp $(dirname $0)/infinispan-core-6.0.0.Final.jar:$(dirname $0)/infinispan-commons-6.0.0.Final.jar:$(dirname $0)/jboss-logging-3.1.2.GA.jar:$(dirname $0)/jboss-marshalling-1.3.18.GA.jar:$(dirname $0)/jgroups-3.4.1.Final.jar:$(dirname $0)/jboss-transaction-api_1.1_spec-1.0.1.Final.jar:$(dirname $0)/jboss-marshalling-river-1.3.18.GA.jar:$(dirname $0)/commons-codec-1.6.jar:$(dirname $0)/commons-io-1.4.jar:$(dirname $0)/service-prefs-${project.version}.jar com.engagepoint.prefs.infinispan.loader.InitialConfigLoader $1

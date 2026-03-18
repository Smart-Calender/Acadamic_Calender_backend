#!/bin/sh
java -jar backend/academicCalander-0.0.1-SNAPSHOT.jar

#!/bin/sh
apt-get update
apt-get install -y openjdk-17-jdk

java -jar backend/academicCalander-0.0.1-SNAPSHOT.jar
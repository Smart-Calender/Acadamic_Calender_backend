

#!/bin/sh
apt-get update
apt-get install -y openjdk-17-jdk

#!/bin/bash
java -jar backend/target/academicCalander-0.0.1-SNAPSHOT.jar
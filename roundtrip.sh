set -x
mvn -DskipTests clean install && java -jar target/*.war

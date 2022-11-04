build:
	mvn clean install -Pqulice -DskipTests -Dlicense.skipAddThirdParty=true

run:
	java -jar target/speco-1.0-SNAPSHOT-jar-with-dependencies.jar --help

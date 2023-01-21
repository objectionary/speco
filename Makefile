dep-tree: ##@Help Draws the maven dependency tree
	mvn dependency:tree

build: ##@Dev Rebuild app
	-rm speco.jar
	mvn clean install -Pqulice
	cp target/speco-1.0-SNAPSHOT-jar-with-dependencies.jar speco.jar

build-force: ##@Dev Rebuild app without linting and tests
	-rm speco.jar
	mvn clean install -Dmaven.test.skip
	cp target/speco-1.0-SNAPSHOT-jar-with-dependencies.jar speco.jar

lint: ##@Dev Runs Qulice
	mvn clean install -Dmaven.test.skip -Pqulice
	cp target/speco-1.0-SNAPSHOT-jar-with-dependencies.jar speco.jar

trans: ##@Usage Run speco on test data
	java -jar speco.jar --source=./tmp/xmir-in --target=./tmp/xmir-out

trans-eo: ##@Usage Run speco on test data with --eo flag
	java -jar speco.jar --source=./tmp/eo-in --target=./tmp/eo-out --eo

run-eo: ##@Usage Compiles and runs eo program
	cd tmp/eo-out && eoc clean && eoc link && eoc --alone dataize app && eoc clean

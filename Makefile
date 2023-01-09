RUN_ARGS := $(wordlist 2, 100, $(MAKECMDGOALS))

HELP_FUN = \
	%help; while(<>){push@{$$help{$$2//'options'}},[$$1,$$3] \
	if/^([\w-_]+)\s*:.*\#\#(?:@(\w+))?\s(.*)$$/}; \
    print"$$_:\n", map"  $$_->[0]".(" "x(20-length($$_->[0])))."$$_->[1]\n",\
    @{$$help{$$_}},"\n" for keys %help; \

make-help: ##@Help Show this help message
	@echo -e "Usage: make [target] ...\n"
	@perl -e '$(HELP_FUN)' $(MAKEFILE_LIST)

build: ##@Application Rebuild app
	-rm speco.jar
	mvn clean install -Pqulice
	cp target/speco-1.0-SNAPSHOT-jar-with-dependencies.jar speco.jar

build-force: ##@Application Rebuild app without linting and tests
	-rm speco.jar
	mvn clean install -Dmaven.test.skip
	cp target/speco-1.0-SNAPSHOT-jar-with-dependencies.jar speco.jar

run: ##@Application Run command line tool
	java -jar speco.jar $(MAKECMDGOALS)

trans: ##@Application Run speco on test data
	java -jar speco.jar --dir=./tmp/xmir-in --target=./tmp/xmir-out

trans-eo: ##@Application Run speco on test data with --eo flag
	java -jar speco.jar --dir=./tmp/eo-in --target=./tmp/eo-out --eo

dep-tree: ##@Help Draws the maven dependency tree
	mvn dependency:tree

clear: ##@Application Clear tmp directory
	-rm tmp/eo-in_prs/*
	-rm tmp/eo-in_prs_aoi/*
	-rm tmp/eo-out/*
	-rm tmp/xmir-out/*
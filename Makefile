#!/usr/bin/env make -f

sources := $(wildcard com/vmonaco/bio/*.java) $(wildcard com/vmonaco/bio/events/*.java)
classes := $(wildcard com/vmonaco/bio/*.class) $(wildcard com/vmonaco/bio/events/*.class)

jnativehook := jnativehook-2.1.0.jar

classpath := .:lib/commons-cli-1.4.jar:lib/$(jnativehook)

build : $(sources)
	javac -cp $(classpath) $(sources) -Xlint:deprecation

run : $(classes)
	java -cp $(classpath) com.vmonaco.bio.BioLogger -v

jar : build
	jar xf lib/$(jnativehook) org
	jar xf lib/commons-cli-1.4.jar org
	jar cvfm biologger.jar MANIFEST.MF com org
	rm -rf org

runjar :
	java -jar biologger.jar

clean :
	rm -f $(subst $$,\$$,$(classes))

COMPILE:

in src folder:
javac -cp .:gson-2.6.2.jar:bcprov-jdk15on-159.jar blockchain/*.java peer2peer/*.java

RUN:

in src folder:
java -cp .:gson-2.6.2.jar:bcprov-jdk15on-159.jar blockchain/Cryptocoin

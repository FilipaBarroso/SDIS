HOW TO COMPILE

In src folder, enter:
javac -cp . service/*.java channels/*.java data/*.java database/*.java protocol/*.java

HOW TO RUN

As a peer, enter in src folder:
java service/PeerService <IP-address> <port-number>

As a client, enter in src folder:
java service/PeerService <IP-address> <port-number>
+
BACKUP <filename.filetype> <replication-degree>
or
DELETE <filename.filetype>
or
RESTORE <filename.filetype>

After loging in as a client, you can call each protocol again without having to log out. Call 'backup', 'delete' or 'restore' and follow instructions on screen.


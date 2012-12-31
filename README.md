dbc
===
[EN]

Database-Container. Collection implementations that store data into a database.

TODO

[DE]<br />
Database-Container.<br />
DBC ist ein Projekt indem die Java-Basis-Collections (Map, List, Set) so implementiert werden, dass sie ihre Daten nicht in den Speicher, sondern in eine Datenbank hinterlegen. Dazu wird die JDBC-Schnittstelle verwendet. 
<br /><br />
<b>Ziel:</b><br />
Datenbanklogik hinter der Implementierung "verstecken", sodass man möglichst leicht in einem bestehenden Projekt die Collection-Implementierung austauschen kann.
<br /><br />
<b>Prioritäten:</b>
<ul>
<li>Schnelligkeit - Sodass es die Gesamtperformance eines Projektes nicht ausgebremst wird</li>
</ul>
<br />

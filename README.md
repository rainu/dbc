dbc
===
<a name="en">[EN]</a>

Database-Container. Collection implementations that store data into a database.

License
-------

Database-Container is distributed under the [The MIT License](http://opensource.org/licenses/MIT).

Maven Integration
--------

If you want to add ___Database-Container___ to your maven project, you can add the following dependency in your __pom.xml__:

```xml
<dependency>
	<groupId>de.raysha.lib</groupId>
	<artifactId>dbc</artifactId>
	<version>2.0</version>
</dependency>
```


TODO

<b>How to build:</b>
<ul>
  <li>Install <b>maven</b> (i use version 2)</li>
  <li>go into the project root-directory</li>
  <li>
    execute the following command:
    <pre>mvn clean install</pre>
  </li>
</ul>
<b>How to use:</b>
<pre>
Example 1:

import de.rainu.lib.dbc.DBMap;
import de.rainu.lib.dbc.beans.ConnectionInfo;
...
ConnectionInfo info = new ConnectionInfo(
  "org.h2.Driver", "jdbc:h2:/tmp/dbc", "sa", "");
Map<String, String> map = new DBMap(info);

map.put("key", "value");
...
</pre>

<hr />
<a name="de">[DE]</a><br />
Database-Container.<br />
DBC ist ein Projekt indem die Java-Basis-Collections (Map, List, Set) so implementiert werden, dass sie ihre Daten nicht in den Speicher, sondern in eine Datenbank hinterlegen. Dazu wird die JDBC-Schnittstelle verwendet. 
<br /><br />
<b>Ziel:</b><br />
Datenbanklogik hinter der Implementierung "verstecken", sodass man möglichst leicht in einem bestehenden Projekt die Collection-Implementierung austauschen kann.
<br /><br />
<b>Prioritäten:</b>
<ul>
<li>Schnelligkeit - Sodass die Gesamtperformance eines Projektes nicht ausgebremst wird</li>
</ul>
<b>Bau-Anleitung:</b>
<ul>
  <li>Installiere <b>maven</b> (ich benutze Version 2)</li>
  <li>Wechsel in das Projekt-Verzeichnis</li>
  <li>
    führe folgendes Kommando aus:
    <pre>mvn clean install</pre>
  </li>
</ul>
<b>Benutzung:</b>
<pre>
Beispiel 1:

import de.rainu.lib.dbc.DBMap;
import de.rainu.lib.dbc.beans.ConnectionInfo;
...
ConnectionInfo info = new ConnectionInfo(
  "org.h2.Driver", "jdbc:h2:/tmp/dbc", "sa", "");
Map<String, String> map = new DBMap(info);

map.put("key", "value");
...
</pre>

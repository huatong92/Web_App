************************************************************************
Introduction:

This application is a web application for a Dashboard. It’s written in Play framework, using Akka actor model. Basically, it pulls data from a database, populates data into am charts, and displays the charts.

************************************************************************

Structure:
app
  └ actors
      └ ChartActor.scala
      └ UserActor.java
  └ controllers
      └ Application.java
  └ models
      └ Database.java
      └ FormateJson.java
      └ Parameters.java
      └ QueryGenerator.java
  └ views
      └ index.scala.html
conf
  └ application.conf
  └ logback.xml
  └ routes
public
  └ images
  └ font-awesome
  └ js
      └ ws.js
  └ css
      └ main.css
************************************************************************
Details for each file:
======APP=======
1. Actors:
	This folder contains Actors written on the bases of Akka’s actor model. 
	ChartActor.scala is written in scala, it contains three classes: ChartActor, ChartsActor, and OrgActor. Each chart we have is represented by a ChartActor instance, each ChartsActor instance manages chartActors in one organization, there will only be one OrgActor instance, it manages all ChartsActors. Each ChartActor has a clock that checks the database for updates every curtain time. 
	UserActor.java is written in java. Each UserActor instance is connected with one open browsing webpage. It sends message to the web socket on its corresponding page, and to the OrgActor that manages charts for the organization this user logs in. 
	
2. Controllers:
	This folder contains the Application class. This class defines functions index() and ws(). index() returns a Result that will render the html page. ws() creates a web socket to send and receive messages on behalf of the webpage. ws() is embedded in the html page, and called by the javascirpt function.

3. Models:
	This package deals with everything about database. 
	Database.java connects to the database, and keeps one connection alive. getJson() calls functions from QueryGenerator to get the query, execute the query, and calls function from FormatJson to format the result set. It returns a json string. 
	FormatJson.java format the result set according to the amcharts’ needs.
	Parameters.java contains parameters for each charts. This is for query generator and json format. 
	QueryGenerator contains methods to generate queries for different charts. 
4. Views:
	html page

======CONF=======
	application.conf has the information of database, and other configuration strings.
	routes has the play framework routing information.

=====PUBLIC======
	ws.js is the main javascript file. It contains mainly message sending/receiving from web socket and amcharts generator. 
	main.css is the main css file.
	images contains images.
	All the others are bootstrap files.
 

************************************************************************

Workflow of a new user:
When a new user opens a window, in routes, it directs to Application.index(), renders the index html page. The ws.js runs, creates a new web socket, start connecting with the Application.ws(). ws() creates a new userActor, pass the out of the web socket to it, so the userActor can write out to the web socket directly. ws() also deals with all incoming message from javascript web socket. All message received by ws() is forwarded to orgActor in ChartActor.scala, the orgActor distribute the message to the corresponding organization ChartsActor, and ChartsActor distribute to the corresponding charts. 
UserActor is set to watch default charts, and then will watch/unwatch charts according to clicks. 

Workflow of date picker:
Date picker is set to a default start-date (read from database) and default end-date (current date). Every time user click on a new category, the date is reset to this default dates. When user selected customized date range, and click submit, a message will be sent to all charts he/she is watching, these charts then record the date info in a hash map, and create queries to get a new set of data, pass to the front-end. 

Workflow of database checking and data updating:
Each chartActor performs a check every 3 seconds using the default date. If the result changes, they query using user’s customized date range and send new data to the userActors. UserActors then send message to their javascript file. 

************************************************************************

Message types:
They are defined at the end of ChartActor.scala

1. WatchChart(chart: String, org: Option[String])
	Send watch chart command. Same as 1, org can be optional when org_id is unknown.

2. UnwatchChart(chart: Option[String], org: Option[String])
	Send unwatch chart command. Same as 1, org can be optional when org_id is unknown. chart is optional too, when it’s null, it means too unwatch all charts.


3. DateUpdate(start: String, end: String, org: Option[String])
	Send customized date range, start and end are date string. Org is the organization id, it is optional because it’s created first in Application.ws() when it hear from javascript, at this time, ws() has no info about organization_id yet. ws() send it to UserActor, UserActor will add the org before forward to chart actors. 

4. ChartUpdate(chart: String, data: String)
	Send update json data for any chart. Chart is the name of the chart, data is the new json string that goes to data in making amchart.

package actors;

import akka.actor.UntypedActor;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.Play;
import scala.Option;

import java.util.Arrays;
import java.util.List;

import models.Database;

/**
 * The broker between the WebSocket and the ChartActor(s).  
 * The UserActor holds the connection and sends serialized JSON data to the client.
 */

public class UserActor extends UntypedActor {
	private final WebSocket.Out<JsonNode> out;
	private final String org;
	private final String grades;
	public UserActor(WebSocket.Out<JsonNode> out) {
		this.out = out;
		
		// for testing, randomly assign an organization to the user
		List<String> list = Arrays.asList("20", "21", "22");
//		org = list.get((int)(Math.floor(Math.random()*3)));
		org = "21";
		System.out.println("New User from: " + org);
		
		grades = Database.getGrades(org);
		ObjectNode gradeMessage = Json.newObject();
		gradeMessage.put("type", "gradeInfo");
		gradeMessage.put("grades", grades);
		out.write(gradeMessage);
		
		List<String> defaultCharts = Play.application().configuration().getStringList("default.charts");
		
		for (String chart : defaultCharts) {
			OrgActor.orgActor().tell(new WatchChart(chart, Option.apply(org)), getSelf());
		}
	}
	
	public void onReceive(Object message) {
		if (message instanceof ChartUpdate) {
			//push the chart to the client
			ChartUpdate chartUpdate = (ChartUpdate) message;
			ObjectNode chartUpdateMessage = Json.newObject();
			chartUpdateMessage.put("type", "chartupdate");
			chartUpdateMessage.put("chart", chartUpdate.chart());
			chartUpdateMessage.put("data", chartUpdate.data());
			out.write(chartUpdateMessage);
		} 
		else if (message instanceof WatchChart) {
			WatchChart watchChart = (WatchChart) message;
			OrgActor.orgActor().tell(new WatchChart(watchChart.chart(), Option.apply(org)), getSelf());
		}
		else if (message instanceof UnwatchChart) {
			UnwatchChart unwatchChart = (UnwatchChart) message;
			OrgActor.orgActor().tell(new UnwatchChart(unwatchChart.chart(), Option.apply(org)), getSelf());
		}
		else if (message instanceof DateUpdate) {
			DateUpdate dateUpdate = (DateUpdate) message;
			OrgActor.orgActor().tell(new DateUpdate(dateUpdate.start(), dateUpdate.end(), Option.apply(org)), getSelf());
		}
	}
	
}

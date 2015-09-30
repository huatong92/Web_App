package controllers;

import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.WebSocket;
import play.mvc.Result;
import scala.Option;
import actors.*;
import akka.actor.ActorRef;
import akka.actor.Props;

import com.fasterxml.jackson.databind.JsonNode;

public class Application extends Controller{
	
	public Result index() {
        return ok(views.html.index.render());
    }
	
	
	public WebSocket<JsonNode> ws() {
		return new WebSocket<JsonNode>() {
			public void onReady(final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {
				final ActorRef userActor = Akka.system().actorOf(Props.create(UserActor.class, out));
				in.onMessage(new F.Callback<JsonNode>() {
					@Override
					public void invoke(JsonNode jsonNode) throws Throwable {
						String msgType = jsonNode.get("msgType").textValue();
						if (msgType.equals("watch")) {
							WatchChart watchChart = new WatchChart(jsonNode.get("chart").textValue(), Option.apply((String)null));
							userActor.tell(watchChart, null);
						} else if (msgType.equals("unwatch")) {
							String chart = jsonNode.get("chart").textValue();
							if (chart.equals("")) {
								UnwatchChart unwatchChart = new UnwatchChart(Option.apply((String)null), Option.apply((String)null));
								userActor.tell(unwatchChart, null);
							} else {
								UnwatchChart unwatchChart = new UnwatchChart(Option.apply(jsonNode.get("chart").textValue()), Option.apply((String)null));
								userActor.tell(unwatchChart, null);
							}
						} else if (msgType.equals("update")) {
							DateUpdate dateUpdate = new DateUpdate(jsonNode.get("start").textValue(), jsonNode.get("end").textValue(), Option.apply((String)null));
							userActor.tell(dateUpdate, null);
						}
						
					}
				});
				
				in.onClose(new F.Callback0() {
					@Override
					public void invoke() throws Throwable {
						userActor.tell(new UnwatchChart(Option.apply((String)null), Option.apply((String)null)), null);
						Akka.system().stop(userActor);
					}
				});
			}
		};
		
	}
	

}

package actors

import akka.actor.{Props, ActorRef, Actor}
import play.libs.Akka
import scala.collection.immutable.HashSet
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.Calendar
import java.text.SimpleDateFormat
import models.Database
import collection.mutable.Map

class ChartActor(chart: String, org: String) extends Actor {
  // a set of watchers
  protected[this] var watchers: HashSet[ActorRef] = HashSet.empty[ActorRef]
  
  protected[this] var dateMap: Map[ActorRef, String] = Map.empty[ActorRef, String]
  
  // data get from database for the default date range 
  protected[this] var json: String = null
  // default date range
  val start_date: String = "'2014-09-01'"
  val end_date: String = "'" + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()) + "'"

  // do Update every 3 secs
  val clock = context.system.scheduler.schedule(Duration.Zero, 3000.millis, self, Update)

  def receive = {
    // update the json data, if it changes, send new json to all watchers
    case Update =>      
      
      var newJson:String = Database.getJson(chart, org, start_date, end_date)
      
      if (json == null) {
        json = newJson
      } else { 
        if (newJson != json) {
          json = newJson
       
          watchers.foreach{ (watcher: ActorRef) =>
            dateMap.get(watcher) match {
              case Some(str) => 
                newJson = Database.getJson(chart, org, str.split("@")(0), str.split("@")(1))
                watcher ! ChartUpdate(chart, newJson)
              case None =>
                watcher ! ChartUpdate(chart, newJson)
            }
          }
        }
      }
   
    // new user actor is watching this chart
    case WatchChart(_, Some(org)) =>
      var newJson:String = Database.getJson(chart, org, start_date, end_date)
      json = newJson
      sender ! ChartUpdate(chart, json)
      watchers = watchers + sender
      
    // one user actor unwatchs this chart
    case UnwatchChart(_, _) =>
      if (watchers.contains(sender)) {
        watchers = watchers - sender
        dateMap.remove(sender)
        
        if (watchers.size == 0) {
          clock.cancel()
          context.stop(self)
        }
      }
      
    case DateUpdate(start, end, Some(org)) =>
      if (watchers.contains(sender)) {
        var newJson:String = Database.getJson(chart, org, "'" + start + "'", "'" + end + "'")
        sender ! ChartUpdate(chart, newJson)
        dateMap.put(sender, "'" + start + "'" + "@" + "'" + end + "'")
      }

      
  }
  
}

class ChartsActor(org: String) extends Actor {
  def receive = {
    case watchChart @ WatchChart(chart, Some(org)) =>
       context.child(chart).getOrElse {
        context.actorOf(Props(new ChartActor(chart, org)), chart)
      } forward watchChart
    case unwatchChart @ UnwatchChart(Some(chart), _) =>
      context.child(chart).foreach(_.forward(unwatchChart))
    case unwatchChart @ UnwatchChart(None, _) =>
      context.children.foreach(_.forward(unwatchChart))
    case dateUpdate @ DateUpdate(_, _, _) =>
      context.children.foreach(_.forward(dateUpdate))
  }
}


class OrgActor extends Actor {
  def receive = {
    case watchChart @ WatchChart(chart, Some(org)) =>
      context.child(org).getOrElse {
        context.actorOf(Props(new ChartsActor(org)), org)
      } forward watchChart
    case unwatchChart @ UnwatchChart(Some(chart), Some(org)) =>
      context.child(org).foreach(_.forward(unwatchChart))
    case unwatchChart @ UnwatchChart(None, Some(org)) =>
      context.child(org).foreach(_.forward(unwatchChart))
    case dateUpdate @ DateUpdate(start, end, Some(org)) =>
      context.child(org).foreach(_.forward(dateUpdate))
  }
}

object OrgActor {
  lazy val orgActor: ActorRef = Akka.system.actorOf(Props(classOf[OrgActor]))
}


case object Update

case class GradeInfo()

case class DateUpdate(start: String, end: String, org: Option[String])

case class ChartUpdate(chart: String, data: String)

case class WatchChart(chart: String, org: Option[String])

case class UnwatchChart(chart: Option[String], org: Option[String])
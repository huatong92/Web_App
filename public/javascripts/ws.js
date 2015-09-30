var location_line_graph = '[{"lineThickness":3,"title":"On School Property","valueField":"on_school_property"},{"lineThickness":3,"title":"Off Site School Event","valueField":"off_site_school_event"},{"lineThickness":3,"title":"Off School Property","valueField":"off_school_property"},{"lineThickness":3,"title":"On School Bus","valueField":"on_school_bus"},{"lineThickness":3,"title":"Online Digital","valueField":"online_digital"},{"lineThickness":3,"title":"Other","valueField":"other"},{"lineThickness":3,"title":"Total Incidents","valueField":"total_incidents"},{"id":"show_all","lineThickness":3,"title":"Show All","valueField":"show_all"}]';
var bias_line_graph = '[{"lineThickness": 3,"title": "National Origin","valueField": "national_origin"},{"lineThickness": 3,"title": "Race","valueField": "race"},{"lineThickness": 3,"title": "Weight","valueField": "weight"},{"lineThickness": 3,"title": "Religion","valueField": "religion"},{"lineThickness": 3,"title": "Disability","valueField": "disability"},{"lineThickness": 3,"title": "Gender","valueField": "gender"},{"lineThickness": 3,"title": "Sexual Orientation","valueField": "sexual_orientation"},{"lineThickness": 3,"title": "Sexuality","valueField": "sexuality"},{"lineThickness": 3,"title": "None of the Above","valueField": "none_of_the_above"},{"linehickness": 3,"title": "Other","valueField": "other"},{"lineThickness": 3,"title": "Total Incidents","valueField": "total_incidents"},{"id": "show_all","title": "Show All"}]';
var harassment_line_graph = '[{"id": "AmGraph-1","lineThickness": 3,"title": "Physical","valueField": "physical"},{"id": "AmGraph-2","lineThickness": 3,"title": "Verbal","valueField": "verbal"},{"id": "AmGraph-3","lineThickness": 3,"title": "Theft","valueField": "theft"},{"id": "AmGraph-4","lineThickness": 3,"title": "Vandalism","valueField": "vandalism"},{"id": "AmGraph-5","lineThickness": 3,"title": "Digital Threat Public","valueField": "digital_threat_public"},{"id": "AmGraph-6","lineThickness": 3,"title": "Digital Threat Private","valueField": "digital_threat_private"},{"id": "AmGraph-7","lineThickness": 3,"title": "Other","valueField": "other"},{"id": "AmGraph-8","lineThickness": 3,"title": "Total Incidents","valueField": "total_incidents"}]';
var rotate_bar_graph = '[{ "balloonText": "<b>[[category]]: [[value]]</b>", "fillColorsField": "color",     "fillAlphas": 0.9, "lineAlpha": 0.2, "type": "column", "valueField": "visits", "labelText": "[[value]]", "labelPosition": "right"}]';
var bar_graph = '[{ "balloonText": "<b>[[category]]: [[value]]</b>", "fillColorsField": "color",     "fillAlphas": 0.9, "lineAlpha": 0.2, "type": "column", "valueField": "visits", "labelText": "[[value]]", "labelPosition": "top"}]';
var by_gender_graph = '[{"balloonText": "<b>[[value]]</b>", "fillColorsField": "color", "fillAlphas": 0.9,"lineAlpha": 0.2, "type": "column", "title": "Male","valueField": "male","labelText": "Male: [[value]]"},{ "balloonText": "<b>[[value]]</b>", "fillColorsField": "color", "fillAlphas": 0.9,"lineAlpha": 0.2, "type": "column", "title": "Female","valueField": "female","labelText": "Female: [[value]]"},{ "balloonText": "<b>[[value]]</b>", "fillColorsField": "color", "fillAlphas": 0.9,"lineAlpha": 0.2, "type": "column", "title": "Not Recorded","valueField": "nr","labelText": "Not Recorded: [[value]]"},{ "id": "show_all", "title": "Show All", }]';
var by_grade_graph;
var radar_graph = '[{"balloonText": "[[value]]","bullet": "round","lineThickness": 3,"valueField": "visits"}]';

var title_map = {"location_day_1":"Incident by Location by Day", 
		"location_week_1":"Incident by Location by Week", 
		"location_month_1":"Incident by Location by Month",
		"harassment_day_1":"Incident by Harassment by Day", 
		"harassment_week_1":"Incident by Harassment by Week",
		"harassment_month_1":"Incident by Harassment by Month",
		"bias_day_1":"Incident by Bias by Day", 
		"bias_week_1":"Incident by Bias by Week", 
		"bias_month_1":"Incident by Bias by Month",
		"location_bar_1":"Incident by Location",
		"bias_bar_1":"Incident by Bias", 
		"harassment_bar_1":"Incident by Harassment",
		"by_gender_2":"Target, Aggressor, Reporter by Gender", 
		"by_grade_2":"Target, Aggressor, Reporter by Grade",
		"reporter_relation_2":"Reporter's Relation with Reports",
		"gender_mix_2":"Aggressor/Target Gender Mix",
		"report_age_3":"Age of Open Reports Summary", 
		"evidence_3":"Attached Evidence Summary", 
		"evidence_restriction_3":"Attached Evidence's Restriction Summary", 
		"report_status_3":"Report Status Summary", 
		"supportive_4":"Supportive Actions for Target",
		"punitive_4":"Punitive Actions for Aggressors", 
		"restorative_4":"Restorative Actions for Aggressors"};

var charts1 = ["location_month_1", "location_week_1", "location_day_1", "harassment_month_1", 
              "harassment_week_1", "harassment_day_1", "bias_month_1", "bias_week_1", "bias_day_1",
              "location_bar_1", "harassment_bar_1", "bias_bar_1"];
var charts2 = ["by_gender_2", "by_grade_2", "reporter_relation_2", "gender_mix_2"];
var charts3 = ["report_age_3", "evidence_3", "evidence_restriction_3", "report_status_3"];
var charts4 = ["supportive_4", "punitive_4", "restorative_4"];

$(document).ready(function() {
	for (var i in charts2) {
		$("#"+charts2[i]).hide();
	}	
	for (var i in charts3) {
		$("#"+charts3[i]).hide();
	}
	for (var i in charts4) {
		$("#"+charts4[i]).hide();
	}
	// get message from web socket
	var ws = new WebSocket($("body").data("ws-url"));
	ws.onmessage = function(event) {
		var message;
		message = JSON.parse(event.data);
		switch (message.type) {
			case "chartupdate":
				makeChart(message.chart, message.data);
				break;
			case "gradeInfo":
				makeGradeGraph(message.grades);
			default:
				return console.log(message);
		}
	};


	
	$('#start-text').calendar({
		dynamic: true,
        first_day_sunday: false,
        inactive_future: false,
        value: '',
        value_input: '#start-value',
        days: ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"],
        on_select: function(year, month, day) {
            
        }
    });
	
	$('#end-text').calendar({
		dynamic: true,
        first_day_sunday: false,
        inactive_future: false,
        value: '',
        value_input: '#end-value',
        days: ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"],
        on_select: function(year, month, day) {
            
        }
    });
	
	defaultDate();
	
	$("#submit").click(function(event) {
		event.preventDefault();		
		// refresh all chart this user is looking at
		var s = $( "#start-value" ).val();
		var e = $( "#end-value" ).val();
		ws.send(JSON.stringify({msgType: "update", start: s, end: e}));

		this.blur();
	});
	
	// click buttons to watch charts
	$("#incident").click(function(event) {
		event.preventDefault();
		showAndWatch(charts1, ws);
	});
	$("#people").click(function(event) {
		event.preventDefault();
		showAndWatch(charts2, ws);
	});
	$("#report").click(function(event) {
		event.preventDefault();
		showAndWatch(charts3, ws);
	
	});
	$("#resolution").click(function(event) {
		event.preventDefault();
		showAndWatch(charts4, ws);
	});
	
});

function showAndWatch(charts, ws) {
	$(".chart").hide();
	ws.send(JSON.stringify({msgType: "unwatch", chart: ""}));
	
	for (var i in charts) {
		$("#"+charts[i]).show();
		ws.send(JSON.stringify({msgType: "watch", chart: charts[i]}));
	}
	defaultDate();
}

function makeGradeGraph( grade ) {
	var grades = grade.split(",");
	by_grade_graph = '[{ "balloonText": "<b>Male: [[value]]</b>", "fillColorsField": "color", "fillAlphas": 0.9,"lineAlpha": 0.2, "type": "column", "title": "Grade ' + grades[0] + ' Male","valueField": "male_1","labelText": "[[value]]"},{ "balloonText": "<b>Female: [[value]]</b>", "fillColorsField": "color", "fillAlphas": 0.9,"lineAlpha": 0.2, "type": "column", "title": "Grade ' + grades[0] + ' Female","valueField": "female_1","labelText": "[[value]]"},{"balloonText": "","type": "column","fillAlphas": 0,"lineAlpha": 0,"visibleInLegend": false,"valueField": "dummy","labelText": "Grade ' + grades[0] + '","labelPosition": "top"},';
	var i = 1;
	while (i < grades.length) {
		by_grade_graph = by_grade_graph + '{ "balloonText": "<b>Male: [[value]]</b>", "fillColorsField": "color", "fillAlphas": 0.9,"lineAlpha": 0.2, "type": "column", "newStack": true, "title": "Grade ' + grades[i] + ' Male","valueField": "male_' + (i+1) + '","labelText": "[[value]]"},{ "balloonText": "<b>Female: [[value]]</b>", "fillColorsField": "color", "fillAlphas": 0.9,"lineAlpha": 0.2, "type": "column", "title": "Grade ' + grades[i] + ' Female","valueField": "female_' + (i+1) + '","labelText": "[[value]]"},{"balloonText": "","type": "column","fillAlphas": 0,"lineAlpha": 0,"visibleInLegend": false,"valueField": "dummy","labelText": "Grade ' + grades[i] + '","labelPosition": "top"},';
		i++;
	}
	by_grade_graph = by_grade_graph + ' { "balloonText": "<b>[[value]]</b>", "fillColorsField": "color", "fillAlphas": 0.9,"lineAlpha": 0.2, "type": "column", "newStack": true, "title": "Staff","valueField": "staff","labelText": "Staff","labelPosition": "top"},{ "balloonText": "<b>[[value]]</b>", "fillColorsField": "color", "fillAlphas": 0.9,"lineAlpha": 0.2, "type": "column", "newStack": true, "title": "Unknown","valueField": "unknown","labelText": "Unknown","labelPosition": "top"},{ "id": "show_all", "title": "Show All"}]';
}


function defaultDate() {
	var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth()+1; //January is 0!
    var monthNames = ["January", "February", "March", "April", "May", "June",
                      "July", "August", "September", "October", "November", "December"
                    ];
    var month = monthNames[today.getMonth()];
    var yyyy = today.getFullYear();
    
    var date_text = month + ' ' + dd + ', ' + yyyy;

    if(dd<10){
        dd='0'+dd
    } 
    if(mm<10){
        mm='0'+mm
    } 
    var date_value = yyyy + '-' + mm + '-' + dd;

	$("#start-value").val("2014-09-01");
	$("#start-text").val("September 1, 2014");
	$("#end-value").val(date_value);
	$("#end-text").val(date_text);


}


//handle legend clicks
function handleLegendClick( graph ) {
	var chart = graph.chart;
	for( var i = 0; i < chart.graphs.length; i++ ) {
	if ( graph.id == chart.graphs[i].id ){
		if ( graph.id == "show_all" ){
			for( var j = 0; j < chart.graphs.length - 1; j++ ) {
				chart.showGraph(chart.graphs[j]);
			}
		}
			chart.showGraph(chart.graphs[i]);
		}
	else
			chart.hideGraph(chart.graphs[i]);
	}

	// return false so that default action is canceled
	return false;
}



// make chart
function makeChart(chart, data) {
	var title = title_map[chart];
	if (chart == "location_day_1" || chart == "location_week_1") {
		makeLineChart(chart, title, "YYYY-MM-DD", location_line_graph, data);
	}
	else if (chart == "bias_day_1" || chart == "bias_week_1") {
		makeLineChart(chart, title, "YYYY-MM-DD", bias_line_graph, data);
	}
	else if (chart == "harassment_day_1" || chart == "harassment_week_1") {
		makeLineChart(chart, title, "YYYY-MM-DD", harassment_line_graph, data);
	}
	else if (chart == "location_month_1") { 
		makeLineChart(chart, title, "YYYY-MM", location_line_graph, data);
	}
	else if (chart == "harassment_month_1") {
		makeLineChart(chart, title, "YYYY-MM", harassment_line_graph, data);
	}
	else if (chart == "bias_month_1") {
		makeLineChart(chart, title, "YYYY-MM", bias_line_graph, data);
	}
	else if (chart == "location_bar_1" || chart == "harassment_bar_1" || chart == "bias_bar_1") {
		makeRotateBarChart(chart, title, "No. of Reports", rotate_bar_graph, data);
	}
	else if (chart == "by_gender_2") {
		makeBarChart(chart, title, "No. of Incidents", by_gender_graph, data);
	}
	else if (chart == "by_grade_2") {
		makeStackedBarChart(chart, title, "No. of Incidents", by_grade_graph, data);
	}
	else if (chart == "gender_mix_2") {
		makeRadarChart(chart, title, radar_graph, data);
	}
	else if (chart == "reporter_relation_2" || chart == "report_age_3" || chart == "evidence_restriction_3") {
		makeBarChart(chart, title, "No. of Reports", bar_graph, data);
	}
	else if (chart == "report_status_3") {
		makeRotatedAxisBarChart(chart, title, "No. of Reports", bar_graph, data);
	}
	else if (chart == "evidence_3") {
		makeBarChart(chart, title, "No. of Reports with Files/Links", bar_graph, data);
	}
	else if (chart == "supportive_4" || chart == "punitive_4" || chart == "restorative_4")  {
		makeRotatedAxisBarChart(chart, title, "No. of People", bar_graph, data);
	}

}

// make line chart 
function makeLineChart(id, title, date_format, graphs, data) {
	AmCharts.makeChart(id,{
		"type": "serial",
		"categoryField": "date",
		"dataDateFormat": date_format,
		"fontSize": 12,
		"fontFamily": "Open Sans",
		
		"categoryAxis": {
			"parseDates": true
		},
		"chartScrollbar": {},
		"legend": {
			 "horizontalGap": 10,
			 "maxColumns": 1,
			 "position": "right",
			 "markerSize": 10,
			 "clickMarker": handleLegendClick,
 				 "clickLabel": handleLegendClick
		},
		"chartCursor": {
		 	"cursorColor": "#17b99b",
			"categoryBalloonAlpha": 0.5,
			"graphBulletAlpha": 0.5,
			"color": "#757679"
        },
		
		"titles": [{
		 "text": title,
		     "size": 15,
		     "bold": false
		 }],
		 
		"valueAxes": [{
			 "position": "left",
			 "title": "No. of Reports",
			 "titleBold": false
			}],
			
		"colors": [
			"#17b99b",
			"#f99d1c",
			"#e5613f",
			"#8fc345",
			"#965ba5",
			"#22add1",
			"#3dac49",
			"#f0c514",
			"#757679"
		],
		
		"graphs": eval(graphs),
		
		"dataProvider": eval(data)
	});
}

// make rotate bar chart
function makeRotateBarChart(id, title, axis_title, graphs, data) {
	AmCharts.makeChart(id, {
		"type": "serial",
		"categoryField": "category",
		"rotate": true,
		"fontFamily": "Open Sans",
		"categoryAxis": {
			"gridPosition": "start",
		},
		"trendLines": [],
		"guides": [],
		"valueAxes": [
			{
				"position": "left",
				"title": axis_title,
				"titleBold": false
				}
		],
		"allLabels": [],
		"balloon": {},
		"titles": [
			{
				"id": "Title-1",
				"size": 15,
				"text": title,
				"bold": false
			}
		],
		"graphs": eval(graphs),
		"dataProvider": eval(data)
	});
}

//make bar chart with rotated axis lable
function makeRotatedAxisBarChart(id, title, axis_title, graphs, data) {
	AmCharts.makeChart(id, {
		"type": "serial",
		"categoryField": "category",
		"fontFamily": "Open Sans",
		"categoryAxis": {
			"gridPosition": "start",
			"labelRotation": 45
		},
		"trendLines": [],
		"guides": [],
		"valueAxes": [
			{
				"position": "left",
				"title": axis_title,
				"titleBold": false
				}
		],
		"allLabels": [],
		"balloon": {},
		"titles": [
			{
				"size": 15,
				"text": title,
				"bold": false
			}
		],
		"graphs": eval(graphs),
		"dataProvider": eval(data)
	});
}

//make stacked bar chart
function makeStackedBarChart(id, title, axis_title, graphs, data) {
	AmCharts.makeChart(id, {
		"type": "serial",
		"categoryField": "category",
		"fontFamily": "Open Sans",
		"categoryAxis": {
			"gridPosition": "start",
		},
		"trendLines": [],
		"guides": [],
		"valueAxes": [
			{
	            "stackType": "regular",
				"position": "left",
				"title": axis_title,
				"titleBold": false
				}
		],
		"legend": {
			"horizontalGap": 10,
			"maxColumns": 1,
			"position": "right",
			"useGraphSettings": false,
			"markerSize": 5,
			"clickMarker": handleLegendClick,
 			"clickLabel": handleLegendClick
		},
		"allLabels": [],
		"balloon": {},
		"titles": [
			{
				"size": 15,
				"text": title,
				"bold": false
			}
		],
		"colors": [
		           "#17b99b",
		            "#f99d1c",
		            "#000000",
		            "#17b99b",
		            "#f99d1c",
		            "#000000",
		            "#17b99b",
		            "#f99d1c",
		            "#000000",
		            "#965ba5",
		            "#22add1"
				],
		"graphs": eval(graphs),
		"dataProvider": eval(data)
	});
}

//make bar chart with regular axis label
function makeBarChart(id, title, axis_title, graphs, data) {
	AmCharts.makeChart(id, {
		"type": "serial",
		"categoryField": "category",
		"fontFamily": "Open Sans",
		"categoryAxis": {
			"gridPosition": "start",
		},
		"trendLines": [],
		"guides": [],
		"valueAxes": [
			{
				"position": "left",
				"title": axis_title,
				"titleBold": false
				}
		],
		"allLabels": [],
		"balloon": {},
		"titles": [
			{
				"size": 15,
				"text": title,
				"bold": false
			}
		],
		"colors": [
					"#17b99b",
					"#f99d1c",
					"#e5613f",
					"#8fc345",
					"#965ba5",
					"#22add1",
					"#3dac49",
					"#f0c514",
					"#757679"
				],
		"graphs": eval(graphs),
		"dataProvider": eval(data)
	});
}

// make radar chart
function makeRadarChart(id, title, graphs, data) {
	AmCharts.makeChart(id,	{
		"type": "radar",
		"categoryField": "category",
		"startDuration": 2,
		"fontFamily": "Open Sans",
		
		"colors": [
			"#17b99b"
		],
		"guides": [],
		"valueAxes": [
			{
				"axisTitleOffset": 20,
				"gridType": "circles",
				"minimum": 0,
				"axisAlpha": 0.15,
				"dashLength": 3
			}
		],
		"allLabels": [],
		"balloon": {},
		"titles": [{
			 "text": title,
			 "size": 15,
		     "bold": false
		}],
		
		"graphs": eval(graphs),
				
		"dataProvider": eval(data)
	});
}

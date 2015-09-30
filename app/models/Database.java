package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import play.Play;

import com.orientechnologies.orient.jdbc.OrientJdbcConnection;

/**
 * Open a connection to the orientdb database
 * Execute query and return data in json format that could directly used in amcharts
 * */
public class Database {
	private static Connection conn = null;
	
	// connects to the database
	public static void connect() {
		String dbDriver = Play.application().configuration().getString("db.default.driver");
		String dbURL = Play.application().configuration().getString("db.default.url");
		String dbUser = Play.application().configuration().getString("db.default.username");
		String dbPassword = Play.application().configuration().getString("db.default.password");
				
		try {
			Class.forName(dbDriver);
			conn = (OrientJdbcConnection) DriverManager.getConnection(dbURL, dbUser, dbPassword);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	// get grades from the table OrgInfo
	public static String getGrades(String org) {
		Statement stmt = null;
		ResultSet rs = null;
		StringBuilder grades = new StringBuilder();
		if (conn == null) {
			connect();
		}
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select number_grades, g1, g2, g3, g4, g5, g6 from OrgInfo where org=" + org);
			int numberOfGrades = Integer.parseInt(rs.getString("number_grades"));

			for (int i = 0; i < numberOfGrades; i ++) {
				grades.append(rs.getString("g" + (i+1)));
				if (i != numberOfGrades - 1) {
					grades.append(",");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return grades.toString();
	}
	
	// depend on which chart it is, form a query, query the database and get result, then format the result into json
	public static String getJson(String chart, String org, String start_date, String end_date) {
		String json = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		if (conn == null) {
			connect();
		}
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		try {
			// decide how many grades this org has
			HashMap<String, String> tableChartRef = Parameters.TABLE_CHART;
			
			String[] para = Parameters.TABLE_INFO.get(tableChartRef.get(chart));
			
			// get result
			if (chart.equals("location_month_1") || chart.equals("harassment_month_1") || chart.equals("bias_month_1")) {
				rs = stmt.executeQuery(QueryGenerator.groupByMonthQuery(para, tableChartRef.get(chart), org, start_date, end_date));
				json = FormatJson.linechartFormat(rs, para);
			}
			else if (chart.equals("location_day_1") || chart.equals("harassment_day_1") || chart.equals("bias_day_1")) {
				rs = stmt.executeQuery(QueryGenerator.groupAndSumQuery(para, tableChartRef.get(chart), "inc_date", org, start_date, end_date));
				json = FormatJson.linechartFormat(rs, para);
			}
			else if (chart.equals("location_week_1") || chart.equals("harassment_week_1") || chart.equals("bias_week_1")) {
				rs = stmt.executeQuery(QueryGenerator.groupAndSumQuery(para, tableChartRef.get(chart), "week_start", org, start_date, end_date));
				json = FormatJson.linechartFormat(rs, para);
			}
			else if (chart.equals("bias_bar_1") || chart.equals("harassment_bar_1") || chart.equals("location_bar_1") ||
					chart.equals("reporter_relation_2") || chart.equals("evidence_3") || chart.equals("evidence_restriction_3") ||
					chart.equals("supportive_4") || chart.equals("punitive_4") || chart.equals("restorative_4")) {
				rs = stmt.executeQuery(QueryGenerator.sumQuery(para, tableChartRef.get(chart), org, start_date, end_date));
				json = FormatJson.barchartFormat(rs, para);
			}
			else if (chart.equals("report_age_3") || chart.equals("report_status_3")) {
				rs = stmt.executeQuery(QueryGenerator.maxDateQuery(para, tableChartRef.get(chart), org));
				json = FormatJson.barchartFormat(rs, para);
			}
			else if (chart.equals("by_gender_2")) {
				rs = stmt.executeQuery(QueryGenerator.genderQuery(org, start_date, end_date));
				json = FormatJson.genderFormat(rs);
			}
			else if (chart.equals("by_grade_2")) {
				String[] grades = getGrades(org).split(",");
				rs = stmt.executeQuery(QueryGenerator.gradeQuery(org, start_date, end_date));
				json = FormatJson.gradeFormat(rs, grades);
			}
			else if (chart.equals("gender_mix_2")) {
				rs = stmt.executeQuery(QueryGenerator.sumQuery(para, tableChartRef.get(chart), org, start_date, end_date));
				json = FormatJson.radarchartFormat(rs, para);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json;
		
	}
	


}

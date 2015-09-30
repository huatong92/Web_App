package models;

/**
 * To generate queries for different charts
 * */
public class QueryGenerator {
	
	// for chart location_bar_1, harassment_bar_1, bias_bar_1, reporter_relation_2, gender_mix_2,
	// evidence_3, evidence_restriction_3, supportive_4, punitive_4, restorative_4
	public static String sumQuery(String[] para, String table_name, String org, String start_date, String end_date) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		for (int i = 0; i < para.length; i ++) {
			sb.append("sum(" + para[i] + ") as " + para[i]);
			if (i != para.length - 1) {
				sb.append(", ");
			}
		}
		sb.append(" from " + table_name + " where inc_date >= " + start_date + " and inc_date <= " + end_date +
				" and org = " + org);
		return sb.toString();
	}
	
	// for location_week_1, harassment_week_1, bias_week_1 (week_start)
	// for location_day_1, harassment_day_1, bias_day_1 (inc_date), 
	public static String groupAndSumQuery(String[] para, String table_name, String groupby, String org, 
			String start_date, String end_date) {
		StringBuilder sb = new StringBuilder();
		
		if (groupby.equals("week_start")) {
			sb.append("select week_start.format('yyyy-MM-dd') as date, ");
		} else if (groupby.equals("inc_date")) { 
			sb.append("select inc_date.format('yyyy-MM-dd') as date, ");
		}
		
		for (int i = 0; i < para.length; i ++) {
			sb.append("sum(" + para[i] + ") as " + para[i]);
			if (i != para.length - 1) {
				sb.append(", ");
			}
		}
		sb.append(" from " + table_name + " where inc_date >= " + start_date + " and inc_date <= " + end_date +
				" and org = " + org);
		sb.append(" group by " + groupby);
		
		return sb.toString();
		
	}
	
	// for chart location_month_1, harassment_month_1, bias_month_1
	public static String groupByMonthQuery(String[] para, String table_name, String org, String start_date, String end_date) {
		StringBuilder sb = new StringBuilder();
		sb.append("select date, ");
		for (int i = 0; i < para.length; i ++) {
			sb.append("sum(" + para[i] + ") as " + para[i]);
			if (i != para.length - 1) {
				sb.append(", ");
			}
		}
		sb.append(" from (select inc_date.format('yyyy-MM') as date, ");
		for (int i = 0; i < para.length; i ++) {
			sb.append(para[i]);
			if (i != para.length - 1) {
				sb.append(", ");
			}
		}
		sb.append(" from " + table_name + " where inc_date >= " + start_date + " and inc_date <= " + end_date +
				" and org = " + org + ")");
		sb.append(" group by date order by date");
		
		return sb.toString();
	}
	
	// for chart report_age_3, report_status_3
	public static String maxDateQuery(String[] para, String table_name, String org) {
		StringBuilder sb = new StringBuilder();
		sb.append("select max(inc_date) as date, ");
		for (int i = 0; i < para.length; i ++) {
			sb.append(para[i]);
			if (i != para.length - 1) {
				sb.append(", ");
			}
		}
		sb.append(" from " + table_name + " where org = " + org );
		return sb.toString();
	}
	
	// for chart by_grade_2
	public static String gradeQuery(String org, String start_date, String end_date){
		StringBuilder sb = new StringBuilder();
		sb.append("select involvement, gender, grade, staff, count(inc_date) as count from PeopleByGradeAndGender");
		sb.append(" where inc_date >= " + start_date + " and inc_date <= " + end_date + " and org = " + org);
		sb.append(" group by involvement, gender, grade, staff");
		return sb.toString();
	}
	
	public static String genderQuery(String org, String start_date, String end_date) {
		StringBuilder sb = new StringBuilder();
		sb.append("select involvement, gender, count(inc_date) as count from PeopleByGradeAndGender");
		sb.append(" where inc_date >= " + start_date + " and inc_date <= " + end_date + " and org = " + org);
		sb.append(" group by involvement, gender");
		return sb.toString();
	}

}

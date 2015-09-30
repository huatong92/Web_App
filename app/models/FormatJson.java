package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FormatJson {
	/* The following methods are to format the result set from quering the database 
	 * into json format to fit amchart's data format
	 * */
		
	// location_month_1, location_week_1, location_day_1,
	// harassment_month_1, harassment_week_1, harassment_day_1,
	// bias_month_1, bias_week_1, bias_day_1,
	@SuppressWarnings("unchecked")
	public static String linechartFormat(ResultSet rs, String[] para) {
		JSONArray list = new JSONArray();
		try {
			while (rs.next()) {
				JSONObject json = new JSONObject();
				json.put("date", rs.getString("date"));
				for (int i = 0; i < para.length; i ++) {
					try {
						json.put(para[i], Integer.parseInt(rs.getString(para[i])));
					} catch (SQLException | NumberFormatException ex) {
						continue;
					}
				}
				list.add(json);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list.toJSONString();
	}
	
	// bias_bar_1, harassment_bar_1, location_bar_1, reporter_relation_2, report_age_3, evidence_3, 
	// evidence_restriction_3, report_status_3, supportive_4, punitive_4, restorative_4
	@SuppressWarnings("unchecked")
	public static String barchartFormat(ResultSet rs, String[] para) {
		JSONArray list = new JSONArray();
		for (int i = 0; i < para.length; i ++) {
			JSONObject json = new JSONObject();
			json.put("category", formatCategory(para[i]));
			try {
				json.put("visits", Integer.parseInt(rs.getString(para[i])));
			} catch (SQLException | NumberFormatException ex) {
				json.put("visits", 0);;
			} 
			json.put("color", Parameters.COLORS[i%9]);
			list.add(json);
		}

		return list.toJSONString();
	}
	
	
	// by_gender_2
	@SuppressWarnings("unchecked")
	public static String genderFormat(ResultSet rs) {
		JSONArray list = new JSONArray();
		JSONObject taJson = new JSONObject();
		JSONObject agJson = new JSONObject();
		JSONObject rpJson = new JSONObject();
		taJson.put("category", "Target");
		agJson.put("category", "Aggressor");
		rpJson.put("category", "Reporter");
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(0, "female");
		map.put(1, "male");
		map.put(2, "nr");
		// check if a category is filled
		boolean[][] check = new boolean[3][3];
		
		String category;
		int count, gender ;
		
		try{
			while (rs.next()) {
				category = rs.getString("involvement");
				gender = (rs.getString("gender") == null) ? 2 : Integer.parseInt(rs.getString("gender"));
				count = Integer.parseInt(rs.getString("count"));
				if (category.equals("target")) {
					taJson.put(map.get(gender), count);
					check[0][gender] = true;
				} else if (category.equals("aggressor")) {
					agJson.put(map.get(gender), count);
					check[1][gender] = true;;
				} else if (category.equals("reporter")) {
					rpJson.put(map.get(gender), count);
					check[2][gender] = true;
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// fill in 0's 
		for (int i = 0; i < 3; i ++) {
			for (int j = 0; j < 3; j ++) {
				if (!check[i][j]) {
					if (i == 0) {
						taJson.put(map.get(j), 0);
					} else if (i == 1) {
						agJson.put(map.get(j), 0);
					} else if (i == 2) {
						rpJson.put(map.get(j), 0);
					}
				}
			}
		}
		
		list.add(taJson);
		list.add(agJson);
		list.add(rpJson);
		return list.toJSONString();
		
	}
	
	// by_gender_2
	@SuppressWarnings("unchecked")
	public static String gradeFormat(ResultSet rs, String[] grades) {
		JSONArray list = new JSONArray();
		JSONObject taJson = new JSONObject();
		JSONObject agJson = new JSONObject();
		JSONObject rpJson = new JSONObject();
		taJson.put("category", "Target");
		agJson.put("category", "Aggressor");
		rpJson.put("category", "Reporter");
		taJson.put("dummy", 0);
		agJson.put("dummy", 0);
		rpJson.put("dummy", 0);
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(0, "female");
		map.put(1, "male");
		map.put(2, "nr");
		
		int gradesNo = grades.length;
		// check if a category is filled
		boolean[][] check = new boolean[3][2*gradesNo + 2];
		// grade_female, grade_male, staff, unknown
		
		String category;
		int count, gender, grade, staff;
		
		try{
			while (rs.next()) {
				category = rs.getString("involvement");
				gender = (rs.getString("gender") == null) ? 2 : Integer.parseInt(rs.getString("gender"));
				grade = (rs.getString("grade") == null) ? 0 : Integer.parseInt(rs.getString("grade")) - Integer.parseInt(grades[0]) + 1;
				staff = Integer.parseInt(rs.getString("staff"));
				count = Integer.parseInt(rs.getString("count"));
				if (category.equals("target")) {
					if (gender == 2 || grade == 0) {
						if (staff == 0){
							taJson.put("unknown", count);
							check[0][2*gradesNo + 1] = true;

						} else {
							taJson.put("staff", count);
							check[0][2*gradesNo] = true;
						}
					} else {
						taJson.put(map.get(gender)+"_"+ (grade), count);
						check[0][grade*(gender+1) - 1] = true;
					}
					
				} else if (category.equals("aggressor")) {
					if (gender == 2 || grade == 0) {
						if (staff == 0){
							agJson.put("unknown", count);
							check[1][2*gradesNo + 1] = true;

						} else {
							agJson.put("staff", count);
							check[1][2*gradesNo] = true;
						}
					} else {
						agJson.put(map.get(gender)+"_"+ (grade), count);
						check[1][grade*(gender+1) - 1] = true;
					}
				} else if (category.equals("reporter")) {
					if (gender == 2 || grade == 0) {
						if (staff == 0){
							rpJson.put("unknown", count);
							check[2][2*gradesNo + 1] = true;

						} else {
							rpJson.put("staff", count);
							check[2][2*gradesNo] = true;
						}
					} else {
						rpJson.put(map.get(gender)+"_"+ (grade), count);
						check[2][grade*(gender+1) - 1] = true;
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// fill in 0's 
		for (int i = 0; i < 3; i ++) {
			for (int j = 0; j < 2*gradesNo + 2; j ++) {
				if (!check[i][j]) {
					if (i == 0) {
						if (j == 2*gradesNo) {
							taJson.put("staff", 0);
						} else if (j == 2*gradesNo + 1) {
							taJson.put("unknown", 0);
						} else {
							taJson.put(map.get(j%2)+"_"+(j/2+1), 0);
						}
					} else if (i == 1) {
						if (j == 2*gradesNo) {
							agJson.put("staff", 0);
						} else if (j == 2*gradesNo + 1) {
							agJson.put("unknown", 0);
						} else {
							agJson.put(map.get(j%2)+"_"+(j/2+1), 0);
						}
					} else if (i == 2) {
						if (j == 2*gradesNo) {
							rpJson.put("staff", 0);
						} else if (j == 2*gradesNo + 1) {
							rpJson.put("unknown", 0);
						} else {
							rpJson.put(map.get(j%2)+"_"+(j/2+1), 0);
						}
					}
				}
			}
		}
		
		list.add(taJson);
		list.add(agJson);
		list.add(rpJson);
		return list.toJSONString();
		
	}
	
//	
//	// by_gender_2, by_grade_2
//	@SuppressWarnings("unchecked")
//	public static String stackedBarchartFormat(ResultSet rs, String[] para, boolean byGrade) {
//		JSONArray list = new JSONArray();
//		List<String> temp = new ArrayList<String>();
//		try {
//			while (rs.next()) {
//				JSONObject json = new JSONObject();
//				String category;
//				try {
//					category = rs.getString("category");
//				} catch (SQLException | NumberFormatException ex) {
//					continue;
//				}
//				json.put("category", formatCategory(category));
//				temp.add(category);
//				
//				for (int i = 0; i < para.length; i ++) {
//					try {
//						json.put(para[i], Integer.parseInt(rs.getString(para[i])));
//					} catch (SQLException | NumberFormatException ex) {
//						json.put(para[i], 0);
//					}
//				}
//				if (byGrade){
//					json.put("dummy", 0);
//				}
//				list.add(json);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		
//		
//		if (temp.size() < 3) {
//			if (!temp.contains("target")) {
//				JSONObject json = new JSONObject();
//				json.put("category", "Target");
//				for (int i = 0; i < para.length; i ++) {
//					json.put(para[i], 0);
//				}
//				list.add(json);
//			}
//			if (!temp.contains("aggressor")) {
//				JSONObject json = new JSONObject();
//				json.put("category", "Aggressor");
//				for (int i = 0; i < para.length; i ++) {
//					json.put(para[i], 0);
//				}
//				list.add(json);
//			}
//			if (!temp.contains("reporter")) {
//				JSONObject json = new JSONObject();
//				json.put("category", "Reporter");
//				for (int i = 0; i < para.length; i ++) {
//					json.put(para[i], 0);
//				}
//				list.add(json);
//			}
//			
//		}
//		return list.toJSONString();
//	}
	
	// gender_mix_2
	@SuppressWarnings("unchecked")
	public static String radarchartFormat(ResultSet rs, String[] para) {
		JSONArray list = new JSONArray();
		String[] names = new String[]{"Male/Female","Female/Female","Female/Male","Male/Male"};
		for (int i = 0; i < para.length; i ++) {
			JSONObject json = new JSONObject();
			json.put("category", names[i]);
			try {
				json.put("visits", Integer.parseInt(rs.getString(para[i])));
			} catch (SQLException | NumberFormatException ex) {
				json.put("visits", 0);
			} 
			list.add(json);
		}
		
		return list.toJSONString();
	}
	
	// change "string_like_this" to "String Like This"
	public static String formatCategory(String str) {
		if (str.equals("days_10")) {
			return "< 10";
		}
		else if (str.equals("days_10_20")) {
			return "10 - 20";
		}
		else if (str.equals("days_20_40")) {
			return "20 - 40";
		}
		else if (str.equals("days_40_80")) {
			return "40 - 80";
		}
		else if (str.equals("days_80")) {
			return "> 80 Days";
		}
		
		StringBuilder rs = new StringBuilder();
		if (str.contains("_")){
			String[] temp = str.split("_");
			for(String s : temp) {
				rs.append(Character.toUpperCase(s.charAt(0)));
				rs.append(s.substring(1) + " ");
			}
		} else {
			rs.append(Character.toUpperCase(str.charAt(0)));
			rs.append(str.substring(1));
		}
		
		return rs.toString().trim();
	}
}

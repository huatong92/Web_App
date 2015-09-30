package models;

import java.util.HashMap;

public class Parameters {
	public final static String[] COLORS = {
		"#17b99b",
		"#f99d1c",
		"#e5613f",
		"#8fc345",
		"#965ba5",
		"#22add1",
		"#3dac49",
		"#f0c514",
		"#757679"
	};


	public final static HashMap<String, String> TABLE_CHART= new HashMap<String, String>();
	static {
		TABLE_CHART.put("bias_month_1","IncidentByBias");
		TABLE_CHART.put("bias_week_1","IncidentByBias");
		TABLE_CHART.put("bias_day_1","IncidentByBias");
		TABLE_CHART.put("bias_bar_1","IncidentByBias");
		TABLE_CHART.put("location_month_1","IncidentByLocation");
		TABLE_CHART.put("location_week_1","IncidentByLocation");
		TABLE_CHART.put("location_day_1","IncidentByLocation");
		TABLE_CHART.put("location_bar_1","IncidentByLocation");
		TABLE_CHART.put("harassment_month_1","IncidentByHarassment");
		TABLE_CHART.put("harassment_week_1","IncidentByHarassment");
		TABLE_CHART.put("harassment_day_1","IncidentByHarassment");
		TABLE_CHART.put("harassment_bar_1","IncidentByHarassment");
		TABLE_CHART.put("by_gender_2", "PeopleByGradeAndGender");
		TABLE_CHART.put("by_grade_2", "PeopleByGradeAndGender");
		TABLE_CHART.put("reporter_relation_2", "ReporterRelation");
		TABLE_CHART.put("gender_mix_2", "GenderMix");
		TABLE_CHART.put("report_age_3", "ReportAge");
		TABLE_CHART.put("evidence_3", "Evidence");
		TABLE_CHART.put("evidence_restriction_3", "EvidenceRestriction");
		TABLE_CHART.put("report_status_3", "ReportStatus");
		TABLE_CHART.put("supportive_4", "Supportive");
		TABLE_CHART.put("restorative_4", "Restorative");
		TABLE_CHART.put("punitive_4", "Punitive");
	}
	
	
	public final static HashMap<String, String[]> TABLE_INFO = new HashMap<String, String[]>();
	static {
		TABLE_INFO.put("IncidentByBias", new String[]{"national_origin", "race", "weight", "religion",
			"disability", "gender", "sexual_orientation", "sexuality", "none_of_the_above", "other", "total_incidents"});
		TABLE_INFO.put("IncidentByLocation", new String[]{"on_school_property", "off_site_school_event",
				"off_school_property","on_school_bus","online_digital","other","total_incidents"});
		TABLE_INFO.put("IncidentByHarassment", new String[]{"physical", "verbal", "theft", "vandalism",
				"digital_threat_public", "digital_threat_private", "other", "total_incidents"});		
		TABLE_INFO.put("GenderMix", new String[]{"Tmale-Afemale", "Tfemale-Afemale", "Tfemale-Amale", "Tmale-Amale"});
		TABLE_INFO.put("ReporterRelation", new String[]{"I_was_the_target","I_participated", "I_saw_it", "someone_showed_me", "someone_told_me"});
		TABLE_INFO.put("ReportStatus", new String[]{"new", "under_review", "repetitive_reports", "non-bullying_incidents", "false_report", "closed", "invalid"});
		TABLE_INFO.put("Evidence", new String[]{"with_files","with_links","with_both","with_neither"});
		TABLE_INFO.put("EvidenceRestriction", new String[]{"explicit", "open", "restricted"});
		TABLE_INFO.put("ReportAge", new String[]{"days_10", "days_10_20", "days_20_40", "days_40_80", "days_80"});
		TABLE_INFO.put("Restorative", new String[]{"reflection_assignment", "student_activity", "peer_groups", "counseling", "class_meeting", 
				"modify_student_program/behavior_contract", "conflict_resolution", "peer_mediation", "teacher_student_mentoring", 
				"parent_outreach",  "community_service", "referral_to_outside_organization"});
		TABLE_INFO.put("Punitive", new String[]{"principle_office", "lunch_detention", "detention", "in_school_suspension", 
				"out_of_school_suspension", "expulsion", "parent_outreach", "parent_conference", "loss_of_privilege", 
				"removal_from_class_by_teacher", "suspension_from_after_school_program", "short-term_behavioral_progress_report", 
				"counselors_office"});
		TABLE_INFO.put("Supportive", new String[]{"peer_groups", "counseling", "conflict_resolution", "peer_mediation", "mentoring_program", 
				"parent_outreach", "referral_to_outside_organization", "other"});
		
				
	};

}

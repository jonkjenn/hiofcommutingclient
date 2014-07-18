package no.hiof.hiofcommuting.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class Study {
	
	private int studyId, departmentId, campusId, startingYear;
	private String studyName;

	public Study(JSONObject jo) {
		try {
			studyId = Integer.parseInt(jo.getString("study_id"));
			departmentId = Integer.parseInt(jo.getString("department_id"));
			campusId = Integer.parseInt(jo.getString("campus_id"));
			studyName = jo.getString("name_of_study");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString(){
		return studyName;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public int getCampusId() {
		return campusId;
	}

	public void setCampusId(int campusId) {
		this.campusId = campusId;
	}

	public int getStartingYear() {
		return startingYear;
	}

	public void setStartingYear(int startingYear) {
		this.startingYear = startingYear;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}
	
}

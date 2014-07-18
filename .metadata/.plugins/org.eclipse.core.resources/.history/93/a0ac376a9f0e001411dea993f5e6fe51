package bachelor.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class Department {
	
	private int departmentId, institutionId;
	private String departmentName;

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public int getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(int institutionId) {
		this.institutionId = institutionId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Department(JSONObject jo) {
		try {
			departmentId = Integer.parseInt(jo.getString("department_id"));
			institutionId = Integer.parseInt(jo.getString("institution_id"));
			departmentName = jo.getString("department_name");
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
		return departmentName;
	}

}

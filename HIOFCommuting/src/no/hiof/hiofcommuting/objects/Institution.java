package no.hiof.hiofcommuting.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class Institution {
	
	private int institutionId;
	private String institutionName;

	public int getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(int institutionId) {
		this.institutionId = institutionId;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public Institution(JSONObject jo) {
		try {
			institutionId = Integer.parseInt(jo.getString("institution_id"));
			institutionName = jo.getString("institution_name");
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
		return institutionName;
	}

}

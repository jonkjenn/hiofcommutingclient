package bachelor.objects;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable{
    private boolean car;
	private int userId;
	private int studyId;
    private int startingYear;
    private double lat;
    private double lon;
    private double distance;
	private String firstname;
	private String surname;
	private String institution;
	private String campus;
	private String department;
	private String study;
    private String photoUrl;
    private String imagePath;
	private String fbId;
    public static List<User> userList;
    public static boolean isUserListFiltered;

    // When creating ListView
    public User(String firstname, String department, String institution, double distance, String imagePath, String fbId){
        this.firstname = firstname;
        this.department = department;
        this.institution = institution;
        this.distance = distance;
        this.imagePath = imagePath;
        this.fbId = fbId;
    }

	public User(int userid, int studyid, String firstname, String surname, double lat, double lon, double distance,
			String institution, String campus, String department,
			String study, int startingYear, boolean car, String photoUrl, String fbId) {
		this.userId = userid;
		this.studyId = studyid;
		this.firstname = firstname;
		this.surname = surname;
		this.lat = lat;
		this.lon = lon;
		this.distance = distance;
		this.institution = institution;
		this.campus = campus;
		this.department = department;
		this.study = study;
		this.startingYear = startingYear;
		this.car = car;
		this.photoUrl = photoUrl;
		this.fbId = fbId;
        this.imagePath = "";
	}

    public boolean hasCar() {
        return car;
    }
    public int getUserid() {
        return userId;
    }
	public int getStudyid() {
		return studyId;
	}
    public int getStartingYear() {
        return startingYear;
    }

	public double getLat() {
		return lat;
	}
	public double getLon() {
		return lon;
	}
	public double getDistance() {
		return distance;
	}

    public String getFbId() {
        return fbId;
    }
    public String getFirstName() {
        return firstname;
    }
    public String getInstitution() {
        return institution;
    }
    public String getCampus() {
        return campus;
    }
    public String getDepartment() {
        return department;
    }
    public String getStudy() {
        return study;
    }
    public String getPhotoUrl() {
        return photoUrl;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setInstitution(String institusjon) {
        this.institution = institusjon;
    }
    public void setCampus(String campus) {
        this.campus = campus;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public void setStudy(String study) {
        this.study = study;
    }
    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }
}

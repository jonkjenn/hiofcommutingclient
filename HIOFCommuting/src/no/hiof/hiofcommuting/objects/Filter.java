package no.hiof.hiofcommuting.objects;

public class Filter {
	private int studyId;
	private double distance;
	private boolean institution, campus, department, study, startingYear, car;
    public static boolean isFilterSet;
    public static Filter currentFilter;

	public Filter(int studyId, double distance, boolean institution,
			boolean campus, boolean department, boolean study,
			boolean startingYear, boolean car) {
		this.studyId = studyId;
		this.distance = distance;
		this.institution = institution;
		this.campus = campus;
		this.department = department;
		this.study = study;
		this.startingYear = startingYear;
		this.car = car;
	}
	
	public int getStudyId() {
		return studyId;
	}
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public boolean isInstitution() {
		return institution;
	}
	public void setInstitution(boolean institution) {
		this.institution = institution;
	}
	public boolean isCampus() {
		return campus;
	}
	public void setCampus(boolean campus) {
		this.campus = campus;
	}
	public boolean isDepartment() {
		return department;
	}
	public void setDepartment(boolean department) {
		this.department = department;
	}
	public boolean isStudy() {
		return study;
	}
	public void setStudy(boolean study) {
		this.study = study;
	}
	public boolean isStartingYear() {
		return startingYear;
	}
	public void setStartingYear(boolean startingYear) {
		this.startingYear = startingYear;
	}
	public boolean isCar() {
		return car;
	}
	public void setCar(boolean car) {
		this.car = car;
	}

}

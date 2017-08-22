import java.util.ArrayList;

// Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
// Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
// Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
// Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
// Vestibulum commodo. Ut rhoncus gravida arcu.
public class City {

  private String cityName;
  private String myProvince;
  private ArrayList<District> districtList;

  public City() {
    cityName = "";
    myProvince = "";
    districtList = new ArrayList<District>();
  }

  public City(String cityName, String provinceName) {
    this.cityName = cityName;
    this.myProvince = provinceName;
    districtList = new ArrayList<District>();
  }

  public int getAddressLength() {
    return myProvince.length() + cityName.length();
  }

  public ArrayList<District> getDistrictListByDistrictNameLength(int length) {
    ArrayList<District> result = new ArrayList<District>();
    for (District district : districtList) {
      if (district.getDistrictName().length() == length) {
        result.add(district);
      }
    }
    return result;
  }

  public void addDistrict(String distric) {
    District district = new District(distric, cityName, myProvince);
    districtList.add(district);
  }

  public void setMyProvince(String cityName) {
    this.cityName = cityName;
  }

  public String getMyProvince() {
    return myProvince;
  }

  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  public ArrayList<District> getDistrictList() {
    return districtList;
  }

  public void setDistrictList(ArrayList<District> districtList) {
    this.districtList = districtList;
  }
}
import java.util.ArrayList;

// Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
// Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
// Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
// Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
// Vestibulum commodo. Ut rhoncus gravida arcu.
public class District
{
  private String districtName;
  private String myCity;
  private String myProvince;

  public District()
  {
    this.districtName="";
    this.myCity="";
    this.myProvince="";
  }

  public District(String districtName,String myCity,String myProvince)
  {
    this.districtName=districtName;
    this.myCity=myCity;
    this.myProvince=myProvince;
  }

  public int getAdderessLength()
  {
    return  myProvince.length()+myCity.length()+districtName.length();
  }
  public String getMyCity()
  {
    return myCity;
  }
  public void setMyCity(String myCity)
  {
    this.myCity = myCity;
  }
  public void setDistrictName(String districtName)
  {
    this.districtName = districtName;
  }
  public String getDistrictName()
  {
    return districtName;
  }
  public String getMyProvince()
  {
    return myProvince;
  }
  public void setMyProvince(String myProvince)
  {
    this.myProvince = myProvince;
  }




}

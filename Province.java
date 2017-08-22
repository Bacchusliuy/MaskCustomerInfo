import java.util.ArrayList;

// Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
// Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
// Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
// Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
// Vestibulum commodo. Ut rhoncus gravida arcu.
public class Province {

  private String provinceName;
  private ArrayList<City> citiesGoverned;


  public Province() {
    this.provinceName = "";
    this.citiesGoverned = new ArrayList<City>();
  }

  public Province(String provinceName) {
    this.provinceName = provinceName;
    citiesGoverned = new ArrayList<City>();
  }

  public int getAddresssLength() {
    return provinceName.length();
  }


  public ArrayList<City> getCitiesGovernedByCitynameLength(int length) {
    ArrayList<City> result = new ArrayList<City>();
    for (City city : citiesGoverned) {
      if (city.getCityName().length() == length) {
        result.add(city);
      }
    }
    return result;
  }

  public void addCity(String cityName) {
    City city = new City(cityName, provinceName);
    citiesGoverned.add(city);
  }

  public void addDistrict(String distric, String cityName) {
    District district = new District(distric, cityName, provinceName);
    for (City c : citiesGoverned) {
      City city = c;
      if (city.getCityName().equals(cityName)) {
        city.addDistrict(distric);
      }
    }
  }

  public void setProvinceName(String provinceName) {
    this.provinceName = provinceName;
  }

  public String getProvinceName() {
    return provinceName;
  }

  public void setCitiesGoverned(ArrayList<City> citiesGoverned) {
    this.citiesGoverned = citiesGoverned;
  }

  public ArrayList<City> getCitiesGoverned() {
    return citiesGoverned;
  }
}

import java.util.ArrayList;
import java.io.*;

// Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
// Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
// Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
// Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
// Vestibulum commodo. Ut rhoncus gravida arcu.
public class AddressDataManager
{
  private ArrayList<Province> allProvince;

  public AddressDataManager()
  {
    allProvince=new ArrayList<Province>();
  }


  public ArrayList<Province> getProvinces()
  {
    //int provinceNum=0;
    Province currentProvince=new Province("blank");
    City currentCity=new City("blank","blank");

    try {
      File file=new File("D:/Project_yl/CustomerInfoMask/src/address-data.txt");
      if(file.isFile() && file.exists()){ //判断文件是否存在
        InputStreamReader read = new InputStreamReader(
            new FileInputStream(file),"UTF-8");//考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        while((lineTxt = bufferedReader.readLine()) != null)
        {
          if(lineTxt.length()>0) {
            if (lineTxt.charAt(0)!='　') {
              allProvince.add(currentProvince);
              String provinceName = lineTxt.substring(11, lineTxt.length());
              currentProvince = new Province(provinceName);
            } else if (lineTxt.charAt(1) != '　') {
              String cityName = lineTxt.substring(17, lineTxt.length());
              currentCity = new City(cityName,currentProvince.getProvinceName());
              currentProvince.addCity(cityName);
            } else if (lineTxt.charAt(2) != '　') {
              String districtName = lineTxt.substring(15, lineTxt.length());
              currentProvince.addDistrict(districtName, currentCity.getCityName());
            }
          }
        }
        read.close();
      }else{
        System.out.println("找不到指定的文件");
      }
    } catch (Exception e) {
      System.out.println("读取文件内容出错");
      e.printStackTrace();
    }
       allProvince.add(currentProvince);
       allProvince.remove(0);
  return allProvince;
  }

  public static void main(String[] args)
{
  AddressDataManager adm=new AddressDataManager();
  ArrayList<Province> list=new ArrayList<Province>();
  list=adm.getProvinces();

  for(Province p:list)
  {
    System.out.println(p.getProvinceName().length()+":"+p.getProvinceName());
    for(City c:p.getCitiesGoverned())
    {
      System.out.println(c.getCityName().length()+":"+c.getCityName());
      for(District d:c.getDistrictList())
      {
        System.out.println(d.getDistrictName().length()+":"+d.getDistrictName());
      }
    }
  }
}


}

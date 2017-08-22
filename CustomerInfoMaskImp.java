import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.omg.PortableServer.POA;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerInfoMaskImp implements CustomerInfoMask {

    //字符串信息中包含的省信息
    private String provinceName;
    //匿名用省信息
    private String makebelieveProvinceName;
    //字符串中包含的城市信息
    private String cityName;
    //匿名用城市信息
    private String makebelieveCityName;
    //字符串中包含的区信息
    private String districtName;
    //匿名用区信息
    private String makebelieveDistrictName;
    //是否包含地址信息
    private Boolean containsStreetInfo;
    //取得所有省市区信息数据
    private ArrayList<Province> allProvince;
    private Boolean processed;


    public CustomerInfoMaskImp() {
        this.provinceName = "";
        this.cityName = "";
        this.districtName = "";
        this.makebelieveProvinceName = "";
        this.makebelieveCityName = "";
        this.makebelieveDistrictName = "";
        containsStreetInfo = false;
        this.allProvince = new ArrayList<Province>();
       processed=false;
    }

    @Override
    public String maskCustomerInfo(String info) {
        String result = info;
        if (containsStreetInfo && verifyStreetDetailInfo(info)) {
// TEST           System.out.println("is streetinfo");
            result = maskStreatDetailInfo(info);
            processed=true;
        }
        if (verifyZipcode(info)) {
//TEST            System.out.println("is zipcode");
            result = maskZipCode(info);
            processed=true;
        }
        if (verifyPhoneNumber(info)) {
//TEST            System.out.println("is phonenumberinfo");
            result = maskPhoneNumber(info);
            processed=true;
        }
        if (verifyQQNum(info) && !verifyPhoneNumber(info)) {
//TEST             System.out.println("is qqnuminfo");
            result = maskQQNum(info);
            processed=true;
        }
        if (verifyEmile(info)) {
//TEST            System.out.println("is emileinfo");
            result = maskEmile(info);
           processed=true;
        }
        if (verifyUserId(info) && !verifyStreetDetailInfo(info) && !verifyZipcode(info)
            && !verifyPhoneNumber(info) && !verifyQQNum(info) && !verifyEmile(info)) {
//TEST             System.out.println("is userid");
            result = maskUserId(info);
             processed=true;
        }
        return result;
    }

    /**
     * 重新将变量初始化
     */
    private void reinitializeData() {
        this.provinceName = "";
        this.cityName = "";
        this.districtName = "";
        this.makebelieveProvinceName = "";
        this.makebelieveCityName = "";
        this.makebelieveDistrictName = "";
        containsStreetInfo = false;
        this.allProvince = new ArrayList<Province>();
         processed=false;
    }


    /**
     * @return processedCustomerInfo 对query进行初步的省市信息替换
     */
    private String harshProcessStreetInfo(String customerInfo) {

        //将已经包含的省市县随机替换
        String processedCustomerInfo = customerInfo;
        containsStreetInfo = containsStreetInfo(customerInfo);
        ArrayList<Province> provincesToMakebelieve = allProvince;
        if (containsStreetInfo) {
            // TEST          processed=true;

            if (isMunicipality(provinceName)) {//如果是直辖市，只在直辖市中考虑
                provincesToMakebelieve = new ArrayList<Province>();
                String[] municilality = {"上海市", "北京市", "天津市", "重庆市"};
                for (Province p : allProvince) {
                    for (int i = 0; i < municilality.length; i++) {
                        if (p.getProvinceName().equals(municilality[i]) && !p.getProvinceName()
                            .equals(provinceName)) {
                            provincesToMakebelieve.add(p);
                        }
                    }
                }

            } else {//如果不是直辖市，不用直辖市信息进行替换
                String[] municilality = {"上海市", "北京市", "天津市", "重庆市"};
                for (int i = 0; i < allProvince.size(); i++) {
                    for (int j = 0; j < municilality.length; j++) {
                        if (allProvince.get(i).getProvinceName().equals(municilality[j])) {
                            provincesToMakebelieve.remove(allProvince.get(i));
                        }
                    }
                }
            }

            if (!districtName.equals("") && !cityName.equals("") && !provinceName.equals("")) {

                int addressLength =
                    provinceName.length() + cityName.length() + districtName.length();
                Collections.shuffle(provincesToMakebelieve);
                Boolean breakfor=true;
                for (int i=0;i<provincesToMakebelieve.size() && breakfor;i++) {
                    Province p=provincesToMakebelieve.get(i);
                    Collections.shuffle(p.getCitiesGoverned());
                    for (int j=0;j<p.getCitiesGoverned().size();j++) {
                        City c=p.getCitiesGoverned().get(j);
                        Collections.shuffle(c.getDistrictList());
                        for (int q=0;q<c.getDistrictList().size();q++) {
                            District d=c.getDistrictList().get(q);
                            if (addressLength == d.getAdderessLength()) {
                                makebelieveDistrictName = d.getDistrictName();
                                makebelieveCityName = c.getCityName();
                                makebelieveProvinceName = provincesToMakebelieve.get(i).getProvinceName();
                                processedCustomerInfo = processedCustomerInfo
                                    .replace(provinceName, makebelieveProvinceName);
                                processedCustomerInfo = processedCustomerInfo
                                    .replace(cityName, makebelieveCityName);
                                processedCustomerInfo = processedCustomerInfo
                                    .replace(districtName, makebelieveDistrictName);
                                processedCustomerInfo = processedCustomerInfo
                                    .replace(provinceName.substring(0, provinceName.length() - 1),
                                        makebelieveProvinceName
                                            .substring(0, makebelieveProvinceName.length() - 1));
                                processedCustomerInfo = processedCustomerInfo
                                    .replace(cityName.substring(0, cityName.length() - 1),
                                        makebelieveCityName
                                            .substring(0, makebelieveCityName.length() - 1));
                                breakfor=false;
                            }
                        }
                    }
                }


            } else if (districtName.equals("") && !cityName.equals("") && !provinceName
                .equals("")) {
                int addressLength = provinceName.length() + cityName.length();
                Collections.shuffle(provincesToMakebelieve);

                Boolean breakfor=true;
                for (int i=0;i<provincesToMakebelieve.size() && breakfor;i++) {
                    Province p=provincesToMakebelieve.get(i);
                    Collections.shuffle(p.getCitiesGoverned());
                    for (int j=0;j<p.getCitiesGoverned().size() && breakfor;j++) {
                        City c=p.getCitiesGoverned().get(j);
                        if (isMunicipality(provinceName)) {//如果是直辖市用区级名代替市级名
                            Collections.shuffle(c.getDistrictList());
                            for (int q=0;q<c.getDistrictList().size() && breakfor;q++) {
                                District d=c.getDistrictList().get(q);
                                if (addressLength == d.getAdderessLength() - 3) {//减3是“市辖区”三个字的长度
                                    makebelieveCityName = d.getDistrictName();
                                    makebelieveProvinceName = p.getProvinceName();
                                    processedCustomerInfo = processedCustomerInfo
                                        .replace(provinceName, makebelieveProvinceName);
                                    processedCustomerInfo = processedCustomerInfo
                                        .replace(cityName, makebelieveCityName);
                                    processedCustomerInfo = processedCustomerInfo
                                        .replace(provinceName.substring(0, 2),
                                            makebelieveProvinceName.substring(0, 2));
                                    breakfor=false;
                                } else {//没有合适的“区”，则还是用“市辖区”
                                    makebelieveProvinceName = p.getProvinceName();
                                    makebelieveCityName = cityName;
                                    processedCustomerInfo = processedCustomerInfo
                                        .replace(provinceName, makebelieveProvinceName);
                                    processedCustomerInfo = processedCustomerInfo
                                        .replace(cityName, makebelieveCityName);
                                    processedCustomerInfo = processedCustomerInfo
                                        .replace(provinceName.substring(0, 2),
                                            makebelieveProvinceName.substring(0, 2));
                                }
                            }
                        } else {
                            if (addressLength == c.getAddressLength()) {

                                makebelieveCityName = c.getCityName();
                                makebelieveProvinceName = p.getProvinceName();
                                processedCustomerInfo = processedCustomerInfo
                                    .replace(provinceName, makebelieveProvinceName);
                                processedCustomerInfo = processedCustomerInfo
                                    .replace(cityName, makebelieveCityName);
                                processedCustomerInfo = processedCustomerInfo
                                    .replace(cityName.substring(0, cityName.length() - 1),
                                        makebelieveCityName
                                            .substring(0, makebelieveCityName.length() - 1));
                                processedCustomerInfo = processedCustomerInfo
                                    .replace(provinceName.substring(0, provinceName.length() - 1),
                                        makebelieveProvinceName
                                            .substring(0, makebelieveProvinceName.length() - 1));
                                breakfor=false;
                            }
                        }
                    }
                }

            } else if (districtName.equals("") && cityName.equals("") && !provinceName.equals("")) {

                int addressLength = provinceName.length();
                Collections.shuffle(provincesToMakebelieve);
                int index = 0;
                for (int i = 0; i < provincesToMakebelieve.size(); i++) {
                    if (provincesToMakebelieve.get(i).getProvinceName().equals(provinceName)) {
                        index = i;
                    }
                }
                provincesToMakebelieve.remove(index);

                for (Province p : provincesToMakebelieve) {
                    if (p.getAddresssLength() == addressLength) {
                        makebelieveProvinceName = p.getProvinceName();
                        processedCustomerInfo = processedCustomerInfo
                            .replace(provinceName, makebelieveProvinceName);
                        processedCustomerInfo = processedCustomerInfo
                            .replace(provinceName.substring(0, provinceName.length() - 1),
                                makebelieveProvinceName
                                    .substring(0, makebelieveProvinceName.length() - 1));
                        break;
                    }
                }

            } else if (provinceName.equals("") && !cityName.equals("") && !districtName
                .equals("")) {
                int addressLength = cityName.length() + districtName.length();
                Collections.shuffle(provincesToMakebelieve);
                Boolean breakfor=true;
                for (int i=0;i<provincesToMakebelieve.size() && breakfor;i++) {
                    Province p=provincesToMakebelieve.get(i);
                    Collections.shuffle(p.getCitiesGoverned());
                    for (int j=0;j<p.getCitiesGoverned().size() && breakfor;j++) {
                        City c=p.getCitiesGoverned().get(j);
                        Collections.shuffle(c.getDistrictList());
                        for (int q=0;q<c.getDistrictList().size() && breakfor;q++) {
                            District d=new District();
                            if ((d.getAdderessLength() - p.getProvinceName().length())
                                == addressLength) {
                                makebelieveCityName = c.getCityName();
                                makebelieveDistrictName = d.getDistrictName();
                                processedCustomerInfo = processedCustomerInfo
                                    .replace(cityName, makebelieveCityName);
                                processedCustomerInfo = processedCustomerInfo
                                    .replace(districtName, makebelieveDistrictName);
                                breakfor=false;
                            }
                        }
                    }
                }

            } else if (provinceName.equals("") && !cityName.equals("") && districtName.equals("")) {
                int addressLength = cityName.length();
                Collections.shuffle(provincesToMakebelieve);
                Boolean breakfor=true;
                for (int i=0;i<provincesToMakebelieve.size()&& breakfor;i++) {
                    Province p=provincesToMakebelieve.get(i);
                    Collections.shuffle(p.getCitiesGoverned());
                    for (int j=0;j<p.getCitiesGoverned().size() && breakfor;j++) {
                        City c=p.getCitiesGoverned().get(j);
                        if (addressLength == (c.getAddressLength() - p.getProvinceName()
                            .length())) {
                            makebelieveCityName = c.getCityName();
                            processedCustomerInfo = processedCustomerInfo
                                .replace(cityName, makebelieveCityName);
                            breakfor=false;
                        }
                    }
                }
            }


        }
        return processedCustomerInfo;
    }

    @Override
    public String processStr(String info) {

        if (info.equals("")) {
            return info;
        }

        String customerInfo = harshProcessStreetInfo(info);

        String processedStr = "";
        // 将：  。 、 ；替代为,
        String[] puctuation = {"。", ",", ";", "；", ":", "：", "、", " "};
        for (int i = 0; i < customerInfo.length(); i++) {
            for (int j = 0; j < puctuation.length; j++) {
                if (String.valueOf(customerInfo.charAt(i)).equals(puctuation[j])) {
                    StringBuilder sb = new StringBuilder(customerInfo);
                    sb.replace(i, i + 1, "，");
                    customerInfo = sb.toString();

                }
            }
        }
        String[] childStrings = customerInfo.split("，");
        //分别将子串进行处理
        for (int i = 0; i < childStrings.length; i++) {
            processedStr += maskCustomerInfo(childStrings[i]) + " ";
        }

        //去掉最末字符
        if (processedStr.length() >= 1) {
            processedStr = processedStr.substring(0, processedStr.length() - 1);
        }
        if(processed)
        {
            File file = new File("D:/Project_yl/CustomerInfoMask/src/masked-data-All.txt");
            try {
                if(!file.exists()) {
                    file.createNewFile();
                }
                RandomAccessFile rf=new RandomAccessFile(file,"rw");
                rf.seek(rf.length());
                rf.writeUTF(info+"\r\n");
                rf.writeUTF(processedStr+"\r\n");
                rf.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //保证字长相等
        if (processedStr.length() == info.length()) {
            this.reinitializeData();
            return processedStr;
        }
        while (processedStr.length() < info.length()) {
            processedStr += " ";
        }
        if (processedStr.length() == info.length()) {
            this.reinitializeData();
            return processedStr;
        } else {
            return processedStr + ": Info length is out of contral！！！！！" + "  before:" + info
                .length() + "  after:" + processedStr.length();
        }


    }

    /**
     * @return boolean 判断传入的信息是否是电话号码（座机或手机号）
     */
    private Boolean verifyPhoneNumber(String info) {

        Boolean result = false;
        //判断是否是座机号
        String LandLine = "^[0-9a-zA-Z_\\u4e00-\\u9fa5]*(\\d{2,4}-?)?\\d{7,8}+[0-9a-zA-Z_\\u4e00-\\u9fa5]*$";
        result = info.matches(LandLine);

        if (!result && info.length() == 11) {
            //判断是否是手机号
            String YD = "^[0-9a-zA-Z_\\u4e00-\\u9fa5]*[1]{1}(([3]{1}[4-9]{1})|([5]{1}[012789]{1})|([8]{1}[2378]{1})|([4]{1}[7]{1}))[0-9]{8}+[0-9a-zA-Z_\\u4e00-\\u9fa5]*$";
            String LT = "^[0-9a-zA-Z_\\u4e00-\\u9fa5]*[1]{1}(([3]{1}[0-2]{1})|([5]{1}[56]{1})|([8]{1}[56]{1}))[0-9]{8}+[0-9a-zA-Z_\\u4e00-\\u9fa5]*$";
            String DX = "^[0-9a-zA-Z_\\u4e00-\\u9fa5]*[1]{1}(([3]{1}[3]{1})|([5]{1}[3]{1})|([8]{1}[09]{1}))[0-9]{8}+[0-9a-zA-Z_\\u4e00-\\u9fa5]*$";
            result = info.matches(YD) || info.matches(LT) || info.matches(DX);
        }
        return result;

    }

    /**
     * @return Boolean 判断传入的信息是否是邮编
     */
    private Boolean verifyZipcode(String info) {
        Boolean result = false;
        String Zipcode = "^[0-9a-zA-Z\\u4e00-\\u9fa5]*[0-9]{6}[0-9a-zA-Z\\u4e00-\\u9fa5]*$";
        result = info.matches(Zipcode);
        return result;
    }

    /**
     * @return Boolean 判断传入的信息是否是邮箱
     */
    private Boolean verifyEmile(String info) {
        Boolean result = false;
        String Emile = "^[0-9a-zA-Z\\u4e00-\\u9fa5]*[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+[0-9a-zA-Z\\u4e00-\\u9fa5]*$";
        result = info.matches(Emile);
        return result;
    }

    /**
     * @return result 判断传入信息是否是QQ号
     */
    private Boolean verifyQQNum(String info) {
        Boolean result = false;
        String QQNum = "^[0-9a-zA-Z_+\\u4e00-\\u9fa5]*([1-9][0-9]{4,14})+[0-9a-zA-Z_\\u4e00-\\u9fa5]*$";
        result = info.matches(QQNum);
        return result;
    }

    /**
     * @return result 判断传入信息是否是用户ID
     */
    public Boolean verifyUserId(String info) {
        Boolean result = false;
        String userId = "^[a-zA-Z_]+[0-9_]*$|^[a-zA-Z]+[0-9]$";
        //"^[0-9_\\u4e00-\\u9fa5]*[a-zA-Z]+[0-9]*$|^[a-zA-Z_\\u4e00-\\u9fa5]+[0-9]$|^[a-zA-Z_]+[\\u4e00-\\u9fa5]+[0-9]$";
        result = info.matches(userId);
        if (info.contains("白T") || info.contains("长袖T") || info.contains("女儿") || info
            .contains("儿子") || info.contains("小孩") || info.contains("Ok") || info.contains("thanks")
            || info.contains("hello") || info.contains("HELLO") || info.contains("CM") || info
            .contains("3Q") || info.contains("EMS") || info.contains("ems") || info.contains("kg")
            || info.contains("KG") || info.contains("vip") || info.contains("ID") || info
            .contains("这款") || info.contains("nihao") || info.contains("T恤") || info.contains("t恤")
            || info.contains("身高") || info.contains("体重") || info.contains("https") | info
            .contains("http") || info.contains("码") || info.contains("快递") || info.contains("费")
            || info.contains("包邮") || info.contains("OK") || info.contains("ok") || info
            .contains("元") || info.contains("cm") || info.contains("公分") || info.contains("斤")
            || info.contains("米")) {
            result = false;
        }
        return result;
    }

    private String maskUserId(String userId) {
        String maskedUserId = "";
        StringBuilder sb = new StringBuilder(userId);
        for (int i = 0; i < userId.length(); i++) {
            char ch = sb.charAt(i);
            if (ch >= '0' && ch <= '9') {
                Random random = new Random();
                int num = random.nextInt(10);
                maskedUserId += num;
            } else if (ch >= 'a' && ch <= 'z') {
                char c = (char) (int) (Math.random() * 26 + 97);
                maskedUserId += c;
            } else if (ch >= 'A' && ch <= 'Z') {
                char c = (char) (int) (Math.random() * 26 + 65);
                maskedUserId += c;
            } else if (ch == '_') {
                maskedUserId += ch;
            } else {
                maskedUserId += ch;
            }
        }

        return maskedUserId;
    }

    /**
     * @return makedQQnum 对识别的QQ号信息进行掩码
     */

    private String maskQQNum(String qqnum) {
        String maskedQQnum = "";
        String QQNum = "[1-9][0-9]{4,14}";
        Pattern pattern = Pattern.compile(QQNum);
        Matcher m = pattern.matcher(qqnum);
        String qqnumber = "";
        if (m.find()) {
            qqnumber = m.group();
        }
        String newQQnumer = qqnumber.substring(0, 1);
        for (int i = 0; i < qqnumber.length() - 1; i++) {
            Random random = new Random();
            String num = String.valueOf(random.nextInt(10));
            newQQnumer += num;
        }
        maskedQQnum = qqnum.replace(qqnumber, newQQnumer);

        return maskedQQnum;
    }


    /**
     * @return maskedEmile 对邮箱进行掩码，统一格式为"xx(...)xx@xx.com"
     */

    private String maskEmile(String emile) {
        String emilePattern = "[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)";
        String maskedEmile = "";
        StringBuilder sb = new StringBuilder(emile);
        for (int i = 0; i < emile.length(); i++) {
            char ch = sb.charAt(i);
            if (ch >= '0' && ch <= '9') {
                Random random = new Random();
                int num = random.nextInt(10);
                maskedEmile += num;
            } else if (ch >= 'a' && ch <= 'z') {
                char c = (char) (int) (Math.random() * 26 + 97);
                maskedEmile += c;
            } else if (ch >= 'A' && ch <= 'Z') {
                char c = (char) (int) (Math.random() * 26 + 65);
                maskedEmile += c;
            } else if (ch == '@' || ch == '.') {
                maskedEmile += ch;
            }
        }
        return maskedEmile;
    }

    /**
     * @return maskedZipcode 对邮编进行掩码（保留首位）
     */
    private String maskZipCode(String zipcode) {
        String zipCodePattern = "[0-9]{6}";
        Pattern pattern = Pattern.compile(zipCodePattern);
        Matcher m = pattern.matcher(zipcode);
        String codeNumber = "";
        if (m.find()) {
            codeNumber = m.group();
        }
        String newCodeNumber = codeNumber.substring(0, 1);
        for (int i = 0; i < codeNumber.length() - 1; i++) {
//         Random random=new Random();
//           newCodeNumber+=random.nextInt(10);
            newCodeNumber += "0";
        }
        String maskedCodeNumber = zipcode.replace(codeNumber, newCodeNumber);
        return maskedCodeNumber;
    }

    /**
     * @return maskedNumber 传入判断过后的电话号码，并对其进行掩码（保留前三位），并返回结果
     */
    private String maskPhoneNumber(String number) {

        String numberPattern = "(\\d{2,4}-?)?\\d{7,8}| [1]{1}(([3]{1}[4-9]{1})|([5]{1}[012789]{1})|([8]{1}[2378]{1})|([4]{1}[7]{1}))[0-9]{8}|[1]{1}(([3]{1}[0-2]{1})|([5]{1}[56]{1})|([8]{1}[56]{1}))[0-9]{8}|[1]{1}(([3]{1}[3]{1})|([5]{1}[3]{1})|([8]{1}[09]{1}))[0-9]{8}";
        Pattern pattern = Pattern.compile(numberPattern);
        Matcher m = pattern.matcher(number);
        String oldNumber = "";

        if (m.find()) {
            oldNumber = m.group();
        }

        String newNumber = oldNumber.substring(0, 3);

        for (int i = 0; i < oldNumber.length() - 3; i++) {
            int j = i + 3;
            if (number.charAt(j) == '-')
                newNumber += "-";
            else {
                Random random = new Random();
                newNumber += random.nextInt(10);
            }
        }

        String maskedNumber = number.replace(oldNumber, newNumber);
        return maskedNumber;
    }

    /**
     * @return Boolean 传入省的名称判断是否是直辖市
     */
    private Boolean isMunicipality(String provinceName) {
        Boolean result = false;
        if (provinceName.equals("上海市") || provinceName.equals("重庆市") || provinceName.equals("北京市")
            || provinceName.equals("天津市")) {
            result = true;
        }
        return result;
    }

    /**
     * @return Boolean 判断传入的query是否包含地址信息，包含省，或者不包含省但包含市信息，则判断为地址信息
     */
    private Boolean containsStreetInfo(String customerInfo) {
        //1、包含省级的；
        Boolean result = false;
        AddressDataManager adm = new AddressDataManager();
        allProvince = adm.getProvinces();
        if (customerInfo.equals("黑龙江") || customerInfo.equals("黑龙江省") || customerInfo
            .equals("新疆维吾尔自治区") || customerInfo.equals("西藏自治区") || customerInfo.equals("内蒙古自治区")) {
            result = true;
            provinceName = customerInfo;
            if (customerInfo.equals("黑龙江")) {
                provinceName = "黑龙江省";
            }
        } else {
            breakFor:
            for (Province p : allProvince) {
                if (customerInfo.contains(p.getProvinceName()) || customerInfo
                    .contains(p.getProvinceName().substring(0, p.getProvinceName().length() - 1))) {
                    result = true;
                    provinceName = p.getProvinceName();
                    customerInfo = customerInfo
                        .replace(provinceName.substring(0, provinceName.length() - 1), "x");
                    //判断是否直辖市
                    if (isMunicipality(provinceName)) {
                        for (City c : p.getCitiesGoverned()) {
                            for (District d : c.getDistrictList()) {
                                if (customerInfo.contains(d.getDistrictName())) {
                                    cityName = d.getDistrictName();
                                    customerInfo = customerInfo
                                        .replace(cityName.substring(0, cityName.length() - 1), "x");
                                    break breakFor;
                                }

                            }
                        }
                    } else {
                        for (City c : p.getCitiesGoverned()) {
                            if (customerInfo.contains(c.getCityName()) || customerInfo.contains(
                                c.getCityName().substring(0, c.getCityName().length() - 1))) {
                                cityName = c.getCityName();
                                customerInfo = customerInfo
                                    .replace(cityName.substring(0, cityName.length() - 1), "x");
                                for (District d : c.getDistrictList()) {
                                    if (customerInfo.contains(d.getDistrictName())) {
                                        districtName = d.getDistrictName();
                                        customerInfo = customerInfo.replace(
                                            districtName.substring(0, districtName.length() - 1),
                                            "x");
                                        break breakFor;
                                    }
                                }
                            }
//                            else {
//                                for (District d : c.getDistrictList()) {
//                                    if (customerInfo.contains(d.getDistrictName()) || customerInfo
//                                        .contains(d.getDistrictName()
//                                            .substring(0, d.getDistrictName().length() - 1))) {
//                                        cityName = d.getDistrictName();
//                                        customerInfo=customerInfo.replace(cityName.substring(0,cityName.length()-1),"x");
//                                        break breakFor;
//                                    }
//                                }
//                            }
                        }
                        //如果在市级信息找不到当前的cityName，在市级下一级的区级里找
                        for (City c : p.getCitiesGoverned()) {
                            for (District d : c.getDistrictList()) {
                                if (customerInfo.contains(d.getDistrictName()) || customerInfo
                                    .contains(d.getDistrictName()
                                        .substring(0, d.getDistrictName().length() - 1))) {
                                    cityName = d.getDistrictName();
                                    customerInfo = customerInfo
                                        .replace(cityName.substring(0, cityName.length() - 1), "x");
                                    break breakFor;
                                }
                            }
                        }
                    }
                }
            }
        }

        //2、不包含省级，但包含市级
        if (!result) {
            for (Province p : allProvince) {
                for (City c : p.getCitiesGoverned()) {
                    if (customerInfo.contains(c.getCityName()) && !c.getCityName().equals("县") && !c
                        .getCityName().equals("区") && !c.getCityName().equals("市")) {
                        result = true;
                        cityName = c.getCityName();
                        int index = customerInfo.indexOf(c.getCityName());
                        int plength = p.getProvinceName().length();
                        if (index > (plength - 1)) {
                            if (p.getProvinceName().substring(0, plength - 1)
                                .equals(customerInfo.substring(index - plength + 1, index))) {
                                provinceName = p.getProvinceName();
                            }
                        }
                        for (District d : c.getDistrictList()) {
                            if (customerInfo.contains(d.getDistrictName())) {
                                districtName = d.getDistrictName();
                            }
                        }
                    }
                }

            }
        }

        return result;
    }


    /**
     * @return Boolean 在已经对字符串进行初步判断是否包含省级市级或市级信息后，处理可能包含地址信息的子串
     */
    private Boolean verifyStreetDetailInfo(String info) {
        boolean result = false;
        if (containsStreetInfo) {
            String[] streetSign = {"市", "弄", "旗", "区", "县", "村", "乡", "镇", "组", "街道", "路"};
            for (int i = 0; i < streetSign.length; i++) {
                if (info.contains(streetSign[i])) {
                    result = true;
                    break;
                }
            }

        }
        return result;
    }

    /**
     *
     * @param streetInfo
     * @return
     */
    private String maskStreetInfoUnified(String streetInfo) {
        return "上海市南丹东路0号";
    }

    private String maskStreatDetailInfo(String streetInfo) {
        String maskedStreetInfo = "";
        StringBuilder sb = new StringBuilder(streetInfo);

        //分割
        int segCount = 0;
        for (int i = 0; i < streetInfo.length(); i++) {
            String ch = String.valueOf(streetInfo.charAt(i));

            if (ch.equals("省") || ch.equals("市") || ch.equals("旗") || ch.equals("区") || ch
                .equals("县") || ch.equals("乡") || ch.equals("镇") || ch.equals("组") || ch.equals("村")
                || ch.equals("街") || ch.equals("弄") || ch.equals("路")) {
                if (ch.equals("街") && i < (streetInfo.length() - 1) && String
                    .valueOf(streetInfo.charAt(i + 1)).equals("道")) {
                    sb.insert(segCount + i + 2, "|");
                } else
                    sb.insert(segCount + i + 1, "|");
                segCount++;
            }
        }
        String[] streetInfoSeg = sb.toString().split("\\|");
        //替换
        for (int i = 0; i < streetInfoSeg.length; i++) {
            //替代字符串中出现的数字，如门牌号等；判断数字是否电话号码和邮编，替代
            if (!streetInfoSeg[i].contains("月") && !streetInfoSeg[i].contains("日")) {
                String numberPattern = "[0-9]*";
                Pattern pattern = Pattern.compile(numberPattern);
                Matcher m = pattern.matcher(streetInfoSeg[i]);
                while (m.find()) {
                    if (!m.group().equals("")) {
                        String number = m.group();
                        if (this.verifyPhoneNumber(number)) {
                            String newNumber = maskPhoneNumber(number);
                            streetInfoSeg[i] = streetInfoSeg[i].replace(number, newNumber);
                        } else if (this.verifyZipcode(number)) {
                            String newNumber = maskZipCode(number);
                            streetInfoSeg[i].replace(number, newNumber);
                        } else {
                            String num = "";
                            for (int j = 0; j < number.length(); j++) {
                                Random random = new Random();
                                num += String.valueOf(random.nextInt(10));
                            }
                            streetInfoSeg[i] = streetInfoSeg[i].replace(number, num);
                        }

                    }
                }
            }

            //替换除了省市区外其余的地址信息
            String[] replaceInfo = {"花", "水", "池", "林", "雨", "润", "丹", "华", "宝", "荣", "上", "下", "左",
                "右", "东", "南", "西", "北", "中"};
            String[] streetSign = {"省", "市", "旗", "县", "村", "乡", "镇", "组", "街道", "路"};
//去掉包含在匿名信息中的地址信息标识
            if (!makebelieveProvinceName.equals("")) {
                String provinceSign = makebelieveProvinceName
                    .substring(makebelieveProvinceName.length() - 1,
                        makebelieveProvinceName.length());
                for (int p = 0; p < streetSign.length; p++) {
                    if (streetSign[p].equals(provinceSign)) {
                        streetSign[p] = "ignore";
                    }
                }
            }
            if (!makebelieveCityName.equals("")) {
                String citySign = makebelieveCityName
                    .substring(makebelieveCityName.length() - 1, makebelieveCityName.length());

                for (int p = 0; p < streetSign.length; p++) {
                    if (streetSign[p].equals(citySign)) {
                        streetSign[p] = "ignore";
                    }
                }

            }
            if (!makebelieveDistrictName.equals("")) {
                String districtSign = makebelieveDistrictName
                    .substring(makebelieveDistrictName.length() - 1,
                        makebelieveDistrictName.length());
                for (int p = 0; p < streetSign.length; p++) {
                    if (streetSign[p].equals(districtSign)) {
                        streetSign[p] = "ignore";
                    }
                }
            }

            for (int j = 0; j < streetSign.length; j++) {

                if (streetInfoSeg[i].contains(streetSign[j])) {
                    if (streetSign[j].equals("街道")) {
                        for (int p = 0; p < streetInfoSeg[i].length() - 2; p++) {
                            StringBuilder strb = new StringBuilder(streetInfoSeg[i]);
                            int q = (int) (Math.random() * replaceInfo.length);
                            strb.replace(p, p + 1, replaceInfo[q]);
                            streetInfoSeg[i] = strb.toString();
                        }
                    } else {
                        for (int p = 0; p < streetInfoSeg[i].length() - 1; p++) {
                            StringBuilder strb = new StringBuilder(streetInfoSeg[i]);
                            int q = (int) (Math.random() * replaceInfo.length);
                            strb.replace(p, p + 1, replaceInfo[q]);
                            streetInfoSeg[i] = strb.toString();
                        }
                    }
                }
            }
        }

        //组合
        for (int i = 0; i < streetInfoSeg.length; i++) {
            maskedStreetInfo += streetInfoSeg[i];

        }

        return maskedStreetInfo;
    }


    public static void main(String[] args) {

        CustomerInfoMaskImp customerInfoMaskImp = new CustomerInfoMaskImp();
//        String str = customerInfoMaskImp.processStr("86cm 12g");
//        System.out.println(str);

//TEST
       try {
           File file=new File("./CustomerInfoMask/src/mask-data-All.txt");
           if(file.isFile() && file.exists()){ //判断文件是否存在
               InputStreamReader read = new InputStreamReader(
                       new FileInputStream(file),"UTF-8");//考虑到编码格式
               BufferedReader bufferedReader = new BufferedReader(read);
               String lineTxt = null;
               while((lineTxt = bufferedReader.readLine()) != null){
                       System.out.println(
                           lineTxt + "\t\t\t\t\t" + customerInfoMaskImp.processStr(lineTxt));
               }
               read.close();
           }else{
               System.out.println("找不到指定的文件");
           }
       } catch (Exception e) {
           System.out.println("读取文件内容出错");
           e.printStackTrace();
       }
    }
}

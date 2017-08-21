public interface CustomerInfoMask
{
    //传入没有逗号、空格、分号等的子字符串，进行信息掩饰处理
    public String maskCustomerInfo(String info);
    //传入一条没有处理过的字符串数据,去掉逗号、空格、分号等生成子字符串后调用maskCustomerInfo(String info)
    public String processStr(String info);
}

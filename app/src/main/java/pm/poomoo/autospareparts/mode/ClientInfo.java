package pm.poomoo.autospareparts.mode;

/**
 * 客户数据模型
 * Created by yangsongyu on 14-9-7.
 */
public class ClientInfo {

    private int id;//公司ID
    private int typeId;//类型ID
    private String name;//名字
    private String explain;//说明
    private String pic;//公司图片
    private String cellPhone;//手机号
    private String specialNumber;//座机号码
    private String fax;//传真
    private String QQ;//联系QQ
    private String email;//邮箱
    private String address;//地址
    private long time;//时间
    private String contact;//联系人名字
    private String weixin;//微信

    public ClientInfo(int id, int typeId, String name, String explain, String pic, String cellPhone, String specialNumber,
                      String fax, String QQ, String email, String address, long time, String contact, String weixin) {
        this.id = id;
        this.typeId = typeId;
        this.name = name;
        this.explain = explain;
        this.pic = pic;
        this.cellPhone = cellPhone;
        this.specialNumber = specialNumber;
        this.fax = fax;
        this.QQ = QQ;
        this.email = email;
        this.address = address;
        this.time = time;
        this.contact = contact;
        this.weixin = weixin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getSpecialNumber() {
        return specialNumber;
    }

    public void setSpecialNumber(String specialNumber) {
        this.specialNumber = specialNumber;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getQQ() {
        return QQ;
    }

    public void setQQ(String QQ) {
        this.QQ = QQ;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }
}

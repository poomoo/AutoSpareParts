package pm.poomoo.autospareparts.mode;

import java.io.Serializable;

/**
 * Created by Android_PM on 2015/11/9.
 * 供求信息
 */
public class SupplyInfo implements Serializable {
    String id = "";//id
    String contact = "";//标题
    String time = "";//日期时间
    String content = "";//内容
    String pictures = "";//图片地址
    String address = "";//地址
    String user_name="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    @Override
    public String toString() {
        return "SupplyInfo{" +
                "id='" + id + '\'' +
                ", contact='" + contact + '\'' +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                ", pictures='" + pictures + '\'' +
                ", address='" + address + '\'' +
                ", user_name='" + user_name + '\'' +
                '}';
    }
}

package pm.poomoo.autospareparts.mode;

import java.io.Serializable;

/**
 * Created by Android_PM on 2015/11/9.
 * 供求信息
 */
public class SupplyInfo implements Serializable {
    String id = "";//id
    String contact = "";//标题
    String dateTime = "";//日期时间
    String content = "";//内容
    String urls = "";//图片地址

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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    @Override
    public String toString() {
        return "SupplyInfo{" +
                "id='" + id + '\'' +
                ", contact='" + contact + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", content='" + content + '\'' +
                ", urls='" + urls + '\'' +
                '}';
    }
}

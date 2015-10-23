package pm.poomoo.autospareparts.mode;

/**
 * 消息数据结构
 * Created by yangsongyu on 2014/9/14.
 */
public class MessageInfo {

    private int id;//消息ID
    private String picPath;//消息图片
    private String title;//消息标题
    private String content;//消息内容
    private long time;//消息时间

    public MessageInfo(int id, String picPath, String title, String content, long time) {
        this.id = id;
        this.picPath = picPath;
        this.title = title;
        this.content = content;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

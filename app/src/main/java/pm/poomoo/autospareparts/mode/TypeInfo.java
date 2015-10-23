package pm.poomoo.autospareparts.mode;

/**
 * 类型的数据模型
 * Created by yangsongyu on 14-9-4.
 */
public class TypeInfo {

    private int id;//类型ID
    private String picPath;//类型图片
    private String name;//类型名字
    private String explain;//类型说明

    public TypeInfo(int id, String picPath, String name, String explain) {
        this.id = id;
        this.picPath = picPath;
        this.name = name;
        this.explain = explain;
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
}

package pm.poomoo.autospareparts.mode;

/**
 * Created by Android_PM on 2015/11/3.
 * 回复数据模型
 */
public class ReplyInfo {
    private String replyName = "";//回复人名称
    private String commentName = "";//被回复人的名称
    private String replyContent = "";//回复的内容

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getCommentName() {
        return commentName;
    }

    public void setCommentName(String commentName) {
        this.commentName = commentName;
    }

    public String getReplayContent() {
        return replyContent;
    }

    public void setReplayContent(String replyContent) {
        this.replyContent = replyContent;
    }
}

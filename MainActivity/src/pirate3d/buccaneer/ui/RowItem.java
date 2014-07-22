package pirate3d.buccaneer.ui;

public class RowItem {
    private String imageUrl;
    private String title;
    private String desc;
     
    public RowItem(String imageUrl, String title, String desc) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.desc = desc;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }   
}
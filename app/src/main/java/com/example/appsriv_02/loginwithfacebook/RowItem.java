package com.example.appsriv_02.loginwithfacebook;

/**
 * Created by appsriv01 on 13/10/15.
 */
public class RowItem
{
    private String title;
    private String desc;

    public RowItem( String title, String desc)
    {
        this.title = title;
        this.desc = desc;
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

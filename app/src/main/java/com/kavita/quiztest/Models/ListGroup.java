package com.kavita.quiztest.Models;

public class ListGroup {

    private String gid, group_name, group_icon;

    public ListGroup(String id, String group_name, String group_icon) {
        this.gid = id;
        this.group_name = group_name;
        this.group_icon = group_icon;
    }

    public String getId() {
        return gid;
    }

    public void setId(String id) {
        this.gid = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_icon() {
        return group_icon;
    }

    public void setGroup_icon(String group_icon) {
        this.group_icon = group_icon;
    }

    @Override
    public String toString() {
        return "ListGroup{" +
                "gid='" + gid + '\'' +
                ", group_name='" + group_name + '\'' +
                ", group_icon='" + group_icon + '\'' +
                '}';
    }
}

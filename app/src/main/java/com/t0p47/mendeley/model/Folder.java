package com.t0p47.mendeley.model;

/**
 * Created by 01 on 08.10.2017.
 */

public class Folder {

    int local_id;
    int global_id;
    String title;
    int parent_id;

    public Folder() {}

    public Folder(int local_id, int global_id, String title, int parent_id) {
        this.local_id = local_id;
        this.global_id = global_id;
        this.title = title;
        this.parent_id = parent_id;
    }

    public Folder(int global_id, String title, int parent_id){
        this.global_id = global_id;
        this.title = title;
        this.parent_id = parent_id;
    }

    public Folder(String title, int parent_id){
        this.title = title;
        this.parent_id = parent_id;
    }

    public int getLocal_id() {
        return local_id;
    }

    public void setLocal_id(int local_id) {
        this.local_id = local_id;
    }

    public int getGlobal_id() {
        return global_id;
    }

    public void setGlobal_id(int global_id) {
        this.global_id = global_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

}

package com.structapp.eagle.models;

public class Bureau {
    private Integer id;
    private String bureauName;
    public Bureau() {
    }

    public Bureau(Integer id, String bureauName) {
        this.id = id;
        this.bureauName = bureauName;
    }
    
    public Bureau(Integer id, String bureauName, String descriptionBureau) {
        this.id = id;
        this.bureauName = bureauName;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBureauName() {
        return bureauName;
    }

    public void setBureauName(String bureauName) {
        this.bureauName = bureauName;
    }

    @Override
    public String toString (){
        return getId()+  " | " + getBureauName();
    }
}

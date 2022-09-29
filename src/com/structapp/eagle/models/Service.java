package com.structapp.eagle.models;

public class Service {
    private Integer id;
    private String serviceName;

    public Service() {
    }

    public Service(Integer id, String serviceName) {
        this.id = id;
        this.serviceName = serviceName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}

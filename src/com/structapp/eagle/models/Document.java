package com.structapp.eagle.models;

import com.jfoenix.controls.JFXButton;

import java.io.InputStream;
import java.sql.Date;


public class Document {
    private Integer id;
    private String barcode;
    private String documentName;
    private InputStream documentFile;
    private Date insertionDate;
    private Date expirationDate;
    private int bureauID;
    private int serviceID;
    private String bureauName;
    private String serviceName;

    private JFXButton action;
    private int nombreDays;


    public  Document(){}

    public Document(Integer id, String barcode, String documentName, Date insertionDate, Date expirationDate, int bureauID, int serviceID) {
        this.id = id;
        this.barcode = barcode;
        this.documentName = documentName;
        this.insertionDate = insertionDate;
        this.expirationDate = expirationDate;
        this.bureauID = bureauID;
        this.serviceID = serviceID;
    }

    public Document(int id, String barcode, String documentName, InputStream documentFile, Date insertionDate, Date expirationDate, int bureauID, int serviceID) {
        this.id = id;
        this.barcode = barcode;
        this.documentName = documentName;
        this.documentFile = documentFile;
        this.insertionDate = insertionDate;
        this.expirationDate = expirationDate;
        this.bureauID = bureauID;
        this.serviceID = serviceID;
    }

    public Document(Integer id, String barcode, String documentName, InputStream documentFile,Date insertionDate, Date expirationDate, int bureauID, int serviceID, JFXButton action) {
        this.id = id;
        this.barcode = barcode;
        this.documentName = documentName;
        this.documentFile = documentFile;
        this.insertionDate = insertionDate;
        this.expirationDate = expirationDate;
        this.bureauID = bureauID;
        this.serviceID = serviceID;
        this.action = action;
    }

    public Document(Integer id, String barcode, String documentName, InputStream documentFile, Date insertionDate, Date expirationDate, int bureauID, int serviceID, JFXButton action, int nombreDays) {
        this.id = id;
        this.barcode = barcode;
        this.documentName = documentName;
        this.documentFile = documentFile;
        this.insertionDate = insertionDate;
        this.expirationDate = expirationDate;
        this.bureauID = bureauID;
        this.serviceID = serviceID;
        this.action = action;
        this.nombreDays = nombreDays;
    }

    public Document(Integer id, String barcode, String documentName, InputStream documentFile, Date insertionDate, Date expirationDate, String bureauName, String serviceName, JFXButton action) {
        this.id = id;
        this.barcode = barcode;
        this.documentName = documentName;
        this.documentFile = documentFile;
        this.insertionDate = insertionDate;
        this.expirationDate = expirationDate;
        this.bureauName = bureauName;
        this.serviceName = serviceName;
        this.action = action;
        this.nombreDays = nombreDays;
    }

    public Document(Integer id, String barcode, String documentName, Date insertionDate, Date expirationDate, String bureauName, String serviceName, int nombreDays) {
        this.id = id;
        this.barcode = barcode;
        this.documentName = documentName;
        this.documentFile = documentFile;
        this.insertionDate = insertionDate;
        this.expirationDate = expirationDate;
        this.bureauName = bureauName;
        this.serviceName = serviceName;
        this.nombreDays = nombreDays;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }


    public InputStream getDocumentFile() {
        return documentFile;
    }

    public void setDocumentFile(InputStream documentFile) {
        this.documentFile = documentFile;
    }

    public Date getInsertionDate() {
        return insertionDate;
    }

    public void setInsertionDate(Date insertionDate) {
        this.insertionDate = insertionDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getBureauID() {
        return bureauID;
    }

    public void setBureauID(int bureauID) {
        this.bureauID = bureauID;
    }

    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }


    public JFXButton getAction() {
        return action;
    }

    public void setAction(JFXButton action) {
        this.action = action;
    }

    public int getNombreDays() {
        return nombreDays;
    }

    public void setNombreDays(int nombreDays) {
        this.nombreDays = nombreDays;
    }

    public String getBureauName() {
        return bureauName;
    }

    public void setBureauName(String bureauName) {
        this.bureauName = bureauName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}

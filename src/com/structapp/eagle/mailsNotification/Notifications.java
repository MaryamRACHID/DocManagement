package com.structapp.eagle.mailsNotification;

import com.jfoenix.controls.JFXButton;
import com.structapp.eagle.models.Document;

import java.sql.Date;

public class Notifications {

    private int id;
    private String titre;
    private String bureau;
    private String service;
    private Date insertionDate;
    private Date expirationDate;
    private int nombreDays;
    private JFXButton action;
    private Document document;
    private int EmailSend = 0;

    public Notifications() {
    }

    public Notifications(int id, String titre, String bureau, String service, Date insertionDate, Date expirationDate, JFXButton action) {
        this.id = id;
        this.titre = titre;
        this.bureau = bureau;
        this.service = service;
        this.insertionDate = insertionDate;
        this.expirationDate = expirationDate;
        this.action = action;
    }

    public Notifications(int id, String titre, String bureau, String service, Date insertionDate, Date expirationDate, int nombreDays, JFXButton action, Document document) {
        this.id = id;
        this.titre = titre;
        this.bureau = bureau;
        this.service = service;
        this.insertionDate = insertionDate;
        this.expirationDate = expirationDate;
        this.nombreDays = nombreDays;
        this.action = action;
        this.document = document;
    }

    public Notifications(int id, String titre, String bureau, String service, Date insertionDate, Date expirationDate, int nombreDays, JFXButton action) {
        this.id = id;
        this.titre = titre;
        this.bureau = bureau;
        this.service = service;
        this.insertionDate = insertionDate;
        this.expirationDate = expirationDate;
        this.nombreDays = nombreDays;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getBureau() {
        return bureau;
    }

    public void setBureau(String bureau) {
        this.bureau = bureau;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
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

    public int getNombreDays() {
        return nombreDays;
    }

    public void setNombreDays(int nombreDays) {
        this.nombreDays = nombreDays;
    }

    public JFXButton getAction() {
        return action;
    }

    public void setAction(JFXButton action) {
        this.action = action;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return "Notifications{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", bureau='" + bureau + '\'' +
                ", service='" + service + '\'' +
                ", insertionDate=" + insertionDate +
                ", expirationDate=" + expirationDate +
                ", nombreDays=" + nombreDays +
                '}';
    }

    public int getEmailSend() {
        return EmailSend;
    }

    public void setEmailSend(int emailSend) {
        EmailSend = emailSend;
    }
}
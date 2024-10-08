package it.univaq.example.webshop.data.model.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import it.univaq.example.webshop.data.model.Proposal;
import it.univaq.example.webshop.data.model.Request;
import it.univaq.example.webshop.data.model.User;
import it.univaq.framework.data.DataItemImpl;

public class ProposalImpl extends DataItemImpl<Integer> implements Proposal {

    private Request request;
    private User technician;
    private String productName;
    private String producerName;
    private String productDescription;
    private Float productPrice;
    private String url;
    private String notes;
    private LocalDateTime creationDate;
    private ProposalStateEnum proposalState;
    private String motivation;

    public ProposalImpl() {
        super();
        request = null;
        technician = null;
        productName = "";
        producerName = "";
        productDescription = "";
        productPrice = 0.00F;
        url = "";
        notes = "";
        creationDate = LocalDateTime.now();
        proposalState = ProposalStateEnum.INATTESA;
        motivation = "";
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public User getTechnician() {
        return technician;
    }

    @Override
    public void setTechnician(User techUser) {
        technician = techUser;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String getProducerName() {
        return producerName;
    }

    @Override
    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    @Override
    public String getProductDescription() {
        return productDescription;
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    @Override
    public Float getProductPrice() {
        BigDecimal bd = new BigDecimal(productPrice);
        return bd.setScale(2, RoundingMode.HALF_EVEN).floatValue();
    }

    @Override
    public void setProductPrice(Float price) {
        BigDecimal bd = new BigDecimal(price);
        this.productPrice = bd.setScale(2, RoundingMode.HALF_EVEN).floatValue();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public boolean setUrl(String url) {
        if(url.length() > 0) {
            try {
                URL u = new URL(url);
                u.toURI();
            } catch (MalformedURLException|URISyntaxException e) {
                e.printStackTrace();
                return false;
            }
        }
        this.url = url;
        return true;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    @Override
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public ProposalStateEnum getProposalState() {
        return proposalState;
    }

    @Override
    public void setProposalState(ProposalStateEnum value) {
        proposalState = value;
    }

    @Override
    public String getMotivation() {
        return motivation;
    }

    @Override
    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }
    
}

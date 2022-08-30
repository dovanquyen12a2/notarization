package com.vinorsoft.microservices.core.notarization.util.repository;


import java.util.Date;

import com.vinorsoft.microservices.core.notarization.util.domain.EntityBase;

public class Notarization extends EntityBase<Notarization> {
    public Notarization() {
    }

    @Override
    public Long getId() {
        return this.ID;
    }

    public void setId(Long id) {
        this.ID = id;
    }

    public String CODE; 
    public String CUSTOMER_CODE; 
    public String CUSTOMER_NAME; 
    public String CUSTOMER_PHONE; 
    public String CUSTOMER_EMAIL; 
    public String RM_NAME; 
    public String RM_PHONE; 
    public String OPTION_CODE; 
    public String OPTION_T24_CODE; 
    public String BRANCH_CODE; 
    public String BRANCH_NAME; 
    public Long CURRENT_USER_ID; 
    public Long PARENT_ID; 
    public Date FINISH_PRINTING_TIME; 
    public String LOCATION_NOTARIZATION; 
    public Date TIME_NOTARIZATION; 
    public Integer STATUS;
}

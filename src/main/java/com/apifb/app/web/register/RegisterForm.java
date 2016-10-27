package com.apifb.app.web.register;

import org.lastaflute.web.validation.Required;

import javax.validation.constraints.Min;

/**
 * @author koki.iwata
 */
public class RegisterForm {

    @Required
    public String memberName;

    @Required
    @Min(3)
    public Integer loginPassword;




}

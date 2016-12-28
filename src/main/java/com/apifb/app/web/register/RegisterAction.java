package com.apifb.app.web.register;

import com.apifb.app.web.base.ApifbBaseAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author koki.iwata
 */
public class RegisterAction extends ApifbBaseAction {

    @Execute
    public HtmlResponse index(Integer puroductId, RegisterForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_Register_RegisterHtml);
        });
        return asHtml(path_Register_RegisterHtml);
    }
}

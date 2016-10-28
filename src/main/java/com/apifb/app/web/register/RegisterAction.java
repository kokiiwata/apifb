package com.apifb.app.web.register;

import com.apifb.ApifbBoot;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author koki.iwata
 */
public class RegisterAction extends ApifbBoot {
    @Excute
    public HtmlResponse index(Integer puroductId, RgisterForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_Register_RegisterHtml);
        });
        return asHtml(path_Register_RegisterHtml);
    }
}

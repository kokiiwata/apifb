/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.apifb.mylasta.direction;

import javax.annotation.Resource;

import com.apifb.mylasta.direction.sponsor.ApifbActionAdjustmentProvider;
import com.apifb.mylasta.direction.sponsor.ApifbApiFailureHook;
import com.apifb.mylasta.direction.sponsor.ApifbCookieResourceProvider;
import com.apifb.mylasta.direction.sponsor.ApifbCurtainBeforeHook;
import com.apifb.mylasta.direction.sponsor.ApifbJsonResourceProvider;
import com.apifb.mylasta.direction.sponsor.ApifbListedClassificationProvider;
import com.apifb.mylasta.direction.sponsor.ApifbMailDeliveryDepartmentCreator;
import com.apifb.mylasta.direction.sponsor.ApifbSecurityResourceProvider;
import com.apifb.mylasta.direction.sponsor.ApifbTimeResourceProvider;
import com.apifb.mylasta.direction.sponsor.ApifbUserLocaleProcessProvider;
import com.apifb.mylasta.direction.sponsor.ApifbUserTimeZoneProcessProvider;
import org.lastaflute.core.direction.CachedFwAssistantDirector;
import org.lastaflute.core.direction.FwAssistDirection;
import org.lastaflute.core.direction.FwCoreDirection;
import org.lastaflute.core.security.InvertibleCryptographer;
import org.lastaflute.core.security.OneWayCryptographer;
import org.lastaflute.db.dbflute.classification.ListedClassificationProvider;
import org.lastaflute.db.direction.FwDbDirection;
import org.lastaflute.thymeleaf.ThymeleafRenderingProvider;
import org.lastaflute.web.direction.FwWebDirection;
import org.lastaflute.web.ruts.renderer.HtmlRenderingProvider;

/**
 * @author jflute
 */
public class ApifbFwAssistantDirector extends CachedFwAssistantDirector {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private ApifbConfig config;

    // ===================================================================================
    //                                                                              Assist
    //                                                                              ======
    @Override
    protected void prepareAssistDirection(FwAssistDirection direction) {
        direction.directConfig(nameList -> nameList.add("apifb_config.properties"), "apifb_env.properties");
    }

    // ===================================================================================
    //                                                                               Core
    //                                                                              ======
    @Override
    protected void prepareCoreDirection(FwCoreDirection direction) {
        // this configuration is on apifb_env.properties because this is true only when development
        direction.directDevelopmentHere(config.isDevelopmentHere());

        // titles of the application for logging are from configurations
        direction.directLoggingTitle(config.getDomainTitle(), config.getEnvironmentTitle());

        // this configuration is on sea_env.properties because it has no influence to production
        // even if you set true manually and forget to set false back
        direction.directFrameworkDebug(config.isFrameworkDebug()); // basically false

        // you can add your own process when your application is booting
        direction.directCurtainBefore(createCurtainBeforeHook());

        direction.directSecurity(createSecurityResourceProvider());
        direction.directTime(createTimeResourceProvider());
        direction.directJson(createJsonResourceProvider());
        direction.directMail(createMailDeliveryDepartmentCreator().create());
    }

    protected ApifbCurtainBeforeHook createCurtainBeforeHook() {
        return new ApifbCurtainBeforeHook();
    }

    protected ApifbSecurityResourceProvider createSecurityResourceProvider() { // #change_it_first
        final InvertibleCryptographer inver = InvertibleCryptographer.createAesCipher("apifb:dockside:");
        final OneWayCryptographer oneWay = OneWayCryptographer.createSha256Cryptographer();
        return new ApifbSecurityResourceProvider(inver, oneWay);
    }

    protected ApifbTimeResourceProvider createTimeResourceProvider() {
        return new ApifbTimeResourceProvider(config);
    }

    protected ApifbJsonResourceProvider createJsonResourceProvider() {
        return new ApifbJsonResourceProvider();
    }

    protected ApifbMailDeliveryDepartmentCreator createMailDeliveryDepartmentCreator() {
        return new ApifbMailDeliveryDepartmentCreator(config);
    }

    // ===================================================================================
    //                                                                                 DB
    //                                                                                ====
    @Override
    protected void prepareDbDirection(FwDbDirection direction) {
        direction.directClassification(createListedClassificationProvider());
    }

    protected ListedClassificationProvider createListedClassificationProvider() {
        return new ApifbListedClassificationProvider();
    }

    // ===================================================================================
    //                                                                                Web
    //                                                                               =====
    @Override
    protected void prepareWebDirection(FwWebDirection direction) {
        direction.directRequest(createUserLocaleProcessProvider(), createUserTimeZoneProcessProvider());
        direction.directCookie(createCookieResourceProvider());
        direction.directAdjustment(createActionAdjustmentProvider());
        direction.directMessage(nameList -> nameList.add("apifb_message"), "apifb_label");
        direction.directApiCall(createApiFailureHook());
        direction.directHtmlRendering(createHtmlRenderingProvider());
    }

    protected ApifbUserLocaleProcessProvider createUserLocaleProcessProvider() {
        return new ApifbUserLocaleProcessProvider();
    }

    protected ApifbUserTimeZoneProcessProvider createUserTimeZoneProcessProvider() {
        return new ApifbUserTimeZoneProcessProvider();
    }

    protected ApifbCookieResourceProvider createCookieResourceProvider() { // #change_it_first
        final InvertibleCryptographer cr = InvertibleCryptographer.createAesCipher("dockside:apifb:");
        return new ApifbCookieResourceProvider(config, cr);
    }

    protected ApifbActionAdjustmentProvider createActionAdjustmentProvider() {
        return new ApifbActionAdjustmentProvider();
    }

    protected ApifbApiFailureHook createApiFailureHook() {
        return new ApifbApiFailureHook();
    }

    protected HtmlRenderingProvider createHtmlRenderingProvider() {
        return new ThymeleafRenderingProvider().asDevelopment(config.isDevelopmentHere());
    }
}

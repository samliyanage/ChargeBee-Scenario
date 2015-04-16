/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.connector.integration.test.chargebee;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

public class ChargebeeConnectorIntegrationTest extends ConnectorIntegrationTestBase {
    
    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();
    
    private String apiUrl;
    
    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
    
        init("chargebee-connector-1.0.0");
        
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        
        
        // Create base64-encoded auth string using apiKey
        final String authString = connectorProperties.getProperty("apiKey") + ":";
        final String base64AuthString = Base64.encode(authString.getBytes());
        
        apiRequestHeadersMap.put("Authorization", "Basic " + base64AuthString);
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v1";
        
        
    }
    
    /**
     * Positive test case for createCoupon method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "chargebee {createCoupon} integration test with mandatory parameters.")
    public void testCreateCouponWithMandatoryParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createCoupon");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_mandatory.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       final String couponId = esbRestResponse.getBody().getJSONObject("coupon").getString("id");
       connectorProperties.setProperty("couponId", couponId);
       
       final String apiEndPoint = apiUrl+"/coupons/" + couponId;
       final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       final JSONObject apiResponseObject=apiRestResponse.getBody().getJSONObject("coupon");
       
       Assert.assertEquals(apiResponseObject.getString("id"), connectorProperties.getProperty("couponIdMand"));
       Assert.assertEquals(apiResponseObject.getString("name"), connectorProperties.getProperty("couponNameMand"));
       Assert.assertEquals(apiResponseObject.getString("discount_amount"), connectorProperties.getProperty("discountAmount"));
       Assert.assertEquals(apiResponseObject.getString("duration_type"), connectorProperties.getProperty("durationType"));
    }
    
    /**
     * Positive test case for createCoupon method with optional parameters.
     */
    @Test(groups = { "wso2.esb" }, description = "chargebee {createCoupon} integration test with optional parameters.")
    public void testCreateCouponWithOptionalParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createCoupon");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_optional.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       final String couponId = esbRestResponse.getBody().getJSONObject("coupon").getString("id");
       
       final String apiEndPoint = apiUrl+ "/coupons/" + couponId;
       final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       final JSONObject apiResponseObject=apiRestResponse.getBody().getJSONObject("coupon");
       
       Assert.assertEquals(apiResponseObject.getString("invoice_name"), connectorProperties.getProperty("invoiceNameOpt"));
       Assert.assertEquals(apiResponseObject.getString("invoice_notes"), connectorProperties.getProperty("invoiceNotesOpt"));
       Assert.assertEquals(apiResponseObject.getString("valid_till"), connectorProperties.getProperty("validTill"));
       Assert.assertEquals(apiResponseObject.getString("max_redemptions"), connectorProperties.getProperty("maxRedemptions"));   
    }
    
    /**
     * Negative test case for createCoupon method.
     */
    @Test(groups = { "wso2.esb" }, description = "chargebee {createUser} integration test with negative case.")
    public void testCreateCouponWithNegativeCase() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createCoupon");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_negative.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
       
       final String apiEndPoint = apiUrl + "/coupons?id=&apply_on=&discount_type=&name=";
       final RestResponse<JSONObject> apiRestResponse =
             sendJsonRestRequest(apiEndPoint, "POST", apiRequestHeadersMap);
       
       Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 400);
       Assert.assertEquals(esbRestResponse.getBody().getString("error_code"), apiRestResponse.getBody().getString("error_code"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error_msg"), apiRestResponse.getBody().getString("error_msg"));
       Assert.assertEquals(esbRestResponse.getBody().getString("error_param"), apiRestResponse.getBody().getString("error_param"));
       Assert.assertEquals(esbRestResponse.getBody().getString("message"), apiRestResponse.getBody().getString("message"));
    }
    
    /**
     * Positive test case for getCoupon method with mandatory parameters.
     */
    @Test(groups = { "wso2.esb" }, dependsOnMethods = { "testCreateCouponWithMandatoryParameters" }, description = "chargebee {getCoupon} integration test with mandatory parameters.")
    public void testGetCouponWithMandatoryParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:getCoupon");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getCoupon_mandatory.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
       
       final JSONObject esbResponseObject= esbRestResponse.getBody().getJSONObject("coupon");
       
       final String apiEndPoint = apiUrl + "/coupons/" + connectorProperties.getProperty("couponId");
       final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(apiRestResponse.getHttpStatusCode(), 200);
       
       final JSONObject apiResponseObject= apiRestResponse.getBody().getJSONObject("coupon");
       
       Assert.assertEquals(esbResponseObject.getString("id"), apiResponseObject.getString("id"));
       Assert.assertEquals(esbResponseObject.getString("name"), apiResponseObject.getString("name"));
       Assert.assertEquals(esbResponseObject.getString("discount_type"), apiResponseObject.getString("discount_type"));
       Assert.assertEquals(esbResponseObject.getString("apply_on"), apiResponseObject.getString("apply_on"));
       Assert.assertEquals(esbResponseObject.getLong("created_at"), apiResponseObject.getLong("created_at"));
    }
    
  
}

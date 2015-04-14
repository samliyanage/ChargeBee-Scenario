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
        
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        
        apiRequestHeadersMap.put("Authorization", "Token token=" + connectorProperties.getProperty("apiToken"));
        
        apiUrl = connectorProperties.getProperty("apiUrl") + "/api/v1";
        
    }
    
    /**
     * Positive test case for createCoupon method with mandatory parameters.
     */
    @Test(priority = 1, groups = { "wso2.esb" }, description = "chargebee {createCoupon} integration test with mandatory parameters.")
    public void testCreateCouponWithMandatoryParameters() throws IOException, JSONException {
    
       esbRequestHeadersMap.put("Action", "urn:createCoupon");
       
       final RestResponse<JSONObject> esbRestResponse =
             sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createCoupon_mandatory.json");
       
       Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 201);
       final String userId = esbRestResponse.getBody().getJSONObject("user").getString("id");
       connectorProperties.setProperty("userIdMandatory", userId);
       
       final String apiEndPoint = apiUrl + "/users/" + userId;
       final RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndPoint, "GET", apiRequestHeadersMap);
       
       Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("name"),
             connectorProperties.getProperty("userName"));
       Assert.assertEquals(apiRestResponse.getBody().getJSONObject("user").getString("email"),
             connectorProperties.getProperty("userEmail"));
    }
    
  
}

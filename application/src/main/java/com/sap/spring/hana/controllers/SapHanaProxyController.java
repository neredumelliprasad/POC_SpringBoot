package com.sap.spring.hana.controllers;

import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.*;

@CrossOrigin
@RestController
@RequestMapping( "/masterdata" )
public class SapHanaProxyController
{
    private static final Logger logger = CloudLoggerFactory.getLogger(SapHanaProxyController.class);
    RestTemplate restTemplate = new RestTemplate();

//    @Value("${sap-connection-server}")
    String baseUrl="https://sap-hana-integration1.cfapps.eu10.hana.ondemand.com";

    @RequestMapping(value = "/product/getByKey", method = RequestMethod.GET )
    public ResponseEntity getProduct( @RequestParam( defaultValue = "" ) final List<String> keys)
    {
        String uri = baseUrl.trim()+"/masterdata/product/getByKey?keys="+"".join(",",keys);
        ResponseEntity responseEntity = restTemplate.getForEntity(uri, Object.class);
        return ResponseEntity.ok(responseEntity.getBody());
    }

    @RequestMapping(value = "/product", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity postProduct(@RequestBody List<Object> rawProductDataList) throws Exception
    {
        ResponseEntity responseEntity = null;
        try{
            String uri = baseUrl.trim()+"/masterdata/product";
            System.out.println("Size:" + rawProductDataList.size());
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity entity = new HttpEntity(rawProductDataList, headers);
            responseEntity = restTemplate.exchange(uri,HttpMethod.PUT, entity, Object.class);
            System.out.println("Response Body:" + responseEntity.getBody());
            System.out.println("Response Header:" + responseEntity.getHeaders());
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        }catch (HttpStatusCodeException e){
            System.out.println("Response Body Exception:" + e.getResponseBodyAsByteArray());
            System.out.println("Response Header Exception:" + e.getResponseHeaders());
            return ResponseEntity.ok(e.getResponseBodyAsByteArray());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}

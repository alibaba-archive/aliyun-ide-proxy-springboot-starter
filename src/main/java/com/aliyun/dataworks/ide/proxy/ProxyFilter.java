package com.aliyun.dataworks.ide.proxy;

import com.aliyun.dataworks.dataservice.model.api.protocol.ApiProtocol;
import com.aliyun.dataworks.dataservice.sdk.facade.DataApiClient;
import com.aliyun.dataworks.dataservice.sdk.loader.http.Request;
import com.aliyun.dataworks.dataservice.common.http.constant.HttpMethod;
import com.aliyun.dataworks.ide.proxy.common.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * proxy aliyun data service to webide
 *
 * @author genxiaogu
 * @date 2019.03.12
 */
public class ProxyFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(ProcessBuilder.class) ;

    static String DS_SA_PI_KEY = "dSaPiId";
    static String DS_API_PATH = "dsApiPath" ;
    static String DS_API_METHOD = "dsApiMethod" ;
    static String DS_HOST = "dsHost" ;
    static String DS_CONTENT_TYPE = "application/json";
    static String ERROR_CODE = "errCode" ;

    static String[] systemParams = new String[]{DS_SA_PI_KEY,DS_API_PATH,DS_API_METHOD,DS_HOST} ;

    ProxyProperties proxyProperties ;
    DataApiClient dataApiClient ;

    public ProxyFilter(ProxyProperties proxyProperties ,
                       DataApiClient dataApiClient){
        this.proxyProperties = proxyProperties ;
        this.dataApiClient = dataApiClient ;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {

        String apiPath = request.getParameter(DS_API_PATH) ;
        String method = request.getParameter(DS_API_METHOD) ;
        String dsHost = request.getParameter(DS_HOST) ;
        Request dsRequest = generateRequest(method , apiPath , dsHost);
        HashMap<String, String> requestParams = new HashMap<>(8);
        request.getParameterMap().forEach((k , v) -> {
            if (isCustomParameter(k)) {
                requestParams.put(k,v[0]);
            }
        });
        dsRequest.getQuerys().putAll(requestParams);
        if (method.toUpperCase().equals(HttpMethod.POST)) {
            JSONObject requestBody = JSON.parseObject(IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8.name()));
            for (Map.Entry<String, Object> item : requestBody.entrySet()) {
                dsRequest.getBodys().put(item.getKey(), item.getValue());
            }
        }
        response.setContentType(DS_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try {
            HashMap payload = dataApiClient.dataLoad(dsRequest);
            if (!payload.get(ERROR_CODE).equals(0)) {
                payload.put("success", false);
            }
            response.getOutputStream().write(JSONObject.toJSONString(payload).getBytes());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception exception) {
            logger.error("aliyun.ide.proxy.error : " , exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if(StringUtils.isEmpty(exception.getMessage())){
                response.getOutputStream().write(JSONObject.toJSONString(Result.ofDefaultError()).getBytes());
                logger.error(Result.defaultError);
            }else {
                response.getOutputStream().write(
                    JSONObject.toJSONString(Result.ofError(exception.getMessage())).getBytes());
            }
        }
        response.getOutputStream().flush();
    }


    /**
     * generator a DS Request
     *
     * @param method
     * @param apiPath
     * @return
     */
    private Request generateRequest(String method , String apiPath , String dsHost){
        Request request = new Request() ;
        request.setMethod(method.toUpperCase().equals(HttpMethod.POST) ? HttpMethod.POST : HttpMethod.GET);
        request.setAppKey(proxyProperties.getAppKey());
        request.setAppSecret(proxyProperties.getAppSecret());
        request.setHost(StringUtils.isNotEmpty(dsHost) ? dsHost : proxyProperties.getHost());
        request.setPath(apiPath);
        request.setApiProtocol(ApiProtocol.HTTP);
        return request ;
    }

    /**
     * verify the parameter is not in [DS_SA_PI_KEY,DS_API_PATH,DS_API_METHOD]
     *
     * @return Boolean
     */
    private static Boolean isCustomParameter(String key){
        return !Arrays.asList(systemParams).contains(key) ;
    }

}

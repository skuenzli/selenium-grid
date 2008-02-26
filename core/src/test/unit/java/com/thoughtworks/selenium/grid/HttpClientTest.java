package com.thoughtworks.selenium.grid;

import junit.framework.Assert;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jbehave.classmock.UsingClassMock;
import org.jbehave.core.mock.Mock;
import org.junit.Test;

import java.io.IOException;
import java.net.ConnectException;


public class HttpClientTest extends UsingClassMock {

    @Test
    public void requestReturnsAResponseWithHttpBody() throws IOException {
        final Response response;
        final Mock method;
        final Mock httpClient;

        httpClient = mock(org.apache.commons.httpclient.HttpClient.class);
        method = mock(GetMethod.class);
        method.expects("getResponseBody").will(returnValue("Body content".getBytes()));
        HttpClient client = new HttpClient((org.apache.commons.httpclient.HttpClient) httpClient);
        response = client.request((GetMethod) method);
        Assert.assertEquals("Body content", response.body());
        verifyMocks();
    }

    @Test
    public void responseHandleInternationalCharactersWhenUTF8EncodedAsRemoteControlDo() throws IOException {
        final Response response;
        final Mock method;
        final Mock httpClient;

        httpClient = mock(org.apache.commons.httpclient.HttpClient.class);
        method = mock(PostMethod.class);
        method.expects("getResponseBody").will(returnValue("32 d�cembre".getBytes("utf-8")));
        HttpClient client = new HttpClient((org.apache.commons.httpclient.HttpClient) httpClient);
        response = client.request((PostMethod) method);
        Assert.assertEquals("32 d�cembre", response.body());
        verifyMocks();
    }

    @Test
    public void requestReleasesTheConnectionOnTheGetMethod() throws IOException {
        final Mock method;
        final Mock httpClient;

        httpClient = mock(org.apache.commons.httpclient.HttpClient.class);
        method = mock(GetMethod.class);
        method.expects("getResponseBody").will(returnValue("".getBytes()));
        method.expects("releaseConnection");
        HttpClient client = new HttpClient((org.apache.commons.httpclient.HttpClient) httpClient);
        client.request((GetMethod) method);
        verifyMocks();
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Test(expected = ConnectException.class)
    public void requestReleasesTheConnectionOnTheGetMethodWhenAProblemOccurs() throws IOException {
        final Mock method;
        final Mock httpClient;

        httpClient = mock(org.apache.commons.httpclient.HttpClient.class);
        method = mock(GetMethod.class);
        httpClient.expects("executeMethod").will(throwException(new ConnectException("an error")));
        method.expects("releaseConnection");
        HttpClient client = new HttpClient((org.apache.commons.httpclient.HttpClient) httpClient);
        client.request((GetMethod) method);
        verifyMocks();
    }

    @Test
    public void buildPostMethodUsesURLProvidedAsAParameter() throws IOException {
        final PostMethod postmethod;
        
        postmethod = new HttpClient().buildPostMethod("http://a.url/somwhere", new HttpParameters());
        Assert.assertEquals("http://a.url/somwhere", postmethod.getURI().toString());
    }

    @Test
    public void buildPostMethodUsesParamsProvidedAsParameter() throws IOException {
        final HttpParameters parameters;
        final PostMethod postmethod;

        parameters = new HttpParameters();
        parameters.put("selenium", "grid");
        parameters.put("open", "qa");
        postmethod = new HttpClient().buildPostMethod("", parameters);
        Assert.assertEquals("grid", postmethod.getParameter("selenium").getValue());
        Assert.assertEquals("qa", postmethod.getParameter("open").getValue());
    }

}
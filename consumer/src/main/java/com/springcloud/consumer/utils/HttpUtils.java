package com.springcloud.consumer.utils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * HTTP请求工具类，包含POST和GET请求
 * 
 * @author ysl
 *
 */
@Slf4j
public final class HttpUtils {

	private static final int CONNECTION_REQUEST_TIMEOUT = 1000 * 5;
	private static final int CONNECT_TIMEOUT = 1000 * 5;
	private static final int SOCKET_TIMEOUT = 1000 * 5;

	private static final CloseableHttpClient httpClient = HttpClientBuilder.create().build();

	private static final CloseableHttpClient httpsClient = sslClient();

	/**
	 * HTTP Get请求
	 * 
	 * @param url     包含请求参数的URL
	 * @param isHttps
	 * @return
	 */
	public static String httpGet(String url, boolean isHttps) {
		HttpGet httpGet = null;
		try {
			CloseableHttpClient client = httpClient;
			if (isHttps) {
				client = httpsClient;
			}
			httpGet = new HttpGet(url);
			httpGet.setConfig(config());

			CloseableHttpResponse response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				String jsonResp = EntityUtils.toString(entity, "utf-8");
				return jsonResp;
			} else {
				log.warn("GET 请求失败; [errorCode = {} , url={}]", response.getStatusLine().getStatusCode(), url);
			}
		} catch (Exception e) {
			throw new RuntimeException("GET 请求失败,url:" + url, e);
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
				httpGet = null;
			}
		}
		return null;
	}

	/**
	 * HTTP Post请求，以Form表单形式提交请求参数
	 * 
	 * @param url     post请求的url
	 * @param params  键值对格式参数值
	 * @param isHttps
	 * @return
	 */
	public static String httpPostByForm(String url, Map<String, String> params, boolean isHttps) {
		HttpPost httpPost = null;
		try {
			CloseableHttpClient client = httpClient;
			if (isHttps) {
				client = httpsClient;
			}
			httpPost = new HttpPost(url);
			httpPost.setConfig(config());
			httpPost.setEntity(new UrlEncodedFormEntity(convertParams(params), "UTF-8"));
			CloseableHttpResponse response = client.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				String jsonResponse = EntityUtils.toString(entity, "utf-8");
				return jsonResponse;
			} else {
				log.warn("POST Form 请求失败; [errorCode = {} , url={}]", response.getStatusLine().getStatusCode(), url);
			}
		} catch (Exception e) {
			throw new RuntimeException("POST Form 请求失败,url:" + url, e);
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
				httpPost = null;
			}
		}
		return null;
	}

	/**
	 * HTTP Post请求，以JSON格式提交请求参数
	 * 
	 * @param url        post请求的url
	 * @param jsonParams JSON格式的参数值
	 * @param isHttps
	 * @return
	 */
	public static String httpPostByJson(String url, String jsonParams, boolean isHttps) {
		HttpPost httpPost = null;
		try {
			CloseableHttpClient client = httpClient;
			if (isHttps) {
				client = httpsClient;
			}
			httpPost = new HttpPost(url);
			httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
			httpPost.setConfig(config());
			httpPost.setEntity(new StringEntity(jsonParams, "UTF-8"));
			CloseableHttpResponse response = client.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				String jsonResponse = EntityUtils.toString(entity, "utf-8");
				return jsonResponse;
			} else {
				log.warn("POST Json 请求失败; [errorCode = {} , url={}]", response.getStatusLine().getStatusCode(), url);
			}
		} catch (Exception e) {
			throw new RuntimeException("POST Json 请求失败,url:" + url, e);
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
				httpPost = null;
			}
		}
		return null;
	}

	private static RequestConfig config() {
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		return requestConfig;
	}

	private static List<NameValuePair> convertParams(Map<String, String> params) {
		List<NameValuePair> nvPairList = new ArrayList<NameValuePair>();
		Iterator<String> its = params.keySet().iterator();
		while (its.hasNext()) {
			String key = its.next();
			nvPairList.add(new BasicNameValuePair(key, params.get(key)));
		}
		return nvPairList;
	}

	/**
	 * 在调用SSL之前需要重写验证方法，取消检测SSL 创建ConnectionManager，添加Connection配置信息
	 * 
	 * @return HttpClient 支持https
	 */
	private static CloseableHttpClient sslClient() {
		try {
			// 在调用SSL之前需要重写验证方法，取消检测SSL
			X509TrustManager trustManager = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] xcs, String str) {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] xcs, String str) {
				}
			};
			SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
			ctx.init(null, new TrustManager[] { trustManager }, null);
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
			// 创建Registry
			RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT)
					.setCookieSpec(CookieSpecs.STANDARD_STRICT).setExpectContinueEnabled(Boolean.TRUE).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
					.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", socketFactory).build();
			// 创建ConnectionManager，添加Connection配置信息
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
			return closeableHttpClient;
		} catch (KeyManagementException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * test
	 * 
	 * @param args
	 * @author ysl
	 * @date 2019年3月27日
	 */
	public static void main(String[] args) {
//		String url = "http://www.baidu.com";
//		// http
//		System.out.println(httpGet(url, false));

		// https
		String urls = "https://www.12306.cn/mormhweb/";
		System.out.println(httpGet(urls, true));
	}

}

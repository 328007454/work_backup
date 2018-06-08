package com.cnksi.sjjc.sync;

import android.text.TextUtils;
import android.util.Log;

import com.cnksi.ksynclib.KNConfig;
import com.cnksi.ksynclib.KSync;
import com.cnksi.ksynclib.exception.KSyncException;
import com.cnksi.ksynclib.model.KMap;
import com.cnksi.ksynclib.service.KSyncService;
import com.cnksi.ksynclib.utils.KOkHttpUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @version 1.0
 * @author wastrel
 * @date 2017/12/14 16:26
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class HttpUtilsProxy extends KOkHttpUtil {
    OkHttpClient clientProxy;
    private KNConfig knConfig;
    private String TAG = "HttpProxy";

    public HttpUtilsProxy(KNConfig config) {
        super(config);
        this.knConfig = config;
    }

    public static void hack(KSync kSync, KNConfig config) throws NoSuchFieldException, IllegalAccessException {
        HttpUtilsProxy proxy = new HttpUtilsProxy(config);
        Field field = kSync.getClass().getDeclaredField("httpUtil");
        field.setAccessible(true);
        field.set(kSync, proxy);
        Field field1 = KSyncService.class.getDeclaredField("httpUtil");
        field1.setAccessible(true);
        Field sField = kSync.getClass().getDeclaredField("service");
        sField.setAccessible(true);
        field1.set(sField.get(kSync), proxy);
        field1.setAccessible(false);
        sField.setAccessible(false);
        field.setAccessible(false);
    }

    @Override
    public String httpReq(String url, KMap params, String bodyJsonData) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(url);
        setUrlParamsProxy(urlBuilder, params);
        OkHttpClient mOkHttpClient = getClientProxy();
        Request.Builder builder = new Request.Builder();
        builder.url(urlBuilder.toString());

        if (!TextUtils.isEmpty(bodyJsonData)) {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, gzip(bodyJsonData));
            builder.addHeader("content-encoding", "gzip");
            builder.post(requestBody);
        }

        Response response = mOkHttpClient.newCall(builder.build()).execute();
        //   String responseContent = ungzip(response.body().byteStream());
        String responseContent = response.body().string();
        if (knConfig.isDebug()) {
            Log.i(TAG, "请求地址:" + urlBuilder.toString());
            Log.i(TAG, "请求成功:" + responseContent);
        }
        if (!response.isSuccessful()) {
            if (knConfig.isDebug()) {
                Log.i(TAG, "请求失败:Code:" + response.code() + " Message:" + response.message() + " Content:" + responseContent);
            }
            //抛出异常非常重要,不要注释掉
            throw new KSyncException("请求失败:Code:" + response.code() + " Message:" + response.message() + " Content:" + responseContent);
        }
        return responseContent;
    }

    /**
     * 设定请求参数
     * * @param params
     *
     * @return
     */
    private void setUrlParamsProxy(StringBuilder urlBuilder, KMap params) {
        if (params == null) {
            params = new KMap();
        }
        params.put("appid", knConfig.getAppid());
        params.put("clientid", knConfig.getClientid());
        params.put("avaialbeMemory", "0");
        urlBuilder.append("?");
        for (String key : params.keySet()) {
            urlBuilder.append("&").append(key).append("=").append(params.get(key));
        }
        urlBuilder.append("&t=").append(System.currentTimeMillis());
    }

    private OkHttpClient getClientProxy() {
        if (clientProxy != null) {
            return clientProxy;
        }
        File sdcache = knConfig.getContext().getExternalCacheDir();
        int cacheSize = 30 * 1024 * 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(120, TimeUnit.SECONDS);
        builder.writeTimeout(120, TimeUnit.SECONDS);
        builder.readTimeout(120, TimeUnit.SECONDS);
        builder.cache(new Cache(sdcache, cacheSize));


        if (knConfig.isHttps()) {
            X509TrustManager trustManager;
            SSLSocketFactory sslSocketFactory;
            InputStream caInputStream = null;
            InputStream bksInputStream = null;
            try {
                caInputStream = knConfig.getContext().getAssets().open(knConfig.getTrustCAName()); // 得到证书的输入流
                bksInputStream = knConfig.getContext().getAssets().open(knConfig.getClientKeyStoreName()); //客户端证书

                //服务器端授信证书根CA证书
                trustManager = trustManagerForCertificatesProxy(caInputStream);//以流的方式读入证书
                SSLContext sslContext = SSLContext.getInstance("TLS");

                //初始化keystore（客户端证书）
                KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                clientKeyStore.load(bksInputStream, knConfig.getClientKeyStorePwd().toCharArray());
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(clientKeyStore, knConfig.getClientKeyStorePwd().toCharArray());


                sslContext.init(keyManagerFactory.getKeyManagers(), new TrustManager[]{trustManager}, null);
                sslSocketFactory = sslContext.getSocketFactory();

                clientProxy = builder.sslSocketFactory(sslSocketFactory, trustManager).build();

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            } finally {
                try {
                    if (caInputStream != null) {
                        caInputStream.close();
                    }
                    if (bksInputStream != null) {
                        bksInputStream.close();
                    }
                } catch (IOException e) {
                    Log.d("Tag", e.getMessage());
                }
            }
        } else {
            clientProxy = builder.build();
        }

        return clientProxy;
    }

    private X509TrustManager trustManagerForCertificatesProxy(InputStream in)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        // Put the certificates a key store.
        char[] password = "cnksi.com".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStoreProxy(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }


    /**
     * 添加password
     *
     * @param password
     * @return
     * @throws GeneralSecurityException
     */
    private KeyStore newEmptyKeyStoreProxy(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); // 这里添加自定义的密码，默认
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}

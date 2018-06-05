package com.cnksi.nari;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.nari.model.BDPackage;
import com.cnksi.nari.type.Regulation;
import com.cnksi.nari.utils.DecodeUtils;
import com.cnksi.nari.utils.DeviceHost;
import com.cnksi.nari.utils.Globalforlogin;
import com.cnksi.nari.utils.JsonUtil;
import com.cnksi.nari.utils.NariDataManager;
import com.cnksi.nari.utils.PMSException;
import com.cnksi.nari.utils.ResultSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/7/21 14:11
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class NARIHelper {
    public static final String MIP_SERVER_IP_KEY = "mip_server_ip_key";
    public static final String PMS_SERVER_IP_KEY = "pms_server_ip_key";
    public static String MIP_SERVER = "http://127.0.0.1:18889";
    public static String PMS_SERVER = "http://127.0.0.1:18011";

    private static Context context;
    private static String name;
    private static String password;
    private static SharedPreferences sp;
    private static String token;
    private static OkHttpClient client;
    private static String baseFolder;
    private static int retryCount = 0;

    public static void init(Context context, String account, String password, String baseFolder) {
        NariDataManager.init(baseFolder);
        NARIHelper.context = context;
        NARIHelper.name = account;
        NARIHelper.password = password;
        NARIHelper.baseFolder = baseFolder;
        NARIHelper.sp = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        NARIHelper.token = sp.getString(account, "");
        NARIHelper.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
    }


    public static void login() throws IOException, PMSException {
        Log.e("nari", "正在登陆！");
        RequestBody body = MultipartBody.create(MediaType.parse("application/json"), buildLoginJson(name, password));
        Request request = new Request.Builder()
                .url(MIP_SERVER + "/clientaccess")
                .post(body)
                .addHeader("cmd", "LOGIN")
                .build();
        Response response = client.newCall(request).execute();
        JSONObject jsonObject = JSON.parseObject(response.body().string());
        token = jsonObject.getString("sessionid");
        if (token == null) {
            Log.e("nari", jsonObject.toString());
            String msg = jsonObject.getString("resultmessage");
            msg = StringUtilsExt.nullTo(msg, "登录失败！");
            throw new PMSException(msg);
        }
        sp.edit().putString(name, token).apply();
    }


    public Response uploadPackageFile(String fileName, String packageId) throws IOException {
        RequestBody body = new MultipartBody.Builder().
                setType(MultipartBody.FORM)
                .addPart(MultipartBody.Part.createFormData("packageFile", "data.zip",
                        RequestBody.create(MediaType.parse("application/octet-stream"), new File(fileName))))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"packageID\""),
                        RequestBody.create(MediaType.parse("text/plain"), packageId))
                .build();
        Request request = new Request.Builder()
                .url(PMS_SERVER + "/MIPService/rest/0/package/upload")
                .addHeader("Token", token)
                .addHeader("UserName", name)
                .post(body).build();
        return client.newCall(request).execute();
    }

    public static boolean uploadPackageFile(File zipFile, String packageID) throws Exception {
        HttpClient httpsClient = new DefaultHttpClient();
        httpsClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Integer.valueOf(120000));
        httpsClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, Integer.valueOf(120000));
        HttpPost httpPost = new HttpPost(PMS_SERVER + "/MIPService/rest/0/package/upload");
        httpPost.addHeader("Token", token);
        httpPost.addHeader("UserName", name);
        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart("packageFile", new FileBody(zipFile));
        multipartEntity.addPart("packageID", new StringBody(packageID, Charset.forName("UTF-8")));

        httpPost.setEntity(multipartEntity);
        HttpResponse execute = httpsClient.execute(httpPost);
        if (200 == execute.getStatusLine().getStatusCode()) {
            execute.getEntity();
            if (multipartEntity != null) {
                System.out.println(multipartEntity.getContentLength());
                return true;
            }
            return true;
        }
        throw new Exception("文件上传失败，状态码：" + execute.getStatusLine() + " 原因：" + execute.getStatusLine().getReasonPhrase());
    }


    private static String buildLoginJson(String name, String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("version", "1.0.0");
        map.put("loginname", name);
        map.put("password", DecodeUtils.EncodePassword(password));
        map.put("validatecode", "");
        map.put("usevalidatecode", false);
        map.put("useouternetwork", false);
        map.put("deviceid", new DeviceHost(context).getDeviceID());
        map.put("ecid", "default");
        map.put("esn", Globalforlogin.getInstance().getESN(context));
        map.put("imsi", Globalforlogin.getInstance().getimsi(context));
        map.put("ostype", "android");
        map.put("ispad", "0");
        map.put("osversion", Globalforlogin.getInstance().getphoneModel());
        map.put("hsetname", "Xiaomi");
        map.put("hsetmodel", "mi max");
        map.put("resolution", "1080*1920");
        map.put("serverip", "127.0.0.1");
        map.put("network", "4G");
        map.put("wifimac", Globalforlogin.getInstance().getDeviceMac(context));
        map.put("clientid", "nari.mip.console");
        map.put("clientversion", Globalforlogin.getInstance().getClientVersion(context));
        map.put("isroot", "0");
        map.put("devicetoken", "");
        map.put("dpi", "xhdpi");
        map.put("appgprsflow", "0.0");
        map.put("devicegprsflow", 0);
        return JSON.toJSONString(map);
    }

    public static List<BDPackage> getPackage(Regulation regulation) throws IOException, PMSException {
        if (TextUtils.isEmpty(token)) login();
        RequestBody body = new FormBody.Builder()
                .add("appURL", "[\"app://nari.pms.app.xunshi\",\"app://PMS.PDA/GG/LXZYB\"]")
                .add("regulationID", regulation.toString())
                .add("version", "2.0.0").build();
        Request request = new Request.Builder()
                .url(PMS_SERVER + "/MIPService/rest/1/user/" + name + "/packages")
                .addHeader("Token", token)
                .post(body)
                .build();
        Response rs = client.newCall(request).execute();
        String src = rs.body().string();
        byte[] bytes = src.getBytes("latin1");
        if (bytes.length > 2 && (bytes[0] == 31 && bytes[1] == -117)) {
            src = GZIPDecode(bytes);
            JSONObject object = JSON.parseObject(src);
            JSONArray jsonArray = object.getJSONArray("packages");
            return jsonArray.toJavaList(BDPackage.class);
        }
        if (src.contains("令牌")) {
            token = null;
            if (retryCount > 0) {
                retryCount = 0;
                throw new PMSException("令牌短时间失效，MIP服务可能异常！");
            }
            retryCount++;
            return getPackage(regulation);
        } else {
            throw new PMSException(src);
        }
    }

    private static String GZIPDecode(byte[] bytes) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream ungzip = new GZIPInputStream(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        int n;
        while ((n = ungzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return out.toString();
    }

    public static String getBDPackageDetail(BDPackage bdPackage) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("userName", name)
                .add("packageID", JsonUtil.toJSONString(new String[]{bdPackage.packageID}))
                .build();
        Request request = new Request.Builder()
                .url(PMS_SERVER + "/MIPService/rest/0/user/package/details")
                .addHeader("Token", token)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        String str = response.body().string();
        Log.e("TAG", str);
        return str;
    }


    public static ResultSet<String> downloadBDPackage(BDPackage bdPackage) {
        Request request = new Request.Builder()
                .addHeader("Token", token)
                .addHeader("UserName", name)
                .url(PMS_SERVER + "/MIPService/rest/0/package/" + bdPackage.packageID).build();
        Response response;
        ResultSet<String> resultSet = new ResultSet<>();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            resultSet.setError(e, "请求出错！");
            return resultSet;
        }
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        String path = baseFolder + bdPackage.packageID + "/";
        try {
            long total = response.body().contentLength();
            is = response.body().byteStream();
            File file = new File(path, "data.zip");
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            long sum = 0;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / total * 100);
                Log.e("nari", "progress=" + progress);
            }
            fos.flush();
            Log.e("nari", "文件下载成功");
            UnZipFolder(file.getAbsolutePath(), path);
            Log.e("nari", "解压完成！！");
            resultSet.setSuccess(path, "下载并解压完成！");
        } catch (IOException e) {
            e.printStackTrace();
            resultSet.setError(e, "下载出错！");
        } finally {
            IOUtils.close(is);
            IOUtils.close(fos);
        }
        return resultSet;
    }


    public static String deletePackageFromServer(String packageId) throws IOException {

        RequestBody body = new FormBody.Builder()
                .add("userName", name)
                .add("packageID", JsonUtil.toJSONString(new String[]{packageId}))
                .build();
        Request request = new Request.Builder()
                .url(PMS_SERVER + "/MIPService/rest/0/package/delete")
                .addHeader("Token", token)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        String str = response.body().string();
        Log.e("TAG", str);
        JSONArray array = JSONArray.parseArray(str);
        JSONObject jsonObject = (JSONObject) array.get(0);
        JSONObject object = (JSONObject) jsonObject.get(packageId);
        String success = (String) object.get("isSuccessful");
        if (success.equalsIgnoreCase("true")) {
            return "OK:" + "PMS服务器数据删除成功";
        } else {
            return "FAIL:" + object.getString("exceptionInfo" + ",PMS服务器数据删除失败");
        }
    }


    public static void UnZipFolder(String zipFileString, String outPathString) throws IOException {
        ZipInputStream inZip = null;
        FileOutputStream out = null;
        ZipEntry zipEntry;
        String szName;
        try {
            inZip = new ZipInputStream(new FileInputStream(zipFileString));
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (!szName.contains("data.db3")) {
                    continue;
                }
                File file = new File(outPathString + File.separator + szName);
                file.createNewFile();
                out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }
        } finally {
            IOUtils.close(inZip);
            IOUtils.close(out);
        }
    }


    public static String query(String sql) throws IOException {
        String encodeSQL = DecodeUtils.EncodeSql(sql);
        String integrity = DecodeUtils.getIntegrity(encodeSQL, token);
        RequestBody requestBody = new FormBody.Builder()
                .add("sysID", "pms")
                .add("maxRows", "50")
                .add("integrity", integrity)
                .add("sql", encodeSQL)
                .add("encrypt", "false")
                .add("version", "2.0.0")
                .build();
        Request request = new Request.Builder()
                .url(PMS_SERVER + "/MIPService/rest/0/table/query")
                .addHeader("Token", token)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String rs = response.body().string();
        byte[] bytes = rs.getBytes("latin1");
        //GZIP文件头
        if (bytes.length > 2 && (bytes[0] == 0x1F && bytes[1] == 0x8B)) {
            rs = GZIPDecode(bytes);
        }
        Log.e("TAG", rs);
        return rs;
    }

    public static boolean isNetWorkException(Exception e) {
        return e != null && (e.getClass().getName().startsWith("java.net") || e.getClass().getName().contains("http"));
    }

}

package com.netease.qa.emmagee.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.netease.qa.emmagee.service.EmmageeService;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class HttpUtils {
    public static int testSuitId;

    private static String createUrl = "";
    private static InputStream in;
    private static BufferedReader reader;
    private static String synchronize = "dragon";

    private static HttpURLConnection connection = null;


    public static String createTestSuit(final String param_json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (synchronize) {
                    HttpURLConnection connection = null;
                    try {
                        Thread.currentThread().setName("createTestSuit");
                        createUrl = "http://" + Settings.serverIp + ":" + Settings.serverPort + "/createTestSuit?data=" + URLEncoder.encode(param_json, "utf-8");
                        URL url = new URL(createUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(3000);
                        int responsecode = connection.getResponseCode();
                        Log.v(Settings.LOG_TAG, "createTestSuit url:" + createUrl + "," + responsecode);
                        if (responsecode == 200) {
                            in = connection.getInputStream();
                            reader = new BufferedReader(new InputStreamReader(in));
                            StringBuffer response = new StringBuffer();
                            String line = "";
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
//                            Log.v(Settings.LOG_TAG, "createTestSuit response:" + response.toString());
                            in.close();
                            reader.close();
                            JSONObject json = new JSONObject(response.toString());
                            testSuitId = json.getInt("testSuitId");
                            EmmageeService.bw.write("Test SuitId" + Constants.COMMA + testSuitId + Constants.LINE_END);
                        }
                    } catch (IOException e) {
                        Log.v(Settings.LOG_TAG, e.toString());
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.v(Settings.LOG_TAG, e.toString());
                        e.printStackTrace();
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }

                    }
                }
            }
        }).start();

        return "";
    }

    public static String postAppPerfData(final String param_json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (synchronize) {
                    Thread.currentThread().setName("postAppPerfData");
                    HttpURLConnection connection = null;
                    try {
                        createUrl = "http://" + Settings.serverIp + ":" + Settings.serverPort + "/postAppPerfData?data=" + URLEncoder.encode(param_json, "utf-8");
                        URL url = new URL(createUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(3000);
                        int responsecode = connection.getResponseCode();
//                        Log.v(Settings.LOG_TAG, "postAppPerfData url:" + createUrl + "," + responsecode);
                    } catch (IOException e) {
                        Log.v(Settings.LOG_TAG, e.toString());
                        e.printStackTrace();
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }

                    }
                }
            }
        }).start();

        return "";
    }

    public static String stopTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (synchronize) {
                    Thread.currentThread().setName("stopTest");
                    HttpURLConnection connection = null;
                    try {
                        createUrl = "http://" + Settings.serverIp + ":" + Settings.serverPort + "/stopTest?testSuitId=" + testSuitId;
                        URL url = new URL(createUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(3000);
                        int responsecode = connection.getResponseCode();
//                        Log.v(Settings.LOG_TAG, "stopTest url:" + createUrl + "," + responsecode);
                    } catch (IOException e) {
                        Log.v(Settings.LOG_TAG, e.toString());
                        e.printStackTrace();
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }

                    }
                }
            }
        }).start();

        return "";
    }

    public static String postLog(final String param_json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    createUrl = "http://" + Settings.serverIp + ":" + Settings.serverPort + "/postLog?data=" + URLEncoder.encode(param_json, "utf-8");
                    URL url = new URL(createUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);
                    int responsecode = connection.getResponseCode();
                    if (responsecode == 200) {

                    }
                } catch (IOException e) {
                    Log.v(Settings.LOG_TAG, e.toString());
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }

                }

            }
        }).start();

        return "success";
    }

    public static String uploadFile(final int logType, final String filePath, final String filename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(filePath);
                    if (file.exists()) {
                        createUrl = "http://" + Settings.serverIp + ":" + Settings.serverPort + "/uploadFile";
                        String end = "\r\n";
                        String twoHyphens = "--";
                        String boundary = "----WebKitFormBoundaryczjBYzhshjq4MPAJ";
                        try {
                            URL url = new URL(createUrl);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();

                            con.setDoInput(true);
                            con.setDoOutput(true);
                            con.setUseCaches(false);

                            con.setRequestMethod("POST");

                            con.setRequestProperty("Connection", "Keep-Alive");
                            con.setRequestProperty("Charset", "UTF-8");
                            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                            DataOutputStream ds = new DataOutputStream(con.getOutputStream());

                            ds.writeBytes(twoHyphens + boundary + end);
                            ds.writeBytes("Content-Disposition: form-data; " + "name=\"testSuitId\"" + end + end + HttpUtils.testSuitId);
                            ds.writeBytes(end);

                            ds.writeBytes(twoHyphens + boundary + end);
                            ds.writeBytes("Content-Disposition: form-data; " + "name=\"logType\"" + end + end + logType);
                            ds.writeBytes(end);

                            ds.writeBytes(twoHyphens + boundary + end);
                            ds.writeBytes("Content-Disposition: form-data; " + "name=\"log\"; filename=\"" + filename + "\"" + end);
                            ds.writeBytes(end);

                            FileInputStream fStream = new FileInputStream(filePath);

                            int bufferSize = 1024;
                            byte[] buffer = new byte[bufferSize];
                            int length = -1;

                            while ((length = fStream.read(buffer)) != -1) {

                                ds.write(buffer, 0, length);
                            }
                            ds.writeBytes(end);
                            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

                            fStream.close();
                            ds.flush();
                            InputStream is = con.getInputStream();
                            int ch;
                            StringBuffer b = new StringBuffer();
                            while ((ch = is.read()) != -1) {
                                b.append((char) ch);
                            }
                            Log.v(Settings.LOG_TAG, "上传成功:" + b.toString().trim());
                            ds.close();
                        } catch (Exception e) {
                            Log.v(Settings.LOG_TAG, "上传失败:" + e);
                        }
                    }else{
                        Log.v(Settings.LOG_TAG, "file not exists. filepath="+filePath);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }

                }

            }
        }).start();

        return "success";
    }

    public void socket(){
        PipedInputStream pin = new PipedInputStream();
    }
}

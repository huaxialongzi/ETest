package com.netease.qa.emmagee.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.netease.qa.emmagee.service.EmmageeService;

import android.util.Log;

public class HttpUtils {
    public static int testSuitId;

    private static String createUrl = "";
    private static InputStream in;
    private static BufferedReader reader;
    private static String synchronize = "dragon";

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

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
                            Log.v(Settings.LOG_TAG, "createTestSuit response:" + response.toString());
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
                        Log.v(Settings.LOG_TAG, "postAppPerfData url:" + createUrl + "," + responsecode);
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
                        Log.v(Settings.LOG_TAG, "stopTest url:" + createUrl + "," + responsecode);
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

    public static String postLog(boolean isServiceStop, final boolean isRoot) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("postLog");
                HttpURLConnection connection = null;
                BufferedReader bufferedReader = null;
                Process process = null;
                DataOutputStream os = null;
                int result = -1;
                try {
//                    Thread.currentThread().sleep(3000);
                    String logcatCommand = "logcat |grep --line-buffered -E \"GreenDaoHelper_insert_e|Displayed\" | grep -v -E \"show|logs|back|info\"";
//						String logcatCommand = "logcat |grep app";
                    process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
                    os = new DataOutputStream(process.getOutputStream());
                    os.write(logcatCommand.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
//						os.writeBytes(COMMAND_EXIT);
                    os.flush();
//						result = process.waitFor();

                    bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//						StringBuilder stringBuilder = new StringBuilder();
                    String line = "";

                    while (true) {
                        bufferedReader.mark(9999);
                        line = bufferedReader.readLine();
                        if (line != null) {
                            if ((line.contains("GreenDaoHelper_insert_e:: {\"appv\":") && line.contains("click")) || line.contains("Displayed")) {

                            }
                            EmmageeService.bw.write(line + Constants.LINE_END);
                        } else {
                            bufferedReader.reset();
                            Thread.currentThread().sleep(Settings.SLEEP_TIME);
                            continue;
                        }
//                        Thread.currentThread().sleep(2000);
                    }

//						createUrl = "http://" + Settings.serverIp + ":" + Settings.serverPort + "/postLog?data=" + URLEncoder.encode(log_json, "utf-8");
//						URL url = new URL(createUrl);
//						connection = (HttpURLConnection) url.openConnection();
//						connection.setRequestMethod("GET");
//						connection.setConnectTimeout(3000);
//						connection.setReadTimeout(3000);
//						int responsecode = connection.getResponseCode();
//						Log.v(Settings.LOG_TAG, "postLog url:" + createUrl + "," + responsecode);
                } catch (IOException e) {
                    Log.v(Settings.LOG_TAG, e.toString());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Log.v(Settings.LOG_TAG, e.toString());
                    e.printStackTrace();
                } finally {
                    try {
                        if (process != null) {
                            process.destroy();
                        }

                        if (connection != null) {
                            connection.disconnect();
                        }
                        os.close();
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }
        }).start();

        return "";
    }
}

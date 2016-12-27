package com.netease.qa.emmagee.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.netease.qa.emmagee.R;
import com.netease.qa.emmagee.utils.HttpUtils;
import com.netease.qa.emmagee.utils.Settings;

public class ServerSettingsActivity extends Activity {
	private static final String BLANK_STRING = "";

	private EditText edtip;
	private EditText edtport;

	private Button serverTest;

	private final static int FAIL = 0;
	private final static int SUCCESS = 200;

	private String serverip, serverport;
	private TextView title;
	private String synchronize = "dragon";

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.server_settings);

		edtip = (EditText) findViewById(R.id.server_ip);
		edtport = (EditText) findViewById(R.id.server_port);
		title = (TextView) findViewById(R.id.nb_title);
		serverTest = (Button) findViewById(R.id.btn_server_test);

		LinearLayout layGoBack = (LinearLayout) findViewById(R.id.lay_go_back);
		LinearLayout layBtnSet = (LinearLayout) findViewById(R.id.lay_btn_set);

		title.setText(R.string.server_settings);

		SharedPreferences preferences = Settings.getDefaultSharedPreferences(getApplicationContext());
		serverip = preferences.getString(Settings.KEY_SERVER_IP, BLANK_STRING);
		serverport = preferences.getString(Settings.KEY_SERVER_PORT, BLANK_STRING);

		edtip.setText(serverip);
		edtport.setText(serverport);

		layGoBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ServerSettingsActivity.this.finish();
			}
		});

		layBtnSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				serverip = edtip.getText().toString().trim();
				if (BLANK_STRING.equals(serverip) || !checkIpFormat(serverip)) {
					Toast.makeText(ServerSettingsActivity.this, getString(R.string.format_incorrect_ip), Toast.LENGTH_LONG).show();
					return;
				}
				serverport = edtport.getText().toString().trim();
				if (BLANK_STRING.equals(serverport)) {
					Toast.makeText(ServerSettingsActivity.this, getString(R.string.format_incorrect_port), Toast.LENGTH_LONG).show();
					return;
				}
				SharedPreferences preferences = Settings.getDefaultSharedPreferences(getApplicationContext());
				Editor editor = preferences.edit();
				editor.putString(Settings.KEY_SERVER_IP, serverip);
				editor.putString(Settings.KEY_SERVER_PORT, serverport);
				editor.commit();
				Settings.serverIp = serverip;
				Settings.serverPort = serverport;
				HttpUtils.createTestSuit("");
				Toast.makeText(ServerSettingsActivity.this, getString(R.string.save_success_toast), Toast.LENGTH_LONG).show();
				// Intent intent = new Intent();
				// setResult(Activity.RESULT_FIRST_USER, intent);
				// ServerSettingsActivity.this.finish();
			}
		});

		serverTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				testServer();
			}
		});
		
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case FAIL:
					Toast.makeText(ServerSettingsActivity.this, "连接失败", Toast.LENGTH_LONG).show();
					break;
				case SUCCESS:
					Toast.makeText(ServerSettingsActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}
			}
		};
	}

	/**
	 * check ip format
	 * 
	 * @return true: valid ip address
	 */
	private boolean checkIpFormat(String serverip) {
		if (serverip.length() < 7 || serverip.length() > 15) {
			return false;
		}
		/**
		 * 判断IP格式和范围
		 */
		String rexp = "^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$";
		Pattern pat = Pattern.compile(rexp);
		Matcher mat = pat.matcher(serverip);
		boolean ipAddress = mat.find();
		return ipAddress;
	}

	private void testServer() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (synchronize) {
					HttpURLConnection connection = null;
					String createUrl = "http://" + Settings.serverIp + ":" + Settings.serverPort + "/testConnect";
					try {
						Log.v(Settings.LOG_TAG, "server test url:" + createUrl);
						URL url = new URL(createUrl);
						connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						connection.setConnectTimeout(3000);
						connection.setReadTimeout(3000);
						int responsecode = connection.getResponseCode();
						Log.v(Settings.LOG_TAG, "server test url:" + createUrl + "," + responsecode);
						InputStream in = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						String line = "";
						line = reader.readLine();
						Log.v(Settings.LOG_TAG, "server test response:" + line);
						in.close();
						reader.close();
						Message message = new Message();
						message.what = SUCCESS;
						message.obj = line + ", code：" + responsecode;
						handler.sendMessage(message);
					} catch (Exception e) {
						Message message = new Message();
						message.what = FAIL;
						handler.sendMessage(message);
						e.printStackTrace();
					} finally {
						if (connection != null) {
							connection.disconnect();
						}
					}
				}

			}
		}).start();
	}
}

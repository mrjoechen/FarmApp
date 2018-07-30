package com.xindany.tao_fen_ny;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NewMainActivity extends Activity {
	private View view;

	int selected_y_begin = -1;
	int selected_y_end = -1;
	int selected_gongxu = -1;
	int selected_caipin = -1;
	Button btn_select_gongxu;
	Button btn_select_caipin;
	Button btn_selectarea;
	Button btn_send;
	LinearLayout liner_caipin;

	String [] gongxus = new String[] { "播种", "浇水", "耕地", "除草" };
	int [] gongxuIds = new int[] { 2, 4, 1, 8 };
	String [] caipins = new String[] { "生菜", "芹菜", "菠菜", "萝卜", "青菜" };
	int [] caipinIds = new int[] { 0, 1, 2, 3, 4 };
	String cmd = "";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setNoTitle();
		setFullscreen();
		view = View.inflate(this, R.layout.new_main_activity, null);
		setContentView(view);

		
		findView();
		

	}
	
	
	void findView(){
		liner_caipin = ((LinearLayout)this.findViewById(R.id.liner_caipin));
		btn_select_gongxu = ((Button)this.findViewById(R.id.btn_select_gongxu));
		btn_select_caipin = ((Button)this.findViewById(R.id.btn_select_caipin));
		btn_selectarea = ((Button)this.findViewById(R.id.btn_selectarea));
		btn_send = ((Button)this.findViewById(R.id.btn_send));
		
		btn_select_gongxu.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(final View v) {
				new AlertDialog.Builder(v.getContext()).setTitle("请选择操作工序").setIcon(
					android.R.drawable.ic_dialog_info).setItems(
							gongxus,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									if(which >= 1)
										liner_caipin.setVisibility(View.GONE);
									else{
										liner_caipin.setVisibility(View.VISIBLE);
										btn_select_caipin.setText("请选择");
										
									}
									selected_gongxu = gongxuIds[which];
									selected_caipin = -1;
									btn_select_gongxu.setText(gongxus[which]);
									dialog.dismiss();
								}
					}).setNegativeButton("取消", null).show();
			}
		});
		
		btn_select_caipin.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(final View v) {
				new AlertDialog.Builder(v.getContext()).setTitle("请选择种植菜品").setIcon(
					android.R.drawable.ic_dialog_info).setItems(
							caipins,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									selected_caipin = caipinIds[which];
									btn_select_caipin.setText(caipins[which]);
									dialog.dismiss();
								}
					}).setNegativeButton("取消", null).show();
			}
		});
		
		btn_selectarea.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(NewMainActivity.this, SelectAreaActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		btn_send.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if(selected_gongxu == -1){
					showMessage("请先选择操作工序");
					return;
				}
				if(selected_gongxu == 2 && selected_caipin == -1){
					showMessage("请先选择菜品种类");
					return;
				}
				if(selected_y_begin == -1 || selected_y_end == -1){
					showMessage("请先选择操作区域");
					return;
				}

				if(selected_gongxu == 4){	//浇水
					cmd = selected_gongxu + "," + "0" + "," + selected_y_begin + "," + selected_y_end;
				}
				if(selected_gongxu == 2){	//播种
					cmd = selected_gongxu + "," + selected_caipin + "," + selected_y_begin + "," + selected_y_end;
				}
				if(selected_gongxu == 1){	//耕地
					cmd = selected_gongxu + "," + selected_y_begin + "," + selected_y_end;
				}
				if(selected_gongxu == 8){	//除草
					cmd = String.valueOf(selected_gongxu);
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(NewMainActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle(R.string.app_name);
				builder.setMessage("是否要开始工作？");
				builder.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								new Thread(runnable).start();
								dialog.dismiss();
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				});
				builder.show();
			}
		});
	}
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);

	        if (requestCode == 1 )
	        {
	            if (resultCode == RESULT_OK)
	            {
					//获取返回信息
	            	selected_y_begin = data.getExtras().getInt("y_begin");
	            	selected_y_end = data.getExtras().getInt("y_end");
					btn_selectarea.setText("起始="+selected_y_begin +",结束="+selected_y_end);
	            }
	            else {
					Toast.makeText(NewMainActivity.this, "操作区域返回出错", Toast.LENGTH_SHORT);
	            }
	        }

	    }
	
	
	/**
	 * 读取流中的数据
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inputStream) throws Exception{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len=inputStream.read(buffer)) != -1){
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inputStream.close();
		return outStream.toByteArray();
	}
	
	
	
	Runnable runnable = new Runnable(){
	    @Override
	    public void run() {
	        // TODO: http request.
	    	try {
				submit();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	};
	
	private void submit() throws  IOException{
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", cmd);
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) HttpRequestUtil
					.sendGetRequest(
							"http://zhny.95yes.cn/sendcmd.ashx",
							params, null);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InputStream in = conn.getInputStream();
		String result = "";
		try {
			byte[]data = readStream(in);
			result = new String(data);// 把字符数组转换成字符串
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Looper.prepare();
				if (conn.getResponseCode() == 200){
				   
				    AlertDialog.Builder builder = new AlertDialog.Builder(NewMainActivity.this);
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setTitle(R.string.app_name);
					builder.setMessage(result);
					builder.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									
								}
							});
					
					builder.show();
				    
				}else{
					AlertDialog.Builder builder = new AlertDialog.Builder(NewMainActivity.this);
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setTitle(R.string.app_name);
					builder.setMessage("服务器请求失败");
					builder.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									
								}
							});
					
					builder.show();
				}
				Looper.loop();
	}
	
	
	private void showMessage(String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(NewMainActivity.this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_name);
		builder.setMessage(msg);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				});
		
		builder.show();
	}


	/**
	 * 全屏
	 */
	public void setFullscreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * 无标题
	 */
	public void setNoTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
}
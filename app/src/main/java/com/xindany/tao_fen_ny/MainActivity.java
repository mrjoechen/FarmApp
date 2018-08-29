package com.xindany.tao_fen_ny;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xindany.App;
import com.xindany.Config;
import com.xindany.LogoutTask;
import com.xindany.OpenCvUtil;
import com.xindany.socket.SocketServer;
import com.xindany.tao_fen_ny.scan.main.CaptureActivity;
import com.xindany.util.DeviceUtil;
import com.xindany.util.SPUtils;
import com.xindany.util.T;


public class MainActivity extends Activity implements View.OnClickListener{
	private View view;
	private AlphaAnimation aa;
	private String selectIndexs = ","; 
	private ImageView[] imgbtns;
	private ImageView select_imgbtn;
	String cmd = ",";
	private ImageView menuBtn;
	private DrawerLayout drawerLayout;
	private NavigationView navigationView;

    private NavigationView navigationView_right;
	private View headerView;

	private HandlerThread mHandlerThread = new HandlerThread("send-thread");
	private Handler mThreadHandler;
	private LinearLayout tiandi_bg;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setNoTitle();
//		setFullscreen();

		if (!mHandlerThread.isAlive()) {
			mHandlerThread.start();
			mThreadHandler = new Handler(mHandlerThread.getLooper());
		}else {
			mThreadHandler.removeCallbacksAndMessages(null);
		}

		view = View.inflate(this, R.layout.main, null);
		setContentView(view);
		findView();
	}


	void findView(){

		menuBtn = findViewById(R.id.iv_menu);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        navigationView = (NavigationView) findViewById(R.id.nav);
        navigationView_right = (NavigationView) findViewById(R.id.nav_right);

		tiandi_bg = findViewById(R.id.tiandi_bg);

		//获取头布局
		headerView = navigationView.getHeaderView(0);
		TextView tvIp = headerView.findViewById(R.id.tv_ip);
		tvIp.setText("当前手机IP："+ DeviceUtil.getIP());


		menuBtn.setOnClickListener(this);
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				//item.setChecked(true);
//				Toast.makeText(MainActivity.this,item.getTitle().toString(), Toast.LENGTH_SHORT).show();


				int itemId = item.getItemId();
				drawerLayout.closeDrawer(navigationView);
				switch (itemId){
					case R.id.farm:
						break;
					case R.id.setting:
						Intent intent = new Intent(MainActivity.this, Setting2Activity.class);
						startActivity(intent);
						break;
					case R.id.cam:
//						Intent intent1 = new Intent(MainActivity.this, VideoActivity.class);
//						startActivity(intent1);
						//启动播放页面
						PlayActivity.startPlayActivity(MainActivity.this, Config.APP_KEY, Config.ACCESS_KEY, Config.PLAY_URL_HD);
						break;
					case R.id.clear:
					    clear();
						break;
					case R.id.capture:

						if (ContextCompat.checkSelfPermission(MainActivity.this,
								Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
							ActivityCompat.requestPermissions(MainActivity.this,
									new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
						}else {
							Intent intent1 = new Intent(MainActivity.this, EZPlayActivity.class);
							startActivity(intent1);
						}

						break;
					case R.id.bond:
							new LogoutTask(MainActivity.this).execute();

						break;
//					case R.id.test:
//						Intent intent1 = new Intent(MainActivity.this, PicActivity.class);
//						startActivity(intent1);
//						break;
					default:
						break;
				}

				return true;
			}
		});


        final PlayFragment frament_play = (PlayFragment) getFragmentManager().findFragmentById(R.id.frament_play);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
//                if (drawerView.equals(navigationView_right)){
////                    PlayActivity.startPlayActivity(MainActivity.this, Config.APP_KEY, Config.ACCESS_KEY, Config.PLAY_URL_HD);
//                    if (frament_play != null){
//                        frament_play.start();
//                    }
//                }
				if (drawerView.equals(navigationView_right)){
					PlayActivity.startPlayActivity(MainActivity.this, Config.APP_KEY, Config.ACCESS_KEY, Config.PLAY_URL_HD);
					drawerLayout.closeDrawers();
				}

            }

            @Override
            public void onDrawerClosed(View drawerView) {
//                if (drawerView.equals(navigationView_right)){
//                    if (frament_play != null){
//                        frament_play.stop();
//                    }
//                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

		((TextView)findViewById(R.id.imgbtnbz)).setOnClickListener(new Button.OnClickListener() {

            private int type = 1;

            @Override
			public void onClick(final View v) {
				if(selectIndexs.length() == 1){
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setTitle(R.string.app_name);
					builder.setMessage("请选择要播种的农田！");
					builder.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									
								}
							});
					
					builder.show();
					return;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View view = inflater.inflate(R.layout.select_plant_type, null);

                final RadioGroup rg_select = view.findViewById(R.id.rg_select);


                builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle(R.string.app_name);
				builder.setMessage("是否要对选中的农田开始播种？");
				builder.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								sort();

								int checkedRadioButtonId = rg_select.getCheckedRadioButtonId();

								if (checkedRadioButtonId == R.id.rb_1){
									type = 1;
								}
								if (checkedRadioButtonId == R.id.rb_2){
									type = 2;
								}

								String selectid = selectIndexs.substring(1, selectIndexs.length() - 1);
								cmd = "1," + type + ","+ selectid.split(",").length + "," + selectid;
//								new Thread(runnable).start();

                                SPUtils.put(MainActivity.this, "data", selectIndexs);
								send();
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				});

                builder.setView(view);
				builder.show();
			}
		});
		((TextView)findViewById(R.id.imgbtnjs)).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if(selectIndexs.length() == 1){
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setTitle(R.string.app_name);
					builder.setMessage("请选择要浇水的农田！");
					builder.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									
								}
							});
					
					builder.show();
					return;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle(R.string.app_name);
				builder.setMessage("是否要对选中的农田开始浇水？");
				builder.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								sort();
								String selectid = selectIndexs.substring(1, selectIndexs.length() - 1);
								cmd = "2,0," + selectid.split(",").length + "," + selectid;
//								new Thread(runnable).start();
								send();
								
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

		((TextView)findViewById(R.id.imgbtnsf)).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if(selectIndexs.length() == 1){
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setTitle(R.string.app_name);
					builder.setMessage("请选择要施肥的农田！");
					builder.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();

								}
							});

					builder.show();
					return;
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle(R.string.app_name);
				builder.setMessage("是否要对选中的农田开始施肥？");
				builder.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								sort();
								String selectid = selectIndexs.substring(1, selectIndexs.length() - 1);
								cmd = "3,0," + selectid.split(",").length + "," + selectid;
//								new Thread(runnable).start();
								send();
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

		((TextView)findViewById(R.id.imgbtncc)).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if(selectIndexs.length() == 1){
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setTitle(R.string.app_name);
					builder.setMessage("请选择要除草的农田！");
					builder.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();

								}
							});

					builder.show();
					return;
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle(R.string.app_name);
				builder.setMessage("是否要对选中的农田开始除草？");
				builder.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								sort();
								String selectid = selectIndexs.substring(1, selectIndexs.length() - 1);
								cmd = "4,0," + selectid.split(",").length + "," + selectid;
//								new Thread(runnable).start();
								send();

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

		((TextView)findViewById(R.id.imgbtnsm)).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(final View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setTitle(R.string.app_name);
				builder.setMessage("是否要对选中的农田开始扫描？");
				builder.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								sort();
//								String selectid = selectIndexs.substring(1, selectIndexs.length() - 1);
								cmd = "5,0,0,0";
//								new Thread(runnable).start();
								if (ContextCompat.checkSelfPermission(MainActivity.this,
										Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
									ActivityCompat.requestPermissions(MainActivity.this,
											new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
								}else {
									Intent intent = new Intent(MainActivity.this, EZPlayActivity.class);
									startActivity(intent);
								}

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
		
		
		imgbtns = new ImageView[66];
		imgbtns[0] = (ImageView)this.findViewById(R.id.img_di_1);
		imgbtns[1] = (ImageView)this.findViewById(R.id.img_di_2);
		imgbtns[2] = (ImageView)this.findViewById(R.id.img_di_3);
		imgbtns[3] = (ImageView)this.findViewById(R.id.img_di_4);
		imgbtns[4] = (ImageView)this.findViewById(R.id.img_di_5);
		imgbtns[5] = (ImageView)this.findViewById(R.id.img_di_6);
		imgbtns[6] = (ImageView)this.findViewById(R.id.img_di_7);
		imgbtns[7] = (ImageView)this.findViewById(R.id.img_di_8);
		imgbtns[8] = (ImageView)this.findViewById(R.id.img_di_9);
		imgbtns[9] = (ImageView)this.findViewById(R.id.img_di_10);
		imgbtns[10] = (ImageView)this.findViewById(R.id.img_di_11);
		imgbtns[11] = (ImageView)this.findViewById(R.id.img_di_12);
		imgbtns[12] = (ImageView)this.findViewById(R.id.img_di_13);
		imgbtns[13] = (ImageView)this.findViewById(R.id.img_di_14);
		imgbtns[14] = (ImageView)this.findViewById(R.id.img_di_15);
		imgbtns[15] = (ImageView)this.findViewById(R.id.img_di_16);
		imgbtns[16] = (ImageView)this.findViewById(R.id.img_di_17);
		imgbtns[17] = (ImageView)this.findViewById(R.id.img_di_18);
		imgbtns[18] = (ImageView)this.findViewById(R.id.img_di_19);
		imgbtns[19] = (ImageView)this.findViewById(R.id.img_di_20);
		imgbtns[20] = (ImageView)this.findViewById(R.id.img_di_21);
		imgbtns[21] = (ImageView)this.findViewById(R.id.img_di_22);
		imgbtns[22] = (ImageView)this.findViewById(R.id.img_di_23);
		imgbtns[23] = (ImageView)this.findViewById(R.id.img_di_24);
		imgbtns[24] = (ImageView)this.findViewById(R.id.img_di_25);
		imgbtns[25] = (ImageView)this.findViewById(R.id.img_di_26);
		imgbtns[26] = (ImageView)this.findViewById(R.id.img_di_27);
		imgbtns[27] = (ImageView)this.findViewById(R.id.img_di_28);
		imgbtns[28] = (ImageView)this.findViewById(R.id.img_di_29);
		imgbtns[29] = (ImageView)this.findViewById(R.id.img_di_30);
		imgbtns[30] = (ImageView)this.findViewById(R.id.img_di_31);
		imgbtns[31] = (ImageView)this.findViewById(R.id.img_di_32);
		imgbtns[32] = (ImageView)this.findViewById(R.id.img_di_33);
		imgbtns[33] = (ImageView)this.findViewById(R.id.img_di_34);
		imgbtns[34] = (ImageView)this.findViewById(R.id.img_di_35);
		imgbtns[35] = (ImageView)this.findViewById(R.id.img_di_36);
		imgbtns[36] = (ImageView)this.findViewById(R.id.img_di_37);
		imgbtns[37] = (ImageView)this.findViewById(R.id.img_di_38);
		imgbtns[38] = (ImageView)this.findViewById(R.id.img_di_39);
		imgbtns[39] = (ImageView)this.findViewById(R.id.img_di_40);
		imgbtns[40] = (ImageView)this.findViewById(R.id.img_di_41);
		imgbtns[41] = (ImageView)this.findViewById(R.id.img_di_42);
		imgbtns[42] = (ImageView)this.findViewById(R.id.img_di_43);
		imgbtns[43] = (ImageView)this.findViewById(R.id.img_di_44);
		imgbtns[44] = (ImageView)this.findViewById(R.id.img_di_45);
		imgbtns[45] = (ImageView)this.findViewById(R.id.img_di_46);
		imgbtns[46] = (ImageView)this.findViewById(R.id.img_di_47);
		imgbtns[47] = (ImageView)this.findViewById(R.id.img_di_48);
		imgbtns[48] = (ImageView)this.findViewById(R.id.img_di_49);
		imgbtns[49] = (ImageView)this.findViewById(R.id.img_di_50);
		imgbtns[50] = (ImageView)this.findViewById(R.id.img_di_51);
		imgbtns[51] = (ImageView)this.findViewById(R.id.img_di_52);
		imgbtns[52] = (ImageView)this.findViewById(R.id.img_di_53);
		imgbtns[53] = (ImageView)this.findViewById(R.id.img_di_54);
		imgbtns[54] = (ImageView)this.findViewById(R.id.img_di_55);
		imgbtns[55] = (ImageView)this.findViewById(R.id.img_di_56);
		imgbtns[56] = (ImageView)this.findViewById(R.id.img_di_57);
		imgbtns[57] = (ImageView)this.findViewById(R.id.img_di_58);
		imgbtns[58] = (ImageView)this.findViewById(R.id.img_di_59);
		imgbtns[59] = (ImageView)this.findViewById(R.id.img_di_60);
		imgbtns[60] = (ImageView)this.findViewById(R.id.img_di_61);
		imgbtns[61] = (ImageView)this.findViewById(R.id.img_di_62);
		imgbtns[62] = (ImageView)this.findViewById(R.id.img_di_63);
		imgbtns[63] = (ImageView)this.findViewById(R.id.img_di_64);
		imgbtns[64] = (ImageView)this.findViewById(R.id.img_di_65);
		imgbtns[65] = (ImageView)this.findViewById(R.id.img_di_66);

        for(int i = 0; i < imgbtns.length; i++){
			imgbtns[i].setTag((i+1));
			imgbtns[i].setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(final View v) {
					select_imgbtn = (ImageView)v;
					if(selectIndexs.indexOf("," + v.getTag() + ",") > -1){
						if(select_imgbtn != null)
							select_imgbtn.setImageResource(R.drawable.null_data);
						selectIndexs = selectIndexs.replace("," + v.getTag() + ",", ",");
						return;
					}
					
					select_imgbtn.setImageResource(R.drawable.select_data);
					selectIndexs = selectIndexs +  v.getTag() + ",";
					
//					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//					builder.setIcon(android.R.drawable.ic_dialog_info);
//					builder.setTitle(R.string.app_name);
//					builder.setMessage("农田："+ v.getTag());
//					builder.setPositiveButton(R.string.sure,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									
//									
//								}
//							});
//					
//					builder.show();
				}
			});
		}
	}


    /**
	 * 读取流中的数据
	 * @param inputStream
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


    @Override
    protected void onResume() {
        super.onResume();

        refresh();

    }

	/**
	 * 根据 路径 得到 file 得到 bitmap
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public Bitmap decodeFile(String filePath) throws IOException{
		Bitmap b = null;
		int IMAGE_MAX_SIZE = 600;

		File f = new File(filePath);
		if (f == null){
			return null;
		}
		//Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;

		FileInputStream fis = new FileInputStream(f);
		BitmapFactory.decodeStream(fis, null, o);
		fis.close();

		int scale = 1;
		if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
			scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
		}

		//Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		fis = new FileInputStream(f);
		b = BitmapFactory.decodeStream(fis, null, o2);
		fis.close();
		return b;
	}

    private void refresh(){
        selectIndexs = (String) SPUtils.get(MainActivity.this, "data", "");

        for(int i = 0; i < imgbtns.length; i++){
            if (selectIndexs != null && selectIndexs.contains(","+ (i + 1) +",")){
                imgbtns[i].setImageResource(R.drawable.select_data);
            }else {
                imgbtns[i].setImageResource(R.drawable.null_data);
            }
        }

		File file = new File(Environment.getExternalStorageDirectory() + "/frambg.jpg");
		if (file.exists()){
			Drawable drawable = null;
			try {
				drawable = new BitmapDrawable(decodeFile(Environment.getExternalStorageDirectory() + "/frambg.jpg"));
				tiandi_bg.setBackground(drawable);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			tiandi_bg.setBackgroundResource(R.drawable.tian_bg);

		}
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

	private void send(){
		mThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				boolean b = SocketServer.getInstance().sendData(cmd);
				if (b){
					T.show(App.getInstance(), "发送成功！");
				}else {
					T.show(App.getInstance(), "失败");
				}
			}
		});
	}
	
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
			result = new String(data);  // 把字符数组转换成字符串
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Looper.prepare();
				if (conn.getResponseCode() == 200){
				   
				    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

	/**
	 * 冒泡法排序<br/>

	 * <li>比较相邻的元素。如果第一个比第二个大，就交换他们两个。</li>
	 * <li>对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。在这一点，最后的元素应该会是最大的数。</li>
	 * <li>针对所有的元素重复以上的步骤，除了最后一个。</li>
	 * <li>持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。</li>

	 *
	 * @param numbers
	 *            需要排序的整型数组
	 */
	public void bubbleSort(int[] numbers) {
		int temp; // 记录临时中间值
		int size = numbers.length; // 数组大小
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				if (numbers[i] < numbers[j]) { // 交换两数的位置
					temp = numbers[i];
					numbers[i] = numbers[j];
					numbers[j] = temp;
				}
			}
		}
	}

	public void sort(){
		if(selectIndexs.length() == 1){
			return;
		}
		String [] ss = selectIndexs.split(",");
		int[] numbers = new int[ss.length];
		for(int i = 0; i < ss.length; i++){
			if(ss[i] != null && !ss[i].equals(""))
				numbers[i] =  Integer.parseInt(ss[i]);
		}
		bubbleSort(numbers);
		selectIndexs = ",";
		for(int i = numbers.length - 1; i >= 0; i--){
			if(numbers[i] > 0)
				selectIndexs += numbers[i] + ",";
		}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.iv_menu://点击菜单，跳出侧滑菜单
				if (drawerLayout.isDrawerOpen(navigationView)){
					drawerLayout.closeDrawer(navigationView);
				}else{
					drawerLayout.openDrawer(navigationView);
				}
				break;
			default:
					break;
		}
	}

	private void modify(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_name);
		builder.setMessage("是否要对选中的农田开始播种？");
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sort();
						String selectid = selectIndexs.substring(1, selectIndexs.length() - 1);
						cmd = "1,0,"  + selectid.split(",").length + "," + selectid;
//								new Thread(runnable).start();
						send();
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

	private void clear(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_name);
		builder.setMessage("是否要清除数据 ？");
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
                        SPUtils.put(MainActivity.this, "data", "");

						File file = new File(Environment.getExternalStorageDirectory() + "/frambg.jpg");
						if (file.exists()){
							file.delete();
						}
						refresh();
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

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(navigationView_right) || drawerLayout.isDrawerOpen(navigationView)){
			drawerLayout.closeDrawers();
		}else {
			super.onBackPressed();

		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
		switch (requestCode){
			case 100:
				if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
					//这里已经获取到了摄像头的权限，想干嘛干嘛了可以
					//跳转到二维码扫描页面扫描二维码获取预览所需参数appkey、accesstoken、url
					Intent intent = new Intent(this, EZPlayActivity.class);
					startActivityForResult(intent,200);
				}else {
					//这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
					T.show(MainActivity.this,"请手动打开文件权限");
				}
				break;
			default:
				break;
		}

	}
}
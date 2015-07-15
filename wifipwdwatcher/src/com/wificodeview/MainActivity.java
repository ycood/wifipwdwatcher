package com.wificodeview;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
 private ListView wifilistlistview;//
 private TextView nomessage;
 private ProgressBar bar;
 private ArrayList<wifiMessage> wifilist;//储存wifi信息的list
 private wifilistAdapter listAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		wifilistlistview=(ListView) findViewById(R.id.wificodelist);
		nomessage=(TextView) findViewById(R.id.nomessage);
		bar=(ProgressBar) findViewById(R.id.progress1);
		wifilist=new ArrayList<wifiMessage>();
		filldata();//对数据进行填充
	}
	private void filldata()
	{
		bar.setVisibility(View.VISIBLE);
		DataOutputStream dos=null;
		DataInputStream  dis=null;
			String sourcefilename="/data/misc/wifi/wpa_supplicant.conf";
			File f=new File(getFilesDir(),"/wifi.conf");
			if(f.exists())
			{
				System.out.println(f.delete());
			}
			try {
			Process p=Runtime.getRuntime().exec("su");
			dos = new DataOutputStream(p.getOutputStream()); 
			dis=new DataInputStream(p.getInputStream());
            dos.writeBytes("cat "+sourcefilename+">"+ getFilesDir()+"/wifi.conf \n");
            dos.writeBytes("chmod 774 "+getFilesDir()+"/wifi.conf \n");
            dos.writeBytes("exit\n"); 
            	getfile();
            dos.flush(); 
            dos.close();
            dis.close();
            p.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Handler handler=new Handler();
			handler.postDelayed(new Thread(){
				@Override
				public void run() {
					super.run();
					getfile();
				}
			},6000);
	}
    private void getfile()
    {
    	 DataInputStream dis = null; 
    	File f=new File(getFilesDir(),"/wifi.conf");
       try{
    	if(f.exists())
        {
    		System.out.println("存在");
        	BufferedReader br=new BufferedReader(new FileReader(f));
        	StringBuffer result=new StringBuffer();
        	String s="";
        	while((s=br.readLine())!=null)
        	{
        		result.append(s);
        	}
        String wifiresult=result.toString();
             Pattern p=Pattern.compile("network=\\{(.*?)\\}");
             Matcher m=p.matcher(wifiresult);
             while(m.find())
             {
            	 wifiMessage wmsg=new wifiMessage();
            	 String line=m.group(1);
            	 if(line.contains("ssid=\"")&&line.contains("psk=\""))
            	 {
            		 Pattern k=Pattern.compile("ssid=\"(.*?)\\\"");
            		 Matcher w=k.matcher(line);
            		if( w.find())
            		{
            		 String i=w.group(1);
            		wmsg.wifiName=i;
            		}
            		 Pattern q=Pattern.compile("psk=\"(.*?)\\\"");
            		 Matcher o=q.matcher(line);
            		if(o.find())
            		{
            		 String j=o.group(1); 
            		wmsg.wifiCode=j;
            		}
            		wifilist.add(wmsg);
            	 }
            	 System.out.println(line+"\n");
             }
  		   br.close();
  		 if(wifilist.isEmpty())
	 		{
  			bar.setVisibility(View.GONE);
	 			nomessage.setVisibility(View.VISIBLE);
	 			nomessage.setText("列表为空，您的手机没有任何wifi的信息");
	 			Toast.makeText(this,"您的设备无wifi信息",Toast.LENGTH_SHORT).show();
	 		}
	 		else
	 		{
	 			bar.setVisibility(View.GONE);
	 		listAdapter=new wifilistAdapter(this,wifilist);
	 		wifilistlistview.setAdapter(listAdapter);
	 	 }
        }
        else
        {
        	System.out.println("没有");
        }
	} catch (Exception e) {
		e.printStackTrace();
	}
	 finally { 
            if (dis != null) { 
                try { 
                    dis.close(); 
                } catch (IOException e) { 
                    e.printStackTrace(); 
                } 
            }
	 }
		}
	/**
	 *wifilist的容器 
	 * 
	 * 
	 */
	private class wifilistAdapter extends BaseAdapter {
		private ArrayList<wifiMessage> list = null;
		private Context context;

		public wifilistAdapter(Context mContext,
				ArrayList<wifiMessage> list) {
			this.context = mContext;
			this.list = list;
		}

		@Override
		public int getCount() {
			return this.list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			wifiMessage wifimsg;
			View view;
			wifimsg = list.get(position);
			if (convertView == null) {
				view = View.inflate(context, R.layout.item_wifilist, null);
				holder = new ViewHolder();
				holder.tvwifiname = (TextView) view
						.findViewById(R.id.wifiname);
				holder.tvwificode = (TextView) view
						.findViewById(R.id.wificode);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			if (!TextUtils.isEmpty(wifimsg.wifiName)) {
				holder.tvwifiname.setText(wifimsg.wifiName);
			}
			if (!TextUtils.isEmpty(wifimsg.wifiCode)) {
				holder.tvwificode.setText(wifimsg.wifiCode);
			}
			return view;
		}

//		/**
//		 * 
//		 * 当ListView数据发生变化时,调用此方法来更新ListView
//		 * 
//		 * @param list
//		 */
//		public void updateListView(ArrayList<wifiMessage> messagelist) {
//			this.list = messagelist;
//			notifyDataSetChanged();
//		}
	}

	private class ViewHolder {
		TextView tvwifiname;
		TextView tvwificode;
	}
}

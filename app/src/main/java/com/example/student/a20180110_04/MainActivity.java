package com.example.student.a20180110_04;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    //ArrayAdapter<String> adapter;
    MyAdapter adapter;
    MyHandler dataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.listview);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it = new Intent(MainActivity.this, DetailActivity.class);
//                it.putExtra("link", dataHandler.links.get(i));    新創Mobile01NewsItem，因此改成下行那樣
                it.putExtra("link",dataHandler.newsItems.get(i).link);
                startActivity(it);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_reload:
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
//                (1)查銀行匯率用
//                String str_url = "http://rate.bot.com.tw/xrt?Lang=zh-TW";
                        String str_url = "https://www.mobile01.com/rss/news.xml";      //去Mobile01抓RRS的頭條
                        URL url = null;
                        try {
                            url = new URL(str_url);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();
                            InputStream inputStream = conn.getInputStream();
                            InputStreamReader isr = new InputStreamReader(inputStream);
                            BufferedReader br = new BufferedReader(isr);
                            StringBuilder sb = new StringBuilder();
                            String str;

                            while ((str = br.readLine()) != null)
                            {
                                sb.append(str);
                            }
                            String str1 = sb.toString();
                            Log.d("NET", str1);
//                    (1)查銀行匯率
//                    int index1 = str1.indexOf("日圓 (JPY)");
//                    int index2 = str1.indexOf("本行現金賣出", index1);
//                    int index3 = str1.indexOf("0.266", index2);
//                    Log.d("NET", "index1:" + index1 + "index2:" + index2 + "index3:" + index3);
//                    String data1 = str1.substring(index2+56, index2+61);       //String 類別 (class) 有 substring() 方法 (method) ，回傳指定範圍的子字串
//                    Log.d("NET", data1);
                            dataHandler = new MyHandler();
                            SAXParserFactory spf = SAXParserFactory.newInstance();      //本三行為固定寫法
                            SAXParser sp = spf.newSAXParser();
                            XMLReader xr = sp.getXMLReader();
                            xr.setContentHandler(dataHandler);
                            xr.parse(new InputSource(new StringReader(str1)));

                            br.close();
                            isr.close();
                            inputStream.close();

                            runOnUiThread(new Runnable() {
                                @Override
//                                新創Mobile01NewsItem，因此改成下面那樣
//                                public void run() {
//                                    adapter = new ArrayAdapter<String>(MainActivity.this,
//                                            android.R.layout.simple_list_item_1,
//                                            dataHandler.titles);
                                public void run(){
//                                    String data[] = new String[dataHandler.newsItems.size()];
//                                    for(int i=0;i<data.length;i++)
//                                    {
//                                        data[i] = dataHandler.newsItems.get(i).title;
//                                    }
//                                    adapter = new ArrayAdapter<String>(MainActivity.this,
//                                            android.R.layout.simple_list_item_1,data);
                                    adapter = new MyAdapter(MainActivity.this,
                                            dataHandler.newsItems);
                                    lv.setAdapter(adapter);
                                }
                            });
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}

//     抓出來的第一行Mobile01 本站新聞不是頭條，不想要的話，用之前教的arraylist，丟到listView裡
//     (因為本案例中頭條一定會包在Item裡，所以用arraylist去篩選出又有Item又有Title的內容)
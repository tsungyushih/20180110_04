package com.example.student.a20180110_04;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Student on 2018/1/10.
 */

//要讀RSSD的方法有很多，如DOM、SAX XML Parser等等，老師這裡教的是SAX，用最麻煩，最原始的寫法，下次再教用picasso寫
public class MyHandler extends DefaultHandler { //自己創一個MyHandler Class，要繼承DefaultHandler(org.xml.sax.helpers)，
    boolean isTitle = false;
    boolean isItem = false;     //抓出來的第一行Mobile01 本站新聞不是頭條，所以要避開他
    boolean isLink = false;
    boolean isDescription = false;
    StringBuilder linkSB = new StringBuilder();

    StringBuilder descSB = new StringBuilder();     //修復抓兩次description而閃退的bug

//    public ArrayList<String> titles = new ArrayList<>();  新創Mobile01NewsItem
    public ArrayList<Mobile01NewsItem> newsItems = new ArrayList<>();
//    public ArrayList<String> links = new ArrayList<>();   新創Mobile01NewsItem
    Mobile01NewsItem item;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        //Log.d("NET",qName);
//        因為寫多個if看起來很亂，所以改switch寫法
//        if(qName.equals("title"))
//        {
//            isTitle = true;
//        }
//        if(qName.equals("item"))
//        {
//            isItem = true;
//            item = new Mobile01NewsItem();
//        }
//        if(qName.equals("link"))
//        {
//            isLink = true;
//        }
        switch(qName)       //JAVA7之後，switch可放字串
        {
            case "title":
                isTitle = true;
                break;
            case "item":
                isItem = true;
                item = new Mobile01NewsItem();
                break;
            case "link":
                isLink = true;
                break;
            case "description":
                isDescription = true;

                descSB = new StringBuilder();       //修復抓兩次description而閃退的bug

                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
//        因為寫多個if看起來很亂，所以改switch寫法
//        if(qName.equals("title"))
//        {
//            isTitle = false;
//        }
//        if (qName.equals("item"))
//        {
//            isItem = false;
//            newsItems.add(item);
//        }
//        if(qName.equals("link"))
//        {
//            isLink = false;
//            if(isItem)
//            {
//                //links.add(linkSB.toString());     新創Mobile01NewsItem，因此改成下行那樣
//                item.link = linkSB.toString();
//                linkSB = new StringBuilder();
        switch(qName)
        {
            case "title":
                isTitle = false;
                break;
            case "item":
                isItem = false;
                Log.d("NET", "When add item, imgurl:" + item.imgurl);       //修復抓兩次description而閃退的bug的LOG
                newsItems.add(item);
                break;
            case "link":
                isLink = false;
                if (isItem)
                {
                    item.link = linkSB.toString();
                    linkSB = new StringBuilder();
                }
                break;
            case "description":
//                發生了重抓兩次description，導致圖抓也了兩次，第二次為空白，所以閃退的bug
//                isDescription = false;

                if (isItem)
                {
                    String str = descSB.toString();
                    Log.d("NET", "end Element str:" + str);
                    Pattern pattern = Pattern.compile("https.*jpg");
                    Matcher m = pattern.matcher(str);
                    String imgurl = "";
                    if (m.find())
                    {
                        imgurl = m.group(0);
                    }
                    str = str.replaceAll("<img.*/>", "");
                    item.description = str;
                    item.imgurl = imgurl;
                    Log.d("NET", "In Handler: Item.desc:" + item.description);
                    Log.d("NET", "In Handler: Item.imgurl:" + item.imgurl);
                }

                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if(isTitle && isItem)
        {
            Log.d("NET1",new String(ch,start,length));
//            titles.add(new String(ch,start,length));
            item.title = new String(ch,start,length);
        }
        if(isLink && isItem)
        {
            Log.d("NET2",new String(ch,start,length));
            linkSB.append(new String(ch,start,length));
        }
        if (isDescription && isItem)
        {
//            發生了重抓兩次description，導致圖抓也了兩次，第二次為空白，所以閃退的bug
//                //item.description = new String(ch, start, length);     把img裡不要的字給拿掉，改成下面3行，需要用到"正規表示式"
//            String str = new String(ch, start, length);
//
//                //用正規表示式把img裡的圖片網址抓出來
//            Pattern pattern = Pattern.compile("https.*jpg");
//            Matcher m = pattern.matcher(str);
//            String imgurl = "";
//            if(m.find())
//            {
//                imgurl = m.group(0);
//            }
//
//            str = str.replaceAll("<img.*/>", "");
//            item.description = str;
//
//            item.imgurl = imgurl;
//            Log.d("NET3",imgurl);
            descSB.append(new String(ch, start, length));
        }
    }
}

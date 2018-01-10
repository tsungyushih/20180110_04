package com.example.student.a20180110_04;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Student on 2018/1/10.
 */

//要讀RSSD的方法有很多，如DOM、SAX XML Parser等等，老師這裡教的是SAX
public class MyHandler extends DefaultHandler { //自己創一個MyHandler Class，要繼承DefaultHandler(org.xml.sax.helpers)，
    boolean isTitle = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        //Log.d("NET",qName);
        if(qName.equals("title"))
        {
            isTitle = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if(qName.equals("title"))
        {
            isTitle = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if(isTitle)
        {
            Log.d("NET",new String(ch,start,length));
        }
    }
}

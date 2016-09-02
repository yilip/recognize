package com.bijietech.indexer;

import com.bijietech.util.Constant;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * Created by Lip on 2016/8/29 0029.
 */
public class HtmlParser {
    public static void main(String[] args) throws Exception {
        String path= Constant.dataPath+"/19630.html";
        DOMParser parser = new DOMParser();
        InputSource source=new InputSource(new InputStreamReader (new FileInputStream(path),"utf-8"));
        parser.parse(source);
        Document w3cDoc=parser.getDocument();
        NodeList nodeList=w3cDoc.getElementsByTagName("h1");
        Element element2=(Element) nodeList.item(0);
        System.out.println(element2.getTextContent());
        for(int i=0;i<nodeList.getLength();i++)
        {
            Element element=(Element) nodeList.item(i);
            //System.out.println(element.getTextContent());
        }
        int s=path.lastIndexOf("/");
        int end=path.lastIndexOf(".");
        System.out.println(path.substring(s+1,end));
    }
    public static String getTile(String html)
    {
        return  null;
    }
    /**
     * 读取文本文件
     * @param path
     * @return
     * @throws Exception
     */
    public static String getHtmlText(String path)throws  Exception
    {
        FileReader fileReader=new FileReader(path);
        BufferedReader bReader=new BufferedReader(fileReader);
        StringBuilder sb=new StringBuilder();
        String str=null;
        while((str=bReader.readLine())!=null)
        {
            sb.append(str);
        }
        return  sb.toString();
    }
}

package com.bijietech.indexer;


import com.bijietech.util.Constant;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Created by Lip on 2016/8/26 0026.
 */
public class FileIndexer {
    static Logger logger= LoggerFactory.getLogger(FileIndexer.class);
    public static void main(String[] args) throws Exception {
        index();
    }

    /**
     * 构建指定目录下的索引
     * @throws Exception
     */
    public static void index() throws Exception
    {
        //indexDir is the directory that hosts Lucene's index files
        //dataDir is the directory that hosts the text files that to be indexed
        Analyzer luceneAnalyzer = new StandardAnalyzer();
        FSDirectory e = FSDirectory.open(Paths.get(Constant.indexDir, new String[0]));
        IndexWriterConfig config=new IndexWriterConfig(luceneAnalyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(e, config);
        //统计时间
        long startTime = new Date().getTime();
        File file=new File(Constant.dataPath);
        if(file.isDirectory())
        {
            File []files=file.listFiles();
            for(File item:files)
            {
                if(item.isDirectory())
                {
                    index(indexWriter,item);
                }
                else {
                    indexFile(indexWriter,item);
                }
            }
        }
        indexWriter.close();
        long endTime = new Date().getTime();
        System.out.println("It takes " + (endTime - startTime)
                + " milliseconds to create index for the files in directory ");
    }
    static  void index(IndexWriter indexWriter,File file) throws Exception
    {
        File []files=file.listFiles();
        for(File item:files)
        {
            if(item.isDirectory())
            {
                index(indexWriter,item);
            }
            else {
                indexFile(indexWriter,item);
            }
        }
    }

    /**
     * 构建单个索引文件
     * @param indexWriter
     * @param file
     * @throws Exception
     */
    static  void indexFile(IndexWriter indexWriter,File file) throws Exception {

        logger.info("index file :"+file.getName());
        InputStream stream = new FileInputStream(file);
        Document doc = new Document();
        String path=file.toString();
        StringField pathField = new StringField("path", path, Field.Store.YES);
        doc.add(pathField);

        DOMParser parser = new DOMParser();
        InputSource source=new InputSource(new InputStreamReader (new FileInputStream(path),"utf-8"));
        parser.parse(source);
        org.w3c.dom.Document w3cDoc=parser.getDocument();
        //标题
        NodeList nodeList=w3cDoc.getElementsByTagName("h1");
        if(nodeList.getLength()>0) {
            Element titleElement = (Element) nodeList.item(0);
            logger.info("index title:"+titleElement.getTextContent());
           //TextField textField=new TextField("title",titleElement.getTextContent(),Field.Store.YES);
            StringField titleField = new StringField("title", titleElement.getTextContent(), Field.Store.YES);
            doc.add(titleField);
        }

        //图片
        NodeList nodeList2=w3cDoc.getElementsByTagName("img");
        if(nodeList2.getLength()>0) {
            Element picElement = (Element) nodeList2.item(0);
            logger.info("index pic url:"+picElement.getTextContent());
            StringField picUrlField = new StringField("picUrl", picElement.getAttribute("src"), Field.Store.YES);
            doc.add(picUrlField);
        }
        //简介
        NodeList nodeList3=w3cDoc.getElementsByTagName("div");
        if(nodeList3.getLength()>1) {
            Element descElement = (Element) nodeList3.item(1);
            logger.info("index description:"+descElement.getTextContent());
            StringField descField = new StringField("description", descElement.getTextContent(), Field.Store.YES);
            doc.add(descField);
        }
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        doc.add(new TextField("contents", bufferedReader));
        if (indexWriter.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
            System.out.println("adding " + file);
            indexWriter.addDocument(doc);
        } else {
            System.out.println("updating " + file);
            indexWriter.updateDocument(new Term("path", file.toString()), doc);
        }
        stream.close();
    }
}

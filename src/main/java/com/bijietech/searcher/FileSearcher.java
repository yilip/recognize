package com.bijietech.searcher;

import com.bijietech.model.SearchResult;
import com.bijietech.util.Constant;
import com.google.common.base.Strings;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Lip on 2016/8/26 0026.
 */
public class FileSearcher {
    public static void main(String[] args)throws  Exception {
        String field = "contents";
        Scanner scanner=new Scanner(System.in);
        String line=scanner.nextLine();
        DirectoryReader directoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(Constant.indexDir, new String[0])));
        IndexSearcher searcher = new IndexSearcher(directoryReader);
        while(!line.equals("q"))
        {
            System.out.println("query string :"+line);
            StandardAnalyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser(field, analyzer);
            Query query = parser.parse(line);
            TopDocs result = searcher.search(query,10);
            int total=result.totalHits;
            System.out.println("total hit:"+total);
            ScoreDoc[] scoreDocs = result.scoreDocs;
            int i=0;
            for(ScoreDoc item:scoreDocs)
            {
                i++;
                System.out.println("***********************");
                Document document = searcher.doc(item.doc);
                System.out.println("path:"+document.get("path"));
                System.out.println("title:"+document.get("title"));
                System.out.println("picUrl:"+document.get("picUrl"));
                System.out.println("description:"+document.get("description"));
            }
            line=scanner.nextLine();
        }
    }

    /**
     * 查询结果
     * @param num
     * @return
     */
    public static SearchResult<Map<String,String>> search(String keyword, int num) throws IOException,ParseException
    {
        SearchResult<Map<String,String>>results=new SearchResult<>();
        List<Map<String,String>>list=new ArrayList<>();
        String field = "contents";
        DirectoryReader directoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(Constant.indexDir, new String[0])));
        IndexSearcher searcher = new IndexSearcher(directoryReader);
        System.out.println("query string :"+keyword);
        StandardAnalyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(keyword);
        TopDocs result = searcher.search(query,num);
        results.setTotal(result.totalHits);
        for(ScoreDoc item:result.scoreDocs)
        {
            Map<String,String>article=new HashMap<>();
            Document document = searcher.doc(item.doc);
            String path = document.get("path");
            if(path != null) {
                article.put("path",path);
                article.put("contents",document.get("contents"));
                String title = document.get("title");
                if(!Strings.isNullOrEmpty(title)) {
                    System.out.println("   Title: " + document.get("title"));
                    article.put("title",title);
                }
                String picUrl=document.get("picUrl");
                if(!Strings.isNullOrEmpty(picUrl))
                {
                    article.put("picUrl",picUrl);
                }
                String description=document.get("description");
                if(!Strings.isNullOrEmpty(description))
                {
                    article.put("description",description);
                }
            } else {
                System.out.println("No path for this document");
            }
            list.add(article);
        }
        results.setRows(list);
        return results;
    }
}

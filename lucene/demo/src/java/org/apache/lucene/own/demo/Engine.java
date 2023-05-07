/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lucene.own.demo;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Engine {
    // 用于索引的基本数据结构
    private IndexWriter writer;
    private Analyzer analyzer;
    private Directory directory;

    public Engine(String dataPath) throws IOException {
        // 使用标准分词器
        this.analyzer = new StandardAnalyzer();
        this.directory = new MMapDirectory(Paths.get(dataPath));
        //this.directory = new SimpleFSDirectory(Paths.get(dataPath));

        // 注：用 SimpleTextCodec 可以读懂数据文件内容，便于学习
        // 初始化 IndexWriter
        IndexWriterConfig config = new IndexWriterConfig(analyzer).setUseCompoundFile(false);
        this.writer = new IndexWriter(directory, config);
    }

    // 写入，单条文档
    public void index(Document document) throws IOException {
        this.writer.addDocument(document);
    }

    // 写入，一次可以写多个文档
    public void batchIndex(List<Document> documents) throws IOException {
        this.writer.addDocuments(documents);
    }

    public void commit() throws IOException {
        this.writer.commit();
    }

    public void flush() throws IOException {
        this.writer.flush();
    }

    public void close() throws IOException {
        this.writer.close();
        this.analyzer.close();
        this.directory.close();
   }
}

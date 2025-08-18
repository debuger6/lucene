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

package org.apache.lucene.own.demo.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.utils.Utils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;

public class MustQueryDemo {
  public static void main(String[] args) throws IOException {
    Engine engine = Utils.engine("./data/must_query_demo");

    // 首先插入 10 条文档
    List<Document> docs = new ArrayList<>();
    // lucene: 0, 4, 5, 6, 7, 8, 9
    // elasticsearch: 1, 4, 5, 6, 7, 9
    // hbase: 2, 4, 5, 6, 9
    // must query result: 4, 5, 6, 9
    docs.add(Utils.genSingleDocWithSingleField("lucene"));
    docs.add(Utils.genSingleDocWithSingleField("elasticsearch"));
    docs.add(Utils.genSingleDocWithSingleField("hbase"));
    docs.add(Utils.genSingleDocWithSingleField("rocksdb"));
    docs.add(Utils.genSingleDocWithSingleField("lucene elasticsearch hbase"));
    docs.add(Utils.genSingleDocWithSingleField("lucene elasticsearch hbase rocksdb"));
    docs.add(Utils.genSingleDocWithSingleField("lucene elasticsearch hbase rocksdb leveldb"));
    docs.add(Utils.genSingleDocWithSingleField("lucene elasticsearch rocksdb clickhouse"));
    docs.add(Utils.genSingleDocWithSingleField("lucene hbase rocksdb leveldb"));
    docs.add(Utils.genSingleDocWithSingleField("lucene elasticsearch hbase rocksdb clickhouse"));

    engine.batchIndex(docs);
    engine.commit();

    // 构造 MUST 查询求多个查询条件的交集
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(new TermQuery(new Term("content", "lucene")), BooleanClause.Occur.MUST);
    builder.add(new TermQuery(new Term("content", "elasticsearch")), BooleanClause.Occur.MUST);
    builder.add(new TermQuery(new Term("content", "hbase")), BooleanClause.Occur.MUST);

    // 查询获取满足条件的文档
    List<ScoreDoc> docList = engine.search(builder.build(), 10);
    for (ScoreDoc scoreDoc : docList) {
      System.out.println("docId: " + scoreDoc.doc + " content: " + engine.fieldValue("content", scoreDoc.doc));
    }
  }
}

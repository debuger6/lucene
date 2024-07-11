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

package org.apache.lucene.own.demo.writepath;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.Row;

public class WritePath {
  private static Engine engine;

  static {
    try {
      engine = new Engine("./data/demo_write_path");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    writeRow(new Row("hello", 1, 1111L, 1.0f, 1.1d));
    writeRow(new Row("world", 2, 2222L, 2.0f, 2.1d));
    writeRow(new Row("kitty", 3, 3333L, 3.0f, 3.1d));
    engine.flush();
  }

  public static void writeRow(Row row) throws IOException {
    Document document = new Document();
    document.add(new StringField("str_col", row.getStrCol(), Field.Store.YES));
    document.add(new IntField("int_col", row.getIntCol()));
    document.add(new LongField("long_col", row.getLongCol()));
    document.add(new FloatField("float_col", row.getFloatCol()));
    document.add(new DoubleField("double_col", row.getDoubleCol()));
    engine.index(document);
  }

}

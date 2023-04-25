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

public class Row {
  private final String strCol;
  private final int intCol;
  private final long longCol;
  private final float floatCol;
  private final double doubleCol;

  public Row(String strCol, int intCol, long longCol, float floatCol, double doubleCol) {
    this.strCol = strCol;
    this.intCol = intCol;
    this.longCol = longCol;
    this.floatCol = floatCol;
    this.doubleCol = doubleCol;
  }

  public String getStrCol() {
    return strCol;
  }

  public int getIntCol() {
    return intCol;
  }

  public long getLongCol() {
    return longCol;
  }

  public float getFloatCol() {
    return floatCol;
  }

  public double getDoubleCol() {
    return doubleCol;
  }
}

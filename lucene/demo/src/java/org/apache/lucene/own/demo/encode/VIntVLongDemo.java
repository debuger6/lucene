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

package org.apache.lucene.own.demo.encode;

import java.io.IOException;

public class VIntVLongDemo {
  public static void main(String[] args) throws IOException {
    int number = 102407970;
    int bits = 3;

    final long infoAndBits = (((long) number) << 3) | bits;
    System.out.println(getSizeOfSignedVLongEncode(infoAndBits));
    System.out.println(getSizeOfSignedVIntEncode(number));
    System.out.println(getSizeOfSignedVLongEncode(bits));
  }

  public static int getSizeOfSignedVLongEncode(long i) throws IOException {
    int bytes = 0;
    while ((i & ~0x7FL) != 0L) {
      i >>>= 7;
      bytes++;
    }
    bytes++;
    return bytes;
  }

  public static int getSizeOfSignedVIntEncode(int i) throws IOException {
    int bytes = 0;
    while ((i & ~0x7F) != 0) {
      i >>>= 7;
      bytes++;
    }
    bytes++;
    return bytes;
  }
}

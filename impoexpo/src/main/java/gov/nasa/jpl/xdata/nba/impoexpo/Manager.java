/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpl.xdata.nba.impoexpo;

import java.io.IOException;

/**
 * Manager interface essentially defines but ultimately delegates 
 * extract, transform, load operations.
 * Manager interface gains value from users when they wish to quickly 
 * understand data they are modeling.
 */
public interface Manager {
  
  /**
   * Essentially, execute an update of the object.
   * The operation usually overwrites all data on the object
   * e.g. cleans the data then write.
   * @param key the key you wish to delete data on
   * @param value the object you wish to delete values for
   */
  void deleteByQuery(Object key, Object value);

  /**
   * Delete and entire object regardless of the fields.
   * @param key the key relating to the object.
   */
  void delete(Object key);

  /**
   * Query will provide key value-based access to objects.
   * @param key the key we wish to obtain
   * @param value the value relating to the above key
   */
  void query(Object key, Object value);

  /**
   * Query will provide key value-based access to objects.
   * @param key the key we wish to obtain
   */
  void query(Object key);

  /**
   * Get enables a plain retrieval on a certain object.
   * This includes a get on ALL fields associated with this object.
   * @param key
   */
  void get(Object key);

  /**
   * This may be the main data acquisition/retrieval method for 
   * the ETL process. It should be protocol unaware and hungry for
   * data.
   * @param input
   */
  void aquire(String parseType, Object input) throws IOException;
}

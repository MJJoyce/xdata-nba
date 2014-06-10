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
@SuppressWarnings("all")
public class GamePlayers extends org.apache.gora.persistency.impl.PersistentBase implements org.apache.avro.specific.SpecificRecord, org.apache.gora.persistency.Persistent {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"GamePlayers\",\"namespace\":\"gov.nasa.jpl.xdata.nba.impoexpo.nba\",\"fields\":[{\"name\":\"__g__dirty\",\"type\":\"bytes\",\"doc\":\"Bytes used to represent weather or not a field is dirty.\",\"default\":\"AA==\"},{\"name\":\"player_id\",\"type\":\"int\",\"default\":0},{\"name\":\"player_name\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"team_id\",\"type\":\"int\",\"default\":0},{\"name\":\"team_city\",\"type\":[\"null\",\"string\"],\"default\":null}]}");

  /** Enum containing all data bean's fields. */
  public static enum Field {
    __G__DIRTY(0, "__g__dirty"),
    PLAYER_ID(1, "player_id"),
    PLAYER_NAME(2, "player_name"),
    TEAM_ID(3, "team_id"),
    TEAM_CITY(4, "team_city"),
    ;
    /**
     * Field's index.
     */
    private int index;

    /**
     * Field's name.
     */
    private String name;

    /**
     * Field's constructor
     * @param index field's index.
     * @param name field's name.
     */
    Field(int index, String name) {this.index=index;this.name=name;}

    /**
     * Gets field's index.
     * @return int field's index.
     */
    public int getIndex() {return index;}

    /**
     * Gets field's name.
     * @return String field's name.
     */
    public String getName() {return name;}

    /**
     * Gets field's attributes to string.
     * @return String field's attributes to string.
     */
    public String toString() {return name;}
  };

  public static final String[] _ALL_FIELDS = {
  "__g__dirty",
  "player_id",
  "player_name",
  "team_id",
  "team_city",
  };

  /** Bytes used to represent weather or not a field is dirty. */
  private java.nio.ByteBuffer __g__dirty = java.nio.ByteBuffer.wrap(new byte[1]);
  private int player_id;
  private java.lang.CharSequence player_name;
  private int team_id;
  private java.lang.CharSequence team_city;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return __g__dirty;
    case 1: return player_id;
    case 2: return player_name;
    case 3: return team_id;
    case 4: return team_city;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value) {
    switch (field$) {
    case 0: __g__dirty = (java.nio.ByteBuffer)(value); break;
    case 1: player_id = (java.lang.Integer)(value); break;
    case 2: player_name = (java.lang.CharSequence)(value); break;
    case 3: team_id = (java.lang.Integer)(value); break;
    case 4: team_city = (java.lang.CharSequence)(value); break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'player_id' field.
   */
  public java.lang.Integer getPlayerId() {
    return player_id;
  }

  /**
   * Sets the value of the 'player_id' field.
   * @param value the value to set.
   */
  public void setPlayerId(java.lang.Integer value) {
    this.player_id = value;
    setDirty(1);
  }
  
  /**
   * Checks the dirty status of the 'player_id' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isPlayerIdDirty(java.lang.Integer value) {
    return isDirty(1);
  }

  /**
   * Gets the value of the 'player_name' field.
   */
  public java.lang.CharSequence getPlayerName() {
    return player_name;
  }

  /**
   * Sets the value of the 'player_name' field.
   * @param value the value to set.
   */
  public void setPlayerName(java.lang.CharSequence value) {
    this.player_name = value;
    setDirty(2);
  }
  
  /**
   * Checks the dirty status of the 'player_name' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isPlayerNameDirty(java.lang.CharSequence value) {
    return isDirty(2);
  }

  /**
   * Gets the value of the 'team_id' field.
   */
  public java.lang.Integer getTeamId() {
    return team_id;
  }

  /**
   * Sets the value of the 'team_id' field.
   * @param value the value to set.
   */
  public void setTeamId(java.lang.Integer value) {
    this.team_id = value;
    setDirty(3);
  }
  
  /**
   * Checks the dirty status of the 'team_id' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isTeamIdDirty(java.lang.Integer value) {
    return isDirty(3);
  }

  /**
   * Gets the value of the 'team_city' field.
   */
  public java.lang.CharSequence getTeamCity() {
    return team_city;
  }

  /**
   * Sets the value of the 'team_city' field.
   * @param value the value to set.
   */
  public void setTeamCity(java.lang.CharSequence value) {
    this.team_city = value;
    setDirty(4);
  }
  
  /**
   * Checks the dirty status of the 'team_city' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isTeamCityDirty(java.lang.CharSequence value) {
    return isDirty(4);
  }

  /** Creates a new GamePlayers RecordBuilder */
  public static gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder newBuilder() {
    return new gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder();
  }
  
  /** Creates a new GamePlayers RecordBuilder by copying an existing Builder */
  public static gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder newBuilder(gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder other) {
    return new gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder(other);
  }
  
  /** Creates a new GamePlayers RecordBuilder by copying an existing GamePlayers instance */
  public static gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder newBuilder(gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers other) {
    return new gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder(other);
  }
  
  private static java.nio.ByteBuffer deepCopyToWriteOnlyBuffer(
      java.nio.ByteBuffer input) {
    java.nio.ByteBuffer copy = java.nio.ByteBuffer.allocate(input.capacity());
    int position = input.position();
    input.reset();
    int mark = input.position();
    int limit = input.limit();
    input.rewind();
    input.limit(input.capacity());
    copy.put(input);
    input.rewind();
    copy.rewind();
    input.position(mark);
    input.mark();
    copy.position(mark);
    copy.mark();
    input.position(position);
    copy.position(position);
    input.limit(limit);
    copy.limit(limit);
    return copy.asReadOnlyBuffer();
  }
  
  /**
   * RecordBuilder for GamePlayers instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<GamePlayers>
    implements org.apache.avro.data.RecordBuilder<GamePlayers> {

    private java.nio.ByteBuffer __g__dirty;
    private int player_id;
    private java.lang.CharSequence player_name;
    private int team_id;
    private java.lang.CharSequence team_city;

    /** Creates a new Builder */
    private Builder() {
      super(gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder other) {
      super(other);
    }
    
    /** Creates a Builder by copying an existing GamePlayers instance */
    private Builder(gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers other) {
            super(gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.SCHEMA$);
      if (isValidValue(fields()[0], other.__g__dirty)) {
        this.__g__dirty = (java.nio.ByteBuffer) data().deepCopy(fields()[0].schema(), other.__g__dirty);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.player_id)) {
        this.player_id = (java.lang.Integer) data().deepCopy(fields()[1].schema(), other.player_id);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.player_name)) {
        this.player_name = (java.lang.CharSequence) data().deepCopy(fields()[2].schema(), other.player_name);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.team_id)) {
        this.team_id = (java.lang.Integer) data().deepCopy(fields()[3].schema(), other.team_id);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.team_city)) {
        this.team_city = (java.lang.CharSequence) data().deepCopy(fields()[4].schema(), other.team_city);
        fieldSetFlags()[4] = true;
      }
    }

    /** Gets the value of the 'player_id' field */
    public java.lang.Integer getPlayerId() {
      return player_id;
    }
    
    /** Sets the value of the 'player_id' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder setPlayerId(int value) {
      validate(fields()[1], value);
      this.player_id = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'player_id' field has been set */
    public boolean hasPlayerId() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'player_id' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder clearPlayerId() {
      fieldSetFlags()[1] = false;
      return this;
    }
    
    /** Gets the value of the 'player_name' field */
    public java.lang.CharSequence getPlayerName() {
      return player_name;
    }
    
    /** Sets the value of the 'player_name' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder setPlayerName(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.player_name = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'player_name' field has been set */
    public boolean hasPlayerName() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'player_name' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder clearPlayerName() {
      player_name = null;
      fieldSetFlags()[2] = false;
      return this;
    }
    
    /** Gets the value of the 'team_id' field */
    public java.lang.Integer getTeamId() {
      return team_id;
    }
    
    /** Sets the value of the 'team_id' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder setTeamId(int value) {
      validate(fields()[3], value);
      this.team_id = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'team_id' field has been set */
    public boolean hasTeamId() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'team_id' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder clearTeamId() {
      fieldSetFlags()[3] = false;
      return this;
    }
    
    /** Gets the value of the 'team_city' field */
    public java.lang.CharSequence getTeamCity() {
      return team_city;
    }
    
    /** Sets the value of the 'team_city' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder setTeamCity(java.lang.CharSequence value) {
      validate(fields()[4], value);
      this.team_city = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'team_city' field has been set */
    public boolean hasTeamCity() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'team_city' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.GamePlayers.Builder clearTeamCity() {
      team_city = null;
      fieldSetFlags()[4] = false;
      return this;
    }
    
    public GamePlayers build() {
      try {
        GamePlayers record = new GamePlayers();
        record.__g__dirty = fieldSetFlags()[0] ? this.__g__dirty : (java.nio.ByteBuffer) java.nio.ByteBuffer.wrap(new byte[1]);
        record.player_id = fieldSetFlags()[1] ? this.player_id : (java.lang.Integer) defaultValue(fields()[1]);
        record.player_name = fieldSetFlags()[2] ? this.player_name : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.team_id = fieldSetFlags()[3] ? this.team_id : (java.lang.Integer) defaultValue(fields()[3]);
        record.team_city = fieldSetFlags()[4] ? this.team_city : (java.lang.CharSequence) defaultValue(fields()[4]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
  
  public GamePlayers.Tombstone getTombstone(){
  	return TOMBSTONE;
  }

  public GamePlayers newInstance(){
    return newBuilder().build();
  }

  private static final Tombstone TOMBSTONE = new Tombstone();
  
  public static final class Tombstone extends GamePlayers implements org.apache.gora.persistency.Tombstone {
  
      private Tombstone() { }
  
	  				  /**
	   * Gets the value of the 'player_id' field.
		   */
	  public java.lang.Integer getPlayerId() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'player_id' field.
		   * @param value the value to set.
	   */
	  public void setPlayerId(java.lang.Integer value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'player_id' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isPlayerIdDirty(java.lang.Integer value) {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
				  /**
	   * Gets the value of the 'player_name' field.
		   */
	  public java.lang.CharSequence getPlayerName() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'player_name' field.
		   * @param value the value to set.
	   */
	  public void setPlayerName(java.lang.CharSequence value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'player_name' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isPlayerNameDirty(java.lang.CharSequence value) {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
				  /**
	   * Gets the value of the 'team_id' field.
		   */
	  public java.lang.Integer getTeamId() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'team_id' field.
		   * @param value the value to set.
	   */
	  public void setTeamId(java.lang.Integer value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'team_id' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isTeamIdDirty(java.lang.Integer value) {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
				  /**
	   * Gets the value of the 'team_city' field.
		   */
	  public java.lang.CharSequence getTeamCity() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'team_city' field.
		   * @param value the value to set.
	   */
	  public void setTeamCity(java.lang.CharSequence value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'team_city' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isTeamCityDirty(java.lang.CharSequence value) {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
		  
  }
  
}
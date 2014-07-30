/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package gov.nasa.jpl.xdata.nba.impoexpo.structs;  
@SuppressWarnings("all")
public class GameInfo extends org.apache.gora.persistency.impl.PersistentBase implements org.apache.avro.specific.SpecificRecord, org.apache.gora.persistency.Persistent {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"GameInfo\",\"namespace\":\"gov.nasa.jpl.xdata.nba.impoexpo.structs\",\"fields\":[{\"name\":\"__g__dirty\",\"type\":\"bytes\",\"doc\":\"Bytes used to represent weather or not a field is dirty.\",\"default\":\"AA==\"},{\"name\":\"gameDate\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"attendance\",\"type\":\"long\",\"default\":0},{\"name\":\"gameTime\",\"type\":[\"null\",\"string\"],\"default\":null}]}");

  /** Enum containing all data bean's fields. */
  public static enum Field {
    __G__DIRTY(0, "__g__dirty"),
    GAME_DATE(1, "gameDate"),
    ATTENDANCE(2, "attendance"),
    GAME_TIME(3, "gameTime"),
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
  "gameDate",
  "attendance",
  "gameTime",
  };

  /** Bytes used to represent weather or not a field is dirty. */
  private java.nio.ByteBuffer __g__dirty = java.nio.ByteBuffer.wrap(new byte[1]);
  private java.lang.CharSequence gameDate;
  private long attendance;
  private java.lang.CharSequence gameTime;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return __g__dirty;
    case 1: return gameDate;
    case 2: return attendance;
    case 3: return gameTime;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value) {
    switch (field$) {
    case 0: __g__dirty = (java.nio.ByteBuffer)(value); break;
    case 1: gameDate = (java.lang.CharSequence)(value); break;
    case 2: attendance = (java.lang.Long)(value); break;
    case 3: gameTime = (java.lang.CharSequence)(value); break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'gameDate' field.
   */
  public java.lang.CharSequence getGameDate() {
    return gameDate;
  }

  /**
   * Sets the value of the 'gameDate' field.
   * @param value the value to set.
   */
  public void setGameDate(java.lang.CharSequence value) {
    this.gameDate = value;
    setDirty(1);
  }
  
  /**
   * Checks the dirty status of the 'gameDate' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isGameDateDirty(java.lang.CharSequence value) {
    return isDirty(1);
  }

  /**
   * Gets the value of the 'attendance' field.
   */
  public java.lang.Long getAttendance() {
    return attendance;
  }

  /**
   * Sets the value of the 'attendance' field.
   * @param value the value to set.
   */
  public void setAttendance(java.lang.Long value) {
    this.attendance = value;
    setDirty(2);
  }
  
  /**
   * Checks the dirty status of the 'attendance' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isAttendanceDirty(java.lang.Long value) {
    return isDirty(2);
  }

  /**
   * Gets the value of the 'gameTime' field.
   */
  public java.lang.CharSequence getGameTime() {
    return gameTime;
  }

  /**
   * Sets the value of the 'gameTime' field.
   * @param value the value to set.
   */
  public void setGameTime(java.lang.CharSequence value) {
    this.gameTime = value;
    setDirty(3);
  }
  
  /**
   * Checks the dirty status of the 'gameTime' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isGameTimeDirty(java.lang.CharSequence value) {
    return isDirty(3);
  }

  /** Creates a new GameInfo RecordBuilder */
  public static gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder newBuilder() {
    return new gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder();
  }
  
  /** Creates a new GameInfo RecordBuilder by copying an existing Builder */
  public static gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder newBuilder(gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder other) {
    return new gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder(other);
  }
  
  /** Creates a new GameInfo RecordBuilder by copying an existing GameInfo instance */
  public static gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder newBuilder(gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo other) {
    return new gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder(other);
  }
  
  private static java.nio.ByteBuffer deepCopyToReadOnlyBuffer(
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
   * RecordBuilder for GameInfo instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<GameInfo>
    implements org.apache.avro.data.RecordBuilder<GameInfo> {

    private java.nio.ByteBuffer __g__dirty;
    private java.lang.CharSequence gameDate;
    private long attendance;
    private java.lang.CharSequence gameTime;

    /** Creates a new Builder */
    private Builder() {
      super(gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder other) {
      super(other);
    }
    
    /** Creates a Builder by copying an existing GameInfo instance */
    private Builder(gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo other) {
            super(gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.SCHEMA$);
      if (isValidValue(fields()[0], other.__g__dirty)) {
        this.__g__dirty = (java.nio.ByteBuffer) data().deepCopy(fields()[0].schema(), other.__g__dirty);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.gameDate)) {
        this.gameDate = (java.lang.CharSequence) data().deepCopy(fields()[1].schema(), other.gameDate);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.attendance)) {
        this.attendance = (java.lang.Long) data().deepCopy(fields()[2].schema(), other.attendance);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.gameTime)) {
        this.gameTime = (java.lang.CharSequence) data().deepCopy(fields()[3].schema(), other.gameTime);
        fieldSetFlags()[3] = true;
      }
    }

    /** Gets the value of the 'gameDate' field */
    public java.lang.CharSequence getGameDate() {
      return gameDate;
    }
    
    /** Sets the value of the 'gameDate' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder setGameDate(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.gameDate = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'gameDate' field has been set */
    public boolean hasGameDate() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'gameDate' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder clearGameDate() {
      gameDate = null;
      fieldSetFlags()[1] = false;
      return this;
    }
    
    /** Gets the value of the 'attendance' field */
    public java.lang.Long getAttendance() {
      return attendance;
    }
    
    /** Sets the value of the 'attendance' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder setAttendance(long value) {
      validate(fields()[2], value);
      this.attendance = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'attendance' field has been set */
    public boolean hasAttendance() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'attendance' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder clearAttendance() {
      fieldSetFlags()[2] = false;
      return this;
    }
    
    /** Gets the value of the 'gameTime' field */
    public java.lang.CharSequence getGameTime() {
      return gameTime;
    }
    
    /** Sets the value of the 'gameTime' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder setGameTime(java.lang.CharSequence value) {
      validate(fields()[3], value);
      this.gameTime = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'gameTime' field has been set */
    public boolean hasGameTime() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'gameTime' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo.Builder clearGameTime() {
      gameTime = null;
      fieldSetFlags()[3] = false;
      return this;
    }
    
    @Override
    public GameInfo build() {
      try {
        GameInfo record = new GameInfo();
        record.__g__dirty = fieldSetFlags()[0] ? this.__g__dirty : (java.nio.ByteBuffer) java.nio.ByteBuffer.wrap(new byte[1]);
        record.gameDate = fieldSetFlags()[1] ? this.gameDate : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.attendance = fieldSetFlags()[2] ? this.attendance : (java.lang.Long) defaultValue(fields()[2]);
        record.gameTime = fieldSetFlags()[3] ? this.gameTime : (java.lang.CharSequence) defaultValue(fields()[3]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
  
  public GameInfo.Tombstone getTombstone(){
  	return TOMBSTONE;
  }

  public GameInfo newInstance(){
    return newBuilder().build();
  }

  private static final Tombstone TOMBSTONE = new Tombstone();
  
  public static final class Tombstone extends GameInfo implements org.apache.gora.persistency.Tombstone {
  
      private Tombstone() { }
  
	  				  /**
	   * Gets the value of the 'gameDate' field.
		   */
	  public java.lang.CharSequence getGameDate() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'gameDate' field.
		   * @param value the value to set.
	   */
	  public void setGameDate(java.lang.CharSequence value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'gameDate' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isGameDateDirty(java.lang.CharSequence value) {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
				  /**
	   * Gets the value of the 'attendance' field.
		   */
	  public java.lang.Long getAttendance() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'attendance' field.
		   * @param value the value to set.
	   */
	  public void setAttendance(java.lang.Long value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'attendance' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isAttendanceDirty(java.lang.Long value) {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
				  /**
	   * Gets the value of the 'gameTime' field.
		   */
	  public java.lang.CharSequence getGameTime() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'gameTime' field.
		   * @param value the value to set.
	   */
	  public void setGameTime(java.lang.CharSequence value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'gameTime' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isGameTimeDirty(java.lang.CharSequence value) {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
		  
  }
  
}


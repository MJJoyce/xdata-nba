/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package gov.nasa.jpl.xdata.nba.impoexpo.structs;  

import java.nio.ByteBuffer;

@SuppressWarnings("all")
public class Preview extends org.apache.gora.persistency.impl.PersistentBase implements org.apache.avro.specific.SpecificRecord, org.apache.gora.persistency.Persistent {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Preview\",\"namespace\":\"gov.nasa.jpl.xdata.nba.impoexpo.structs\",\"fields\":[{\"name\":\"previewText\",\"type\":[\"null\",\"string\"],\"default\":null}]}");

  /** Enum containing all data bean's fields. */
  public static enum Field {
    PREVIEW_TEXT(0, "previewText"),
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
  "previewText",
  };

  /**
   * Gets the total field count.
   * @return int field count
   */
  public int getFieldsCount() {
    return Preview._ALL_FIELDS.length;
  }

  private java.lang.CharSequence previewText;
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return previewText;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value) {
    switch (field$) {
    case 0: previewText = (java.lang.CharSequence)(value); break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'previewText' field.
   */
  public java.lang.CharSequence getPreviewText() {
    return previewText;
  }

  /**
   * Sets the value of the 'previewText' field.
   * @param value the value to set.
   */
  public void setPreviewText(java.lang.CharSequence value) {
    this.previewText = value;
    setDirty(0);
  }
  
  /**
   * Checks the dirty status of the 'previewText' field. A field is dirty if it represents a change that has not yet been written to the database.
   * @param value the value to set.
   */
  public boolean isPreviewTextDirty(java.lang.CharSequence value) {
    return isDirty(0);
  }

  /** Creates a new Preview RecordBuilder */
  public static gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.Builder newBuilder() {
    return new gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.Builder();
  }
  
  /** Creates a new Preview RecordBuilder by copying an existing Builder */
  public static gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.Builder newBuilder(gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.Builder other) {
    return new gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.Builder(other);
  }
  
  /** Creates a new Preview RecordBuilder by copying an existing Preview instance */
  public static gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.Builder newBuilder(gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview other) {
    return new gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.Builder(other);
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
   * RecordBuilder for Preview instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Preview>
    implements org.apache.avro.data.RecordBuilder<Preview> {

    private java.lang.CharSequence previewText;

    /** Creates a new Builder */
    private Builder() {
      super(gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.Builder other) {
      super(other);
    }
    
    /** Creates a Builder by copying an existing Preview instance */
    private Builder(gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview other) {
            super(gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.SCHEMA$);
      if (isValidValue(fields()[0], other.previewText)) {
        this.previewText = (java.lang.CharSequence) data().deepCopy(fields()[0].schema(), other.previewText);
        fieldSetFlags()[0] = true;
      }
    }

    /** Gets the value of the 'previewText' field */
    public java.lang.CharSequence getPreviewText() {
      return previewText;
    }
    
    /** Sets the value of the 'previewText' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.Builder setPreviewText(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.previewText = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'previewText' field has been set */
    public boolean hasPreviewText() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'previewText' field */
    public gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview.Builder clearPreviewText() {
      previewText = null;
      fieldSetFlags()[0] = false;
      return this;
    }
    
    @Override
    public Preview build() {
      try {
        Preview record = new Preview();
        record.previewText = fieldSetFlags()[0] ? this.previewText : (java.lang.CharSequence) defaultValue(fields()[0]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
  
  public Preview.Tombstone getTombstone(){
  	return TOMBSTONE;
  }

  public Preview newInstance(){
    return newBuilder().build();
  }

  private static final Tombstone TOMBSTONE = new Tombstone();
  
  public static final class Tombstone extends Preview implements org.apache.gora.persistency.Tombstone {
  
      private Tombstone() { }
  
	  		  /**
	   * Gets the value of the 'previewText' field.
		   */
	  public java.lang.CharSequence getPreviewText() {
	    throw new java.lang.UnsupportedOperationException("Get is not supported on tombstones");
	  }
	
	  /**
	   * Sets the value of the 'previewText' field.
		   * @param value the value to set.
	   */
	  public void setPreviewText(java.lang.CharSequence value) {
	    throw new java.lang.UnsupportedOperationException("Set is not supported on tombstones");
	  }
	  
	  /**
	   * Checks the dirty status of the 'previewText' field. A field is dirty if it represents a change that has not yet been written to the database.
		   * @param value the value to set.
	   */
	  public boolean isPreviewTextDirty(java.lang.CharSequence value) {
	    throw new java.lang.UnsupportedOperationException("IsDirty is not supported on tombstones");
	  }
	
		  
  }
  
}


// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: msg_info.proto

package com.test.proto;

public final class MsgInfo {
  private MsgInfo() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface MsgBodyOrBuilder extends
      // @@protoc_insertion_point(interface_extends:MsgBody)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int32 command = 1;</code>
     * @return The command.
     */
    int getCommand();

    /**
     * <code>bytes content = 2;</code>
     * @return The content.
     */
    com.google.protobuf.ByteString getContent();
  }
  /**
   * Protobuf type {@code MsgBody}
   */
  public  static final class MsgBody extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:MsgBody)
      MsgBodyOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use MsgBody.newBuilder() to construct.
    private MsgBody(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private MsgBody() {
      content_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new MsgBody();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private MsgBody(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {

              command_ = input.readInt32();
              break;
            }
            case 18: {

              content_ = input.readBytes();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.test.proto.MsgInfo.internal_static_MsgBody_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.test.proto.MsgInfo.internal_static_MsgBody_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.test.proto.MsgInfo.MsgBody.class, com.test.proto.MsgInfo.MsgBody.Builder.class);
    }

    public static final int COMMAND_FIELD_NUMBER = 1;
    private int command_;
    /**
     * <code>int32 command = 1;</code>
     * @return The command.
     */
    public int getCommand() {
      return command_;
    }

    public static final int CONTENT_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString content_;
    /**
     * <code>bytes content = 2;</code>
     * @return The content.
     */
    public com.google.protobuf.ByteString getContent() {
      return content_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (command_ != 0) {
        output.writeInt32(1, command_);
      }
      if (!content_.isEmpty()) {
        output.writeBytes(2, content_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (command_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, command_);
      }
      if (!content_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, content_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.test.proto.MsgInfo.MsgBody)) {
        return super.equals(obj);
      }
      com.test.proto.MsgInfo.MsgBody other = (com.test.proto.MsgInfo.MsgBody) obj;

      if (getCommand()
          != other.getCommand()) return false;
      if (!getContent()
          .equals(other.getContent())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + COMMAND_FIELD_NUMBER;
      hash = (53 * hash) + getCommand();
      hash = (37 * hash) + CONTENT_FIELD_NUMBER;
      hash = (53 * hash) + getContent().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.test.proto.MsgInfo.MsgBody parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.test.proto.MsgInfo.MsgBody parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.test.proto.MsgInfo.MsgBody parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.test.proto.MsgInfo.MsgBody parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.test.proto.MsgInfo.MsgBody parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.test.proto.MsgInfo.MsgBody parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.test.proto.MsgInfo.MsgBody parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.test.proto.MsgInfo.MsgBody parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.test.proto.MsgInfo.MsgBody parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.test.proto.MsgInfo.MsgBody parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.test.proto.MsgInfo.MsgBody parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.test.proto.MsgInfo.MsgBody parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.test.proto.MsgInfo.MsgBody prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code MsgBody}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:MsgBody)
        com.test.proto.MsgInfo.MsgBodyOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.test.proto.MsgInfo.internal_static_MsgBody_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.test.proto.MsgInfo.internal_static_MsgBody_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.test.proto.MsgInfo.MsgBody.class, com.test.proto.MsgInfo.MsgBody.Builder.class);
      }

      // Construct using com.test.proto.MsgInfo.MsgBody.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        command_ = 0;

        content_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.test.proto.MsgInfo.internal_static_MsgBody_descriptor;
      }

      @java.lang.Override
      public com.test.proto.MsgInfo.MsgBody getDefaultInstanceForType() {
        return com.test.proto.MsgInfo.MsgBody.getDefaultInstance();
      }

      @java.lang.Override
      public com.test.proto.MsgInfo.MsgBody build() {
        com.test.proto.MsgInfo.MsgBody result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public com.test.proto.MsgInfo.MsgBody buildPartial() {
        com.test.proto.MsgInfo.MsgBody result = new com.test.proto.MsgInfo.MsgBody(this);
        result.command_ = command_;
        result.content_ = content_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.test.proto.MsgInfo.MsgBody) {
          return mergeFrom((com.test.proto.MsgInfo.MsgBody)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.test.proto.MsgInfo.MsgBody other) {
        if (other == com.test.proto.MsgInfo.MsgBody.getDefaultInstance()) return this;
        if (other.getCommand() != 0) {
          setCommand(other.getCommand());
        }
        if (other.getContent() != com.google.protobuf.ByteString.EMPTY) {
          setContent(other.getContent());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.test.proto.MsgInfo.MsgBody parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.test.proto.MsgInfo.MsgBody) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int command_ ;
      /**
       * <code>int32 command = 1;</code>
       * @return The command.
       */
      public int getCommand() {
        return command_;
      }
      /**
       * <code>int32 command = 1;</code>
       * @param value The command to set.
       * @return This builder for chaining.
       */
      public Builder setCommand(int value) {
        
        command_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 command = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearCommand() {
        
        command_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString content_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes content = 2;</code>
       * @return The content.
       */
      public com.google.protobuf.ByteString getContent() {
        return content_;
      }
      /**
       * <code>bytes content = 2;</code>
       * @param value The content to set.
       * @return This builder for chaining.
       */
      public Builder setContent(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        content_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes content = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearContent() {
        
        content_ = getDefaultInstance().getContent();
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:MsgBody)
    }

    // @@protoc_insertion_point(class_scope:MsgBody)
    private static final com.test.proto.MsgInfo.MsgBody DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.test.proto.MsgInfo.MsgBody();
    }

    public static com.test.proto.MsgInfo.MsgBody getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<MsgBody>
        PARSER = new com.google.protobuf.AbstractParser<MsgBody>() {
      @java.lang.Override
      public MsgBody parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new MsgBody(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<MsgBody> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<MsgBody> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public com.test.proto.MsgInfo.MsgBody getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_MsgBody_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_MsgBody_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016msg_info.proto\"+\n\007MsgBody\022\017\n\007command\030\001" +
      " \001(\005\022\017\n\007content\030\002 \001(\014B\031\n\016com.test.protoB" +
      "\007MsgInfob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_MsgBody_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_MsgBody_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_MsgBody_descriptor,
        new java.lang.String[] { "Command", "Content", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}

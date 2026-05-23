package io.bubble.core.protocol.consult.proto;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final class ConsultProto {

    /* JADX INFO: renamed from: io.bubble.core.protocol.consult.proto.ConsultProto$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke;

        static {
            int[] iArr = new int[GeneratedMessageLite.MethodToInvoke.values().length];
            $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke = iArr;
            try {
                iArr[GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.NEW_BUILDER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.BUILD_MESSAGE_INFO.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_DEFAULT_INSTANCE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_PARSER.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_MEMOIZED_IS_INITIALIZED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.SET_MEMOIZED_IS_INITIALIZED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    public static final class AESConsult extends GeneratedMessageLite<AESConsult, AESConsult.Builder> implements AESConsultOrBuilder {
        public static final int AES_IV_FIELD_NUMBER = 2;
        public static final int AES_KEY_ENCRYPTED_FIELD_NUMBER = 1;
        public static final int DATA_FIELD_NUMBER = 3;
        private static final AESConsult DEFAULT_INSTANCE;
        public static final int ENCRYPT_DATA_FIELD_NUMBER = 4;
        private static volatile Parser<AESConsult> PARSER = null;
        public static final int STATE_FIELD_NUMBER = 5;
        private ByteString aESIv_;
        private ByteString aESKeyEncrypted_;
        private ByteString data_;
        private ByteString encryptData_;
        private int state_;

        public static final class Builder extends GeneratedMessageLite.Builder<AESConsult, AESConsult.Builder> implements AESConsultOrBuilder {
            public Builder clearAESIv() {
                copyOnWrite();
                ((AESConsult) this.instance).clearAESIv();
                return this;
            }

            public Builder clearAESKeyEncrypted() {
                copyOnWrite();
                ((AESConsult) this.instance).clearAESKeyEncrypted();
                return this;
            }

            public Builder clearData() {
                copyOnWrite();
                ((AESConsult) this.instance).clearData();
                return this;
            }

            public Builder clearEncryptData() {
                copyOnWrite();
                ((AESConsult) this.instance).clearEncryptData();
                return this;
            }

            public Builder clearState() {
                copyOnWrite();
                ((AESConsult) this.instance).clearState();
                return this;
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.AESConsultOrBuilder
            public ByteString getAESIv() {
                return ((AESConsult) this.instance).getAESIv();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.AESConsultOrBuilder
            public ByteString getAESKeyEncrypted() {
                return ((AESConsult) this.instance).getAESKeyEncrypted();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.AESConsultOrBuilder
            public ByteString getData() {
                return ((AESConsult) this.instance).getData();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.AESConsultOrBuilder
            public ByteString getEncryptData() {
                return ((AESConsult) this.instance).getEncryptData();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.AESConsultOrBuilder
            public int getState() {
                return ((AESConsult) this.instance).getState();
            }

            public Builder setAESIv(ByteString byteString) {
                copyOnWrite();
                ((AESConsult) this.instance).setAESIv(byteString);
                return this;
            }

            public Builder setAESKeyEncrypted(ByteString byteString) {
                copyOnWrite();
                ((AESConsult) this.instance).setAESKeyEncrypted(byteString);
                return this;
            }

            public Builder setData(ByteString byteString) {
                copyOnWrite();
                ((AESConsult) this.instance).setData(byteString);
                return this;
            }

            public Builder setEncryptData(ByteString byteString) {
                copyOnWrite();
                ((AESConsult) this.instance).setEncryptData(byteString);
                return this;
            }

            public Builder setState(int i10) {
                copyOnWrite();
                ((AESConsult) this.instance).setState(i10);
                return this;
            }

            private Builder() {
                super(AESConsult.DEFAULT_INSTANCE);
            }
        }

        static {
            AESConsult aESConsult = new AESConsult();
            DEFAULT_INSTANCE = aESConsult;
            GeneratedMessageLite.registerDefaultInstance(AESConsult.class, aESConsult);
        }

        private AESConsult() {
            ByteString byteString = ByteString.EMPTY;
            this.aESKeyEncrypted_ = byteString;
            this.aESIv_ = byteString;
            this.data_ = byteString;
            this.encryptData_ = byteString;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearAESIv() {
            this.aESIv_ = getDefaultInstance().getAESIv();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearAESKeyEncrypted() {
            this.aESKeyEncrypted_ = getDefaultInstance().getAESKeyEncrypted();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearData() {
            this.data_ = getDefaultInstance().getData();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearEncryptData() {
            this.encryptData_ = getDefaultInstance().getEncryptData();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearState() {
            this.state_ = 0;
        }

        public static AESConsult getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static AESConsult parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (AESConsult) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static AESConsult parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (AESConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Parser<AESConsult> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAESIv(ByteString byteString) {
            byteString.getClass();
            this.aESIv_ = byteString;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAESKeyEncrypted(ByteString byteString) {
            byteString.getClass();
            this.aESKeyEncrypted_ = byteString;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setData(ByteString byteString) {
            byteString.getClass();
            this.data_ = byteString;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setEncryptData(ByteString byteString) {
            byteString.getClass();
            this.encryptData_ = byteString;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setState(int i10) {
            this.state_ = i10;
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            int i10 = AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()];
            switch (i10) {
                case 1:
                    return new AESConsult();
                case 2:
                    return new Builder();
                case 3:
                    return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0000\u0005\u0000\u0000\u0001\u0005\u0005\u0000\u0000\u0000\u0001\n\u0002\n\u0003\n\u0004\n\u0005\u000b", new Object[]{"aESKeyEncrypted_", "aESIv_", "data_", "encryptData_", "state_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<AESConsult> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (AESConsult.class) {
                            defaultInstanceBasedParser = PARSER;
                            if (defaultInstanceBasedParser == null) {
                                defaultInstanceBasedParser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                                PARSER = defaultInstanceBasedParser;
                            }
                        }
                    }
                    return defaultInstanceBasedParser;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.AESConsultOrBuilder
        public ByteString getAESIv() {
            return this.aESIv_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.AESConsultOrBuilder
        public ByteString getAESKeyEncrypted() {
            return this.aESKeyEncrypted_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.AESConsultOrBuilder
        public ByteString getData() {
            return this.data_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.AESConsultOrBuilder
        public ByteString getEncryptData() {
            return this.encryptData_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.AESConsultOrBuilder
        public int getState() {
            return this.state_;
        }

        public static Builder newBuilder(AESConsult aESConsult) {
            return DEFAULT_INSTANCE.createBuilder(aESConsult);
        }

        public static AESConsult parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (AESConsult) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static AESConsult parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (AESConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static AESConsult parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (AESConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static AESConsult parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (AESConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static AESConsult parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (AESConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static AESConsult parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (AESConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static AESConsult parseFrom(InputStream inputStream) throws IOException {
            return (AESConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static AESConsult parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (AESConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static AESConsult parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (AESConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static AESConsult parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (AESConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }
    }

    public interface AESConsultOrBuilder extends MessageLiteOrBuilder {
        ByteString getAESIv();

        ByteString getAESKeyEncrypted();

        ByteString getData();

        ByteString getEncryptData();

        int getState();
    }

    public static final class IdentityCheck extends GeneratedMessageLite<IdentityCheck, IdentityCheck.Builder> implements IdentityCheckOrBuilder {
        public static final int DATA_FIELD_NUMBER = 1;
        private static final IdentityCheck DEFAULT_INSTANCE;
        public static final int ENCRYPT_DATA_FIELD_NUMBER = 2;
        private static volatile Parser<IdentityCheck> PARSER;
        private ByteString data_;
        private ByteString encryptData_;

        public static final class Builder extends GeneratedMessageLite.Builder<IdentityCheck, IdentityCheck.Builder> implements IdentityCheckOrBuilder {
            public Builder clearData() {
                copyOnWrite();
                ((IdentityCheck) this.instance).clearData();
                return this;
            }

            public Builder clearEncryptData() {
                copyOnWrite();
                ((IdentityCheck) this.instance).clearEncryptData();
                return this;
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.IdentityCheckOrBuilder
            public ByteString getData() {
                return ((IdentityCheck) this.instance).getData();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.IdentityCheckOrBuilder
            public ByteString getEncryptData() {
                return ((IdentityCheck) this.instance).getEncryptData();
            }

            public Builder setData(ByteString byteString) {
                copyOnWrite();
                ((IdentityCheck) this.instance).setData(byteString);
                return this;
            }

            public Builder setEncryptData(ByteString byteString) {
                copyOnWrite();
                ((IdentityCheck) this.instance).setEncryptData(byteString);
                return this;
            }

            private Builder() {
                super(IdentityCheck.DEFAULT_INSTANCE);
            }
        }

        static {
            IdentityCheck identityCheck = new IdentityCheck();
            DEFAULT_INSTANCE = identityCheck;
            GeneratedMessageLite.registerDefaultInstance(IdentityCheck.class, identityCheck);
        }

        private IdentityCheck() {
            ByteString byteString = ByteString.EMPTY;
            this.data_ = byteString;
            this.encryptData_ = byteString;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearData() {
            this.data_ = getDefaultInstance().getData();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearEncryptData() {
            this.encryptData_ = getDefaultInstance().getEncryptData();
        }

        public static IdentityCheck getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static IdentityCheck parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (IdentityCheck) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static IdentityCheck parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (IdentityCheck) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Parser<IdentityCheck> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setData(ByteString byteString) {
            byteString.getClass();
            this.data_ = byteString;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setEncryptData(ByteString byteString) {
            byteString.getClass();
            this.encryptData_ = byteString;
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            int i10 = AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()];
            switch (i10) {
                case 1:
                    return new IdentityCheck();
                case 2:
                    return new Builder();
                case 3:
                    return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001\n\u0002\n", new Object[]{"data_", "encryptData_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<IdentityCheck> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (IdentityCheck.class) {
                            defaultInstanceBasedParser = PARSER;
                            if (defaultInstanceBasedParser == null) {
                                defaultInstanceBasedParser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                                PARSER = defaultInstanceBasedParser;
                            }
                        }
                    }
                    return defaultInstanceBasedParser;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.IdentityCheckOrBuilder
        public ByteString getData() {
            return this.data_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.IdentityCheckOrBuilder
        public ByteString getEncryptData() {
            return this.encryptData_;
        }

        public static Builder newBuilder(IdentityCheck identityCheck) {
            return DEFAULT_INSTANCE.createBuilder(identityCheck);
        }

        public static IdentityCheck parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (IdentityCheck) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static IdentityCheck parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (IdentityCheck) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static IdentityCheck parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (IdentityCheck) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static IdentityCheck parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (IdentityCheck) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static IdentityCheck parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (IdentityCheck) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static IdentityCheck parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (IdentityCheck) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static IdentityCheck parseFrom(InputStream inputStream) throws IOException {
            return (IdentityCheck) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static IdentityCheck parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (IdentityCheck) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static IdentityCheck parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (IdentityCheck) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static IdentityCheck parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (IdentityCheck) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }
    }

    public interface IdentityCheckOrBuilder extends MessageLiteOrBuilder {
        ByteString getData();

        ByteString getEncryptData();
    }

    public static final class IdentityConsult extends GeneratedMessageLite<IdentityConsult, IdentityConsult.Builder> implements IdentityConsultOrBuilder {
        private static final IdentityConsult DEFAULT_INSTANCE;
        private static volatile Parser<IdentityConsult> PARSER = null;
        public static final int RANDOM_NUMBER_FIELD_NUMBER = 1;
        private long randomNumber_;

        public static final class Builder extends GeneratedMessageLite.Builder<IdentityConsult, IdentityConsult.Builder> implements IdentityConsultOrBuilder {
            public Builder clearRandomNumber() {
                copyOnWrite();
                ((IdentityConsult) this.instance).clearRandomNumber();
                return this;
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.IdentityConsultOrBuilder
            public long getRandomNumber() {
                return ((IdentityConsult) this.instance).getRandomNumber();
            }

            public Builder setRandomNumber(long j10) {
                copyOnWrite();
                ((IdentityConsult) this.instance).setRandomNumber(j10);
                return this;
            }

            private Builder() {
                super(IdentityConsult.DEFAULT_INSTANCE);
            }
        }

        static {
            IdentityConsult identityConsult = new IdentityConsult();
            DEFAULT_INSTANCE = identityConsult;
            GeneratedMessageLite.registerDefaultInstance(IdentityConsult.class, identityConsult);
        }

        private IdentityConsult() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearRandomNumber() {
            this.randomNumber_ = 0L;
        }

        public static IdentityConsult getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static IdentityConsult parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (IdentityConsult) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static IdentityConsult parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (IdentityConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Parser<IdentityConsult> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRandomNumber(long j10) {
            this.randomNumber_ = j10;
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            int i10 = AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()];
            switch (i10) {
                case 1:
                    return new IdentityConsult();
                case 2:
                    return new Builder();
                case 3:
                    return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0000\u0000\u0001\u0003", new Object[]{"randomNumber_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<IdentityConsult> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (IdentityConsult.class) {
                            defaultInstanceBasedParser = PARSER;
                            if (defaultInstanceBasedParser == null) {
                                defaultInstanceBasedParser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                                PARSER = defaultInstanceBasedParser;
                            }
                        }
                    }
                    return defaultInstanceBasedParser;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.IdentityConsultOrBuilder
        public long getRandomNumber() {
            return this.randomNumber_;
        }

        public static Builder newBuilder(IdentityConsult identityConsult) {
            return DEFAULT_INSTANCE.createBuilder(identityConsult);
        }

        public static IdentityConsult parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (IdentityConsult) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static IdentityConsult parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (IdentityConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static IdentityConsult parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (IdentityConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static IdentityConsult parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (IdentityConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static IdentityConsult parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (IdentityConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static IdentityConsult parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (IdentityConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static IdentityConsult parseFrom(InputStream inputStream) throws IOException {
            return (IdentityConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static IdentityConsult parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (IdentityConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static IdentityConsult parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (IdentityConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static IdentityConsult parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (IdentityConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }
    }

    public interface IdentityConsultOrBuilder extends MessageLiteOrBuilder {
        long getRandomNumber();
    }

    public static final class KeyConsult extends GeneratedMessageLite<KeyConsult, KeyConsult.Builder> implements KeyConsultOrBuilder {
        private static final KeyConsult DEFAULT_INSTANCE;
        public static final int IS_CONSULT_KEY_FIELD_NUMBER = 2;
        public static final int KEY_FIELD_NUMBER = 1;
        private static volatile Parser<KeyConsult> PARSER = null;
        public static final int STATE_FIELD_NUMBER = 3;
        private boolean isConsultKey_;
        private ByteString key_ = ByteString.EMPTY;
        private int state_;

        public static final class Builder extends GeneratedMessageLite.Builder<KeyConsult, KeyConsult.Builder> implements KeyConsultOrBuilder {
            public Builder clearIsConsultKey() {
                copyOnWrite();
                ((KeyConsult) this.instance).clearIsConsultKey();
                return this;
            }

            public Builder clearKey() {
                copyOnWrite();
                ((KeyConsult) this.instance).clearKey();
                return this;
            }

            public Builder clearState() {
                copyOnWrite();
                ((KeyConsult) this.instance).clearState();
                return this;
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.KeyConsultOrBuilder
            public boolean getIsConsultKey() {
                return ((KeyConsult) this.instance).getIsConsultKey();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.KeyConsultOrBuilder
            public ByteString getKey() {
                return ((KeyConsult) this.instance).getKey();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.KeyConsultOrBuilder
            public int getState() {
                return ((KeyConsult) this.instance).getState();
            }

            public Builder setIsConsultKey(boolean z10) {
                copyOnWrite();
                ((KeyConsult) this.instance).setIsConsultKey(z10);
                return this;
            }

            public Builder setKey(ByteString byteString) {
                copyOnWrite();
                ((KeyConsult) this.instance).setKey(byteString);
                return this;
            }

            public Builder setState(int i10) {
                copyOnWrite();
                ((KeyConsult) this.instance).setState(i10);
                return this;
            }

            private Builder() {
                super(KeyConsult.DEFAULT_INSTANCE);
            }
        }

        static {
            KeyConsult keyConsult = new KeyConsult();
            DEFAULT_INSTANCE = keyConsult;
            GeneratedMessageLite.registerDefaultInstance(KeyConsult.class, keyConsult);
        }

        private KeyConsult() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearIsConsultKey() {
            this.isConsultKey_ = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearKey() {
            this.key_ = getDefaultInstance().getKey();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearState() {
            this.state_ = 0;
        }

        public static KeyConsult getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static KeyConsult parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (KeyConsult) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static KeyConsult parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (KeyConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Parser<KeyConsult> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setIsConsultKey(boolean z10) {
            this.isConsultKey_ = z10;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setKey(ByteString byteString) {
            byteString.getClass();
            this.key_ = byteString;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setState(int i10) {
            this.state_ = i10;
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            int i10 = AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()];
            switch (i10) {
                case 1:
                    return new KeyConsult();
                case 2:
                    return new Builder();
                case 3:
                    return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0000\u0000\u0001\n\u0002\u0007\u0003\u000b", new Object[]{"key_", "isConsultKey_", "state_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<KeyConsult> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (KeyConsult.class) {
                            defaultInstanceBasedParser = PARSER;
                            if (defaultInstanceBasedParser == null) {
                                defaultInstanceBasedParser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                                PARSER = defaultInstanceBasedParser;
                            }
                        }
                    }
                    return defaultInstanceBasedParser;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.KeyConsultOrBuilder
        public boolean getIsConsultKey() {
            return this.isConsultKey_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.KeyConsultOrBuilder
        public ByteString getKey() {
            return this.key_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.KeyConsultOrBuilder
        public int getState() {
            return this.state_;
        }

        public static Builder newBuilder(KeyConsult keyConsult) {
            return DEFAULT_INSTANCE.createBuilder(keyConsult);
        }

        public static KeyConsult parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (KeyConsult) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static KeyConsult parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (KeyConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static KeyConsult parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (KeyConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static KeyConsult parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (KeyConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static KeyConsult parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (KeyConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static KeyConsult parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (KeyConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static KeyConsult parseFrom(InputStream inputStream) throws IOException {
            return (KeyConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static KeyConsult parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (KeyConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static KeyConsult parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (KeyConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static KeyConsult parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (KeyConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }
    }

    public interface KeyConsultOrBuilder extends MessageLiteOrBuilder {
        boolean getIsConsultKey();

        ByteString getKey();

        int getState();
    }

    public static final class RSAConsult extends GeneratedMessageLite<RSAConsult, RSAConsult.Builder> implements RSAConsultOrBuilder {
        private static final RSAConsult DEFAULT_INSTANCE;
        private static volatile Parser<RSAConsult> PARSER = null;
        public static final int RSA_BITS_FIELD_NUMBER = 1;
        public static final int RSA_KEY_EXPONENT_FIELD_NUMBER = 3;
        public static final int RSA_KEY_MODULUS_FIELD_NUMBER = 2;
        private int rsaBits_;
        private ByteString rsaKeyExponent_;
        private ByteString rsaKeyModulus_;

        public static final class Builder extends GeneratedMessageLite.Builder<RSAConsult, RSAConsult.Builder> implements RSAConsultOrBuilder {
            public Builder clearRsaBits() {
                copyOnWrite();
                ((RSAConsult) this.instance).clearRsaBits();
                return this;
            }

            public Builder clearRsaKeyExponent() {
                copyOnWrite();
                ((RSAConsult) this.instance).clearRsaKeyExponent();
                return this;
            }

            public Builder clearRsaKeyModulus() {
                copyOnWrite();
                ((RSAConsult) this.instance).clearRsaKeyModulus();
                return this;
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.RSAConsultOrBuilder
            public int getRsaBits() {
                return ((RSAConsult) this.instance).getRsaBits();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.RSAConsultOrBuilder
            public ByteString getRsaKeyExponent() {
                return ((RSAConsult) this.instance).getRsaKeyExponent();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.RSAConsultOrBuilder
            public ByteString getRsaKeyModulus() {
                return ((RSAConsult) this.instance).getRsaKeyModulus();
            }

            public Builder setRsaBits(int i10) {
                copyOnWrite();
                ((RSAConsult) this.instance).setRsaBits(i10);
                return this;
            }

            public Builder setRsaKeyExponent(ByteString byteString) {
                copyOnWrite();
                ((RSAConsult) this.instance).setRsaKeyExponent(byteString);
                return this;
            }

            public Builder setRsaKeyModulus(ByteString byteString) {
                copyOnWrite();
                ((RSAConsult) this.instance).setRsaKeyModulus(byteString);
                return this;
            }

            private Builder() {
                super(RSAConsult.DEFAULT_INSTANCE);
            }
        }

        static {
            RSAConsult rSAConsult = new RSAConsult();
            DEFAULT_INSTANCE = rSAConsult;
            GeneratedMessageLite.registerDefaultInstance(RSAConsult.class, rSAConsult);
        }

        private RSAConsult() {
            ByteString byteString = ByteString.EMPTY;
            this.rsaKeyModulus_ = byteString;
            this.rsaKeyExponent_ = byteString;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearRsaBits() {
            this.rsaBits_ = 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearRsaKeyExponent() {
            this.rsaKeyExponent_ = getDefaultInstance().getRsaKeyExponent();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearRsaKeyModulus() {
            this.rsaKeyModulus_ = getDefaultInstance().getRsaKeyModulus();
        }

        public static RSAConsult getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static RSAConsult parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (RSAConsult) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static RSAConsult parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (RSAConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Parser<RSAConsult> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRsaBits(int i10) {
            this.rsaBits_ = i10;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRsaKeyExponent(ByteString byteString) {
            byteString.getClass();
            this.rsaKeyExponent_ = byteString;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRsaKeyModulus(ByteString byteString) {
            byteString.getClass();
            this.rsaKeyModulus_ = byteString;
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            int i10 = AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()];
            switch (i10) {
                case 1:
                    return new RSAConsult();
                case 2:
                    return new Builder();
                case 3:
                    return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0000\u0000\u0001\u000b\u0002\n\u0003\n", new Object[]{"rsaBits_", "rsaKeyModulus_", "rsaKeyExponent_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<RSAConsult> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (RSAConsult.class) {
                            defaultInstanceBasedParser = PARSER;
                            if (defaultInstanceBasedParser == null) {
                                defaultInstanceBasedParser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                                PARSER = defaultInstanceBasedParser;
                            }
                        }
                    }
                    return defaultInstanceBasedParser;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.RSAConsultOrBuilder
        public int getRsaBits() {
            return this.rsaBits_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.RSAConsultOrBuilder
        public ByteString getRsaKeyExponent() {
            return this.rsaKeyExponent_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.RSAConsultOrBuilder
        public ByteString getRsaKeyModulus() {
            return this.rsaKeyModulus_;
        }

        public static Builder newBuilder(RSAConsult rSAConsult) {
            return DEFAULT_INSTANCE.createBuilder(rSAConsult);
        }

        public static RSAConsult parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (RSAConsult) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static RSAConsult parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (RSAConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static RSAConsult parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (RSAConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static RSAConsult parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (RSAConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static RSAConsult parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (RSAConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static RSAConsult parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (RSAConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static RSAConsult parseFrom(InputStream inputStream) throws IOException {
            return (RSAConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static RSAConsult parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (RSAConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static RSAConsult parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (RSAConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static RSAConsult parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (RSAConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }
    }

    public interface RSAConsultOrBuilder extends MessageLiteOrBuilder {
        int getRsaBits();

        ByteString getRsaKeyExponent();

        ByteString getRsaKeyModulus();
    }

    public static final class ShakeHand extends GeneratedMessageLite<ShakeHand, ShakeHand.Builder> implements ShakeHandOrBuilder {
        public static final int DATA_FIELD_NUMBER = 1;
        private static final ShakeHand DEFAULT_INSTANCE;
        public static final int ENCRYPT_DATA_FIELD_NUMBER = 2;
        private static volatile Parser<ShakeHand> PARSER;
        private int dataMemoizedSerializedSize = -1;
        private int encryptDataMemoizedSerializedSize = -1;
        private Internal.IntList data_ = GeneratedMessageLite.emptyIntList();
        private Internal.IntList encryptData_ = GeneratedMessageLite.emptyIntList();

        public static final class Builder extends GeneratedMessageLite.Builder<ShakeHand, ShakeHand.Builder> implements ShakeHandOrBuilder {
            public Builder addAllData(Iterable<? extends Integer> iterable) {
                copyOnWrite();
                ((ShakeHand) this.instance).addAllData(iterable);
                return this;
            }

            public Builder addAllEncryptData(Iterable<? extends Integer> iterable) {
                copyOnWrite();
                ((ShakeHand) this.instance).addAllEncryptData(iterable);
                return this;
            }

            public Builder addData(int i10) {
                copyOnWrite();
                ((ShakeHand) this.instance).addData(i10);
                return this;
            }

            public Builder addEncryptData(int i10) {
                copyOnWrite();
                ((ShakeHand) this.instance).addEncryptData(i10);
                return this;
            }

            public Builder clearData() {
                copyOnWrite();
                ((ShakeHand) this.instance).clearData();
                return this;
            }

            public Builder clearEncryptData() {
                copyOnWrite();
                ((ShakeHand) this.instance).clearEncryptData();
                return this;
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
            public int getData(int i10) {
                return ((ShakeHand) this.instance).getData(i10);
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
            public int getDataCount() {
                return ((ShakeHand) this.instance).getDataCount();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
            public List<Integer> getDataList() {
                return Collections.unmodifiableList(((ShakeHand) this.instance).getDataList());
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
            public int getEncryptData(int i10) {
                return ((ShakeHand) this.instance).getEncryptData(i10);
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
            public int getEncryptDataCount() {
                return ((ShakeHand) this.instance).getEncryptDataCount();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
            public List<Integer> getEncryptDataList() {
                return Collections.unmodifiableList(((ShakeHand) this.instance).getEncryptDataList());
            }

            public Builder setData(int i10, int i11) {
                copyOnWrite();
                ((ShakeHand) this.instance).setData(i10, i11);
                return this;
            }

            public Builder setEncryptData(int i10, int i11) {
                copyOnWrite();
                ((ShakeHand) this.instance).setEncryptData(i10, i11);
                return this;
            }

            private Builder() {
                super(ShakeHand.DEFAULT_INSTANCE);
            }
        }

        static {
            ShakeHand shakeHand = new ShakeHand();
            DEFAULT_INSTANCE = shakeHand;
            GeneratedMessageLite.registerDefaultInstance(ShakeHand.class, shakeHand);
        }

        private ShakeHand() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addAllData(Iterable<? extends Integer> iterable) {
            ensureDataIsMutable();
            AbstractMessageLite.addAll((Iterable) iterable, (List) this.data_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addAllEncryptData(Iterable<? extends Integer> iterable) {
            ensureEncryptDataIsMutable();
            AbstractMessageLite.addAll((Iterable) iterable, (List) this.encryptData_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addData(int i10) {
            ensureDataIsMutable();
            this.data_.addInt(i10);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addEncryptData(int i10) {
            ensureEncryptDataIsMutable();
            this.encryptData_.addInt(i10);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearData() {
            this.data_ = GeneratedMessageLite.emptyIntList();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearEncryptData() {
            this.encryptData_ = GeneratedMessageLite.emptyIntList();
        }

        private void ensureDataIsMutable() {
            Internal.IntList intList = this.data_;
            if (intList.isModifiable()) {
                return;
            }
            this.data_ = GeneratedMessageLite.mutableCopy(intList);
        }

        private void ensureEncryptDataIsMutable() {
            Internal.IntList intList = this.encryptData_;
            if (intList.isModifiable()) {
                return;
            }
            this.encryptData_ = GeneratedMessageLite.mutableCopy(intList);
        }

        public static ShakeHand getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static ShakeHand parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (ShakeHand) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ShakeHand parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (ShakeHand) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Parser<ShakeHand> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setData(int i10, int i11) {
            ensureDataIsMutable();
            this.data_.setInt(i10, i11);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setEncryptData(int i10, int i11) {
            ensureEncryptDataIsMutable();
            this.encryptData_.setInt(i10, i11);
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            int i10 = AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()];
            switch (i10) {
                case 1:
                    return new ShakeHand();
                case 2:
                    return new Builder();
                case 3:
                    return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0000\u0002\u0000\u0000\u0001\u0002\u0002\u0000\u0002\u0000\u0001+\u0002+", new Object[]{"data_", "encryptData_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<ShakeHand> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (ShakeHand.class) {
                            defaultInstanceBasedParser = PARSER;
                            if (defaultInstanceBasedParser == null) {
                                defaultInstanceBasedParser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                                PARSER = defaultInstanceBasedParser;
                            }
                        }
                    }
                    return defaultInstanceBasedParser;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
        public int getData(int i10) {
            return this.data_.getInt(i10);
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
        public int getDataCount() {
            return this.data_.size();
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
        public List<Integer> getDataList() {
            return this.data_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
        public int getEncryptData(int i10) {
            return this.encryptData_.getInt(i10);
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
        public int getEncryptDataCount() {
            return this.encryptData_.size();
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.ShakeHandOrBuilder
        public List<Integer> getEncryptDataList() {
            return this.encryptData_;
        }

        public static Builder newBuilder(ShakeHand shakeHand) {
            return DEFAULT_INSTANCE.createBuilder(shakeHand);
        }

        public static ShakeHand parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ShakeHand) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ShakeHand parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ShakeHand) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static ShakeHand parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (ShakeHand) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static ShakeHand parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ShakeHand) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static ShakeHand parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (ShakeHand) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static ShakeHand parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ShakeHand) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static ShakeHand parseFrom(InputStream inputStream) throws IOException {
            return (ShakeHand) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ShakeHand parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ShakeHand) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ShakeHand parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (ShakeHand) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static ShakeHand parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ShakeHand) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }
    }

    public interface ShakeHandOrBuilder extends MessageLiteOrBuilder {
        int getData(int i10);

        int getDataCount();

        List<Integer> getDataList();

        int getEncryptData(int i10);

        int getEncryptDataCount();

        List<Integer> getEncryptDataList();
    }

    public static final class TransportConsult extends GeneratedMessageLite<TransportConsult, TransportConsult.Builder> implements TransportConsultOrBuilder {
        private static final TransportConsult DEFAULT_INSTANCE;
        public static final int INTERVAL_FIELD_NUMBER = 4;
        public static final int MAX_FRAME_SIZE_FIELD_NUMBER = 2;
        public static final int MAX_TRANSIMISSION_UNIT_FIELD_NUMBER = 3;
        private static volatile Parser<TransportConsult> PARSER = null;
        public static final int PROTOCOL_VERSION_FIELD_NUMBER = 1;
        public static final int SUPPORT_PROTOCOL_FIELD_NUMBER = 5;
        private int interval_;
        private int maxFrameSize_;
        private int maxTransimissionUnit_;
        private int protocolVersion_;
        private int supportProtocol_;

        public static final class Builder extends GeneratedMessageLite.Builder<TransportConsult, TransportConsult.Builder> implements TransportConsultOrBuilder {
            public Builder clearInterval() {
                copyOnWrite();
                ((TransportConsult) this.instance).clearInterval();
                return this;
            }

            public Builder clearMaxFrameSize() {
                copyOnWrite();
                ((TransportConsult) this.instance).clearMaxFrameSize();
                return this;
            }

            public Builder clearMaxTransimissionUnit() {
                copyOnWrite();
                ((TransportConsult) this.instance).clearMaxTransimissionUnit();
                return this;
            }

            public Builder clearProtocolVersion() {
                copyOnWrite();
                ((TransportConsult) this.instance).clearProtocolVersion();
                return this;
            }

            public Builder clearSupportProtocol() {
                copyOnWrite();
                ((TransportConsult) this.instance).clearSupportProtocol();
                return this;
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.TransportConsultOrBuilder
            public int getInterval() {
                return ((TransportConsult) this.instance).getInterval();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.TransportConsultOrBuilder
            public int getMaxFrameSize() {
                return ((TransportConsult) this.instance).getMaxFrameSize();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.TransportConsultOrBuilder
            public int getMaxTransimissionUnit() {
                return ((TransportConsult) this.instance).getMaxTransimissionUnit();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.TransportConsultOrBuilder
            public int getProtocolVersion() {
                return ((TransportConsult) this.instance).getProtocolVersion();
            }

            @Override // io.bubble.core.protocol.consult.proto.ConsultProto.TransportConsultOrBuilder
            public int getSupportProtocol() {
                return ((TransportConsult) this.instance).getSupportProtocol();
            }

            public Builder setInterval(int i10) {
                copyOnWrite();
                ((TransportConsult) this.instance).setInterval(i10);
                return this;
            }

            public Builder setMaxFrameSize(int i10) {
                copyOnWrite();
                ((TransportConsult) this.instance).setMaxFrameSize(i10);
                return this;
            }

            public Builder setMaxTransimissionUnit(int i10) {
                copyOnWrite();
                ((TransportConsult) this.instance).setMaxTransimissionUnit(i10);
                return this;
            }

            public Builder setProtocolVersion(int i10) {
                copyOnWrite();
                ((TransportConsult) this.instance).setProtocolVersion(i10);
                return this;
            }

            public Builder setSupportProtocol(int i10) {
                copyOnWrite();
                ((TransportConsult) this.instance).setSupportProtocol(i10);
                return this;
            }

            private Builder() {
                super(TransportConsult.DEFAULT_INSTANCE);
            }
        }

        static {
            TransportConsult transportConsult = new TransportConsult();
            DEFAULT_INSTANCE = transportConsult;
            GeneratedMessageLite.registerDefaultInstance(TransportConsult.class, transportConsult);
        }

        private TransportConsult() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearInterval() {
            this.interval_ = 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearMaxFrameSize() {
            this.maxFrameSize_ = 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearMaxTransimissionUnit() {
            this.maxTransimissionUnit_ = 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearProtocolVersion() {
            this.protocolVersion_ = 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSupportProtocol() {
            this.supportProtocol_ = 0;
        }

        public static TransportConsult getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static TransportConsult parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (TransportConsult) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static TransportConsult parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (TransportConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Parser<TransportConsult> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setInterval(int i10) {
            this.interval_ = i10;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setMaxFrameSize(int i10) {
            this.maxFrameSize_ = i10;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setMaxTransimissionUnit(int i10) {
            this.maxTransimissionUnit_ = i10;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setProtocolVersion(int i10) {
            this.protocolVersion_ = i10;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSupportProtocol(int i10) {
            this.supportProtocol_ = i10;
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            int i10 = AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()];
            switch (i10) {
                case 1:
                    return new TransportConsult();
                case 2:
                    return new Builder();
                case 3:
                    return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0000\u0005\u0000\u0000\u0001\u0005\u0005\u0000\u0000\u0000\u0001\u000b\u0002\u000b\u0003\u000b\u0004\u000b\u0005\u000b", new Object[]{"protocolVersion_", "maxFrameSize_", "maxTransimissionUnit_", "interval_", "supportProtocol_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<TransportConsult> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (TransportConsult.class) {
                            defaultInstanceBasedParser = PARSER;
                            if (defaultInstanceBasedParser == null) {
                                defaultInstanceBasedParser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                                PARSER = defaultInstanceBasedParser;
                            }
                        }
                    }
                    return defaultInstanceBasedParser;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.TransportConsultOrBuilder
        public int getInterval() {
            return this.interval_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.TransportConsultOrBuilder
        public int getMaxFrameSize() {
            return this.maxFrameSize_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.TransportConsultOrBuilder
        public int getMaxTransimissionUnit() {
            return this.maxTransimissionUnit_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.TransportConsultOrBuilder
        public int getProtocolVersion() {
            return this.protocolVersion_;
        }

        @Override // io.bubble.core.protocol.consult.proto.ConsultProto.TransportConsultOrBuilder
        public int getSupportProtocol() {
            return this.supportProtocol_;
        }

        public static Builder newBuilder(TransportConsult transportConsult) {
            return DEFAULT_INSTANCE.createBuilder(transportConsult);
        }

        public static TransportConsult parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TransportConsult) GeneratedMessageLite.parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static TransportConsult parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TransportConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static TransportConsult parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (TransportConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static TransportConsult parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TransportConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static TransportConsult parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (TransportConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static TransportConsult parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TransportConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static TransportConsult parseFrom(InputStream inputStream) throws IOException {
            return (TransportConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static TransportConsult parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TransportConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static TransportConsult parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (TransportConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static TransportConsult parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TransportConsult) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }
    }

    public interface TransportConsultOrBuilder extends MessageLiteOrBuilder {
        int getInterval();

        int getMaxFrameSize();

        int getMaxTransimissionUnit();

        int getProtocolVersion();

        int getSupportProtocol();
    }

    private ConsultProto() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite extensionRegistryLite) {
    }
}

package com.sesolutions.http;

import org.apache.http.entity.mime.MultipartEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.apache.http.entity.mime.HttpMultipartMode.BROWSER_COMPATIBLE;

public class MyMultiPartEntity extends MultipartEntity {

    private ProgressListener progressListener;

    public MyMultiPartEntity(final ProgressListener progressListener) {
        super(BROWSER_COMPATIBLE);
        this.progressListener = progressListener;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        //  super.writeTo(outstream);
        //  this.multipart.writeTo(outstream);
        super.writeTo(outstream instanceof ProgressOutputStream ? outstream :
                new ProgressOutputStream(outstream, this.progressListener,
                        this.getContentLength()));
    }

    public interface ProgressListener {
        void transferred(float progress);
    }

    public static class ProgressOutputStream extends FilterOutputStream {

        private final ProgressListener progressListener;

        private long transferred;

        private long total;

        public ProgressOutputStream(final OutputStream outputStream,
                                    final ProgressListener progressListener,
                                    long total) {

            super(outputStream);
            this.progressListener = progressListener;
            this.transferred = 0;
            this.total = total;
        }

        @Override
        public void write(byte[] buffer, int offset, int length) throws IOException {

            out.write(buffer, offset, length);
            this.transferred += length;
            this.progressListener.transferred(this.getCurrentProgress() );
        }

        @Override
        public void write(byte[] buffer) throws IOException {

            out.write(buffer);
            this.transferred++;
            this.progressListener.transferred(this.getCurrentProgress() );
        }

        private float getCurrentProgress() {
            return ((float) this.transferred / this.total) * 100;
        }
    }
}

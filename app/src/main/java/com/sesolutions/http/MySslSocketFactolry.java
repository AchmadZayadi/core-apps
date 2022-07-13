package com.sesolutions.http;

import com.sesolutions.utils.CustomLog;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by root on 5/2/18.
 */
/*

public class MySslSocketFactolry extends SSLSocketFactory {
    private SSLContext sslContext = SSLContext.getInstance("TLS");
    private X509Certificate CERT;

    public MySslSocketFactolry(final X509Certificate CERT, KeyStore truststore)
            throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        this.CERT = CERT;
        TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {

                try {
                    //Ignore error
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    new X509Certificate[]{CERT}[0].checkValidity();
                } catch (Exception e) {
                    CustomLog.e(e);
                    throw new CertificateException("Certificate not valid or trusted.");
                }
            }

           */
/* @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }*//*


           */
/* @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }*//*


            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] certificates = new X509Certificate[]{CERT};

                return certificates;
                //asset folder path is  taken in method.Only provide path respective to asset folder like /certificate/MyCertificate.crt inside assest.

            }
        };

        sslContext.init(null, new TrustManager[]{tm}, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port,
                               boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port,
                autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }


}
*/

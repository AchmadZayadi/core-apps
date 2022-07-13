package com.sesolutions.http

import android.content.Context
import android.content.res.AssetManager
import android.util.Base64

import com.sesolutions.utils.CustomLog

import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


internal object HttpRequestHelper {

    @JvmField
    var sslCertificate: String? = null
    @Throws(Exception::class)
    fun getCertFromFile(context: Context/*String rawSSL,Context context, String path*/): X509Certificate? {
        val assetManager = context.resources.assets
        var inputStream: InputStream? = null
        try {
            inputStream = assetManager.open("cert/ssl.crt")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val caInput = BufferedInputStream(inputStream)
        //InputStream caInput = new BufferedInputStream(new ByteArrayInputStream(sslCertificate.getBytes(Charset.forName("UTF-8"))));
        //CustomLog.e("certificate", sslCertificate);
        var cert: X509Certificate? = null
        val cf = CertificateFactory.getInstance("X509")
        cert = cf.generateCertificate(caInput) as X509Certificate
        //CustomLog.e("serialNumber", "" + cert.getSerialNumber());
        return cert
    }

    fun getCertFromString(): X509Certificate? {
        var cert: X509Certificate? = null
        try {

            val decoded = Base64.decode(sslCertificate, Base64.DEFAULT)
            val inputStream = ByteArrayInputStream(decoded)

            cert = CertificateFactory.getInstance("X.509").generateCertificate(inputStream) as? X509Certificate

        } catch (e : Exception){
            CustomLog.e(e)
        }
        return cert
    }
}

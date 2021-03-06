package com.unionpay.apay.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.commons.codec.binary.Base64;

/**
 * 将私钥文件，公钥文件转换成公私钥内容工具类
 * @ClassName CertUtil
 * @Description TODO
 * @date 2018-6-8 上午10:56:57
 * 声明：以下代码只是为了方便测试而提供的样例代码，可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码性能规范性等方面的保障
 */
public class CertUtil {
	
	public static PrivateKey getPriKey(KeyStore keyStore,String password){
		try {
			Enumeration<String> aliasenum  = keyStore.aliases();
			if (aliasenum.hasMoreElements()) {
				String keyAlias = aliasenum.nextElement();
				if (keyStore.isKeyEntry(keyAlias)) {
					PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias,password.toCharArray());
					return privateKey;
				}
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static PublicKey getPubKey(KeyStore keyStore){
		try {
			Enumeration<String> aliasenum = keyStore.aliases();
			String keyAlias = null;
			if (aliasenum.hasMoreElements()) {
				keyAlias = aliasenum.nextElement();
				if (keyStore.isKeyEntry(keyAlias)) {
					X509Certificate  x509Certificate = (X509Certificate) keyStore.getCertificate(keyAlias);
					PublicKey publicKey = x509Certificate.getPublicKey();
					return publicKey;
				}
			}
		  } catch (KeyStoreException e) {
			  e.printStackTrace();
		  }
		return null;
	}
	
	
	public static KeyStore loadKeyStore(String pfxkeyfile,String password){
		System.out.println("加载签名证书==>" + pfxkeyfile);
		FileInputStream fis = null;
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			fis = new FileInputStream(pfxkeyfile);
			char[] nPassword = password.toCharArray();
			if (null != keyStore) {
				keyStore.load(fis, nPassword);
			}
			return keyStore;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null!=fis)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
	public static PublicKey loadPubkey(String pubkeyfile){
		System.out.println("加载验签证书==>" + pubkeyfile);
		CertificateFactory cf = null;
		FileInputStream in = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
			in = new FileInputStream(pubkeyfile);
			X509Certificate validateCert = (X509Certificate) cf.generateCertificate(in);
			return validateCert.getPublicKey();
		}catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取公钥证书内容
	 * @param pubKeyFile
	 * @return
	 */
	public static String getPubKeyStr(String pubKeyFile){
		PublicKey publicKey1 = loadPubkey(pubKeyFile);
		return Base64.encodeBase64String(publicKey1.getEncoded());
	}
	
	/**
	 * 获取私钥证书-私钥内容
	 * @param priKeyFile
	 * @param pwd
	 * @return
	 */
	public static String getPriKeyStr(String priKeyFile,String pwd){
		KeyStore keyStore = loadKeyStore(priKeyFile,pwd);
		PrivateKey privateKey = getPriKey(keyStore,pwd);
		return Base64.encodeBase64String(privateKey.getEncoded());
	}
	
	/**
	 * 获取私钥证书-公钥内容
	 * @param priKeyFile
	 * @param pwd
	 * @return
	 */
	public static String getPubKeyStr(String priKeyFile,String pwd){
		KeyStore keyStore = loadKeyStore(priKeyFile,pwd);
		PublicKey priPublicKey = getPubKey(keyStore);
		return Base64.encodeBase64String(priPublicKey.getEncoded());
	}
	
	public static void main(String[] args) {
		String pfxkeyfile ="E:\\app\\apiclient_union.2.pfx";
		String password = "123456";
		String pubkeyfile ="E:\\app\\支付宝公钥.cer";
		
		System.out.println("签名私钥-私钥内容：" +getPriKeyStr(pfxkeyfile,password));
		System.out.println("签名私钥-公钥内容：" +getPubKeyStr(pfxkeyfile,password));
		
		System.out.println("验签证书-公钥内容："+getPubKeyStr(pubkeyfile));
	}
}

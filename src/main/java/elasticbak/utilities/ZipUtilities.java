package elasticbak.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class ZipUtilities {
	
	public void zipfile(String src, String dest, String passwd) {
		File srcfile = new File(src);
		// 创建目标文件
		ZipParameters par = new ZipParameters();
		par.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		par.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		if (passwd != null) {
			par.setEncryptFiles(true);
			par.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
			par.setPassword(passwd.toCharArray());
		}
		try {
			ZipFile zipfile = new ZipFile(dest);
			if (srcfile.isDirectory()) {

				File[] listFiles = srcfile.listFiles();
				ArrayList<File> temp = new ArrayList<File>();
				Collections.addAll(temp, listFiles);
				zipfile.addFiles(temp, par);

				zipfile.addFolder(srcfile, par);
			} else {
				zipfile.addFile(srcfile, par);
			}
		} catch (ZipException e) {
			e.printStackTrace();
		}

	}

	public  void unzipfile(File zipfile, String dest, String passwd) throws ZipException {
		ZipFile zfile = new ZipFile(zipfile);
		// zfile.setFileNameCharset("GBK");//在GBK系统中需要设置
		if (!zfile.isValidZipFile()) {
			throw new ZipException("压缩文件不合法，可能已经损坏！");
		}
		File file = new File(dest);
		if (file.isDirectory() && !file.exists()) {
			file.mkdirs();
		}
		if (zfile.isEncrypted()) {
			zfile.setPassword(passwd.toCharArray());
		}
		zfile.extractAll(dest);

	}

}

package Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.io.FileInputStream;

import Protocol.Protocol;
import service.PeerService;

public class Files {

	public static String getFileID(File file) {
		String file_metadata = file.getName() + file.lastModified() + PeerService.getLocalPeer().toString();

		return getSha256(file_metadata);
	}

	public static String getSha256(String base) {
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}

	public static byte[] getFileData(File file) throws FileNotFoundException {
		FileInputStream fileStream = new FileInputStream(file);


		byte[] fileData = new byte[(int) file.length()];

		try {
			fileStream.read(fileData);
			fileStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileData;
	}
}

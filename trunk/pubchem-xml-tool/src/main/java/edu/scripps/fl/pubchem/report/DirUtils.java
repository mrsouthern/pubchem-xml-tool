package edu.scripps.fl.pubchem.report;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class DirUtils {
	private static final Object tmpDirectoryLock = new Object();
	private static int counter = -1; /* Protected by tmpDirectoryLock */
	static int maxAttempts = 9;

	private static File generateFile(String prefix, String suffix, File dir) throws IOException {
		if (counter == -1) {
			counter = new Random().nextInt() & 0xffff;
		}
		counter++;
		return new File(dir, prefix + Integer.toString(counter) + suffix);
	}

	public static File createTempDirectory(String prefix, String suffix, File directory) throws IOException {
		if (prefix == null)
			throw new NullPointerException();
		if (prefix.length() < 3)
			throw new IllegalArgumentException("Prefix string too short");
		String s = (suffix == null) ? ".tmp" : suffix;
		synchronized (tmpDirectoryLock) {
			if (directory == null) {
				String tmpDir = getTempDir();
				directory = new File(tmpDir);
			}
			File f;
			int attemptCount = 0;
			do {
				if( attemptCount++ > maxAttempts )
					throw new IOException(
							String.format("The highly improbable has occurred! Failed to create a unique temporary directory after %s attempts."
									     ,maxAttempts));
				f = generateFile(prefix, s, directory);
			} while (!f.mkdirs());
			return f;
		}
	}

	public static File createTempDirectory(String prefix, String suffix) throws IOException {
		return createTempDirectory(prefix, suffix, null);
	}

	private static String getTempDir() {
		return System.getProperty("java.io.tmpdir");
	}

}

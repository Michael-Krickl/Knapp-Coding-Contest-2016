package com.knapp.codingcontest.cc20160408.util;

import com.knapp.codingcontest.cc20160408.Settings;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Helper class to create zip for upload
 */
public final class PrepareUpload {
	// ----------------------------------------------------------------------------


	private PrepareUpload() {

	}

	// ----------------------------------------------------------------------------


	/**
	 * Create uploadable zip file
	 *
	 * @throws Exception
	 */
	public static void CreateZipFile()
			throws Exception {

		final File basedir = new File(Settings.outputPath);

		final String zipFileName = Settings.outputPath + File.separator + Settings.outZipFilename;
		final File zipFile = new File(zipFileName);
		if(zipFile.exists()) {
			zipFile.delete();
		}

		ZipOutputStream archive = null;
		try {
			archive = new ZipOutputStream(new FileOutputStream(zipFile));

			final String resultsFileName = Settings.outputPath + File.separator + Settings.outReplenFilename;
			PrepareUpload.add(archive, basedir, new File(resultsFileName));

			final String propertiesFileName = Settings.outputPath + File.separator + Settings.outPropertyFilename;
			PrepareUpload.add(archive, basedir, new File(propertiesFileName));

			PrepareUpload.add(archive, basedir, new File("src"));
		} finally {
			if(archive != null) {
				archive.close();
			}
		}
	}

	// ............................................................................


	private static void add(final ZipOutputStream archive, final File basedir, final File file)
			throws IOException {

		if(file.isDirectory()) {
			if(PrepareUpload.shouldAddDirectory(basedir, file)) {
				PrepareUpload.addDirectory(archive, basedir, file);
			}
		} else {
			if(PrepareUpload.shouldAddFile(basedir, file)) {
				PrepareUpload.addFile(archive, basedir, file);
			}
		}
	}


	private static boolean shouldAddDirectory(final File basedir, final File file) {

		return !"META-INF".equals(file.getName());
	}

	// ----------------------------------------------------------------------------


	private static boolean shouldAddFile(final File basedir, final File file) {

		return !file.getName().equals(Settings.outZipFilename);
	}

	// ............................................................................


	private static void addDirectory(final ZipOutputStream archive, final File basedir, final File file)
			throws IOException {

		if(!basedir.equals(file)) {
			String name = file.getAbsolutePath().replace("\\", "/");
			if(!name.isEmpty()) {
				if(!name.endsWith("/")) {
					name += "/";
				}

				final ZipEntry entry = new ZipEntry(name.substring(basedir.getAbsolutePath().length() + 1));
				entry.setTime(file.lastModified());
				archive.putNextEntry(entry);
				archive.closeEntry();
			}
		}

		for(final File nestedFile : file.listFiles()) {
			PrepareUpload.add(archive, basedir, nestedFile);
		}
	}

	// ----------------------------------------------------------------------------


	private static void addFile(final ZipOutputStream archive, final File basedir, final File file)
			throws IOException, FileNotFoundException {

		BufferedInputStream in = null;
		try {
			final ZipEntry entry = new ZipEntry(file.getAbsolutePath()
			                                        .replace("\\", "/")
			                                        .substring(basedir.getAbsolutePath().length() + 1));
			entry.setTime(file.lastModified());
			archive.putNextEntry(entry);

			in = new BufferedInputStream(new FileInputStream(file));
			PrepareUpload.copyContent(in, archive);

			archive.closeEntry();
		} finally {
			if(in != null) {
				in.close();
			}
		}
	}

	// ----------------------------------------------------------------------------


	private static void copyContent(final InputStream in, final OutputStream out)
			throws IOException {

		final byte[] buffer = new byte[8192];
		for(int count = in.read(buffer); count >= 0; count = in.read(buffer)) {
			out.write(buffer, 0, count);
		}
	}
}

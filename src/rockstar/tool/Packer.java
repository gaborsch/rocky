package rockstar.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Packer {

	public static final String ROCKY_JAR = "rocky.jar";
	private static String ROCKY_PATH = ".";
	
	private void pack(String... args) {
		List<String> fileNames = new LinkedList<>();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("-rocky-path") && i+1 < args.length) {
				ROCKY_PATH = args[++i]; 
			} else if (! arg.startsWith("-")) {
				fileNames.add(arg);
			}
		}
		if (!fileNames.isEmpty()) {
			String mainFile = fileNames.remove(0);
			if (mainFile.endsWith(".rock")) {
				pack(mainFile, fileNames);
				return;
			}
		}
		System.out.println("Usage: pack.sh mainfile.rock [file.rock ...]");
	}
	
	public void pack(String mainFileName, List<String> rockFileNames) {
		String executableFileName = mainFileName.replace(".rock", "");
		List<String> allFileNames = new ArrayList<>(rockFileNames.size() + 1);
		allFileNames.add(mainFileName);
		allFileNames.addAll(rockFileNames);

		File outFile = new File(executableFileName);
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(outFile);

			prependShellWrapper(mainFileName, fos);
			fos.flush();

			zos = new ZipOutputStream(fos);
			repackRockyJar(zos);
			zos.flush();

			packRockFiles(allFileNames, zos);
			zos.flush();

		} catch (RuntimeException | IOException e) {
			throw new RuntimeException("Exception while creating file " + executableFileName, e);
			
		} finally {
			if (zos != null) {
				try {
					zos.flush();
					zos.close();
				} catch (IOException e) {
				}
			}
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
				}
				outFile.setExecutable(true, true);
			}
		}
		System.out.println("Executable file '" + executableFileName + "' created");

	}

	private void prependShellWrapper(String mainFileName, FileOutputStream fos) throws IOException {
		// #!/bin/sh
		// exec java -jar $0 run 'mainFileName' "$@"

		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(fos, "iso-8859-1");
			writer.append("#!/bin/sh\n");
			writer.append("exec java -jar $0 run '" + mainFileName + "' \"$@\"\n\n");
			writer.flush();
			fos.flush();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Invalid encoding", e);
		}
	}

	private void repackRockyJar(ZipOutputStream zos) throws IOException {
		String pathToRockyJar = ROCKY_PATH + "/" + ROCKY_JAR;
		if (pathToRockyJar.startsWith("/c/")) {
			pathToRockyJar = pathToRockyJar.substring(2);
		}
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(new FileInputStream(pathToRockyJar));
			System.out.println("Rocky found at " + pathToRockyJar);
			for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
//				System.out.println("File: " + entry.getName());
				if (entry.isDirectory()) {
					writeToZip(entry, null, zos);
				} else {
					writeToZip(entry, zis, zos);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not find " + pathToRockyJar +"! Add -rocky-path <path-to-rocky> to resolve.");
			throw new RuntimeException(e);
		} finally {
			if (zis != null) {
				zis.closeEntry();
				zis.close();
			}
		}
	}

	private void packRockFiles(List<String> rockFileNames, ZipOutputStream zos) throws IOException {
		Set<String> directoriesCreated = new HashSet<>();
		directoriesCreated.add("rockstar-lib/");

		for (String srcFile : rockFileNames) {
			File fileToZip = new File(srcFile);
			if (fileToZip.exists()) {
				String fileNameInZip = "rockstar-lib/" + srcFile;
				createDirectoriesFor(fileNameInZip, zos, directoriesCreated);
				FileInputStream fis = new FileInputStream(fileToZip);
				ZipEntry zipEntry = new ZipEntry(fileNameInZip);
				writeToZip(zipEntry, fis, zos);
				fis.close();
				System.out.println("Source file: " + srcFile);
			} else {
				System.out.println("Could not find file " + fileToZip +"!");
				throw new RuntimeException("File not found: "+ fileToZip);
			}
		}
	}

	private void createDirectoriesFor(String filename, ZipOutputStream zos, Set<String> directoriesCreated) throws IOException {
		String[] path = filename.split("/");
		// last part is the file name
		String current = "";
		for (int i = 0; i < path.length - 1; i++) {
			current = current + path[i] + "/";
			if (!directoriesCreated.contains(current)) {
				zos.putNextEntry(new ZipEntry(current));
				zos.closeEntry();
				directoriesCreated.add(current);
			}
		}
	}

	private byte[] bytes = new byte[1024];
	private void writeToZip(ZipEntry zipEntry, InputStream is, ZipOutputStream zos) throws IOException {
		zos.putNextEntry(zipEntry);
		if (is != null) {
			int length;
			while ((length = is.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
		}
//		System.out.println("File: " + zipEntry.getName());
	}

	public static void main(String[] args) {
//		new Packer().pack("-rocky-path","tmp","a.rock");
		new Packer().pack(args);
	}

	
}

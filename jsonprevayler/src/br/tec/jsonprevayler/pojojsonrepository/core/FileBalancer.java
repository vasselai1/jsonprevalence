package br.tec.jsonprevayler.pojojsonrepository.core;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBalancer {
	
	private final String prefixDirName;
	private final Integer limitForNew;//1.000 a 1.000.000 dependendo do sistema de arquivos?
	private final Path basePath;
	
	private Integer filesCounter = 0;
	private Integer directoriesCounter = 0;
	private Map<Long, Path> pathMapOfEntities = new HashMap<Long, Path>();
	private Path actualPath;
	
	private final FilenameFilter fileNameFilterDirectoryBalance = new FilenameFilter() {
		@Override
		public boolean accept(File dir, java.lang.String name) {
			return name.startsWith(prefixDirName);
		}
	};
	
	private final Comparator<File> indexFileComparator = new Comparator<File>() {
		@Override
		public int compare(File f1, File f2) {
			Integer indexF1 = Integer.parseInt(f1.getName().replace(prefixDirName, ""));
			Integer indexF2 = Integer.parseInt(f2.getName().replace(prefixDirName, ""));
			return indexF1.compareTo(indexF2);
		}
	};
	
	public FileBalancer(String prefixDirName, Integer limitForNew, Path basePath) {
		this.prefixDirName = prefixDirName;
		this.limitForNew = limitForNew;
		this.basePath = basePath;
	}

	public void addPath(Long id, Path filePath) {
		pathMapOfEntities.put(id, filePath);
		filesCounter++;
	}

	public Path getPath(Long id) {
		return pathMapOfEntities.get(id);
	}
	
	public void resetFilesCounter() {
		filesCounter = 0;
	}	
	
	public List<File> listBalancedDirectories() {
		List<File> balancedDirectories = null;
		File[] directories = basePath.toFile().listFiles(fileNameFilterDirectoryBalance);
		if ((directories != null) && (directories.length > 0)) {
			balancedDirectories = Arrays.asList(directories);
			balancedDirectories.sort(indexFileComparator);
			directoriesCounter = directories.length;
		} else {
			directoriesCounter = 1;
			File firstDir = new File(basePath.toFile(), prefixDirName + directoriesCounter);
			firstDir.mkdir();
			balancedDirectories = Arrays.asList(basePath.toFile().listFiles(fileNameFilterDirectoryBalance));
		}
		if (isActualPathNotInitialized()) {
			actualPath = balancedDirectories.get(balancedDirectories.size() - 1).toPath();
		}
		return balancedDirectories;
	}
		
	public synchronized File getNewFile(String fileName) {
		filesCounter++;
		if (filesCounter <= limitForNew) {
			return new File(actualPath.toFile(), fileName);
		}
		filesCounter = 1;
		directoriesCounter++;
		File newDir = new File(basePath.toFile(), prefixDirName + directoriesCounter);
		newDir.mkdir();
		actualPath = newDir.toPath();
		return new File(newDir, fileName);
	}	
	
	public boolean isActualPathNotInitialized() {
		return (actualPath == null);
	}
}
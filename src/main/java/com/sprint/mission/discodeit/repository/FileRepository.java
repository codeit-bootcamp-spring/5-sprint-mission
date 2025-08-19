package com.sprint.mission.discodeit.repository;

import java.util.Map;

public interface FileRepository<K, V> {
	void createDirectoryIfNotExists();
	void loadFile();
	void saveFile();
}
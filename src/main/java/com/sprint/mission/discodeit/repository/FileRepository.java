package com.sprint.mission.discodeit.repository;

import java.util.Map;

public interface FileRepository {
	void createDirectoryIfNotExists();
	void loadFile(String filename, Map map);
	void saveFile(String filename, Object data);
}
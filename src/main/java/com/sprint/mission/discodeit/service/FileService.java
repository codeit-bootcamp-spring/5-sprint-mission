package com.sprint.mission.discodeit.service;

import java.util.Map;

public interface FileService {
	void createDirectoryIfNotExists();
	void loadFile(String filename, Map map);
	void saveFile(String filename, Object data);
}

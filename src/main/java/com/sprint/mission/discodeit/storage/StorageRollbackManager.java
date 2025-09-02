package com.sprint.mission.discodeit.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
public class StorageRollbackManager {

    public StorageRollbackToken start(BinaryContentStorage storage) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return StorageRollbackToken.noop();
        }
        StorageRollbackToken token = new StorageRollbackToken(storage);
        TransactionSynchronizationManager.registerSynchronization(token);
        return token;
    }

    public static class StorageRollbackToken implements TransactionSynchronization {

        private final BinaryContentStorage storage;
        private final List<UUID> ids = new ArrayList<>();
        private final boolean noop;

        private StorageRollbackToken(BinaryContentStorage storage) {
            this.storage = storage;
            this.noop = (storage == null);
        }

        public static StorageRollbackToken noop() {
            return new StorageRollbackToken(null);
        }

        public void add(UUID id) {
            if (!noop) {
                ids.add(id);
            }
        }

        @Override
        public void afterCompletion(int status) {
            if (noop) {
                return;
            }
            if (status == STATUS_ROLLED_BACK) {
                for (UUID id : ids) {
                    try {
                        storage.delete(id);
                    } catch (Exception e) {
                        log.warn("보상 삭제 실패: {}", id, e);
                    }
                }
            }
        }
    }
}

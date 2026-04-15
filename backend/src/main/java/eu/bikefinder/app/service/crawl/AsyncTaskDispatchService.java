package eu.bikefinder.app.service.crawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AsyncTaskDispatchService {

    private static final Logger log = LoggerFactory.getLogger(AsyncTaskDispatchService.class);

    private final TaskExecutor taskExecutor;
    private final ConcurrentMap<String, TaskState> tasks = new ConcurrentHashMap<>();

    public AsyncTaskDispatchService(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public TaskState dispatch(String taskType, Runnable taskWork) {
        String taskId = UUID.randomUUID().toString();
        TaskState state = TaskState.queued(taskId, taskType);
        tasks.put(taskId, state);
        taskExecutor.execute(() -> runTask(state, taskWork));
        return state;
    }

    public Optional<TaskState> find(String taskId) {
        return Optional.ofNullable(tasks.get(taskId));
    }

    private void runTask(TaskState state, Runnable taskWork) {
        state.markRunning();
        try {
            taskWork.run();
            state.markSucceeded();
        } catch (Exception ex) {
            state.markFailed(ex.getMessage());
            log.error("Async task {} ({}) failed", state.taskId(), state.taskType(), ex);
        }
    }

    public static final class TaskState {
        private final String taskId;
        private final String taskType;
        private final Instant queuedAt;
        private volatile String status;
        private volatile Instant startedAt;
        private volatile Instant finishedAt;
        private volatile String errorMessage;

        private TaskState(String taskId, String taskType, Instant queuedAt) {
            this.taskId = taskId;
            this.taskType = taskType;
            this.queuedAt = queuedAt;
            this.status = "queued";
        }

        public static TaskState queued(String taskId, String taskType) {
            return new TaskState(taskId, taskType, Instant.now());
        }

        public synchronized void markRunning() {
            this.status = "running";
            this.startedAt = Instant.now();
        }

        public synchronized void markSucceeded() {
            this.status = "succeeded";
            this.finishedAt = Instant.now();
        }

        public synchronized void markFailed(String errorMessage) {
            this.status = "failed";
            this.errorMessage = errorMessage;
            this.finishedAt = Instant.now();
        }

        public String taskId() {
            return taskId;
        }

        public String taskType() {
            return taskType;
        }

        public Instant queuedAt() {
            return queuedAt;
        }

        public String status() {
            return status;
        }

        public Instant startedAt() {
            return startedAt;
        }

        public Instant finishedAt() {
            return finishedAt;
        }

        public String errorMessage() {
            return errorMessage;
        }
    }
}

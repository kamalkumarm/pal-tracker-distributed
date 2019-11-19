package io.pivotal.pal.tracker.allocations;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String registrationServerEndpoint;
    private ConcurrentHashMap<Long, ProjectInfo> concurrentProjectMap = new ConcurrentHashMap<>();
    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations= restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
    }
    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo projectInfo =restOperations.getForObject(registrationServerEndpoint + "/projects/" + projectId, ProjectInfo.class);
        concurrentProjectMap.put(projectId,projectInfo);
        return projectInfo;
    }
    public ProjectInfo getProjectFromCache(long projectId) {
        return concurrentProjectMap.get(projectId);
    }
}

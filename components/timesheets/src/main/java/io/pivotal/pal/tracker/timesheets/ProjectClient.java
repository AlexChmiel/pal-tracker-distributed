package io.pivotal.pal.tracker.timesheets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProjectClient {

    @Autowired
    private final RestOperations restOperations;
    private final String endpoint;
    private Map<Long, ProjectInfo> map = new ConcurrentHashMap<>();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo project = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        storeProjectInMemory(project, projectId);

        return project;
    }

    public void storeProjectInMemory(ProjectInfo project, long id){
        map.put(id, project);
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        return map.get(projectId);
    }
}

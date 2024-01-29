import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActor
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.util.SimpleErrorCollection
import groovy.transform.Field
import org.apache.log4j.Level
import org.apache.log4j.Logger

import java.util.stream.Collectors

@Field Logger log = Logger.getLogger("Add group to role: ")
log.setLevel(Level.DEBUG)

ProjectRoleManager projectRoleManager = (ProjectRoleManager) ComponentAccessor.getComponentOfType(ProjectRoleManager.class)
ProjectRoleService projectRoleService = (ProjectRoleService) ComponentAccessor.getComponentOfType(ProjectRoleService.class)
List<String> projKeys=ComponentAccessor.projectManager.getProjects().stream().map{it -> it.getKey()}.collect(Collectors.toList())
String groupToAdd = "USER_GROUPS"
List<ProjectRole> roles = projectRoleManager.getProjectRoles().toList()
//List<String> rolesToOperateOn = ["Administrators","Developers","Users","Timesheet Project Managers","Tempo Project Managers"]
List<String> rolesToOperateOn = ["Administrators"]
//ApplicationUser a = ComponentAccessor.userManager.getUserByName("user_to_add")
for (ProjectRole role : roles) {
    log.info(role.name)
    if(rolesToOperateOn.indexOf(role.name) == -1) continue
    for (String projKey : projKeys) {
        Project proj = ComponentAccessor.projectManager.getProjectByCurrentKey(projKey)
        if(proj == null) {
            log.info("no such project ${projKey}")
            continue
        }
        def sec = new SimpleErrorCollection()
        projectRoleService.addActorsToProjectRole([groupToAdd], role, proj, ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE, sec)
        log.info(sec.getErrorMessages())
    }
}

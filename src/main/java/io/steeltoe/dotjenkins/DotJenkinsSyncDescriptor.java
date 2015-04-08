package io.steeltoe.dotjenkins;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

@Extension
public class DotJenkinsSyncDescriptor extends BuildStepDescriptor<Builder> {

  public DotJenkinsSyncDescriptor() {
    super(DotJenkinsSyncBuilder.class);
    load();
  }

  public String getDisplayName() {
    return "(dot) Jenkins Sync";
  }

  @Override
  public boolean isApplicable(Class<? extends AbstractProject> jobType) {
    return true;
  }
}

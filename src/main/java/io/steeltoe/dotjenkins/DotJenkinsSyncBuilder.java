package io.steeltoe.dotjenkins;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.scm.ChangeLogSet;
import hudson.tasks.Builder;
import javaposse.jobdsl.plugin.ExecuteDslScripts;
import javaposse.jobdsl.plugin.LookupStrategy;
import javaposse.jobdsl.plugin.RemovedJobAction;
import javaposse.jobdsl.plugin.RemovedViewAction;
import org.kohsuke.stapler.DataBoundConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A build step to sync dotjenkins iff changes have been made.
 */
public class DotJenkinsSyncBuilder extends Builder {

  private static final Logger log = LoggerFactory.getLogger(DotJenkinsSyncBuilder.class);

  private static final boolean IGNORE_EXISTING = false;
  private static final String EXTRA_CP = "";
  private static final String SUFFIX = "/*.groovy";

  private final String directory;
  private final ExecuteDslScripts delegate;

  @DataBoundConstructor
  public DotJenkinsSyncBuilder(String directory) {
    if (directory == null || directory.isEmpty()) {
      this.directory = ".jenkins";
    } else {
      this.directory = directory;
    }
    log.error("directory is " + this.directory);
    
    this.delegate = new ExecuteDslScripts(
        new ExecuteDslScripts.ScriptLocation("", this.directory + SUFFIX, ""),
        IGNORE_EXISTING,
        RemovedJobAction.DISABLE,
        RemovedViewAction.DELETE,
        LookupStrategy.SEED_JOB,
        EXTRA_CP);
  }

  public String getDirectory() {
    return this.directory;
  }

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
      throws InterruptedException, IOException {
    final ChangeLogSet<? extends ChangeLogSet.Entry> changes = build.getChangeSet();
    if (changes.isEmptySet() || shouldSync(changes)) {
      // run the action
      listener.getLogger().println("[dotjenkins] syncing dotjenkins");
      return delegate.perform(build, launcher, listener);
    } else {
      // don't run the action
      listener.getLogger().println("[dotjenkins] skipping sync, no changes detected");
      return true;
    }
  }

  /*
   * determines if the sync should proceed by checking if any changes apply to the directory
   */
  private boolean shouldSync(ChangeLogSet<? extends ChangeLogSet.Entry> changes) {
    for (ChangeLogSet.Entry entry : changes) {
      for (String path : entry.getAffectedPaths()) {
        if (path.contains(directory)) {
          return true;
        }
      }
    }
    return false;
  }
}

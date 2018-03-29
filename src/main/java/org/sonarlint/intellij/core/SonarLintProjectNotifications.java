/*
 * CodeScan for IntelliJ IDEA
 * Copyright (C) 2015 SonarSource
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarlint.intellij.core;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import javax.annotation.Nullable;
import javax.swing.event.HyperlinkEvent;
import org.jetbrains.annotations.NotNull;
import org.sonarlint.intellij.config.global.SonarLintGlobalConfigurable;
import org.sonarlint.intellij.config.global.SonarQubeServer;
import org.sonarlint.intellij.config.global.SonarQubeServerMgmtPanel;
import org.sonarlint.intellij.config.project.SonarLintProjectConfigurable;
import org.sonarsource.sonarlint.core.client.api.connected.ConnectedSonarLintEngine;

public class SonarLintProjectNotifications extends AbstractProjectComponent {
  public static final String GROUP_UPDATE_NOTIFICATION = "CodeScan: Configuration update";
  public static final String GROUP_BINDING_PROBLEM = "CodeScan: Server Binding Errors";
  private static final String UPDATE_SERVER_MSG = "\n<br>Please update the SonarQube server in the <a href='#'>CodeScan General Settings</a>";
  private static final String UPDATE_BINDING_MSG = "\n<br>Please check the <a href='#'>CodeScan project configuration</a>";
  private volatile boolean shown = false;

  protected SonarLintProjectNotifications(Project project) {
    super(project);
  }

  public static SonarLintProjectNotifications get(Project project) {
    return project.getComponent(SonarLintProjectNotifications.class);
  }

  public void reset() {
    shown = false;
  }

  public void notifyServerIdInvalid() {
    if (shown) {
      return;
    }
    Notification notification = new Notification(GROUP_BINDING_PROBLEM,
      "<b>CodeScan - Project bound to invalid SonarQube server</b>",
      UPDATE_BINDING_MSG,
      NotificationType.WARNING, new OpenProjectSettingsNotificationListener(myProject));
    notification.setImportant(true);
    notification.notify(myProject);
    shown = true;
  }

  public void notifyModuleInvalid() {
    if (shown) {
      return;
    }
    Notification notification = new Notification(GROUP_BINDING_PROBLEM,
      "<b>CodeScan - Project bound to an invalid remote module</b>",
      UPDATE_BINDING_MSG,
      NotificationType.WARNING, new OpenProjectSettingsNotificationListener(myProject));
    notification.setImportant(true);
    notification.notify(myProject);
    shown = true;
  }

  public void notifyModuleStale() {
    if (shown) {
      return;
    }
    Notification notification = new Notification(GROUP_BINDING_PROBLEM,
      "<b>CodeScan - Project's binding data is invalid</b>",
      UPDATE_BINDING_MSG,
      NotificationType.WARNING, new OpenProjectSettingsNotificationListener(myProject));
    notification.setImportant(true);
    notification.notify(myProject);
    shown = true;
  }

  public void notifyServerNotUpdated() {
    if (shown) {
      return;
    }
    Notification notification = new Notification(GROUP_BINDING_PROBLEM,
      "<b>CodeScan - No data for SonarQube server</b>",
      UPDATE_SERVER_MSG,
      NotificationType.WARNING, new OpenGeneralSettingsNotificationListener(myProject));
    notification.setImportant(true);
    notification.notify(myProject);
    shown = true;
  }

  public void notifyServerStorageNeedsUpdate(String serverId) {
    if (shown) {
      return;
    }
    Notification notification = new Notification(GROUP_BINDING_PROBLEM,
      "<b>CodeScan - Binding for server '" + serverId + "' outdated</b>",
      UPDATE_SERVER_MSG,
      NotificationType.WARNING, new OpenGeneralSettingsNotificationListener(myProject));
    notification.setImportant(true);
    notification.notify(myProject);
    shown = true;
  }

  public void notifyServerHasUpdates(String serverId, ConnectedSonarLintEngine engine, SonarQubeServer server, boolean onlyProjects) {
    Notification notification = new Notification(GROUP_UPDATE_NOTIFICATION,
      "Configuration update",
      "Configuration changed on SonarQube server '" + serverId + "'. <a href=\"#update\">Update now</a>",
      NotificationType.INFORMATION, new NotificationListener.Adapter() {
      @Override
      public void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
        notification.hideBalloon();
        SonarQubeServerMgmtPanel.updateServerBinding(server, engine, onlyProjects);
      }
    });
    notification.notify(myProject);
  }

  private static class OpenProjectSettingsNotificationListener extends NotificationListener.Adapter {
    private final Project project;

    public OpenProjectSettingsNotificationListener(@Nullable Project project) {
      this.project = project;
    }

    @Override
    protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent e) {
      if (project != null && !project.isDisposed()) {
        SonarLintProjectConfigurable configurable = new SonarLintProjectConfigurable(project);
        ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
      } else {
        notification.expire();
      }
    }
  }

  private static class OpenGeneralSettingsNotificationListener extends NotificationListener.Adapter {
    private final Project project;

    public OpenGeneralSettingsNotificationListener(@Nullable Project project) {
      this.project = project;
    }

    @Override
    protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent e) {
      if (project != null && !project.isDisposed()) {
        SonarLintGlobalConfigurable configurable = new SonarLintGlobalConfigurable();
        ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
      } else {
        notification.expire();
      }
    }
  }
}

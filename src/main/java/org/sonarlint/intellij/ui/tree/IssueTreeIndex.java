/*
 * CodeScan for IntelliJ IDEA
 * Copyright (C) 2015-2020 SonarSource
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
package org.sonarlint.intellij.ui.tree;

import com.intellij.openapi.vfs.VirtualFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;
import org.sonarlint.intellij.ui.nodes.FileNode;

public class IssueTreeIndex {
  private final Map<VirtualFile, FileNode> fileNodes = new HashMap<>();

  @CheckForNull
  public FileNode getFileNode(VirtualFile file) {
    return fileNodes.get(file);
  }

  public void setFileNode(FileNode node) {
    fileNodes.put(node.file(), node);
  }

  public void remove(VirtualFile file) {
    fileNodes.remove(file);
  }

  public void clear() {
    fileNodes.clear();
  }

  public Set<VirtualFile> getAllFiles() {
    return fileNodes.keySet();
  }
}

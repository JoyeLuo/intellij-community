// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.diagnostic.errordialog;

import com.intellij.diagnostic.Developer;
import com.intellij.diagnostic.DiagnosticBundle;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.ComboboxSpeedSearch;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.util.containers.ComparatorUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author ksafonov
 */
public class DetailsTabForm {
  private JTextArea myDetailsPane;
  private JPanel myContentPane;
  private LabeledTextComponent myCommentsArea;
  private JPanel myDetailsHolder;
  private JButton myAnalyzeStacktraceButton;
  private JComboBox<Developer> myAssigneeComboBox;
  private JPanel myAssigneePanel;
  private Integer myAssigneeId;
  private boolean myProcessEvents = true;

  public DetailsTabForm(@Nullable Action analyzeAction) {
    myCommentsArea.setTitle(DiagnosticBundle.message("error.dialog.comment.prompt"));
    myDetailsPane.setBackground(UIUtil.getTextFieldBackground());
    myDetailsHolder.setPreferredSize(JBUI.size(670, 150));
    myDetailsHolder.setBorder(IdeBorderFactory.createBorder());
    if (analyzeAction != null) {
      myAnalyzeStacktraceButton.setAction(analyzeAction);
    }
    else {
      myAnalyzeStacktraceButton.setVisible(false);
    }
    myAssigneeComboBox.setRenderer(new DeveloperRenderer());
    myAssigneeComboBox.setPrototypeDisplayValue(new Developer(0, "Here Goes Some Very Long String"));
    myAssigneeComboBox.addActionListener(new ActionListenerProxy(e -> myAssigneeId = getAssigneeId()));
    new ComboboxSpeedSearch(myAssigneeComboBox) {
      @Override
      protected String getElementText(Object element) {
        return element == null ? "" : ((Developer)element).getSearchableText();
      }
    };
  }

  public void setCommentsAreaVisible(boolean b) {
    myCommentsArea.getContentPane().setVisible(b);
  }

  public void setDetailsText(String s) {
    LabeledTextComponent.setText(myDetailsPane, s, false);
  }

  public void setCommentsText(String s) {
    LabeledTextComponent.setText(myCommentsArea.getTextComponent(), s, true);
  }

  public JPanel getContentPane() {
    return myContentPane;
  }

  public JComponent getPreferredFocusedComponent() {
    return myCommentsArea.getContentPane().isVisible() ? myCommentsArea.getTextComponent() : null;
  }

  public void setCommentsTextEnabled(boolean b) {
    if (myCommentsArea.getContentPane().isVisible()) {
      myCommentsArea.getTextComponent().setEnabled(b);
    }
  }

  public void addCommentsListener(LabeledTextComponent.TextListener l) {
    myCommentsArea.addCommentsListener(l);
  }

  public void setAssigneeVisible(boolean visible) {
    myAssigneePanel.setVisible(visible);
  }

  public void setDevelopers(List<Developer> developers) {
    myAssigneeComboBox.setModel(new CollectionComboBoxModel<>(developers));
    updateSelectedDeveloper();
  }

  public void setAssigneeId(@Nullable Integer assigneeId) {
    myAssigneeId = assigneeId;
    if (myAssigneeComboBox.getItemCount() > 0) {
      updateSelectedDeveloper();
    }
  }

  private void updateSelectedDeveloper() {
    myProcessEvents = false;

    Integer index = null;
    for (int i = 0; i < myAssigneeComboBox.getItemCount(); i++) {
      Developer developer = myAssigneeComboBox.getItemAt(i);
      if (ComparatorUtil.equalsNullable(developer.getId(), myAssigneeId)) {
        index = i;
        break;
      }
    }
    setSelectedAssigneeIndex(index);

    myProcessEvents = true;
  }

  private void setSelectedAssigneeIndex(Integer index) {
    if (index == null) {
      myAssigneeComboBox.setSelectedItem(null);
    }
    else {
      myAssigneeComboBox.setSelectedIndex(index);
    }
  }

  @Nullable
  public Integer getAssigneeId() {
    Developer assignee = (Developer)myAssigneeComboBox.getSelectedItem();
    return assignee == null ? null : assignee.getId();
  }

  public void addAssigneeListener(ActionListener listener) {
    myAssigneeComboBox.addActionListener(new ActionListenerProxy(listener));
  }

  private static class DeveloperRenderer extends ListCellRendererWrapper<Developer> {
    @Override
    public void customize(JList list, Developer value, int index, boolean selected, boolean hasFocus) {
      setText(value == null ? "<unavailable>" : value.getDisplayText());
    }
  }

  private class ActionListenerProxy implements ActionListener {
    private final ActionListener myDelegate;

    public ActionListenerProxy(ActionListener delegate) {
      myDelegate = delegate;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (myProcessEvents) {
        myDelegate.actionPerformed(e);
      }
    }
  }
}
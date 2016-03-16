/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Created by IntelliJ IDEA.
 * User: max
 * Date: May 16, 2002
 * Time: 6:16:33 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.intellij.codeInsight.template.impl.actions;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.codeInsight.template.impl.TemplateState;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.application.TransactionKind;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PreviousVariableAction extends EditorAction {
  public PreviousVariableAction() {
    super(new Handler());
    setInjectedContext(true);
  }

  private static class Handler extends EditorActionHandler {

    @Override
    public AccessToken startTransaction(@NotNull Editor editor) {
      return TransactionGuard.getInstance().startSynchronousTransaction(TransactionKind.TEXT_EDITING);
    }

    @Override
    protected void doExecute(Editor editor, @Nullable Caret caret, DataContext dataContext) {
      final TemplateState templateState = TemplateManagerImpl.getTemplateState(editor);
      assert templateState != null;
      CommandProcessor.getInstance().setCurrentCommandName(CodeInsightBundle.message("template.previous.variable.command"));
      templateState.previousTab();
    }

    @Override
    protected boolean isEnabledForCaret(@NotNull Editor editor, @NotNull Caret caret, DataContext dataContext) {
      final TemplateState templateState = TemplateManagerImpl.getTemplateState(editor);
      return templateState != null && !templateState.isFinished();
    }
  }
}

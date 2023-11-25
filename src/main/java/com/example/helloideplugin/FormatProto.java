package com.example.helloideplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.ui.Messages;

public class FormatProto extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取当前的编辑器和项目对象
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Project project = e.getProject();

        // 获取选中的文本
        String selectedText = editor.getSelectionModel().getSelectedText();

        // todo: 原地修改选中的文本
        // 在选中的文本第一行后添加：aa
        // 第二行后添加：bb
        // 如果选中的文本不为空，进行修改操作
        // 如果选中的文本不为空，进行修改操作
        if (selectedText != null) {
            // 添加的文本内容
            String addedText = "aa\nbb\n";

            // 获取文档对象，用于修改编辑器中的文本
            Document document = editor.getDocument();

            // 在写操作的上下文中执行插入操作
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // 获取选中文本的结束偏移量
                int endOffset = editor.getSelectionModel().getSelectionEnd();

                // 在编辑器中插入文本
                document.insertString(endOffset, addedText);
            });

            // 弹窗显示修改后的文本
            Messages.showMessageDialog(project, "Modified Text:\n" + selectedText + addedText, "Modified Text", Messages.getInformationIcon());
        }
    }
}

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

import java.util.Arrays;
import java.util.List;

public class FormatProto extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取当前的编辑器和项目对象
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Project project = e.getProject();

        // 获取选中的文本
        String selectedText = editor.getSelectionModel().getSelectedText();
        // 获取选中的文本的起始行数和结束行数
        int offsetStart = editor.getSelectionModel().getSelectionStart();
        int offsetEnd = editor.getSelectionModel().getSelectionEnd();
        int startLine = editor.getDocument().getLineNumber(offsetStart);
        int endLine = editor.getDocument().getLineNumber(offsetEnd);
        // log
        System.out.println("selectedText: " + selectedText);
        System.out.println("startLine: " + startLine);
        System.out.println("endLine: " + endLine);

        if (selectedText != null) {
            // 添加的文本内容
            String addedText = "aa\nbb\n";

            // 获取文档对象，用于修改编辑器中的文本
            Document document = editor.getDocument();

            // 在写操作的上下文中执行插入操作
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // todo 将选中的文本按行分隔，传入 formatMessage 方法，得到格式化后的文本
                // 将选中的文本按行分隔
                List<String> msgLines = Arrays.asList(selectedText.split("\n"));
                try {
                    // 格式化选中的文本
                    List<String> formattedLines = formatMessage(msgLines);

                    // 替换选中的文本
                    document.replaceString(offsetStart, offsetEnd, String.join("\n", formattedLines));
                } catch (Exception exception) {
                    Messages.showErrorDialog(project, exception.getMessage(), "Format Error");
                    exception.printStackTrace();
                }
//                // 把每一行替换成格式化后的文本
//                for (int i = startLine; i <= endLine; i++) {
//                    // 替换 startLine 行的内容
//                    int startOffset = document.getLineStartOffset(startLine);
//                    int endOffset = document.getLineEndOffset(startLine);
//                    // todo
//                }


            });
        }
    }

    private static List<String> formatMessage(List<String> msgLines) throws Exception {
        int headIdx = -1;
        for (int i = 0; i < msgLines.size(); i++) {
            String line = msgLines.get(i);
            if (line.contains("message")) {
                headIdx = i;
                break;
            }
        }
        if (headIdx == -1) {
            throw new Exception("Could not find message head");
        }

        int maxLeftLen = -1;
        int endIdx = headIdx + 1;
        int fldNum = 0;

        for (; endIdx < msgLines.size(); endIdx++) {
            if (msgLines.get(endIdx).equals("}")) {
                break;
            }

            int idxSepCh = msgLines.get(endIdx).indexOf(";");
            if (idxSepCh < 0) {
                continue;
            }

            int idxEqCh = msgLines.get(endIdx).indexOf("=");
            if (idxEqCh < 0) {
                continue;
            }

            fldNum++;
            msgLines.set(endIdx, String.format("%s= %d%s", msgLines.get(endIdx).substring(0, idxEqCh), fldNum, msgLines.get(endIdx).substring(idxSepCh)));
            maxLeftLen = Math.max(maxLeftLen, msgLines.get(endIdx).indexOf(";") + 1);
        }

        if (endIdx == msgLines.size()) {
            throw new Exception("Could not find message end");
        }

        for (int i = headIdx + 1; i < endIdx; i++) {
            int idxSepCh = msgLines.get(i).indexOf(";");
            if (idxSepCh < 0) {
                continue;
            }

            int leftLen = idxSepCh + 1;
            int idxComment = msgLines.get(i).indexOf("//");
            if (idxComment < 0) {
                continue;
            }

            msgLines.set(i, String.format("%s%s%s", msgLines.get(i).substring(0, leftLen), repeatSpaces(maxLeftLen - leftLen + 1), msgLines.get(i).substring(idxComment)));
        }

        return msgLines;
    }

    private static String repeatSpaces(int count) {
        return " ".repeat(count);
    }
}

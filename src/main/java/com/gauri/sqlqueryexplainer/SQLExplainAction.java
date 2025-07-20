package com.gauri.sqlqueryexplainer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.jetbrains.annotations.NotNull;

public class SQLExplainAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);

        if(editor != null){
            SelectionModel selectionModel = editor.getSelectionModel();
            String selectedText = selectionModel.getSelectedText();

            if(selectedText != null){
                String explanation = explainSQL(selectedText);
//                Messages.showMessageDialog(project, explanation, "SQL Query Explanation", Messages.getInformationIcon());
                ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("SQL Explainer");
                if (toolWindow != null) {
                    toolWindow.activate(() -> SQLToolWindowFactory.jTextArea.setText(explanation), true);
                }
            }
            else{
                Messages.showMessageDialog(project, "Please select an SQL query to explain.", "No Selection", Messages.getWarningIcon());
            }
        }
    }

    String explainSQL(String query) {
        try {
            Statement stmt = CCJSqlParserUtil.parse(query);

            if (stmt instanceof Select) {
                Select select = (Select) stmt;
                SelectBody selectBody = select.getSelectBody();

                if (selectBody instanceof PlainSelect) {
                    PlainSelect plainSelect = (PlainSelect) selectBody;

                    StringBuilder explanation = new StringBuilder("SQL Query Breakdown:\n\n");

                    // 🔹 SELECT
                    explanation.append("SELECT: ").append(plainSelect.getSelectItems()).append("\n");
                    explanation.append("    Retrieves the specified columns from the table.\n\n");

                    // 🔹 FROM
                    explanation.append("FROM: ").append(plainSelect.getFromItem()).append("\n");
                    explanation.append("    Specifies the source table containing the data.\n\n");

                    // 🔹 WHERE
                    if (plainSelect.getWhere() != null) {
                        explanation.append("WHERE: ").append(plainSelect.getWhere()).append("\n");
                        explanation.append("    Filters the rows based on the given condition.\n\n");
                    }

                    // 🔹 JOIN
                    if (plainSelect.getJoins() != null) {
                        explanation.append("JOIN: ").append(plainSelect.getJoins()).append("\n");
                        explanation.append("    Combines rows from multiple tables based on related columns.\n\n");
                    }

                    // 🔹 GROUP BY
                    if (plainSelect.getGroupBy() != null && plainSelect.getGroupBy().getGroupByExpressionList() != null) {
                        explanation.append("GROUP BY: ").append(plainSelect.getGroupBy().getGroupByExpressionList().getExpressions()).append("\n");
                        explanation.append("    Groups rows that have the same values in specified columns.\n\n");
                    }

                    // 🔹 HAVING
                    if (plainSelect.getHaving() != null) {
                        explanation.append("HAVING: ").append(plainSelect.getHaving()).append("\n");
                        explanation.append("    → Applies conditions to grouped rows (after GROUP BY).\n\n");
                    }

                    // 🔹 ORDER BY
                    if (plainSelect.getOrderByElements() != null) {
                        explanation.append("ORDER BY: ").append(plainSelect.getOrderByElements()).append("\n");
                        explanation.append("    → Sorts the result set based on one or more columns.\n\n");
                    }

                    // 🔹 LIMIT
                    if (plainSelect.getLimit() != null) {
                        explanation.append("LIMIT: ").append(plainSelect.getLimit()).append("\n");
                        explanation.append("    → Restricts the number of rows returned by the query.\n\n");
                    }

                    return explanation.toString();
                }
            }
        } catch (Exception e) {
            return "Error parsing SQL: " + e.getMessage();
        }

        return "Unsupported or invalid SQL statement.";
    }
}
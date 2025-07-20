package com.gauri.sqlqueryexplainer;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class SQLToolWindowFactory implements ToolWindowFactory, DumbAware {

    public static JTextArea jTextArea;

    @Override
    public void createToolWindowContent(@NotNull Project project, ToolWindow toolWindow) {
        JPanel panel = new JPanel(new BorderLayout());

        // 🔹 Sample SQLs
        List<String> sampleQueries = Arrays.asList(
                "SELECT * FROM employees;",
                "SELECT name, salary FROM employees WHERE department = 'HR';",
                "SELECT department, COUNT(*) FROM employees GROUP BY department;",
                "SELECT * FROM orders WHERE price > 500 ORDER BY date DESC LIMIT 10;",
                "SELECT p.name, o.quantity FROM products p JOIN orders o ON p.id = o.product_id;"
        );

        JComboBox<String> queryDropdown = new com.intellij.openapi.ui.ComboBox<>(sampleQueries.toArray(new String[0]));
        JButton explainButton = new JButton("Explain Selected Query");

        // 🔹 Text area for explanation
        jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        JScrollPane scrollPane = new com.intellij.ui.components.JBScrollPane(jTextArea);

        // 🔹 Button Action
        explainButton.addActionListener(e -> {
            String selectedQuery = (String) queryDropdown.getSelectedItem();
            if (selectedQuery != null && !selectedQuery.isEmpty()) {
                String explanation = new SQLExplainAction().explainSQL(selectedQuery);
                jTextArea.setText(explanation);
            }
        });

        // 🔹 Top panel with dropdown and button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(queryDropdown, BorderLayout.CENTER);
        topPanel.add(explainButton, BorderLayout.EAST);

        // 🔹 Add components to main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 🔹 Show in tool window
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}

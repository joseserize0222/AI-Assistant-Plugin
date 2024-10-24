package com.github.joseserize0222.aiassistantplugin.toolWindow
import com.github.joseserize0222.aiassistantplugin.services.FileAnalyzerService
import com.github.joseserize0222.aiassistantplugin.utils.FileStatsListener
import com.github.joseserize0222.aiassistantplugin.utils.KotlinFileStats
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.*

class ProjectStatsPanel(project: Project) : FileStatsListener, Disposable {
    val content: JComponent
    private val editorField: EditorTextField
    init {
        val panel = JBPanel<JBPanel<*>>(BorderLayout())
        val fileType = FileTypeManager.getInstance().getFileTypeByExtension("kt")
        editorField = EditorTextField("", project, fileType).apply {
            isViewer = true
            font = Font("JetBrains Mono", Font.PLAIN, 12)
            setOneLineMode(false)
            setAutoscrolls(true)
            text = "Please select a File for showing statistics."
            this.addSettingsProvider { editor ->
                val contentComponent = editor.contentComponent
                contentComponent.border = BorderFactory.createEmptyBorder(
                    10,
                    10,
                    10,
                    10)
            }
        }
        val scrollPane = JBScrollPane(editorField)
        panel.add(scrollPane, BorderLayout.CENTER)
        content = panel
        ApplicationManager.getApplication().messageBus.connect(this).subscribe(
            LafManagerListener.TOPIC, LafManagerListener {
                updateEditorFieldTheme()
            }
        )
        project.service<FileAnalyzerService>().addListener(this)
    }

    private fun updateEditorFieldTheme() {
        val scheme = EditorColorsManager.getInstance().globalScheme
        editorField.background = scheme.defaultBackground
        editorField.foreground = scheme.defaultForeground
    }

    override fun callback(allStats: KotlinFileStats) {
        val stats = buildString {
            append("Kotlin File Stats for ${allStats.fileName}\n\n")
            append("All Lines: ${allStats.totalLines}\n")
            append("TODO Lines: ${allStats.todoLines}\n")
            append("Longest Function: " +
                    "${allStats.getFunctionName()} with " +
                    "${allStats.getFunctionLines()} " +
                    "${ if (allStats.getFunctionLines() == 1) 
                        "line" 
                    else 
                        "lines"}\n")
            append("Body Expression:\n")
            append(allStats.getFunctionContent())
        }
        SwingUtilities.invokeLater {
            editorField.text = stats
        }
    }

    override fun dispose() {}
}
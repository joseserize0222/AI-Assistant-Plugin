package com.github.joseserize0222.aiassistantplugin.toolWindow
import com.github.joseserize0222.aiassistantplugin.services.KtorClientService
import com.github.joseserize0222.aiassistantplugin.utils.ChatGptListener
import com.intellij.ide.ui.LafManagerListener
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

class ChatGptPanel(project: Project) : ChatGptListener {
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
            text = "Please select a function to be described"
        }
        val scrollPane = JBScrollPane(editorField)
        panel.add(scrollPane, BorderLayout.CENTER)
        content = panel
        ApplicationManager.getApplication().messageBus.connect().subscribe(
            LafManagerListener.TOPIC, LafManagerListener {
                updateEditorFieldTheme()
            }
        )
        project.service<KtorClientService>().addListener(this)
    }

    private fun updateEditorFieldTheme() {
        val scheme = EditorColorsManager.getInstance().globalScheme
        editorField.background = scheme.defaultBackground
        editorField.foreground = scheme.defaultForeground
    }

    override fun callback(response: String) {
        SwingUtilities.invokeLater {
            editorField.text = response
        }
    }
}
package com.github.joseserize0222.aiassistantplugin.toolWindow

import com.github.joseserize0222.aiassistantplugin.services.KtorClientService
import com.github.joseserize0222.aiassistantplugin.utils.ChatGptListener
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.*

class ChatGptPanel(project: Project) : ChatGptListener {
    val content: JComponent
    private val textArea: JTextArea

    init {
        val panel = JBPanel<JBPanel<*>>(BorderLayout())

        textArea = JTextArea().apply {
            text = "Please select a function to be described"
            font = Font("JetBrains Mono", Font.PLAIN, 12)
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
            margin = JBUI.insets(10)
        }

        val scrollPane = JBScrollPane(textArea)
        panel.add(scrollPane, BorderLayout.CENTER)

        content = panel

        ApplicationManager.getApplication().messageBus.connect().subscribe(
            LafManagerListener.TOPIC, LafManagerListener {
                updateTextAreaTheme()
            }
        )
        project.service<KtorClientService>().addListener(this)
    }

    private fun updateTextAreaTheme() {
        val scheme = com.intellij.openapi.editor.colors.EditorColorsManager.getInstance().globalScheme
        textArea.background = scheme.defaultBackground
        textArea.foreground = scheme.defaultForeground
    }

    override fun callback(response: String) {
        SwingUtilities.invokeLater {
            textArea.text = response
        }
    }
}

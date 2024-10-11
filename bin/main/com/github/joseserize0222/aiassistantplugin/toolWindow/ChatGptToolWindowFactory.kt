package com.github.joseserize0222.aiassistantplugin.toolWindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


class ChatGptToolWindowFactory: ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val chatGptPanel = ChatGptPanel(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(chatGptPanel.content, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
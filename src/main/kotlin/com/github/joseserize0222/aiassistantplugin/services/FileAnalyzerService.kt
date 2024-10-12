package com.github.joseserize0222.aiassistantplugin.services

import com.github.joseserize0222.aiassistantplugin.utils.FileStatsListener
import com.github.joseserize0222.aiassistantplugin.utils.KotlinFileStats
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction

@Service(Service.Level.PROJECT)
class FileAnalyzerService(private val project: Project) : Disposable {
    private val psiManager: PsiManager = PsiManager.getInstance(project)
    private var listener: FileStatsListener? = null
    private val regexTODO =  Regex("""TODO\s*\("?.*"?\)""")
    init {
        setupListeners()
    }

    fun calculateStats(file: VirtualFile) : KotlinFileStats {
        val ktFile = psiManager.findFile(file) ?:  return KotlinFileStats(0, 0, null, "None")
        val lines = ktFile.text.lines()
        val totalLines = lines.size
        val totalTODOLines = lines.count { it.contains(regexTODO) }
        var longestFunction: KtFunction? = null
        var maxFunctionLines = 0

        val functions = PsiTreeUtil.findChildrenOfType(ktFile, KtNamedFunction::class.java)
        for (function in functions) {
            if (function.text.lines().size > maxFunctionLines) {
                maxFunctionLines = function.text.lines().size
                longestFunction = function
            }
        }
        return KotlinFileStats(totalLines, totalTODOLines, longestFunction, file.name)
    }

    private fun setupListeners() {
        EditorFactory.getInstance().eventMulticaster.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                ApplicationManager.getApplication().invokeLater {
                    PsiDocumentManager.getInstance(project).commitDocument(event.document)
                    updateStats()
                }
            }
        }, this)
        project.messageBus.connect(this).subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object :
            FileEditorManagerListener {
            override fun selectionChanged(event: FileEditorManagerEvent) {
                updateStats()
            }
        })
    }

    fun addListener(listenerClass: FileStatsListener) {
        listener = listenerClass
        updateStats()
    }

    fun updateStats() {
        ApplicationManager.getApplication().runReadAction {
            val virtualFile = FileEditorManager.getInstance(project).selectedFiles.firstOrNull() ?: return@runReadAction
            val allStats = calculateStats(virtualFile)
            listener?.callback(allStats)
        }
    }

    override fun dispose() {}
}
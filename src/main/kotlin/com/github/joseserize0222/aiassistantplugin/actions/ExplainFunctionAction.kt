package com.github.joseserize0222.aiassistantplugin.actions


import com.github.joseserize0222.aiassistantplugin.services.KtorClientService
import com.github.joseserize0222.aiassistantplugin.utils.Token
import com.github.joseserize0222.aiassistantplugin.utils.getToken
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.psi.util.PsiTreeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.kotlin.psi.KtNamedFunction

class ExplainMethodAction : AnAction() {

    private var selectedFunctionCode: String? = null
    private val token: Token = try {
        getToken()
    } catch (e: IllegalStateException) {
        Token("INSERT YOUR API KEY HERE")
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val functionElement = getSelectedFunction(event) ?: return

        selectedFunctionCode = functionElement.text
        val localFunctionCode = selectedFunctionCode
        if (localFunctionCode != null) {
            val service = project.service<KtorClientService>()
            CoroutineScope(Dispatchers.IO).launch {
                service.postFunctionToOpenAi(localFunctionCode, token)
            }
        }
    }

    override fun update(event: AnActionEvent) {
        val presentation = event.presentation
        presentation.isEnabledAndVisible = getSelectedFunction(event) != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private fun getSelectedFunction(event: AnActionEvent): KtNamedFunction? {
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return null
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return null

        return ReadAction.compute<KtNamedFunction?, Throwable> {
            val elementAtCaret = psiFile.findElementAt(editor.caretModel.offset)
            PsiTreeUtil.getParentOfType(elementAtCaret, KtNamedFunction::class.java)
        }
    }
}


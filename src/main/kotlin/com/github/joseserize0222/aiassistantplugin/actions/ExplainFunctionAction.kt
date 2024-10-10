package com.github.joseserize0222.aiassistantplugin.actions


import com.github.joseserize0222.aiassistantplugin.services.KtorClientService
import com.github.joseserize0222.aiassistantplugin.utils.Token
import com.github.joseserize0222.aiassistantplugin.utils.getToken
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
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
    } catch (e: Error) {
        Token("INSERT YOUR API KEY HERE")
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val editor = event.getData(CommonDataKeys.EDITOR)
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)

        if (editor == null || psiFile == null) {
            return
        }

        val elementAtCaret = psiFile.findElementAt(editor.caretModel.offset)
        val functionElement = PsiTreeUtil.getParentOfType(elementAtCaret, KtNamedFunction::class.java)

        if (functionElement != null) {
            selectedFunctionCode = functionElement.text
            val localFunctionCode = selectedFunctionCode

            if (localFunctionCode != null) {
                val service = project.service<KtorClientService>()
                CoroutineScope(Dispatchers.IO).launch {
                    service.postFunctionToOpenAi(localFunctionCode, token)
                }
            }
        }
    }

    override fun update(event: AnActionEvent) {
        val presentation = event.presentation
        presentation.isEnabledAndVisible = false

        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return
        val elementAtCaret = psiFile.findElementAt(editor.caretModel.offset)

        if (PsiTreeUtil.getParentOfType(elementAtCaret, KtNamedFunction::class.java) != null) {
            presentation.isEnabledAndVisible = true
        }
    }
}


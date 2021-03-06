package io.github.kings1990.plugin.fastrequest.view.linemarker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.util.messages.MessageBus;
import icons.PluginIcons;
import io.github.kings1990.plugin.fastrequest.config.Constant;
import io.github.kings1990.plugin.fastrequest.configurable.ConfigChangeNotifier;
import io.github.kings1990.plugin.fastrequest.service.GeneratorUrlService;
import org.jetbrains.annotations.NotNull;

public class FastRequestLineMarkerProvider implements LineMarkerProvider {

    private GeneratorUrlService generatorUrlService = ServiceManager.getService(GeneratorUrlService.class);

    public LineMarkerInfo<PsiElement> getLineMarkerInfo(@NotNull PsiElement element) {
        LineMarkerInfo<PsiElement> lineMarkerInfo;

        if (element instanceof PsiIdentifier && element.getParent() instanceof PsiMethod) {
            if (!judgeMethod(element)) {
                return null;
            }
            PsiMethod methodElement = (PsiMethod)element.getParent();
            lineMarkerInfo = new LineMarkerInfo<>(element, element.getTextRange(), PluginIcons.fastRequest,
                    new FunctionTooltip(methodElement),
                    (e, elt) -> {
                        Project project = elt.getProject();
                        generatorUrlService.generate(methodElement);

                        //send message to change param
                        MessageBus messageBus = project.getMessageBus();
                        messageBus.connect();
                        ConfigChangeNotifier configChangeNotifier = messageBus.syncPublisher(ConfigChangeNotifier.PARAM_CHANGE_TOPIC);
                        configChangeNotifier.configChanged(true);
                    },
                    GutterIconRenderer.Alignment.LEFT);
            lineMarkerInfo.createGutterRenderer();
            return lineMarkerInfo;
        }
        return null;
    }



    private boolean judgeMethod(@NotNull PsiElement psiElement) {
            PsiMethod psiMethod = (PsiMethod) psiElement.getParent();
            Constant.SpringMappingConfig[] mappingConfigArray = Constant.SpringMappingConfig.values();
            PsiAnnotation annotationRequestMapping = null;
            for (Constant.SpringMappingConfig mappingConfig : mappingConfigArray) {
                String code = mappingConfig.getCode();
                annotationRequestMapping = psiMethod.getAnnotation(code);
                if (annotationRequestMapping != null) {
                    break;
                }
            }
            return annotationRequestMapping != null;
    }

}

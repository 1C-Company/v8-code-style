/**
 *
 */
package com.e1c.v8codestyle.bsl.check;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.google.inject.Singleton;

/**
 * @author Dmitriy Marmyshev
 *
 */
@Singleton
public class LocalizableRegistry
{

    private Map<CaseInsensitiveString, Collection<Integer>> staticInvocation;

    private Map<Pair<CaseInsensitiveString, Integer>, Collection<String>> dynamicInvocation;

    private Map<CaseInsensitiveString, Collection<String>> dynamicProperties;

    private volatile boolean initialized;

    public Collection<Integer> getStaticInvocationParameters(String methodName)
    {
        if (methodName == null)
        {
            return Set.of();
        }
        checkInit();

        return staticInvocation.getOrDefault(new CaseInsensitiveString(methodName), Set.of());
    }

    public Collection<String> getDynamicTypesForMethod(String methodName, int index)
    {
        if (methodName == null || index < 0)
        {
            return Set.of();
        }

        checkInit();

        return dynamicInvocation.getOrDefault(Tuples.create(new CaseInsensitiveString(methodName), index), Set.of());
    }

    public Collection<String> getDynamicTypesForProperty(String propertyName)
    {
        if (propertyName == null)
        {
            return Set.of();
        }

        checkInit();

        return dynamicProperties.getOrDefault(new CaseInsensitiveString(propertyName), Set.of());
    }

    private void checkInit()
    {
        if (initialized)
        {
            return;
        }

        init();
    }

    private synchronized void init()
    {
        if (initialized)
        {
            return;
        }

        initStaticInvocation();
        initDynamicInvocation();
        initDynamicProperties();

        initialized = true;
    }

    private void initStaticInvocation()
    {
        // Global context method name and index of localizable string parameter
        Map<CaseInsensitiveString, Collection<Integer>> invocations = new HashMap<>();
        invocations.put(new CaseInsensitiveString("ПоказатьВопрос"), Set.of(1, 5)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("ShowQueryBox"), Set.of(1, 5)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("Вопрос"), Set.of(0, 4)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("DoQueryBox"), Set.of(0, 4)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("Сообщить"), Set.of(0)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("Message"), Set.of(0)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("Состояние"), Set.of(0, 2)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("Status"), Set.of(0, 2)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("ПоказатьОповещениеПользователя"), Set.of(0, 2)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("ShowUserNotification"), Set.of(0, 2)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("ПоказатьПредупреждение"), Set.of(1, 3)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("ShowMessageBox"), Set.of(1, 3)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("Предупреждение"), Set.of(0, 2)); //$NON-NLS-1$
        invocations.put(new CaseInsensitiveString("DoMessageBox"), Set.of(0, 2)); //$NON-NLS-1$

        staticInvocation = Map.copyOf(invocations);
    }

    private void initDynamicInvocation()
    {
        //@formatter:off

        // Object's method name and index of localizable string parameter, and collection of object's type names
        dynamicInvocation = Map.of(
            Tuples.create(new CaseInsensitiveString("Добавить"), 1), Set.of(IEObjectTypeNames.VALUE_LIST), //$NON-NLS-1$
            Tuples.create(new CaseInsensitiveString("Add"), 1), Set.of(IEObjectTypeNames.VALUE_LIST) //$NON-NLS-1$
            );

      //@formatter:on

    }

    private void initDynamicProperties()
    {
        //@formatter:off

        // Types that contains property Title
        Set<String> titleTypes = Set.of(
            IEObjectTypeNames.FORM_FIELD,
            IEObjectTypeNames.FORM_GROUP,
            IEObjectTypeNames.FORM_TABLE,
            IEObjectTypeNames.FORM_DECORATION,
            IEObjectTypeNames.FORM_COMMAND,
            "FormAttribute", //$NON-NLS-1$
            "FormItemAddition", //$NON-NLS-1$
            IEObjectTypeNames.FORM_BUTTON,
            IEObjectTypeNames.CLIENT_APPLICATION_FORM,
            "ConditionalAppearenceItem", //$NON-NLS-1$
            "AppearenceSettingItem", //$NON-NLS-1$
            "CollaborationSystemConversation", //$NON-NLS-1$
            "DeliverableNotification", //$NON-NLS-1$
            "RepresentableDocumentBatch", //$NON-NLS-1$
            "HTMLDocument", //$NON-NLS-1$
            "ValueTableColumn", //$NON-NLS-1$
            "ValueTreeColumn", //$NON-NLS-1$
            "DataCompositionAreaTemplateValueCollectionHeaderCell", //$NON-NLS-1$
            IEObjectTypeNames.DATA_COMPOSITION_USER_FIELD_EXPRESSION,
            IEObjectTypeNames.DATA_COMPOSITION_USER_FIELD_CASE,
            IEObjectTypeNames.DATA_COMPOSITION_SELECTED_FIELD_GROUP,
            IEObjectTypeNames.DATA_COMPOSITION_SELECTED_FIELD,
            IEObjectTypeNames.DATA_COMPOSITION_FILTER_AVAILABLE_FIELD,
            "NestedDataCompositionSchema", //$NON-NLS-1$
            "DataCompositionSchemaParameter", //$NON-NLS-1$
            "DataCompositionSchemaNestedDataSet", //$NON-NLS-1$
            "DataCompositionSchemaDataSetFieldFolder", //$NON-NLS-1$
            "DataCompositionSchemaDataSetField", //$NON-NLS-1$
            "DataCompositionSchemaCalculatedField", //$NON-NLS-1$
            IEObjectTypeNames.DATA_ANALYSIS_PARAMETERS,
            "GanttChartPlotArea", //$NON-NLS-1$
            "FileDialog" //$NON-NLS-1$
            );

        // Types that contains property ToolTip
        // TODO add all types with tooltip
        Set<String> toolTipTypes = Set.of(
            IEObjectTypeNames.FORM_FIELD,
            IEObjectTypeNames.FORM_GROUP,
            IEObjectTypeNames.FORM_TABLE,
            IEObjectTypeNames.FORM_DECORATION,
            IEObjectTypeNames.FORM_COMMAND,
            "FormItemAddition", //$NON-NLS-1$
            "DateAppearence" //$NON-NLS-1$
            );

        // TODO add types of graphical scheme with Description

        // TODO add types of DCS with Presentation

        // Localizable property name, and collection of types
        dynamicProperties = Map.of(
            new CaseInsensitiveString("Подсказка"), toolTipTypes, //$NON-NLS-1$
            new CaseInsensitiveString("ToolTip"), toolTipTypes, //$NON-NLS-1$
            new CaseInsensitiveString("ПодсказкаВвода"), Set.of("FormFieldExtenstionForTextBox"), //$NON-NLS-1$
            new CaseInsensitiveString("InputHint"), Set.of("FormFieldExtenstionForTextBox"), //$NON-NLS-1$
            new CaseInsensitiveString("Заголовок"), titleTypes, //$NON-NLS-1$
            new CaseInsensitiveString("Title"), titleTypes //$NON-NLS-1$
            );
        //@formatter:on

    }

}

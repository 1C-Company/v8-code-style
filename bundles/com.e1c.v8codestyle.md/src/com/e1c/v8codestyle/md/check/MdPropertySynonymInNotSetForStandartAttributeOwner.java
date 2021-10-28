
/**
 * Check for standard  
 * Synonym of the \"Owner\" standard attribute is not specified
 * @author Bombin Valentin
 */
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_DB_OBJECT__STANDARD_ATTRIBUTES;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.Language;

import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.ObjectBelonging;

import com._1c.g5.v8.dt.metadata.mdclass.StandardAttribute;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

import com.google.inject.Inject;

public class MdPropertySynonymInNotSetForStandartAttributeOwner extends BasicCheck {
	//getSynonym StandardAttribute
	private static final String CHECK_ID = "MdPropertySynonymInNotSetForStandartAttributeOwner"; //$NON-NLS-1$
	private static final String owner = "Owner"; //$NON-NLS-1$

	private final IV8ProjectManager v8ProjectManager;

	@Inject
	public MdPropertySynonymInNotSetForStandartAttributeOwner(IV8ProjectManager v8ProjectManager) {
		super();
		this.v8ProjectManager = v8ProjectManager;
	}

	@Override
	public String getCheckId() {

		return CHECK_ID;
	}

	@Override
	protected void configureCheck(CheckConfigurer builder) {
		builder.title(Messages.MdPropertySynonymInNotSetForStandartAttributeOwner_Title)
				.description(Messages.MdPropertySynonymInNotSetForStandartAttributeOwner_Description)
				.complexity(CheckComplexity.NORMAL).severity(IssueSeverity.MINOR).issueType(IssueType.UI_STYLE)
				.topObject(CATALOG).checkTop().features(BASIC_DB_OBJECT__STANDARD_ATTRIBUTES);
	}

	@Override
	protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
			IProgressMonitor monitor) {

		MdObject mdObject = (MdObject) object;
		if (mdObject.getObjectBelonging() != ObjectBelonging.NATIVE) {
			// skip extended object in Extension project
			return;
		}

		if (monitor.isCanceled() || !(object instanceof EObject))
			return;

		// Если нет списка владельцев, то проверять нечего
		if (((Catalog) object).getOwners().isEmpty())
			return;

		EList<StandardAttribute> standardAttributes = ((Catalog) object).getStandardAttributes();
		String message = Messages.MdPropertySynonymInNotSetForStandartAttributeOwner_ErrorMessage;
		EStructuralFeature feature = BASIC_DB_OBJECT__STANDARD_ATTRIBUTES;

		// Список стандартных реквизитов пустой - значит никто ничего не редактировал
		// Следовательно, нет и синонима для владельца
		if (standardAttributes.isEmpty()) {
			resultAceptor.addIssue(message, feature);
			return;
		}

		IV8Project project = v8ProjectManager.getProject(mdObject);
		String language = project.getDefaultLanguage().getLanguageCode();
		if (monitor.isCanceled())
			return;

		boolean hasError = false;
		boolean checkOwner = false;
		// Список не пустой, ищем ревизит владелец и анализируем его синоним
		// Если в списке стандартных реквизитов нет владельца, значит его не редакти ровали, и значит не устанавливали синоним
		// Если в списке стандартных реквизитов есть владелец, то проверим что синоним не пустой.
		for (StandardAttribute standardAttribute : standardAttributes) {
			if (standardAttribute.getName() == owner) {
				checkOwner = true;
				hasError = StringUtils.isBlank(standardAttribute.getSynonym().get(language));
			}
		}

		if (!checkOwner || hasError)
			resultAceptor.addIssue(message, feature);
	}
}

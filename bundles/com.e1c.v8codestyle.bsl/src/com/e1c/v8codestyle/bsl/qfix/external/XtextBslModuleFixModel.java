/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.qfix.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.xtext.validation.Issue;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.services.BslGrammarAccess;
import com._1c.g5.v8.dt.common.PreferenceUtils;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

/**
 * The xtext module quick fix model
 *
 * @author Vadim Geraskin
 */
public class XtextBslModuleFixModel
    implements IXtextBslModuleFixModel
{
    protected final IV8Project v8project;
    private final Supplier<Module> moduleSupp;
    private final BslGrammarAccess bslGrammar;
    private final IResourceLookup resourceLookup;
    private final Issue issue;
    private IDocument document;

    /**
     * Creates module quick fix model
     *
     * @param moduleSupp supplier for {@link Module}, cannot be {@code null}
     * @param v8projectManager V8 project manager, cannot be {@code null}
     * @param bslGrammar BSL grammar, cannot be {@code null}
     * @param resourceLookup resource lookup service, cannot be {@code null}
     * @param issue issue, cannot be {@code null}
     * @param dtProject {@code IDtProject}, cannot be {@code null}
     */
    public XtextBslModuleFixModel(Supplier<Module> moduleSupp, IV8ProjectManager v8projectManager,
        BslGrammarAccess bslGrammar, IResourceLookup resourceLookup, Issue issue, IDtProject dtProject)
    {
        this.moduleSupp = Preconditions.checkNotNull(moduleSupp);
        this.bslGrammar = Preconditions.checkNotNull(bslGrammar);
        this.resourceLookup = Preconditions.checkNotNull(resourceLookup);
        this.issue = Preconditions.checkNotNull(issue);
        this.v8project = v8projectManager.getProject(dtProject);
    }

    @Override
    public IDocument getDocument()
    {
        if (document == null)
        {
            Module module = moduleSupp.get();
            if (module != null)
            {
                URI uri = EcoreUtil.getURI(module).trimFragment();
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(URIConverter.INSTANCE.createInputStream(uri), Charsets.UTF_8)))
                {
                    Stream<String> lines = reader.lines();
                    String content = lines.collect(Collectors.joining(System.lineSeparator()));
                    document = new Document(content);
                }
                catch (IOException e)
                {
                    BslPlugin.log(BslPlugin.createErrorStatus("Unable to read bsl module file", e)); //$NON-NLS-1$
                }
            }
        }
        return document;
    }

    @Override
    public IResourceLookup getResourceLookup()
    {
        return resourceLookup;
    }

    @Override
    public EObject getElement()
    {
        URI uriToProblem = issue.getUriToProblem();
        String fragment = uriToProblem != null ? uriToProblem.fragment() : null;
        if (fragment != null)
        {
            Module module = moduleSupp.get();
            return module != null ? module.eResource().getEObject(fragment) : null;
        }
        return null;
    }

    @Override
    public Issue getIssue()
    {
        return issue;
    }

    @Override
    public String[] getIssueData()
    {
        return issue.getData();
    }

    @Override
    public BslGrammarAccess getBslGrammar()
    {
        return bslGrammar;
    }

    @Override
    public ScriptVariant getScriptVariant()
    {
        return getScriptVariant(v8project);
    }

    @Override
    public String getLineSeparator()
    {
        return PreferenceUtils.getLineSeparator(v8project.getProject());
    }

    protected static ScriptVariant getScriptVariant(IV8Project v8project)
    {
        return v8project != null ? v8project.getScriptVariant()
            : (Locale.getDefault().getLanguage().equals(new Locale("ru").getLanguage())) ? ScriptVariant.RUSSIAN //$NON-NLS-1$
                : ScriptVariant.ENGLISH;
    }
}

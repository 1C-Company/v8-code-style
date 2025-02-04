/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.internal.bsl.ui.views;

import java.text.MessageFormat;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;

import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ParametersSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.ReturnSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Section;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TagPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.FieldDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.LinkContainsTypeDefinition;
import com._1c.g5.v8.dt.bsl.documentation.comment.TypeSection.TypeDefinition;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com.e1c.v8codestyle.internal.bsl.ui.SharedImages;
import com.e1c.v8codestyle.internal.bsl.ui.UiPlugin;

/**
 * The View to show {@link BslDocumentationComment} model that selected in bsl-module editor.
 *
 * @author Dmitriy Marmyshev
 */
public class BslDocCommentView
    extends ViewPart
{

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "com.e1c.v8codestyle.bsl.ui.views.BslDocCommentView"; //$NON-NLS-1$

    private TreeViewer viewer;

    private ISelectionListener listener;

    @Override
    public void createPartControl(Composite parent)
    {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getControl());

        viewer.setContentProvider(new BslDocumentationCommentContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
        getSite().setSelectionProvider(viewer);
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();

        IResourceLookup resourceLookup = UiPlugin.getDefault().getInjector().getInstance(IResourceLookup.class);
        IBslPreferences bslPreferences = UiPlugin.getDefault().getInjector().getInstance(IBslPreferences.class);
        BslMultiLineCommentDocumentationProvider commentProvider =
            UiPlugin.getDefault().getInjector().getInstance(BslMultiLineCommentDocumentationProvider.class);
        EObjectAtOffsetHelper bslObjectAtOffsetHelper =
            UiPlugin.getDefault().getInjector().getInstance(EObjectAtOffsetHelper.class);

        listener = new BslDocCommentSelectionListener(viewer, resourceLookup, bslPreferences, bslObjectAtOffsetHelper,
            commentProvider);

        getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(listener);
    }

    @Override
    public void dispose()
    {
        getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(listener);
        super.dispose();
    }

    private void hookContextMenu()
    {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(this::fillContextMenu);

        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars()
    {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager)
    {
        manager.add(new Separator());
    }

    private void fillContextMenu(IMenuManager manager)
    {
        manager.add(new Separator());
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager)
    {
        manager.add(new Separator());
    }

    private void makeActions()
    {
        // TODO add actions
    }

    private void hookDoubleClickAction()
    {
        viewer.addDoubleClickListener(new IDoubleClickListener()
        {
            @Override
            public void doubleClick(DoubleClickEvent event)
            {
                // TODO add action
            }
        });
    }

    @Override
    public void setFocus()
    {
        viewer.getControl().setFocus();
    }

    class ViewLabelProvider
        extends LabelProvider
    {

        @Override
        public String getText(Object obj)
        {
            if (obj instanceof Description)
            {
                return Messages.BslDocCommentView_Description;
            }
            else if (obj instanceof TextPart)
            {
                return MessageFormat.format(Messages.BslDocCommentView_Text, ((TextPart)obj).getText());
            }
            else if (obj instanceof LinkPart)
            {
                return MessageFormat.format(Messages.BslDocCommentView_Link, ((LinkPart)obj).getLinkText());
            }
            else if (obj instanceof TagPart tagPart)
            {
                return MessageFormat.format(Messages.BslDocCommentView_Tag, tagPart.getContent());
            }
            else if (obj instanceof ParametersSection)
            {
                return Messages.BslDocCommentView_Parameters;
            }
            else if (obj instanceof TypeSection)
            {
                return Messages.BslDocCommentView_Types;
            }
            else if (obj instanceof FieldDefinition)
            {
                return MessageFormat.format(Messages.BslDocCommentView_Field, ((FieldDefinition)obj).getName());
            }
            else if (obj instanceof ReturnSection)
            {
                return Messages.BslDocCommentView_Returns;
            }
            else if (obj instanceof Section)
            {
                return Messages.BslDocCommentView_Section;
            }
            else if (obj instanceof LinkContainsTypeDefinition)
            {
                return MessageFormat.format(Messages.BslDocCommentView_Link_type,
                    ((LinkContainsTypeDefinition)obj).getTypeName());
            }
            else if (obj instanceof TypeDefinition)
            {
                return MessageFormat.format(Messages.BslDocCommentView_Type, ((TypeDefinition)obj).getTypeName());
            }
            return obj.toString();
        }

        @Override
        public Image getImage(Object obj)
        {
            String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
            if (obj instanceof LinkPart)
            {
                return UiPlugin.getDefault().getImage(SharedImages.IMG_OBJ16_LINK);
            }
            else if (obj instanceof TextPart)
            {
                return UiPlugin.getDefault().getImage(SharedImages.IMG_OBJ16_TEXT);
            }
            else if (obj instanceof FieldDefinition)
            {
                return UiPlugin.getDefault().getImage(SharedImages.IMG_OBJ16_FIELD);
            }
            else if (obj instanceof TypeDefinition)
            {
                return UiPlugin.getDefault().getImage(SharedImages.IMG_OBJ16_TYPE);
            }
            else if (obj instanceof Description)
            {
                return UiPlugin.getDefault().getImage(SharedImages.IMG_OBJ16_DESCRIPTION);
            }
            else if (obj instanceof TypeSection)
            {
                return UiPlugin.getDefault().getImage(SharedImages.IMG_OBJ16_TYPE_SECTION);
            }
            else if (obj instanceof ParametersSection)
            {
                return UiPlugin.getDefault().getImage(SharedImages.IMG_OBJ16_PARAMETERS_SECTION);
            }
            else if (obj instanceof ReturnSection || obj instanceof Section)
            {
                imageKey = ISharedImages.IMG_OBJ_FOLDER;
            }
            return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
        }
    }

}

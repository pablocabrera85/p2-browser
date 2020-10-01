/*******************************************************************************
 * Copyright (c) 2011 Igor Fedorenko
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Igor Fedorenko - initial API and implementation
 *******************************************************************************/

package com.ifedorenko.p2browser.dialogs;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RepositoryLocationDialog extends TrayDialog {
    private static final String RELEASE_URL = "http://download.eclipse.org/releases/%s";
    private static final String[] RELEASED_VERSIONS = new String[] { "2020-09", "2020-06", "2020-03", "2019-12",
            "2019-09" };
    private static final String UPDATE_URL = "http://download.eclipse.org/eclipse/updates/4.%s";

    private URI location;

    private Text message;

    private Combo combo;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public RepositoryLocationDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        setHelpAvailable(false);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(2, false));

        Label lblLocation = new Label(container, SWT.NONE);
        lblLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblLocation.setText("Location");

        combo = new Combo(container, SWT.NONE);
        combo.addModifyListener(event -> {
            try {
                location = new URI(combo.getText());
                message.setText("");
                message.setVisible(false);
                getButton(IDialogConstants.OK_ID).setEnabled(true);
            } catch (URISyntaxException ex) {
                message.setText(ex.getMessage());
                message.setVisible(true);
                getButton(IDialogConstants.OK_ID).setEnabled(false);
            }
        });
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        message = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        message.setEditable(false);
        message.setVisible(false);
        message.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        return container;
    }

    @Override
    public void create() {
        super.create();
        for (String release : RELEASED_VERSIONS) {
            combo.add(String.format(RELEASE_URL, release));
        }

        for (int version = 17; version >= 13; version--) {
            combo.add(String.format(UPDATE_URL, String.valueOf(version)));
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        button.setEnabled(false);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Add repository");
    }

    @Override
    protected Point getInitialSize() {
        return new Point(450, 200);
    }

    public URI getLocation() {
        return location;
    }
}

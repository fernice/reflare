/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.ui;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import org.fernice.reflare.element.ComponentElement;
import org.fernice.reflare.element.FileChooserElement;
import org.fernice.reflare.element.StyleTreeElementLookup;
import org.fernice.reflare.render.icon.IconHelper;
import org.jetbrains.annotations.NotNull;

public abstract class FlareAbstractFileChooserUI extends BasicFileChooserUI implements FlareUI {

    private JButton approveButton;
    private JButton cancelButton;
    private Action fileNameCompletionAction = new FileNameCompletionAction();
    private FileFilter actualFileFilter = null;
    private GlobFilter globFilter = null;
    private String fileNameCompletionString;

    public static ComponentUI createUI(JComponent var0) {
        return new FlareFileChooserUI((JFileChooser) var0);
    }

    public FlareAbstractFileChooserUI(JFileChooser var1) {
        super(var1);
    }

    private int getComponentState(JComponent var1) {
        if (var1.isEnabled()) {
            return var1.isFocusOwner() ? 257 : 1;
        } else {
            return 8;
        }
    }

    private ComponentElement element;

    public void installUI(JComponent var1) {
        super.installUI(var1);
        SwingUtilities.replaceUIActionMap(var1, this.createActionMap());

        JFileChooser fileChooser = getFileChooser();

        if (element == null) {
            element = new FileChooserElement(fileChooser);
        }

        UIDefaultsHelper.installDefaultProperties(this, fileChooser);

        StyleTreeElementLookup.registerElement(fileChooser, this);
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        StyleTreeElementLookup.deregisterElement(c);
    }

    public void installComponents(JFileChooser var1) {
        this.cancelButton = new JButton(this.cancelButtonText);
        this.cancelButton.setName("SynthFileChooser.cancelButton");
        this.cancelButton.setMnemonic(this.cancelButtonMnemonic);
        this.cancelButton.setToolTipText(this.cancelButtonToolTipText);
        this.cancelButton.addActionListener(this.getCancelSelectionAction());
        this.approveButton = new JButton(this.getApproveButtonText(var1));
        this.approveButton.setName("SynthFileChooser.approveButton");
        this.approveButton.setMnemonic(this.getApproveButtonMnemonic(var1));
        this.approveButton.setToolTipText(this.getApproveButtonToolTipText(var1));
        this.approveButton.addActionListener(this.getApproveSelectionAction());
    }

    public void uninstallComponents(JFileChooser var1) {
        var1.removeAll();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        paintBackground(c, g);
    }

    private void paintBackground(JComponent component, Graphics g) {
        element.paintBackground(component, g);
    }

    @Override
    public void paintBorder(@NotNull final Component c, @NotNull final Graphics g, final int x, final int y, final int width, final int height) {
        element.paintBorder(c, g);
    }

    @NotNull
    @Override
    public ComponentElement getElement() {
        return element;
    }

    protected void installListeners(JFileChooser var1) {
        super.installListeners(var1);
        this.getModel().addListDataListener(new ListDataListener() {
            public void contentsChanged(ListDataEvent var1) {
                new DelayedSelectionUpdater();
            }

            public void intervalAdded(ListDataEvent var1) {
                new DelayedSelectionUpdater();
            }

            public void intervalRemoved(ListDataEvent var1) {
            }
        });
    }

    protected abstract ActionMap createActionMap();

    protected void installDefaults(JFileChooser var1) {
        super.installDefaults(var1);
    }

    protected void uninstallDefaults(JFileChooser var1) {
        super.uninstallDefaults(var1);
    }

    protected void installIcons(JFileChooser var1) {
        fileIcon = IconHelper.getIcon("/reflare/icons/file.png");
        directoryIcon = IconHelper.getIcon("/reflare/icons/folder.png");
    }

    public abstract void setFileName(String var1);

    public abstract String getFileName();

    protected void doSelectedFileChanged(PropertyChangeEvent var1) {
    }

    protected void doSelectedFilesChanged(PropertyChangeEvent var1) {
    }

    protected void doDirectoryChanged(PropertyChangeEvent var1) {
    }

    protected void doAccessoryChanged(PropertyChangeEvent var1) {
    }

    protected void doFileSelectionModeChanged(PropertyChangeEvent var1) {
    }

    protected void doMultiSelectionChanged(PropertyChangeEvent var1) {
        if (!this.getFileChooser().isMultiSelectionEnabled()) {
            this.getFileChooser().setSelectedFiles((File[]) null);
        }

    }

    protected void doControlButtonsChanged(PropertyChangeEvent var1) {
        if (this.getFileChooser().getControlButtonsAreShown()) {
            this.approveButton.setText(this.getApproveButtonText(this.getFileChooser()));
            this.approveButton.setToolTipText(this.getApproveButtonToolTipText(this.getFileChooser()));
            this.approveButton.setMnemonic(this.getApproveButtonMnemonic(this.getFileChooser()));
        }

    }

    protected void doAncestorChanged(PropertyChangeEvent var1) {
    }

    public PropertyChangeListener createPropertyChangeListener(JFileChooser var1) {
        return new SynthFCPropertyChangeListener();
    }

    private void updateFileNameCompletion() {
        if (this.fileNameCompletionString != null && this.fileNameCompletionString.equals(this.getFileName())) {
            File[] var1 = (File[]) this.getModel().getFiles().toArray(new File[0]);
            String var2 = this.getCommonStartString(var1);
            if (var2 != null && var2.startsWith(this.fileNameCompletionString)) {
                this.setFileName(var2);
            }

            this.fileNameCompletionString = null;
        }

    }

    private String getCommonStartString(File[] var1) {
        String var2 = null;
        String var3 = null;
        int var4 = 0;
        if (var1.length == 0) {
            return null;
        } else {
            while (true) {
                for (int var5 = 0; var5 < var1.length; ++var5) {
                    String var6 = var1[var5].getName();
                    if (var5 == 0) {
                        if (var6.length() == var4) {
                            return var2;
                        }

                        var3 = var6.substring(0, var4 + 1);
                    }

                    if (!var6.startsWith(var3)) {
                        return var2;
                    }
                }

                var2 = var3;
                ++var4;
            }
        }
    }

    private void resetGlobFilter() {
        if (this.actualFileFilter != null) {
            JFileChooser var1 = this.getFileChooser();
            FileFilter var2 = var1.getFileFilter();
            if (var2 != null && var2.equals(this.globFilter)) {
                var1.setFileFilter(this.actualFileFilter);
                var1.removeChoosableFileFilter(this.globFilter);
            }

            this.actualFileFilter = null;
        }

    }

    private static boolean isGlobPattern(String var0) {
        return File.separatorChar == '\\' && var0.indexOf(42) >= 0 ||
                File.separatorChar == '/' && (var0.indexOf(42) >= 0 || var0.indexOf(63) >= 0 || var0.indexOf(91) >= 0);
    }

    public Action getFileNameCompletionAction() {
        return this.fileNameCompletionAction;
    }

    protected JButton getApproveButton(JFileChooser var1) {
        return this.approveButton;
    }

    protected JButton getCancelButton(JFileChooser var1) {
        return this.cancelButton;
    }

    public void clearIconCache() {
    }

    class GlobFilter extends FileFilter {

        Pattern pattern;
        String globPattern;

        GlobFilter() {
        }

        public void setPattern(String var1) {
            char[] var2 = var1.toCharArray();
            char[] var3 = new char[var2.length * 2];
            boolean var4 = File.separatorChar == '\\';
            boolean var5 = false;
            int var6 = 0;
            this.globPattern = var1;
            int var7;
            if (var4) {
                var7 = var2.length;
                if (var1.endsWith("*.*")) {
                    var7 -= 2;
                }

                for (int var8 = 0; var8 < var7; ++var8) {
                    if (var2[var8] == '*') {
                        var3[var6++] = '.';
                    }

                    var3[var6++] = var2[var8];
                }
            } else {
                for (var7 = 0; var7 < var2.length; ++var7) {
                    int var10001;
                    switch (var2[var7]) {
                        case '*':
                            if (!var5) {
                                var3[var6++] = '.';
                            }

                            var3[var6++] = '*';
                            break;
                        case '?':
                            var3[var6++] = (char) (var5 ? 63 : 46);
                            break;
                        case '[':
                            var5 = true;
                            var3[var6++] = var2[var7];
                            if (var7 < var2.length - 1) {
                                switch (var2[var7 + 1]) {
                                    case '!':
                                    case '^':
                                        var3[var6++] = '^';
                                        ++var7;
                                        break;
                                    case ']':
                                        var10001 = var6++;
                                        ++var7;
                                        var3[var10001] = var2[var7];
                                }
                            }
                            break;
                        case '\\':
                            if (var7 == 0 && var2.length > 1 && var2[1] == '~') {
                                var10001 = var6++;
                                ++var7;
                                var3[var10001] = var2[var7];
                            } else {
                                var3[var6++] = '\\';
                                if (var7 < var2.length - 1 && "*?[]".indexOf(var2[var7 + 1]) >= 0) {
                                    var10001 = var6++;
                                    ++var7;
                                    var3[var10001] = var2[var7];
                                    continue;
                                }

                                var3[var6++] = '\\';
                            }
                            break;
                        case ']':
                            var3[var6++] = var2[var7];
                            var5 = false;
                            break;
                        default:
                            if (!Character.isLetterOrDigit(var2[var7])) {
                                var3[var6++] = '\\';
                            }

                            var3[var6++] = var2[var7];
                    }
                }
            }

            this.pattern = Pattern.compile(new String(var3, 0, var6), 2);
        }

        public boolean accept(File var1) {
            if (var1 == null) {
                return false;
            } else {
                return var1.isDirectory() ? true : this.pattern.matcher(var1.getName()).matches();
            }
        }

        public String getDescription() {
            return this.globPattern;
        }
    }

    private class FileNameCompletionAction extends AbstractAction {

        protected FileNameCompletionAction() {
            super("fileNameCompletion");
        }

        public void actionPerformed(ActionEvent var1) {
            JFileChooser var2 = FlareAbstractFileChooserUI.this.getFileChooser();
            String var3 = FlareAbstractFileChooserUI.this.getFileName();
            if (var3 != null) {
                var3 = var3.trim();
            }

            FlareAbstractFileChooserUI.this.resetGlobFilter();
            if (var3 != null && !var3.equals("") && (!var2.isMultiSelectionEnabled() || !var3.startsWith("\""))) {
                FileFilter var4 = var2.getFileFilter();
                if (FlareAbstractFileChooserUI.this.globFilter == null) {
                    FlareAbstractFileChooserUI.this.globFilter = new GlobFilter();
                }

                try {
                    FlareAbstractFileChooserUI.this.globFilter.setPattern(!FlareAbstractFileChooserUI.isGlobPattern(var3) ? var3 + "*" : var3);
                    if (!(var4 instanceof FlareAbstractFileChooserUI.GlobFilter)) {
                        FlareAbstractFileChooserUI.this.actualFileFilter = var4;
                    }

                    var2.setFileFilter((FileFilter) null);
                    var2.setFileFilter(FlareAbstractFileChooserUI.this.globFilter);
                    FlareAbstractFileChooserUI.this.fileNameCompletionString = var3;
                } catch (PatternSyntaxException var6) {
                }

            }
        }
    }

    private class SynthFCPropertyChangeListener implements PropertyChangeListener {

        private SynthFCPropertyChangeListener() {
        }

        public void propertyChange(PropertyChangeEvent var1) {
            String var2 = var1.getPropertyName();
            if (var2.equals("fileSelectionChanged")) {
                FlareAbstractFileChooserUI.this.doFileSelectionModeChanged(var1);
            } else if (var2.equals("SelectedFileChangedProperty")) {
                FlareAbstractFileChooserUI.this.doSelectedFileChanged(var1);
            } else if (var2.equals("SelectedFilesChangedProperty")) {
                FlareAbstractFileChooserUI.this.doSelectedFilesChanged(var1);
            } else if (var2.equals("directoryChanged")) {
                FlareAbstractFileChooserUI.this.doDirectoryChanged(var1);
            } else if (var2 == "MultiSelectionEnabledChangedProperty") {
                FlareAbstractFileChooserUI.this.doMultiSelectionChanged(var1);
            } else if (var2 == "AccessoryChangedProperty") {
                FlareAbstractFileChooserUI.this.doAccessoryChanged(var1);
            } else if (var2 != "ApproveButtonTextChangedProperty" && var2 != "ApproveButtonToolTipTextChangedProperty" && var2 != "DialogTypeChangedProperty" &&
                    var2 != "ControlButtonsAreShownChangedProperty") {
                if (var2.equals("componentOrientation")) {
                    ComponentOrientation var3 = (ComponentOrientation) var1.getNewValue();
                    JFileChooser var4 = (JFileChooser) var1.getSource();
                    if (var3 != (ComponentOrientation) var1.getOldValue()) {
                        var4.applyComponentOrientation(var3);
                    }
                } else if (var2.equals("ancestor")) {
                    FlareAbstractFileChooserUI.this.doAncestorChanged(var1);
                }
            } else {
                FlareAbstractFileChooserUI.this.doControlButtonsChanged(var1);
            }

        }
    }

    private class DelayedSelectionUpdater implements Runnable {

        DelayedSelectionUpdater() {
            SwingUtilities.invokeLater(this);
        }

        public void run() {
            FlareAbstractFileChooserUI.this.updateFileNameCompletion();
        }
    }
}


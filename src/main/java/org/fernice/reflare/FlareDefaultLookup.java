package org.fernice.reflare;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.text.DefaultEditorKit;
import org.fernice.reflare.internal.DefaultLookup;
import org.fernice.reflare.internal.DefaultLookupDelegate;
import org.fernice.reflare.ui.FlareUI;

public class FlareDefaultLookup implements DefaultLookup {

    @Override
    public Object getDefault(JComponent component, ComponentUI ui, String key, DefaultLookupDelegate delegate) {
        if (ui instanceof FlareUI) {
            final String partialKey = key.substring(key.indexOf(".") + 1);

            Object value = UIManager.get(key);

            if (value == null && partialKey.equals("focusInputMap")) {
                value = getDefault(key);
            }

            return value;
        }

        return delegate.getDefault(component, ui, key);
    }

    private static Map<Object, Object> DEFAULTS;

    private static Object getDefault(final Object key) {
        if (DEFAULTS == null) {
            synchronized (FlareDefaultLookup.class) {
                if (DEFAULTS == null) {
                    DEFAULTS = new HashMap<>();
                    fillDefaults();
                }
            }
        }

        Object value = DEFAULTS.get(key);
        if (value instanceof UIDefaults.LazyValue) {
            value = ((UIDefaults.LazyValue) value).createValue(null);
            DEFAULTS.put(key, value);
        }
        return value;
    }

    private static void fillDefaults() {
        // TODO we can use this to insert platform specific keystrokes like the copy action on mac
        // This is a direct copy of the actions used by any SynthLookAndFeel.
        // As we do not base onto Synth we have to provide our own actions and
        // the only convenient way to this, is the way Synth does it.

        // @formatter:off
        DEFAULTS.put("Table.dropCellForeground", Defaults.COLOR_TRANSPARENT);
        DEFAULTS.put("Table.dropCellBackground", Defaults.COLOR_TRANSPARENT);
        DEFAULTS.put("Table.alternateRowColor", Defaults.COLOR_TRANSPARENT);
        DEFAULTS.put("Table.focusSelectedCellHighlightBorder", null);
        DEFAULTS.put("Table.focusCellHighlightBorder", null);
        DEFAULTS.put("Table.focusCellForeground", Defaults.COLOR_TRANSPARENT);
        DEFAULTS.put("Table.focusCellBackground", Defaults.COLOR_TRANSPARENT);

        Object buttonMap = new UIDefaults.LazyInputMap(new Object[] {
                "SPACE", "pressed",
                "released SPACE", "released"
        });
        DEFAULTS.put("Button.focusInputMap", buttonMap);
        DEFAULTS.put("CheckBox.focusInputMap", buttonMap);
        DEFAULTS.put("RadioButton.focusInputMap", buttonMap);
        DEFAULTS.put("ToggleButton.focusInputMap", buttonMap);
        DEFAULTS.put("SynthArrowButton.focusInputMap", buttonMap);
        DEFAULTS.put("List.dropLineColor", Color.BLACK);
        DEFAULTS.put("Tree.dropLineColor", Color.BLACK);
        DEFAULTS.put("Table.dropLineColor", Color.BLACK);
        DEFAULTS.put("Table.dropLineShortColor", Color.RED);

        Object multilineInputMap = new UIDefaults.LazyInputMap(new Object[] {
                "ctrl C", DefaultEditorKit.copyAction,
                "ctrl V", DefaultEditorKit.pasteAction,
                "ctrl X", DefaultEditorKit.cutAction,
                "COPY", DefaultEditorKit.copyAction,
                "PASTE", DefaultEditorKit.pasteAction,
                "CUT", DefaultEditorKit.cutAction,
                "control INSERT", DefaultEditorKit.copyAction,
                "shift INSERT", DefaultEditorKit.pasteAction,
                "shift DELETE", DefaultEditorKit.cutAction,
                "shift LEFT", DefaultEditorKit.selectionBackwardAction,
                "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
                "shift RIGHT", DefaultEditorKit.selectionForwardAction,
                "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
                "ctrl LEFT", DefaultEditorKit.previousWordAction,
                "ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
                "ctrl RIGHT", DefaultEditorKit.nextWordAction,
                "ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,
                "ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
                "ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
                "ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
                "ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
                "ctrl A", DefaultEditorKit.selectAllAction,
                "HOME", DefaultEditorKit.beginLineAction,
                "END", DefaultEditorKit.endLineAction,
                "shift HOME", DefaultEditorKit.selectionBeginLineAction,
                "shift END", DefaultEditorKit.selectionEndLineAction,

                "UP", DefaultEditorKit.upAction,
                "KP_UP", DefaultEditorKit.upAction,
                "DOWN", DefaultEditorKit.downAction,
                "KP_DOWN", DefaultEditorKit.downAction,
                "PAGE_UP", DefaultEditorKit.pageUpAction,
                "PAGE_DOWN", DefaultEditorKit.pageDownAction,
                "shift PAGE_UP", "selection-page-up",
                "shift PAGE_DOWN", "selection-page-down",
                "ctrl shift PAGE_UP", "selection-page-left",
                "ctrl shift PAGE_DOWN", "selection-page-right",
                "shift UP", DefaultEditorKit.selectionUpAction,
                "shift KP_UP", DefaultEditorKit.selectionUpAction,
                "shift DOWN", DefaultEditorKit.selectionDownAction,
                "shift KP_DOWN", DefaultEditorKit.selectionDownAction,
                "ENTER", DefaultEditorKit.insertBreakAction,
                "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
                "shift BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
                "ctrl H", DefaultEditorKit.deletePrevCharAction,
                "DELETE", DefaultEditorKit.deleteNextCharAction,
                "ctrl DELETE", DefaultEditorKit.deleteNextWordAction,
                "ctrl BACK_SPACE", DefaultEditorKit.deletePrevWordAction,
                "RIGHT", DefaultEditorKit.forwardAction,
                "LEFT", DefaultEditorKit.backwardAction,
                "KP_RIGHT", DefaultEditorKit.forwardAction,
                "KP_LEFT", DefaultEditorKit.backwardAction,
                "TAB", DefaultEditorKit.insertTabAction,
                "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
                "ctrl HOME", DefaultEditorKit.beginAction,
                "ctrl END", DefaultEditorKit.endAction,
                "ctrl shift HOME", DefaultEditorKit.selectionBeginAction,
                "ctrl shift END", DefaultEditorKit.selectionEndAction,
                "ctrl T", "next-link-action",
                "ctrl shift T", "previous-link-action",
                "ctrl SPACE", "activate-link-action",
                "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
        });
        DEFAULTS.put("EditorPane.focusInputMap", multilineInputMap);
        DEFAULTS.put("TextArea.focusInputMap", multilineInputMap);
        DEFAULTS.put("TextPane.focusInputMap", multilineInputMap);

        Object fieldInputMap = new UIDefaults.LazyInputMap(new Object[] {
                "ctrl C", DefaultEditorKit.copyAction,
                "ctrl V", DefaultEditorKit.pasteAction,
                "ctrl X", DefaultEditorKit.cutAction,
                "COPY", DefaultEditorKit.copyAction,
                "PASTE", DefaultEditorKit.pasteAction,
                "CUT", DefaultEditorKit.cutAction,
                "control INSERT", DefaultEditorKit.copyAction,
                "shift INSERT", DefaultEditorKit.pasteAction,
                "shift DELETE", DefaultEditorKit.cutAction,
                "shift LEFT", DefaultEditorKit.selectionBackwardAction,
                "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
                "shift RIGHT", DefaultEditorKit.selectionForwardAction,
                "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
                "ctrl LEFT", DefaultEditorKit.previousWordAction,
                "ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
                "ctrl RIGHT", DefaultEditorKit.nextWordAction,
                "ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,
                "ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
                "ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
                "ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
                "ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
                "ctrl A", DefaultEditorKit.selectAllAction,
                "HOME", DefaultEditorKit.beginLineAction,
                "END", DefaultEditorKit.endLineAction,
                "shift HOME", DefaultEditorKit.selectionBeginLineAction,
                "shift END", DefaultEditorKit.selectionEndLineAction,
                "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
                "shift BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
                "ctrl H", DefaultEditorKit.deletePrevCharAction,
                "DELETE", DefaultEditorKit.deleteNextCharAction,
                "ctrl DELETE", DefaultEditorKit.deleteNextWordAction,
                "ctrl BACK_SPACE", DefaultEditorKit.deletePrevWordAction,
                "RIGHT", DefaultEditorKit.forwardAction,
                "LEFT", DefaultEditorKit.backwardAction,
                "KP_RIGHT", DefaultEditorKit.forwardAction,
                "KP_LEFT", DefaultEditorKit.backwardAction,
                "ENTER", JTextField.notifyAction,
                "ctrl BACK_SLASH", "unselect"/*DefaultEditorKit.unselectAction*/,
                "control shift O", "toggle-componentOrientation"/*DefaultEditorKit.toggleComponentOrientation*/
        });
        DEFAULTS.put("TextField.focusInputMap", fieldInputMap);
        DEFAULTS.put("PasswordField.focusInputMap", fieldInputMap);


        DEFAULTS.put("ComboBox.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "ESCAPE", "hidePopup",
                        "PAGE_UP", "pageUpPassThrough",
                        "PAGE_DOWN", "pageDownPassThrough",
                        "HOME", "homePassThrough",
                        "END", "endPassThrough",
                        "DOWN", "selectNext",
                        "KP_DOWN", "selectNext",
                        "alt DOWN", "togglePopup",
                        "alt KP_DOWN", "togglePopup",
                        "alt UP", "togglePopup",
                        "alt KP_UP", "togglePopup",
                        "SPACE", "spacePopup",
                        "ENTER", "enterPressed",
                        "UP", "selectPrevious",
                        "KP_UP", "selectPrevious"
                }));

        DEFAULTS.put("Desktop.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "ctrl F5", "restore",
                        "ctrl F4", "close",
                        "ctrl F7", "move",
                        "ctrl F8", "resize",
                        "RIGHT", "right",
                        "KP_RIGHT", "right",
                        "shift RIGHT", "shrinkRight",
                        "shift KP_RIGHT", "shrinkRight",
                        "LEFT", "left",
                        "KP_LEFT", "left",
                        "shift LEFT", "shrinkLeft",
                        "shift KP_LEFT", "shrinkLeft",
                        "UP", "up",
                        "KP_UP", "up",
                        "shift UP", "shrinkUp",
                        "shift KP_UP", "shrinkUp",
                        "DOWN", "down",
                        "KP_DOWN", "down",
                        "shift DOWN", "shrinkDown",
                        "shift KP_DOWN", "shrinkDown",
                        "ESCAPE", "escape",
                        "ctrl F9", "minimize",
                        "ctrl F10", "maximize",
                        "ctrl F6", "selectNextFrame",
                        "ctrl TAB", "selectNextFrame",
                        "ctrl alt F6", "selectNextFrame",
                        "shift ctrl alt F6", "selectPreviousFrame",
                        "ctrl F12", "navigateNext",
                        "shift ctrl F12", "navigatePrevious"
                }));

        DEFAULTS.put("FileChooser.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "ESCAPE", "cancelSelection",
                        "F2", "editFileName",
                        "F5", "refresh",
                        "BACK_SPACE", "Go Up",
                        "ENTER", "approveSelection",
                        "ctrl ENTER", "approveSelection"
                }));

        DEFAULTS.put("FormattedTextField.focusInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "ctrl C", DefaultEditorKit.copyAction,
                        "ctrl V", DefaultEditorKit.pasteAction,
                        "ctrl X", DefaultEditorKit.cutAction,
                        "COPY", DefaultEditorKit.copyAction,
                        "PASTE", DefaultEditorKit.pasteAction,
                        "CUT", DefaultEditorKit.cutAction,
                        "control INSERT", DefaultEditorKit.copyAction,
                        "shift INSERT", DefaultEditorKit.pasteAction,
                        "shift DELETE", DefaultEditorKit.cutAction,
                        "shift LEFT", DefaultEditorKit.selectionBackwardAction,
                        "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
                        "shift RIGHT", DefaultEditorKit.selectionForwardAction,
                        "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
                        "ctrl LEFT", DefaultEditorKit.previousWordAction,
                        "ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
                        "ctrl RIGHT", DefaultEditorKit.nextWordAction,
                        "ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,
                        "ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
                        "ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
                        "ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
                        "ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
                        "ctrl A", DefaultEditorKit.selectAllAction,
                        "HOME", DefaultEditorKit.beginLineAction,
                        "END", DefaultEditorKit.endLineAction,
                        "shift HOME", DefaultEditorKit.selectionBeginLineAction,
                        "shift END", DefaultEditorKit.selectionEndLineAction,
                        "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
                        "shift BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
                        "ctrl H", DefaultEditorKit.deletePrevCharAction,
                        "DELETE", DefaultEditorKit.deleteNextCharAction,
                        "ctrl DELETE", DefaultEditorKit.deleteNextWordAction,
                        "ctrl BACK_SPACE", DefaultEditorKit.deletePrevWordAction,
                        "RIGHT", DefaultEditorKit.forwardAction,
                        "LEFT", DefaultEditorKit.backwardAction,
                        "KP_RIGHT", DefaultEditorKit.forwardAction,
                        "KP_LEFT", DefaultEditorKit.backwardAction,
                        "ENTER", JTextField.notifyAction,
                        "ctrl BACK_SLASH", "unselect",
                        "control shift O", "toggle-componentOrientation",
                        "ESCAPE", "reset-field-edit",
                        "UP", "increment",
                        "KP_UP", "increment",
                        "DOWN", "decrement",
                        "KP_DOWN", "decrement",
                }));

        DEFAULTS.put("InternalFrame.icon",
                LookAndFeel.makeIcon(BasicLookAndFeel.class,
                        "icons/JavaCup16.png"));

        DEFAULTS.put("InternalFrame.windowBindings",
                new Object[] {
                        "shift ESCAPE", "showSystemMenu",
                        "ctrl SPACE", "showSystemMenu",
                        "ESCAPE", "hideSystemMenu"});

        DEFAULTS.put("List.focusInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "ctrl C", "copy",
                        "ctrl V", "paste",
                        "ctrl X", "cut",
                        "COPY", "copy",
                        "PASTE", "paste",
                        "CUT", "cut",
                        "control INSERT", "copy",
                        "shift INSERT", "paste",
                        "shift DELETE", "cut",
                        "UP", "selectPreviousRow",
                        "KP_UP", "selectPreviousRow",
                        "shift UP", "selectPreviousRowExtendSelection",
                        "shift KP_UP", "selectPreviousRowExtendSelection",
                        "ctrl shift UP", "selectPreviousRowExtendSelection",
                        "ctrl shift KP_UP", "selectPreviousRowExtendSelection",
                        "ctrl UP", "selectPreviousRowChangeLead",
                        "ctrl KP_UP", "selectPreviousRowChangeLead",
                        "DOWN", "selectNextRow",
                        "KP_DOWN", "selectNextRow",
                        "shift DOWN", "selectNextRowExtendSelection",
                        "shift KP_DOWN", "selectNextRowExtendSelection",
                        "ctrl shift DOWN", "selectNextRowExtendSelection",
                        "ctrl shift KP_DOWN", "selectNextRowExtendSelection",
                        "ctrl DOWN", "selectNextRowChangeLead",
                        "ctrl KP_DOWN", "selectNextRowChangeLead",
                        "LEFT", "selectPreviousColumn",
                        "KP_LEFT", "selectPreviousColumn",
                        "shift LEFT", "selectPreviousColumnExtendSelection",
                        "shift KP_LEFT", "selectPreviousColumnExtendSelection",
                        "ctrl shift LEFT", "selectPreviousColumnExtendSelection",
                        "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection",
                        "ctrl LEFT", "selectPreviousColumnChangeLead",
                        "ctrl KP_LEFT", "selectPreviousColumnChangeLead",
                        "RIGHT", "selectNextColumn",
                        "KP_RIGHT", "selectNextColumn",
                        "shift RIGHT", "selectNextColumnExtendSelection",
                        "shift KP_RIGHT", "selectNextColumnExtendSelection",
                        "ctrl shift RIGHT", "selectNextColumnExtendSelection",
                        "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection",
                        "ctrl RIGHT", "selectNextColumnChangeLead",
                        "ctrl KP_RIGHT", "selectNextColumnChangeLead",
                        "HOME", "selectFirstRow",
                        "shift HOME", "selectFirstRowExtendSelection",
                        "ctrl shift HOME", "selectFirstRowExtendSelection",
                        "ctrl HOME", "selectFirstRowChangeLead",
                        "END", "selectLastRow",
                        "shift END", "selectLastRowExtendSelection",
                        "ctrl shift END", "selectLastRowExtendSelection",
                        "ctrl END", "selectLastRowChangeLead",
                        "PAGE_UP", "scrollUp",
                        "shift PAGE_UP", "scrollUpExtendSelection",
                        "ctrl shift PAGE_UP", "scrollUpExtendSelection",
                        "ctrl PAGE_UP", "scrollUpChangeLead",
                        "PAGE_DOWN", "scrollDown",
                        "shift PAGE_DOWN", "scrollDownExtendSelection",
                        "ctrl shift PAGE_DOWN", "scrollDownExtendSelection",
                        "ctrl PAGE_DOWN", "scrollDownChangeLead",
                        "ctrl A", "selectAll",
                        "ctrl SLASH", "selectAll",
                        "ctrl BACK_SLASH", "clearSelection",
                        "SPACE", "addToSelection",
                        "ctrl SPACE", "toggleAndAnchor",
                        "shift SPACE", "extendTo",
                        "ctrl shift SPACE", "moveSelectionTo"
                }));

        DEFAULTS.put("List.focusInputMap.RightToLeft",
                new UIDefaults.LazyInputMap(new Object[] {
                        "LEFT", "selectNextColumn",
                        "KP_LEFT", "selectNextColumn",
                        "shift LEFT", "selectNextColumnExtendSelection",
                        "shift KP_LEFT", "selectNextColumnExtendSelection",
                        "ctrl shift LEFT", "selectNextColumnExtendSelection",
                        "ctrl shift KP_LEFT", "selectNextColumnExtendSelection",
                        "ctrl LEFT", "selectNextColumnChangeLead",
                        "ctrl KP_LEFT", "selectNextColumnChangeLead",
                        "RIGHT", "selectPreviousColumn",
                        "KP_RIGHT", "selectPreviousColumn",
                        "shift RIGHT", "selectPreviousColumnExtendSelection",
                        "shift KP_RIGHT", "selectPreviousColumnExtendSelection",
                        "ctrl shift RIGHT", "selectPreviousColumnExtendSelection",
                        "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection",
                        "ctrl RIGHT", "selectPreviousColumnChangeLead",
                        "ctrl KP_RIGHT", "selectPreviousColumnChangeLead",
                }));

        DEFAULTS.put("MenuBar.windowBindings",
                new Object[] { "F10", "takeFocus" });

        DEFAULTS.put("OptionPane.windowBindings",
                new Object[] { "ESCAPE", "close" });

        DEFAULTS.put("RootPane.defaultButtonWindowKeyBindings",
                new Object[] {
                        "ENTER", "press",
                        "released ENTER", "release",
                        "ctrl ENTER", "press",
                        "ctrl released ENTER", "release"
                });

        DEFAULTS.put("RootPane.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "shift F10", "postPopup"
                }));

        DEFAULTS.put("ScrollBar.anecstorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "RIGHT", "positiveUnitIncrement",
                        "KP_RIGHT", "positiveUnitIncrement",
                        "DOWN", "positiveUnitIncrement",
                        "KP_DOWN", "positiveUnitIncrement",
                        "PAGE_DOWN", "positiveBlockIncrement",
                        "LEFT", "negativeUnitIncrement",
                        "KP_LEFT", "negativeUnitIncrement",
                        "UP", "negativeUnitIncrement",
                        "KP_UP", "negativeUnitIncrement",
                        "PAGE_UP", "negativeBlockIncrement",
                        "HOME", "minScroll",
                        "END", "maxScroll"
                }));

        DEFAULTS.put("ScrollBar.ancestorInputMap.RightToLeft",
                new UIDefaults.LazyInputMap(new Object[] {
                        "RIGHT", "negativeUnitIncrement",
                        "KP_RIGHT", "negativeUnitIncrement",
                        "LEFT", "positiveUnitIncrement",
                        "KP_LEFT", "positiveUnitIncrement",
                }));

        DEFAULTS.put("ScrollPane.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "RIGHT", "unitScrollRight",
                        "KP_RIGHT", "unitScrollRight",
                        "DOWN", "unitScrollDown",
                        "KP_DOWN", "unitScrollDown",
                        "LEFT", "unitScrollLeft",
                        "KP_LEFT", "unitScrollLeft",
                        "UP", "unitScrollUp",
                        "KP_UP", "unitScrollUp",
                        "PAGE_UP", "scrollUp",
                        "PAGE_DOWN", "scrollDown",
                        "ctrl PAGE_UP", "scrollLeft",
                        "ctrl PAGE_DOWN", "scrollRight",
                        "ctrl HOME", "scrollHome",
                        "ctrl END", "scrollEnd"
                }));
        DEFAULTS.put("ScrollPane.ancestorInputMap.RightToLeft",
                new UIDefaults.LazyInputMap(new Object[] {
                        "ctrl PAGE_UP", "scrollRight",
                        "ctrl PAGE_DOWN", "scrollLeft",
                }));

        DEFAULTS.put("SplitPane.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "UP", "negativeIncrement",
                        "DOWN", "positiveIncrement",
                        "LEFT", "negativeIncrement",
                        "RIGHT", "positiveIncrement",
                        "KP_UP", "negativeIncrement",
                        "KP_DOWN", "positiveIncrement",
                        "KP_LEFT", "negativeIncrement",
                        "KP_RIGHT", "positiveIncrement",
                        "HOME", "selectMin",
                        "END", "selectMax",
                        "F8", "startResize",
                        "F6", "toggleFocus",
                        "ctrl TAB", "focusOutForward",
                        "ctrl shift TAB", "focusOutBackward"
                }));

        DEFAULTS.put("Spinner.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "UP", "increment",
                        "KP_UP", "increment",
                        "DOWN", "decrement",
                        "KP_DOWN", "decrement"
                }));

        DEFAULTS.put("Slider.focusInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "RIGHT", "positiveUnitIncrement",
                        "KP_RIGHT", "positiveUnitIncrement",
                        "DOWN", "negativeUnitIncrement",
                        "KP_DOWN", "negativeUnitIncrement",
                        "PAGE_DOWN", "negativeBlockIncrement",
                        "ctrl PAGE_DOWN", "negativeBlockIncrement",
                        "LEFT", "negativeUnitIncrement",
                        "KP_LEFT", "negativeUnitIncrement",
                        "UP", "positiveUnitIncrement",
                        "KP_UP", "positiveUnitIncrement",
                        "PAGE_UP", "positiveBlockIncrement",
                        "ctrl PAGE_UP", "positiveBlockIncrement",
                        "HOME", "minScroll",
                        "END", "maxScroll"
                }));

        DEFAULTS.put("Slider.focusInputMap.RightToLeft",
                new UIDefaults.LazyInputMap(new Object[] {
                        "RIGHT", "negativeUnitIncrement",
                        "KP_RIGHT", "negativeUnitIncrement",
                        "LEFT", "positiveUnitIncrement",
                        "KP_LEFT", "positiveUnitIncrement",
                }));

        DEFAULTS.put("TabbedPane.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "ctrl PAGE_DOWN", "navigatePageDown",
                        "ctrl PAGE_UP", "navigatePageUp",
                        "ctrl UP", "requestFocus",
                        "ctrl KP_UP", "requestFocus",
                }));

        DEFAULTS.put("TabbedPane.focusInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "RIGHT", "navigateRight",
                        "KP_RIGHT", "navigateRight",
                        "LEFT", "navigateLeft",
                        "KP_LEFT", "navigateLeft",
                        "UP", "navigateUp",
                        "KP_UP", "navigateUp",
                        "DOWN", "navigateDown",
                        "KP_DOWN", "navigateDown",
                        "ctrl DOWN", "requestFocusForVisibleComponent",
                        "ctrl KP_DOWN", "requestFocusForVisibleComponent",
                }));

        DEFAULTS.put("Table.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "ctrl C", "copy",
                        "ctrl V", "paste",
                        "ctrl X", "cut",
                        "COPY", "copy",
                        "PASTE", "paste",
                        "CUT", "cut",
                        "control INSERT", "copy",
                        "shift INSERT", "paste",
                        "shift DELETE", "cut",
                        "RIGHT", "selectNextColumn",
                        "KP_RIGHT", "selectNextColumn",
                        "shift RIGHT", "selectNextColumnExtendSelection",
                        "shift KP_RIGHT", "selectNextColumnExtendSelection",
                        "ctrl shift RIGHT", "selectNextColumnExtendSelection",
                        "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection",
                        "ctrl RIGHT", "selectNextColumnChangeLead",
                        "ctrl KP_RIGHT", "selectNextColumnChangeLead",
                        "LEFT", "selectPreviousColumn",
                        "KP_LEFT", "selectPreviousColumn",
                        "shift LEFT", "selectPreviousColumnExtendSelection",
                        "shift KP_LEFT", "selectPreviousColumnExtendSelection",
                        "ctrl shift LEFT", "selectPreviousColumnExtendSelection",
                        "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection",
                        "ctrl LEFT", "selectPreviousColumnChangeLead",
                        "ctrl KP_LEFT", "selectPreviousColumnChangeLead",
                        "DOWN", "selectNextRow",
                        "KP_DOWN", "selectNextRow",
                        "shift DOWN", "selectNextRowExtendSelection",
                        "shift KP_DOWN", "selectNextRowExtendSelection",
                        "ctrl shift DOWN", "selectNextRowExtendSelection",
                        "ctrl shift KP_DOWN", "selectNextRowExtendSelection",
                        "ctrl DOWN", "selectNextRowChangeLead",
                        "ctrl KP_DOWN", "selectNextRowChangeLead",
                        "UP", "selectPreviousRow",
                        "KP_UP", "selectPreviousRow",
                        "shift UP", "selectPreviousRowExtendSelection",
                        "shift KP_UP", "selectPreviousRowExtendSelection",
                        "ctrl shift UP", "selectPreviousRowExtendSelection",
                        "ctrl shift KP_UP", "selectPreviousRowExtendSelection",
                        "ctrl UP", "selectPreviousRowChangeLead",
                        "ctrl KP_UP", "selectPreviousRowChangeLead",
                        "HOME", "selectFirstColumn",
                        "shift HOME", "selectFirstColumnExtendSelection",
                        "ctrl shift HOME", "selectFirstRowExtendSelection",
                        "ctrl HOME", "selectFirstRow",
                        "END", "selectLastColumn",
                        "shift END", "selectLastColumnExtendSelection",
                        "ctrl shift END", "selectLastRowExtendSelection",
                        "ctrl END", "selectLastRow",
                        "PAGE_UP", "scrollUpChangeSelection",
                        "shift PAGE_UP", "scrollUpExtendSelection",
                        "ctrl shift PAGE_UP", "scrollLeftExtendSelection",
                        "ctrl PAGE_UP", "scrollLeftChangeSelection",
                        "PAGE_DOWN", "scrollDownChangeSelection",
                        "shift PAGE_DOWN", "scrollDownExtendSelection",
                        "ctrl shift PAGE_DOWN", "scrollRightExtendSelection",
                        "ctrl PAGE_DOWN", "scrollRightChangeSelection",
                        "TAB", "selectNextColumnCell",
                        "shift TAB", "selectPreviousColumnCell",
                        "ENTER", "selectNextRowCell",
                        "shift ENTER", "selectPreviousRowCell",
                        "ctrl A", "selectAll",
                        "ctrl SLASH", "selectAll",
                        "ctrl BACK_SLASH", "clearSelection",
                        "ESCAPE", "cancel",
                        "F2", "startEditing",
                        "SPACE", "addToSelection",
                        "ctrl SPACE", "toggleAndAnchor",
                        "shift SPACE", "extendTo",
                        "ctrl shift SPACE", "moveSelectionTo",
                        "F8", "focusHeader"
                }));

        DEFAULTS.put("TableHeader.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "SPACE", "toggleSortOrder",
                        "LEFT", "selectColumnToLeft",
                        "KP_LEFT", "selectColumnToLeft",
                        "RIGHT", "selectColumnToRight",
                        "KP_RIGHT", "selectColumnToRight",
                        "alt LEFT", "moveColumnLeft",
                        "alt KP_LEFT", "moveColumnLeft",
                        "alt RIGHT", "moveColumnRight",
                        "alt KP_RIGHT", "moveColumnRight",
                        "alt shift LEFT", "resizeLeft",
                        "alt shift KP_LEFT", "resizeLeft",
                        "alt shift RIGHT", "resizeRight",
                        "alt shift KP_RIGHT", "resizeRight",
                        "ESCAPE", "focusTable",
                }));

        DEFAULTS.put("Tree.ancestorInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "ESCAPE", "cancel"
                }));
        DEFAULTS.put("Tree.focusInputMap",
                new UIDefaults.LazyInputMap(new Object[] {
                        "ADD", "expand",
                        "SUBTRACT", "collapse",
                        "ctrl C", "copy",
                        "ctrl V", "paste",
                        "ctrl X", "cut",
                        "COPY", "copy",
                        "PASTE", "paste",
                        "CUT", "cut",
                        "control INSERT", "copy",
                        "shift INSERT", "paste",
                        "shift DELETE", "cut",
                        "UP", "selectPrevious",
                        "KP_UP", "selectPrevious",
                        "shift UP", "selectPreviousExtendSelection",
                        "shift KP_UP", "selectPreviousExtendSelection",
                        "ctrl shift UP", "selectPreviousExtendSelection",
                        "ctrl shift KP_UP", "selectPreviousExtendSelection",
                        "ctrl UP", "selectPreviousChangeLead",
                        "ctrl KP_UP", "selectPreviousChangeLead",
                        "DOWN", "selectNext",
                        "KP_DOWN", "selectNext",
                        "shift DOWN", "selectNextExtendSelection",
                        "shift KP_DOWN", "selectNextExtendSelection",
                        "ctrl shift DOWN", "selectNextExtendSelection",
                        "ctrl shift KP_DOWN", "selectNextExtendSelection",
                        "ctrl DOWN", "selectNextChangeLead",
                        "ctrl KP_DOWN", "selectNextChangeLead",
                        "RIGHT", "selectChild",
                        "KP_RIGHT", "selectChild",
                        "LEFT", "selectParent",
                        "KP_LEFT", "selectParent",
                        "PAGE_UP", "scrollUpChangeSelection",
                        "shift PAGE_UP", "scrollUpExtendSelection",
                        "ctrl shift PAGE_UP", "scrollUpExtendSelection",
                        "ctrl PAGE_UP", "scrollUpChangeLead",
                        "PAGE_DOWN", "scrollDownChangeSelection",
                        "shift PAGE_DOWN", "scrollDownExtendSelection",
                        "ctrl shift PAGE_DOWN", "scrollDownExtendSelection",
                        "ctrl PAGE_DOWN", "scrollDownChangeLead",
                        "HOME", "selectFirst",
                        "shift HOME", "selectFirstExtendSelection",
                        "ctrl shift HOME", "selectFirstExtendSelection",
                        "ctrl HOME", "selectFirstChangeLead",
                        "END", "selectLast",
                        "shift END", "selectLastExtendSelection",
                        "ctrl shift END", "selectLastExtendSelection",
                        "ctrl END", "selectLastChangeLead",
                        "F2", "startEditing",
                        "ctrl A", "selectAll",
                        "ctrl SLASH", "selectAll",
                        "ctrl BACK_SLASH", "clearSelection",
                        "ctrl LEFT", "scrollLeft",
                        "ctrl KP_LEFT", "scrollLeft",
                        "ctrl RIGHT", "scrollRight",
                        "ctrl KP_RIGHT", "scrollRight",
                        "SPACE", "addToSelection",
                        "ctrl SPACE", "toggleAndAnchor",
                        "shift SPACE", "extendTo",
                        "ctrl shift SPACE", "moveSelectionTo"
                }));
        DEFAULTS.put("Tree.focusInputMap.RightToLeft",
                new UIDefaults.LazyInputMap(new Object[] {
                        "RIGHT", "selectParent",
                        "KP_RIGHT", "selectParent",
                        "LEFT", "selectChild",
                        "KP_LEFT", "selectChild",
                }));
        // @formatter:on
    }
}

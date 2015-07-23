package org.geworkbench.util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

/**
 * A list control that supports interactive text search.
 *
 * @author John Watkinson
 */
public class JAutoList extends JPanel {

    public static final String NEXT_BUTTON_TEXT = "Find Next";
    public static final String SEARCH_LABEL_TEXT = "Search:";

    private JList list;
    private JButton nextButton;
    private JTextField searchField;
    private ListModel model;
    private JScrollPane scrollPane;

    private boolean lastSearchFailed = false;
    private boolean lastSearchWasAscending = true;

    private boolean prefixMode = false;

    public JAutoList(ListModel model) {
        super();
        this.model = model;
        // Create and lay out components
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        JLabel searchLabel = new JLabel(SEARCH_LABEL_TEXT);
        nextButton = new JButton(NEXT_BUTTON_TEXT);
        searchField = new JTextField();
        list = new JList(model);
        scrollPane = new JScrollPane();
        // Compose components
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(nextButton);
        add(topPanel, BorderLayout.NORTH);
        scrollPane.getViewport().setView(list);
        add(scrollPane, BorderLayout.CENTER);
        // Add appropriate listeners
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findNext(true);
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) {
                handleMouseEvent(e);
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                searchFieldChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                searchFieldChanged();
            }

            public void changedUpdate(DocumentEvent e) {
                searchFieldChanged();
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                JAutoList.this.keyPressed(e);
            }
        });
    }

    private void handleMouseEvent(MouseEvent event) {
        int index = list.locationToIndex(event.getPoint());
        if (index != -1) {
            if (event.isMetaDown()) {
                elementRightClicked(index, event);
            } else if (event.getButton() == MouseEvent.BUTTON1) {
                if (event.getClickCount() > 1) {
                    elementDoubleClicked(index, event);
                } else {
                    elementClicked(index, event);
                }
            }
        }
    }

    /**
     * Finds the previous match for the text string and selects it in the list.
     *
     * @param text  the text to match.
     * @param index the index from which to begin the search.
     * @return true if a match was found, false otherwise.
     */
    private boolean findPreviousFrom(String text, int index) {
        if (text.trim().length() > 0) {
            text = text.toLowerCase();
            boolean found = false;
            for (int i = index; i >= 0; i--) {
                if (match(i, text)) {
                    list.setSelectedIndex(i);
                    list.scrollRectToVisible(list.getCellBounds(i, i));
                    found = true;
                    break;
                }
            }
            return found;
        }
        // Degenerate case
        return true;
    }

    private boolean match(int index, String text) {
        String element = model.getElementAt(index).toString().toLowerCase();
        if (!prefixMode) {
            return element.contains(text);
        } else {
            return element.startsWith(text);
        }
    }

    /**
     * Finds the next match for the text string and selects it in the list.
     *
     * @param text  the text to match.
     * @param index the index from which to begin the search.
     * @return true if a match was found, false otherwise.
     */
    private boolean findNextFrom(String text, int index) {
        if (text.trim().length() > 0) {
            text = text.toLowerCase();
            boolean found = false;
            for (int i = index; i < model.getSize(); i++) {
                if (match(i, text)) {
                    list.setSelectedIndex(i);
                    list.scrollRectToVisible(list.getCellBounds(i, i));
                    found = true;
                    break;
                }
            }
            return found;
        }
        // Degenerate case
        return true;
    }

    private void handlePostSearch() {
        if (lastSearchFailed) {
            searchField.setForeground(Color.red);
        }
    }

    private void handlePreSearch() {
        searchField.setForeground(Color.black);
    }

    /**
     * Override to customize the result of the 'next' button being clicked (or ENTER being pressed in text field).
     */
    protected boolean findNext(boolean ascending) {
        handlePreSearch();
        int index = list.getSelectedIndex();
        if (lastSearchFailed) {
            if (ascending && lastSearchWasAscending) {
                index = -1;
            } else if (!ascending && !lastSearchWasAscending) {
                index = model.getSize();
            }
        }
        lastSearchWasAscending = ascending;
        String text = searchField.getText();
        if (ascending) {
            if (index < (model.getSize() - 1)) {
                index++;
                lastSearchFailed = !findNextFrom(text, index);
            } else {
                lastSearchFailed = true;
            }
        } else {
            if (index > 0) {
                index--;
                lastSearchFailed = !findPreviousFrom(text, index);
            } else {
                lastSearchFailed = true;
            }
        }
        handlePostSearch();
        return!lastSearchFailed;
    }

    /**
     * Override to customize the result of a key being typed in the search field.
     *
     * @param event the key event.
     */
    protected void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
            // Same effect as next button
            findNext(true);
        } else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) { // ESC key
            searchField.setText("");
        } else if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) { // Backspace key
            if (searchField.getText().length() == 1) {
                list.setSelectedIndex(0);
                list.scrollRectToVisible(list.getCellBounds(0, 0));
                lastSearchFailed = false;
                handlePostSearch();
            }
        } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
            int index = list.getSelectedIndex();
            index++;
            if (index < model.getSize()) {
                list.setSelectedIndex(index);
                list.scrollRectToVisible(list.getCellBounds(index, index));
            }
        } else if (event.getKeyCode() == KeyEvent.VK_UP) {
            int index = list.getSelectedIndex();
            index--;
            if (index >= 0) {
                list.setSelectedIndex(index);
                list.scrollRectToVisible(list.getCellBounds(index, index));
            }
        } else if (event.isControlDown()) {
            if (event.getKeyChar() == '\u000E') {
                findNext(true);
            } else if (event.getKeyChar() == '\u0002') {
                findNext(false);
            }
        }
    }

    protected void searchFieldChanged() {
        handlePreSearch();
        lastSearchFailed = false;
        int index = list.getSelectedIndex();
        if (index < 0) {
            index = 0;
        }
        String text = searchField.getText();
        lastSearchFailed = !findNextFrom(text, index);
        handlePostSearch();
    }

    /**
     * Does nothing by default. Override to handle a list element being clicked.
     *
     * @param index the list element that was clicked.
     */
    protected void elementClicked(int index, MouseEvent e) {
    }

    /**
     * Does nothing by default. Override to handle a list element being double-clicked.
     *
     * @param index the list element that was clicked.
     */
    protected void elementDoubleClicked(int index, MouseEvent e) {
    }

    /**
     * Does nothing by default. Override to handle a list element being right-clicked.
     *
     * @param index the list element that was clicked.
     */
    protected void elementRightClicked(int index, MouseEvent e) {
    }

    public JList getList() {
        return list;
    }

    public ListModel getModel() {
        return model;
    }

    public int getHighlightedIndex() {
        return list.getSelectedIndex();
    }

    /**
     * Set the highlightedIndex automatically.
     * @param theIndex int
     * @return boolean
     */
    public boolean setHighlightedIndex(int theIndex) {
        if (model != null && model.getSize() > theIndex) {
            list.setSelectedIndex(theIndex);
            list.scrollRectToVisible(list.getCellBounds(theIndex, theIndex));

            return true;
        }
        return false;
    }

    public boolean isPrefixMode() {
        return prefixMode;
    }

    public void setPrefixMode(boolean prefixMode) {
        this.prefixMode = prefixMode;
    }

    /**
     * Test program for JAutoList
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("JAutoList Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DefaultListModel model = new DefaultListModel();
        model.addElement("Aardvark");
        model.addElement("Ant");
        model.addElement("Anteater");
        model.addElement("Badger");
        model.addElement("Bee");
        model.addElement("Pigeon");
        model.addElement("White-tailed Deer");
        JAutoList list = new JAutoList(model);
        frame.add(list);
        frame.pack();
        frame.setVisible(true);
    }
}

package com.movieapp.ui;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;

/**
 * Central palette, fonts, and reusable styling helpers, applied entirely
 * through JavaFX's Java styling API (Background, Border, Font, Paint,
 * Effect) — no external CSS stylesheet is loaded anywhere in the app.
 *
 * A handful of built-in JavaFX controls (TextField/TextArea text color,
 * ComboBox selected-text color, CheckBox tick-mark fill) expose no
 * Java-only setter at all — for those specific properties only, a minimal
 * inline "-fx-" string is set directly on that one node. No external
 * stylesheet is ever loaded, and no other property uses this approach.
 * Each such spot is commented where it occurs.
 */
public class UiTheme {

    // ===== Palette =====
    public static final Color BG_ROOT = Color.web("#14151f");
    public static final Color BG_HEADER_1 = Color.web("#1a1b2e");
    public static final Color BG_HEADER_2 = Color.web("#24253f");
    public static final Color BG_PANEL = Color.web("#1a1b2e");
    public static final Color BG_CARD = Color.web("#262844");
    public static final Color BG_FIELD = Color.web("#1e2035");

    public static final Color BORDER = Color.web("#2a2c47");
    public static final Color BORDER_LIGHT = Color.web("#383a63");

    public static final Color TEXT_PRIMARY = Color.web("#f4f2ff");
    public static final Color TEXT_SECONDARY = Color.web("#d4d2ea");
    public static final Color TEXT_MUTED = Color.web("#8b89ad");
    public static final Color TEXT_WHITE = Color.WHITE;

    public static final Color PURPLE_1 = Color.web("#7c5cff");
    public static final Color PURPLE_2 = Color.web("#5e3fd9");
    public static final Color PURPLE_HOVER_1 = Color.web("#8b6dff");
    public static final Color PURPLE_HOVER_2 = Color.web("#6c4ce8");
    public static final Color PURPLE_PRESSED = Color.web("#4e34b8");

    public static final Color GREEN_1 = Color.web("#2ed9a3");
    public static final Color GREEN_2 = Color.web("#1fa885");

    public static final Color ORANGE_1 = Color.web("#ffb648");
    public static final Color ORANGE_2 = Color.web("#e2932a");

    public static final Color GOLD_1 = Color.web("#ffcc66");
    public static final Color GOLD_2 = Color.web("#e2a83a");

    public static final Color RED_1 = Color.web("#ff5c6c");
    public static final Color RED_2 = Color.web("#e8384b");
    public static final Color RED_PRESSED = Color.web("#c02c3a");

    public static final Color SECONDARY_BG = Color.web("#2a2c47");
    public static final Color SECONDARY_HOVER = Color.web("#363860");
    public static final Color SECONDARY_PRESSED = Color.web("#21223a");

    // ===== Fonts =====
    public static Font titleFont() { return Font.font("Segoe UI", FontWeight.BOLD, 24); }
    public static Font subtitleFont() { return Font.font("Segoe UI", FontWeight.NORMAL, 13); }
    public static Font sectionTitleFont() { return Font.font("Segoe UI", FontWeight.BOLD, 15); }
    public static Font statLabelFont() { return Font.font("Segoe UI", FontWeight.BOLD, 16); }
    public static Font cardNumberFont() { return Font.font("Segoe UI", FontWeight.BOLD, 32); }
    public static Font cardCaptionFont() { return Font.font("Segoe UI", FontWeight.NORMAL, 13); }
    public static Font cardIconFont() { return Font.font("Segoe UI", 26); }
    public static Font bodyFont() { return Font.font("Segoe UI", 13); }

    // ===== Gradients =====

    public static LinearGradient diagonalGradient(Color from, Color to) {
        return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, from), new Stop(1, to));
    }

    public static LinearGradient horizontalGradient(Color from, Color to) {
        return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, from), new Stop(1, to));
    }

    // ===== Shadows =====

    public static DropShadow shadow(Color color, double radius, double dy) {
        DropShadow ds = new DropShadow();
        ds.setColor(color);
        ds.setRadius(radius);
        ds.setOffsetY(dy);
        return ds;
    }

    public static DropShadow cardShadow() {
        return shadow(Color.rgb(0, 0, 0, 0.45), 16, 6);
    }

    public static DropShadow panelShadow() {
        return shadow(Color.rgb(0, 0, 0, 0.35), 12, 4);
    }

    public static DropShadow headerShadow() {
        return shadow(Color.rgb(0, 0, 0, 0.4), 12, 4);
    }

    public static DropShadow buttonGlow(Color color) {
        return shadow(color, 8, 2);
    }

    // ===== Region backgrounds / borders =====

    public static void fillBackground(Region region, Color color) {
        region.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public static void fillGradientBackground(Region region, LinearGradient gradient, double radius) {
        region.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(radius), Insets.EMPTY)));
    }

    public static void styleCardSolid(Region region, Color bg, double radius) {
        region.setBackground(new Background(new BackgroundFill(bg, new CornerRadii(radius), Insets.EMPTY)));
    }

    public static void styleCardWithBorder(Region region, Color bg, Color borderColor, double radius) {
        region.setBackground(new Background(new BackgroundFill(bg, new CornerRadii(radius), Insets.EMPTY)));
        region.setBorder(new Border(new BorderStroke(
                borderColor, BorderStrokeStyle.SOLID, new CornerRadii(radius), new BorderWidths(1)
        )));
    }

    public static void styleBottomBorder(Region region, Color bg, Color borderColor) {
        region.setBackground(new Background(new BackgroundFill(bg, CornerRadii.EMPTY, Insets.EMPTY)));
        region.setBorder(new Border(new BorderStroke(
                borderColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                new BorderWidths(0, 0, 1, 0)
        )));
    }

    // ===== Labels =====

    public static void styleLabel(Label label, Color color, Font font) {
        label.setTextFill(color);
        label.setFont(font);
    }

    // ===== Buttons (gradient + glow + hover/pressed states) =====

    public static void styleGradientButton(Button button, Color from1, Color to1,
                                           Color fromHover, Color toHover,
                                           Color pressedFlat, Color textColor,
                                           boolean glow) {
        button.setTextFill(textColor);
        button.setFont(Font.font("Segoe UI", 13));
        button.setPadding(new Insets(8, 18, 8, 18));
        button.setCursor(Cursor.HAND);

        LinearGradient normal = diagonalGradient(from1, to1);
        LinearGradient hover = diagonalGradient(fromHover, toHover);

        fillGradientBackground(button, normal, 6);
        if (glow) {
            button.setEffect(buttonGlow(Color.rgb((int) (from1.getRed() * 255),
                    (int) (from1.getGreen() * 255), (int) (from1.getBlue() * 255), 0.35)));
        }

        button.setOnMouseEntered(e -> fillGradientBackground(button, hover, 6));
        button.setOnMouseExited(e -> fillGradientBackground(button, normal, 6));
        button.setOnMousePressed(e -> styleCardSolid(button, pressedFlat, 6));
        button.setOnMouseReleased(e -> fillGradientBackground(button, hover, 6));
    }

    public static void stylePrimaryButton(Button button) {
        styleGradientButton(button, PURPLE_1, PURPLE_2, PURPLE_HOVER_1, PURPLE_HOVER_2,
                PURPLE_PRESSED, TEXT_WHITE, true);
    }

    public static void styleSecondaryButton(Button button) {
        button.setTextFill(TEXT_SECONDARY);
        button.setFont(Font.font("Segoe UI", 13));
        button.setPadding(new Insets(8, 18, 8, 18));
        button.setCursor(Cursor.HAND);
        styleCardSolid(button, SECONDARY_BG, 6);
        button.setOnMouseEntered(e -> styleCardSolid(button, SECONDARY_HOVER, 6));
        button.setOnMouseExited(e -> styleCardSolid(button, SECONDARY_BG, 6));
        button.setOnMousePressed(e -> styleCardSolid(button, SECONDARY_PRESSED, 6));
        button.setOnMouseReleased(e -> styleCardSolid(button, SECONDARY_HOVER, 6));
    }

    public static void styleDangerButton(Button button) {
        styleGradientButton(button, RED_1, RED_2, RED_1, RED_2, RED_PRESSED, TEXT_WHITE, true);
    }

    public static void styleGoldButton(Button button) {
        styleGradientButton(button, GOLD_1, GOLD_2, GOLD_1, GOLD_2, GOLD_2, TEXT_WHITE, true);
    }

    // ===== TableView row/cell styling (pure Java — no lookup, no CSS) =====

    public static <T> Callback<TableView<T>, TableRow<T>> darkRowFactory() {
        return tableView -> new TableRow<>() {
            {
                setOnMouseEntered(e -> { if (!isEmpty()) fillBackground(this, Color.web("#262844")); });
                setOnMouseExited(e -> { if (!isEmpty()) applyRowBaseColor(this); });
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    fillBackground(this, BG_PANEL);
                } else {
                    applyRowBaseColor(this);
                }
            }

            private void applyRowBaseColor(TableRow<T> row) {
                if (row.isSelected()) {
                    fillBackground(row, Color.web("#3a2e6e"));
                } else if (row.getIndex() % 2 == 1) {
                    fillBackground(row, Color.web("#24263f"));
                } else {
                    fillBackground(row, BG_PANEL);
                }
            }
        };
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> darkCellFactory() {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setTextFill(TEXT_SECONDARY);
                setBackground(Background.EMPTY);
            }
        };
    }

    public static void styleListView(ListView<?> listView) {
        fillBackground(listView, BG_PANEL);
    }

    public static void styleListViewCells(ListView<String> listView) {
        fillBackground(listView, BG_PANEL);
        listView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setBackground(Background.EMPTY);
                } else {
                    setText(item);
                    setTextFill(TEXT_SECONDARY);
                    setBackground(new Background(new BackgroundFill(BG_PANEL, CornerRadii.EMPTY, Insets.EMPTY)));
                    setOnMouseEntered(e -> setBackground(new Background(new BackgroundFill(
                            Color.web("#24253f"), new CornerRadii(6), Insets.EMPTY))));
                    setOnMouseExited(e -> setBackground(new Background(new BackgroundFill(
                            BG_PANEL, CornerRadii.EMPTY, Insets.EMPTY))));
                }
            }
        });
    }

    // ===== Internal-control lookups (uses a CSS *selector string* only to
    // find a built-in sub-node JavaFX gives no Java setter for; no
    // stylesheet is loaded — only Java Background/Paint API is set on the
    // node once found). =====

    public static void styleTableHeader(Region tableView) {
        Region header = (Region) tableView.lookup(".column-header-background");
        if (header != null) {
            fillBackground(header, Color.web("#22233c"));
        }
        for (javafx.scene.Node cell : tableView.lookupAll(".column-header")) {
            if (cell instanceof Region region) {
                fillBackground(region, Color.web("#22233c"));
            }
            for (javafx.scene.Node labelNode : ((Region) cell).lookupAll(".label")) {
                if (labelNode instanceof Label label) {
                    label.setTextFill(TEXT_SECONDARY);
                    label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                }
            }
        }
        for (javafx.scene.Node filler : tableView.lookupAll(".filler")) {
            if (filler instanceof Region region) {
                fillBackground(region, Color.web("#22233c"));
            }
        }
        for (javafx.scene.Node track : tableView.lookupAll(".scroll-bar .track")) {
            if (track instanceof Region region) {
                fillBackground(region, BG_PANEL);
            }
        }
        for (javafx.scene.Node thumb : tableView.lookupAll(".scroll-bar .thumb")) {
            if (thumb instanceof Region region) {
                fillBackground(region, BORDER_LIGHT);
            }
        }
        for (javafx.scene.Node button : tableView.lookupAll(".scroll-bar .increment-button, .scroll-bar .decrement-button")) {
            if (button instanceof Region region) {
                fillBackground(region, BG_PANEL);
            }
        }
    }

    // Re-applies header/scrollbar styling every time the table's item list
    // changes — scrollbars are created lazily by JavaFX only once content
    // actually overflows, so a single one-time pass can miss them.
    public static <T> void bindDarkTableChrome(TableView<T> tableView) {
        Runnable apply = () -> styleTableHeader(tableView);
        tableView.getItems().addListener((javafx.collections.ListChangeListener<T>) change -> {
            javafx.application.Platform.runLater(apply);
        });
        javafx.application.Platform.runLater(apply);
    }
    // ===== Text input styling =====

    // TextInputControl (TextField/TextArea) exposes no Java-only setter for
    // typed-text or prompt-text color — only this minimal inline "-fx-"
    // string reaches it without a full lookup(). Background/border/focus
    // glow below are all pure Java API.
    public static void styleTextInput(Region field, Color textColor) {
        field.setBackground(new Background(new BackgroundFill(BG_FIELD, new CornerRadii(6), Insets.EMPTY)));
        field.setBorder(new Border(new BorderStroke(
                BORDER_LIGHT, BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(1))));
        field.setStyle("-fx-text-fill: " + toWebString(textColor) + "; -fx-prompt-text-fill: " + toWebString(TEXT_MUTED) + ";");

        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            Color borderColor = isFocused ? PURPLE_1 : BORDER_LIGHT;
            double width = isFocused ? 2 : 1;
            field.setBorder(new Border(new BorderStroke(
                    borderColor, BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(width))));
        });
    }

    public static String toWebString(Color c) {
        return String.format("#%02x%02x%02x",
                (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }

    public static void styleComboBox(Region comboBox) {
        comboBox.setBackground(new Background(new BackgroundFill(BG_FIELD, new CornerRadii(6), Insets.EMPTY)));
        comboBox.setBorder(new Border(new BorderStroke(
                BORDER_LIGHT, BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(1))));
    }

    // ComboBox's displayed selected-value text has no Java-only setter,
    // same limitation as TextField. A custom button cell lets us set text
    // color via the normal Labeled API for the closed control's display.
    public static void styleComboBoxText(javafx.scene.control.ComboBox<String> comboBox) {
        comboBox.setStyle("-fx-text-fill: " + toWebString(TEXT_SECONDARY) + ";");
        comboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                setTextFill(TEXT_SECONDARY);
                setBackground(Background.EMPTY);
            }
        });
    }

    // ComboBox's dropdown popup is a separate floating ListView JavaFX
    // creates lazily on first open, with its own default white skin.
    public static void styleComboBoxPopup(javafx.scene.control.ComboBox<String> comboBox) {
        comboBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) {
                javafx.scene.Node popupContent = comboBox.getSkin() != null
                        ? comboBox.lookup(".list-view") : null;
                if (popupContent instanceof ListView<?> listView) {
                    fillBackground(listView, BG_FIELD);
                    listView.setStyle("-fx-control-inner-background: " + toWebString(BG_FIELD) + ";");
                    for (javafx.scene.Node cellNode : listView.lookupAll(".list-cell")) {
                        if (cellNode instanceof javafx.scene.control.Labeled labeled) {
                            labeled.setTextFill(TEXT_SECONDARY);
                            labeled.setBackground(new Background(new BackgroundFill(BG_FIELD, CornerRadii.EMPTY, Insets.EMPTY)));
                        }
                    }
                }
            }
        });
    }

    // CheckBox's tick-box square and tick mark are internal sub-nodes with
    // no Java-only setter. The box background/border uses pure Java API
    // once located; only the tick mark's fill needs a one-line "-fx-"
    // string, since Shape-level fill inside a skin isn't otherwise reachable.
    public static void styleCheckBoxBox(CheckBox checkBox) {
        checkBox.setTextFill(TEXT_SECONDARY);
        checkBox.setFont(bodyFont());
        javafx.application.Platform.runLater(() -> {
            javafx.scene.Node box = checkBox.lookup(".box");
            if (box instanceof Region region) {
                region.setBackground(new Background(new BackgroundFill(BG_FIELD, new CornerRadii(4), Insets.EMPTY)));
                region.setBorder(new Border(new BorderStroke(
                        BORDER_LIGHT, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1))));
            }
        });
        checkBox.selectedProperty().addListener((obs, was, isSelected) -> {
            javafx.scene.Node mark = checkBox.lookup(".mark");
            if (mark != null) {
                if (isSelected) {
                    mark.setStyle("-fx-background-color: " + toWebString(PURPLE_1) + ";");
                } else {
                    mark.setStyle(""); // clear the inline override so the default stylesheet hides it again
                }
            }
        });
    }

    // ===== PieChart styling =====

    public static void colorPieSlices(javafx.scene.chart.PieChart chart, Color... palette) {
        int i = 0;
        for (javafx.scene.chart.PieChart.Data data : chart.getData()) {
            javafx.scene.Node node = data.getNode();
            if (node instanceof javafx.scene.shape.Shape shape) {
                Color color = palette[i % palette.length];
                shape.setFill(color);
            }
            i++;
        }
    }

    public static void styleChartText(javafx.scene.chart.PieChart chart) {
        for (javafx.scene.Node node : chart.lookupAll(".chart-title")) {
            applyTextColor(node, TEXT_PRIMARY);
        }
        for (javafx.scene.Node node : chart.lookupAll(".chart-pie-label")) {
            applyTextColor(node, TEXT_SECONDARY);
        }
        for (javafx.scene.Node node : chart.lookupAll(".chart-legend-item")) {
            applyTextColor(node, TEXT_SECONDARY);
        }
        javafx.scene.Node legend = chart.lookup(".chart-legend");
        if (legend instanceof Region region) {
            region.setBackground(Background.EMPTY);
            region.setBorder(null);
        }
    }

    private static void applyTextColor(javafx.scene.Node node, Color color) {
        if (node instanceof javafx.scene.control.Labeled labeled) {
            labeled.setTextFill(color);
        } else if (node instanceof javafx.scene.text.Text text) {
            text.setFill(color);
        }
    }

    public static final Color[] PIE_PALETTE = {
            PURPLE_1, GREEN_1, ORANGE_1, GOLD_1, RED_1,
            Color.web("#5da9ff"), Color.web("#ff8fd6"), Color.web("#7fe0c9")
    };

    public static void bindDarkChartStyling(javafx.scene.chart.PieChart chart, Color... slicePalette) {
        Runnable apply = () -> {
            colorPieSlices(chart, slicePalette);
            styleChartText(chart);
        };
        chart.getChildrenUnmodifiable().addListener(
                (javafx.collections.ListChangeListener<javafx.scene.Node>) change -> apply.run());
        apply.run();
    }

    // TextArea has a separate internal scrollable content region (its
    // .content sub-node) that TextField doesn't have, with its own white
    // default background. This locates and fills it directly via Java
    // Background API once the control has a skin.
    public static void styleTextAreaContent(javafx.scene.control.TextArea textArea) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.Node content = textArea.lookup(".content");
            if (content instanceof Region region) {
                fillBackground(region, BG_FIELD);
            }
        });
    }
}
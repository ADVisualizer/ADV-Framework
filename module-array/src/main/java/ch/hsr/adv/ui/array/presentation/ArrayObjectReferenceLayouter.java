package ch.hsr.adv.ui.array.presentation;

import ch.hsr.adv.commons.array.logic.domain.ArrayElement;
import ch.hsr.adv.commons.core.logic.domain.ModuleGroup;
import ch.hsr.adv.commons.core.logic.domain.styles.ADVStyle;
import ch.hsr.adv.commons.core.logic.domain.styles.presets
        .ADVDefaultElementStyle;
import ch.hsr.adv.commons.core.logic.domain.styles.presets
        .ADVDefaultRelationStyle;
import ch.hsr.adv.ui.core.presentation.widgets.AutoScalePane;
import ch.hsr.adv.ui.core.presentation.widgets.ConnectorType;
import ch.hsr.adv.ui.core.presentation.widgets.LabeledEdge;
import ch.hsr.adv.ui.core.presentation.widgets.LabeledNode;
import com.google.inject.Singleton;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Positions the array objects inclusive the references on the Pane
 */
@Singleton
public class ArrayObjectReferenceLayouter {
    private static final int SPACING = 30;
    private AutoScalePane scalePane;
    private VBox boxContainer;
    private HBox valueContainer;
    private HBox referenceContainer;

    /**
     * Layout array object reference
     *
     * @param moduleGroup moduleGroup to layout
     * @return layouted pane
     */
    public Pane layout(ModuleGroup moduleGroup) {
        initializeContainer();
        drawElements(moduleGroup);

        boxContainer.getChildren()
                .addAll(referenceContainer, valueContainer);
        scalePane.addChildren(boxContainer);

        return scalePane;
    }

    private void initializeContainer() {
        scalePane = new AutoScalePane();
        boxContainer = new VBox();
        valueContainer = new HBox();
        referenceContainer = new HBox();
        referenceContainer.setAlignment(Pos.CENTER);
        valueContainer.setAlignment(Pos.CENTER);
        valueContainer.setSpacing(SPACING);
        boxContainer.setSpacing(SPACING);
    }

    private void drawElements(ModuleGroup moduleGroup) {
        moduleGroup.getElements().forEach(e -> {
            ArrayElement element = (ArrayElement) e;
            ADVStyle style = element.getStyle();
            if (style == null) {
                style = new ADVDefaultElementStyle();
            }

            LabeledNode referenceNode;
            if (element.getContent() != null) {
                LabeledNode valueNode = new LabeledNode(
                        element.getContent(), style);
                referenceNode = new LabeledNode("*", style);
                drawRelations(referenceNode, valueNode);

                valueContainer.getChildren().add(valueNode);
            } else {
                referenceNode = new LabeledNode("null", style);
            }

            referenceContainer.getChildren().addAll(referenceNode);
        });
    }

    private void drawRelations(LabeledNode referenceNode,
                               LabeledNode valueNode) {

        LabeledEdge relation = new LabeledEdge("",
                referenceNode, ConnectorType.BOTTOM,
                valueNode, ConnectorType.TOP,
                new ADVDefaultRelationStyle(),
                LabeledEdge.DirectionType.UNIDIRECTIONAL);

        scalePane.addChildren(relation);
    }
}
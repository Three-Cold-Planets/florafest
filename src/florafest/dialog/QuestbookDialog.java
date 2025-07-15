package florafest.dialog;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.Fill;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.Group;
import arc.scene.actions.Actions;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.ui.Button;
import arc.scene.ui.ImageButton;
import arc.util.*;
import florafest.Florafest;
import florafest.content.FloraBlocks;
import mindustry.content.Blocks;
import mindustry.content.TechTree;
import mindustry.game.EventType;
import mindustry.gen.Sounds;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.ResearchDialog;
import mindustry.ui.dialogs.ResearchDialog.TechTreeNode;

import mindustry.Vars;
import mindustry.ui.ItemsDisplay;

import static mindustry.Vars.*;

public class QuestbookDialog extends BaseDialog {

    public TechTreeNode current;

    public QuestTree quest;

    public QuestTree.QuestNode hovered;

    public View view;

    public ItemsDisplay itemDisplay;

    public QuestbookDialog(){
        super("I am going absolutely insane");

        Vars.ui.research.fill(t -> t.update(() -> {
            if (Vars.ui.research.root.node.content == FloraBlocks.coreCentral) {
                Vars.ui.research.hide(Actions.fadeOut(0f));
                Vars.ui.hudfrag.shown = false;
                Florafest.ui.questbook.show(ui.research.root);
            }
        }));

        Events.on(EventType.ResetEvent.class, e -> {
            hide();
        });

        /*
        Events.on(EventType.UnlockEvent.class, e -> {
            if (net.client() && !needsRebuild) {
                needsRebuild = true;
                Core.app.post(() -> {
                    needsRebuild = false;

                    checkNodes(root);
                    view.hoverNode = null;
                    treeLayout();
                    view.rebuild();
                    Core.scene.act();
                });
            }
        });

         */
        /*

        titleTable.button(b -> {
            //TODO custom icon here.
            b.imageDraw(() -> ui.research.root.node.icon()).padRight(8).size(iconMed);
            b.add().growX();
            b.label(() -> ui.research.root.node.localizedName()).color(Pal.heal);
            b.add().growX();
            b.add().size(iconMed);
        }, () -> {
            new BaseDialog("@techtree.select") {{
                cont.pane(t -> {
                    t.table(Tex.button, in -> {
                        in.defaults().width(300f).height(60f);
                        for (TechTree.TechNode node : TechTree.roots) {
                            if (node.requiresUnlock && !node.content.unlockedHost() && node != Vars.ui.research.getPrefRoot()) continue;

                            //TODO toggle
                            in.button(node.localizedName(), node.icon(), Styles.flatTogglet, iconMed, () -> {
                                if (node == Vars.ui.research.lastNode) {
                                    return;
                                }

                                ui.research.rebuildTree(node);
                                hide();
                            }).marginLeft(12f).checked(node == ui.research.lastNode).row();
                        }
                    });
                });

                addCloseButton();
            }}.show();
        }).visible(() -> TechTree.roots.count(node -> !(node.requiresUnlock && !node.content.unlockedHost())) > 1).minWidth(300f);


         */


        titleTable.remove();
        titleTable.clear();
        titleTable.top();

        titleTable.button(b -> {
            b.imageDraw(() -> ui.research.root.node.icon()).padRight(8).size(iconMed);
            b.add().growX();
            b.label(() -> ui.research.root.node.localizedName()).color(Pal.heal);
            b.add().growX();
            b.add().size(iconMed);
        }, () -> {
            Log.info("FYGASDHGASJD");
            view.rebuildAll();
        });

        margin(0f).marginBottom(8);

        cont.stack(titleTable, view = new View(), itemDisplay = new ItemsDisplay()).grow();
        itemDisplay.visible(() -> !net.client());

        titleTable.toFront();

        addCloseButton();

        shown(() -> {
            Sounds.wind3.play();
        });

        addCaptureListener(new ElementGestureListener() {
            @Override
            public void zoom(InputEvent event, float initialDistance, float distance) {
                Log.info("Zoomies! " + distance);
                if (view.lastZoom < 0) {
                    view.lastZoom = view.scaleX;
                }

                view.setScale(Mathf.clamp(distance / initialDistance * view.lastZoom, 0.25f, 1f));
                view.setOrigin(Align.center);
                view.setTransform(true);
            }

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
                super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                view.lastZoom = view.scaleX;
                hovered = null;
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                if(view.canDrag == false) return;
                if(hovered != null){

                    hovered.x += deltaX / view.scaleX;
                    hovered.y += deltaY / view.scaleY;

                    return;
                }

                view.panX += deltaX / view.scaleX;
                view.panY += deltaY / view.scaleY;
                view.moved = true;
            }
        });
    }

    public void show(TechTreeNode root){
        current = root;
        quest = QuestTree.allTrees.find(tree -> tree.root == root.node);
        show();
    }

    public class View extends Group{
        public float panX = 0, panY = -200, lastZoom = -1;
        public boolean moved = false;

        public boolean canDrag = true;

        {
            rebuildAll();
        }

        public void rebuildAll(){
            clear();

            if(quest != null) quest.all.each(node -> {

                ImageButton button = new ImageButton(Blocks.duo.uiIcon, new ImageButton.ImageButtonStyle());
                button.resizeImage(32f * 2);
                button.getImage().setScaling(Scaling.fit);

                button.update(() -> {
                    float offset = (Core.graphics.getHeight() % 2) / 2f;

                    button.setPosition(node.x / view.scaleX + panX + width / 2f, node.y / view.scaleY + panY + height / 2f + offset, Align.center);

                    button.getImage().layout();

                    //Log.info(Strings.format("Button X: @, Y: @"), button.x, button.y);

                    //Log.info(Strings.format("Dialog X: @, Y: @"), x, y);
                });

                button.hovered(() -> {
                    //if(button.isPressed()) canDrag = false;

                    button.color.hue(Time.time);

                    if(button.isPressed()) {
                        hovered = node;
                        button.toFront();
                    }
                });

                button.clicked(() -> {

                });

                node.button = button;
                addChild(button);
            });



            setOrigin(Align.center);
            setTransform(true);
            released(() -> moved = false);
        }

        @Override
        protected void drawChildren() {
            super.drawChildren();

            float offset = (Core.graphics.getHeight() % 2) / 2f;
            //Fill.rect();
        }
    }
}

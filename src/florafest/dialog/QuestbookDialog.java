package florafest.dialog;

import arc.Core;
import arc.Events;
import arc.Input;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.actions.Actions;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.ui.Button;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Cell;
import arc.util.*;
import florafest.Florafest;
import florafest.content.FloraBlocks;
import florafest.questing.QuestLoader;
import florafest.questing.QuestTree;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.dialogs.BaseDialog;
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

    public boolean editor;
    public DialogState state = DialogState.regular;

    public static class DialogState{
        boolean dragButtons;
        boolean dragBackground;

        public DialogState(boolean dragButtons, boolean dragBackground){

            this.dragButtons = dragButtons;
            this.dragBackground = dragBackground;
        }

        public static DialogState

        regular = new DialogState(true, true),
        deleting = new DialogState(false, true),
        connecting = new DialogState(false, true);
    }

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


        buttons.button("@add", Icon.left, () -> {
            QuestTree.QuestNode node = new QuestTree.QuestNode();
            quest.all.add(node);
            view.addNode(node);
        }).size(width, 64);

        Button deleteButton = new Button(Icon.left);
        deleteButton.label(() -> state == DialogState.deleting ? "Deleting: <Y>" : "Deleting: <N>").pad(4).padBottom(0).wrap().growX();
        deleteButton.clicked(() -> {
            if(state == DialogState.deleting) state = DialogState.regular;
            else state = DialogState.deleting;
        });

        buttons.add(deleteButton);

        Button editorButton = new Button(Icon.left);
        editorButton.label(() -> editor ? "Editing: <Y>" : "Editing: <N>").pad(4).padBottom(0).wrap().growX();
        editorButton.clicked(() -> {
            editor = !editor;
        });

        buttons.add(editorButton);

        buttons.button(("@save"), () -> {
            QuestLoader.save(quest);
        }).size(width, 64);

        buttons.button(("@load"), () -> {
            QuestLoader.load(quest);
            view.rebuildAll();
        }).size(width, 64);

        shown(() -> {
            Sounds.wind3.play();
            view.rebuildAll();
        });

        addListener(new InputListener() {
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                view.setScale(Mathf.clamp(view.scaleX - amountY / 10.0F * view.scaleX, 0.25F, 1.0F));
                view.setOrigin(1);
                view.setTransform(true);
                return true;
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
                view.requestScroll();
                return super.mouseMoved(event, x, y);
            }
        });

        touchable = Touchable.enabled;
        addCaptureListener(new ElementGestureListener() {
            @Override
            public void zoom(InputEvent event, float initialDistance, float distance) {
                Log.info("Zoomies! " + distance);
                view.lastZoom = view.scaleX;

                view.setScale(Mathf.clamp(distance / initialDistance * view.lastZoom, 0.25f, 1f));
                view.setOrigin(Align.center);
                view.setTransform(true);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                view.lastZoom = view.scaleX;
                hovered = null;
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {

                if(hovered != null){

                    if(view.canDrag()){
                        hovered.data.x += deltaX / view.scaleX;
                        hovered.data.y += deltaY / view.scaleY;
                    }
                    return;
                }

                if(!state.dragBackground) return;

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

        //Only for buttons
        public boolean canDrag(){
            return state.dragButtons && editor;
        };

        {
            rebuildAll();
        }

        public void rebuildAll(){
            clear();

            if(quest != null) quest.all.each(this::addNode);



            setOrigin(Align.center);
            setTransform(true);
            released(() -> moved = false);
        }

        public void addNode(QuestTree.QuestNode node){
            ImageButton button = new ImageButton(Blocks.duo.uiIcon, new ImageButton.ImageButtonStyle());
            button.resizeImage(32f * 2);
            button.getImage().setScaling(Scaling.fit);

            button.update(() -> {
                float offset = (Core.graphics.getHeight() % 2) / 2f;

                button.setPosition(node.data.x + panX + width / 2f, node.data.y + panY + height / 2f + offset, Align.center);

                button.getImage().layout();

                //Log.info(Strings.format("Button X: @, Y: @"), button.x, button.y);

                //Log.info(Strings.format("Dialog X: @, Y: @"), x, y);
            });

            button.hovered(() -> {
                //if(button.isPressed()) canDrag = false;

                if(button.isPressed()) {
                    if(state == DialogState.deleting){
                        quest.all.remove(node);
                        view.removeChild(button);
                        rebuildAll();
                        return;
                    }

                    hovered = node;
                    button.toFront();
                }
            });

            button.clicked(() -> {
            });

            node.button = button;
            addChild(button);
        }

        @Override
        public void drawChildren() {
            Draw.sort(true);
            float offsetX = panX + width / 2f, offsetY = panY + height / 2f;
            int maxDepth = 10;
            float spacing = 500; // same as layout spacing

            Draw.z(0f);


            for(int i = 1; i <= maxDepth; i++){
                float radius = spacing * i;
                float cx = panX + width / 2f;
                float cy = panY + height / 2f;

                Draw.color(Pal.darkerGray);
                Lines.stroke(12f);
                Lines.circle(cx, cy, radius);

                // Dashed echo rings — apply parallax scaling
                for(int e = 1; e <= 3; e++){
                    float parallax = 1f / (e + 1f);
                    float px = panX * parallax + width / 2f;
                    float py = panY * parallax + height / 2f;

                    Draw.color(Pal.darkestGray.a(1f / (e * 1.5f)));
                    Lines.stroke(10f / e);

                    Lines.dashCircle(px, py, radius);
                }
            }
            Draw.color();

            //Tmp.v1.set(Core.input.mouse()).sub(Core.graphics.getWidth()/2, Core.graphics.getHeight()/2).scl(width/Core.graphics.getWidth(), height/Core.graphics.getHeight()).scl(1/scaleX, 1/scaleY);
            Tmp.v1.set(Core.scene.stageToScreenCoordinates(Core.input.mouse()));
            Fill.circle((Tmp.v1.x + width/2), (Tmp.v1.y + height/2), 40 * scaleX);



            Draw.sort(false);
            Draw.reset();
            super.drawChildren();
        }
    }
}

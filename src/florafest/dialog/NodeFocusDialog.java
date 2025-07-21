package florafest.dialog;

import florafest.questing.QuestTree;
import mindustry.ui.dialogs.BaseDialog;

public class NodeFocusDialog extends BaseDialog {
    public NodeFocusDialog(String title) {
        super(title);
        addCloseButton();
    }

    public void show(QuestTree.QuestNode node){
        show();
    }
}

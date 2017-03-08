import builder.ParamsFileMapBuilder;
import builder.ParamsFilePartBuilder;
import builder.ParamsStringBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Created by wang on 2017/3/7.
 */
public class GetParamsAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        TypePickDialog dialog = new TypePickDialog();
        dialog.setListener(type -> build(type, e));
        dialog.pack();
        dialog.setVisible(true);
    }

    private void build(int type, AnActionEvent e){
        switch (type){
            case 1:
                new ParamsStringBuilder().build(e);
                break;
            case 2:
                new ParamsFileMapBuilder().build(e);
                break;
            case 3:
                new ParamsFilePartBuilder().build(e);
                break;
        }
    }
}

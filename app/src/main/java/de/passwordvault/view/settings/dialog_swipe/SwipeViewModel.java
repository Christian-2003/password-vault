package de.passwordvault.view.settings.dialog_swipe;

import androidx.lifecycle.ViewModel;
import de.passwordvault.model.detail.DetailSwipeAction;
import de.passwordvault.model.storage.settings.Config;


/**
 * Class implements the view model for the
 * {@link SwipeDialog}.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class SwipeViewModel extends ViewModel {

    /**
     * Attribute stores the left swipe action.
     */
    private DetailSwipeAction leftSwipeAction;

    /**
     * Attribute stores the right swipe action.
     */
    private DetailSwipeAction rightSwipeAction;


    /**
     * Constructor instantiates a new view model.
     */
    public SwipeViewModel() {
        leftSwipeAction = Config.getInstance().leftSwipeAction.get();
        rightSwipeAction = Config.getInstance().rightSwipeAction.get();
    }


    /**
     * Method returns the left swipe action.
     *
     * @return  Left swipe action.
     */
    public DetailSwipeAction getLeftSwipeAction() {
        return leftSwipeAction;
    }

    /**
     * Method changes the left swipe action to the passed argument. If the current right swipe action
     * is identical to the passed swipe action, the right swipe action will be set to the current
     * left swipe action.
     *
     * @param swipeAction           New left swipe action.
     * @throws NullPointerException The passed swipe action is {@code null}.
     */
    public void setLeftSwipeAction(DetailSwipeAction swipeAction) throws NullPointerException {
        if (swipeAction == null) {
            throw new NullPointerException();
        }
        if (rightSwipeAction == swipeAction) {
            rightSwipeAction = leftSwipeAction;
            Config.getInstance().rightSwipeAction.set(rightSwipeAction);
        }
        leftSwipeAction = swipeAction;
        Config.getInstance().leftSwipeAction.set(leftSwipeAction);
    }

    /**
     * Method returns the right swipe action.
     *
     * @return  Right swipe action.
     */
    public DetailSwipeAction getRightSwipeAction() {
        return rightSwipeAction;
    }

    /**
     * Method changes the right swipe action to the passed argument. If the current left swipe action
     * is identical to the passed swipe action, the left swipe action will be set to the current
     * right swipe action.
     *
     * @param swipeAction           New right swipe action.
     * @throws NullPointerException The passed swipe action is {@code null}.
     */
    public void setRightSwipeAction(DetailSwipeAction swipeAction) throws NullPointerException {
        if (swipeAction == null) {
            throw new NullPointerException();
        }
        if (leftSwipeAction == swipeAction) {
            leftSwipeAction = rightSwipeAction;
            Config.getInstance().leftSwipeAction.set(leftSwipeAction);
        }
        rightSwipeAction = swipeAction;
        Config.getInstance().rightSwipeAction.set(rightSwipeAction);
    }

}

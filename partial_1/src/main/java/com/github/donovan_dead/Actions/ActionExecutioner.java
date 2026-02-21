package com.github.donovan_dead.Actions;

import java.awt.image.BufferedImage;
import java.util.Stack;

public class ActionExecutioner {
    private Stack<Actionable> actions;
    private Stack<Actionable> undoneActions;

    private BufferedImage lastImg;
    private BufferedImage initImage;


    public ActionExecutioner( BufferedImage init){
        actions = new Stack<Actionable>();
        initImage = init;
        lastImg = init;
    }

    public void addAction(Actionable action){
        actions.add(action);
    }
    
    public void  regainLostAction(){
        if(undoneActions.empty()) return;
    
        actions.add(undoneActions.peek());
        undoneActions.pop();
    }

    public void popAction(){
        if(actions.empty()) return;
        
        undoneActions.add(actions.peek());
        actions.pop();
    }

    public void clearActions(){
        while (!actions.isEmpty()) { 
            undoneActions.add(actions.peek());
            actions.pop();
        }
    }

    public BufferedImage executeActions(){
        BufferedImage current = initImage;

        for(Actionable a : actions){
            current = a.ApplyAction(current);
        }

        lastImg = current;
        return lastImg;
    }

    public BufferedImage getLastImage(){
        return lastImg;
    }
}

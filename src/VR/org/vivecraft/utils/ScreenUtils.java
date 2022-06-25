package org.vivecraft.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.optifine.util.GuiUtils;

public class ScreenUtils {
    
    //Vivecraft / OF
    public static List<AbstractWidget> getButtonList(Screen screen)
    {
        List<AbstractWidget> list = new ArrayList<>();

        for (GuiEventListener guieventlistener : screen.children())
        {
            if (guieventlistener instanceof AbstractWidget)
            {
                list.add((AbstractWidget)guieventlistener);
            }
        }

        return list;
    }
    
    public static AbstractWidget getSelectedButton(Screen screen, int x, int y)
    {
        for (AbstractWidget butt : getButtonList(screen))
        {
            if (butt.visible && butt.isHoveredOrFocused())
            {
                return butt;
            }
        }

        return null;
    }
    
    public static int getBGFrom(){
    	if(Minecraft.getInstance().vrSettings==null || Minecraft.getInstance().vrSettings.menuBackground)
    		return -1072689136;
    	return 0;
    }
    
    public static int getBGTo(){
    	if(Minecraft.getInstance().vrSettings==null || Minecraft.getInstance().vrSettings.menuBackground)
    		return -804253680;
    	return 0;
    }
    
    public static AbstractWidget getSelectedButton(int x, int y, List<AbstractWidget> listButtons)
    {
        for (int i = 0; i < listButtons.size(); ++i)
        {
            AbstractWidget abstractwidget = listButtons.get(i);

            if (abstractwidget.visible)
            {
                int j = GuiUtils.getWidth(abstractwidget);
                int k = GuiUtils.getHeight(abstractwidget);

                if (x >= abstractwidget.x && y >= abstractwidget.y && x < abstractwidget.x + j && y < abstractwidget.y + k)
                {
                    return abstractwidget;
                }
            }
        }

        return null;
    }
    //
}

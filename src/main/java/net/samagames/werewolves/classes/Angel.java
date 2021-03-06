package net.samagames.werewolves.classes;

import java.util.ArrayList;

import net.samagames.tools.chat.ChatUtils;
import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.GameState;
import net.samagames.werewolves.util.WinType;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/*
 * This file is part of Thiercelieux.
 *
 * Thiercelieux is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Thiercelieux is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thiercelieux.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Angel extends WWClass
{
    private int turns;

    protected Angel()
    {
        super("angel", "L'", "&bAnge", new ItemStack(Material.SNOW_BLOCK), new String[]{
            "Votre objectif est de mourir",
            "lors de la première nuit ou",
            "la première journée. Sinon, vous",
            "devenez un simple villageois."}, null);
        this.turns = 0;
    }

    @Override
    public boolean canPlayAtNight()
    {
        this.turns++;
        return false;
    }

    @Override
    public WinType getWinType()
    {
        return WinType.INNOCENTS;
    }

    @Override
    public boolean handleDeath(WWPlugin plugin, WWPlayer who, WWClass by)
    {
        if (turns == 2 && (plugin.getGame().getGameState() == GameState.DAY_1 || plugin.getGame().getGameState() == GameState.DAY_2)) 
        {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                ArrayList<String> list = new ArrayList<>();
                list.add(ChatUtils.getCenteredText("L'ange (" + who.getDisplayName() + ") est mort au premier tour."));
                list.add(ChatUtils.getCenteredText("Il gagne donc la partie !"));
                plugin.getGame().getCoherenceMachine().getTemplateManager().getBasicMessageTemplate().execute(list);
                who.win();
                plugin.getGame().finishGame();
            });
            return false;
        }
        return true;
    }
}

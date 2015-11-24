package net.samagames.werewolves.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.samagames.tssamabot.SamaBOTConnector;
import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.classes.WWClass;

import org.bukkit.ChatColor;

public class VocalGame extends WWGame
{
	public VocalGame(WWPlugin plugin)
	{
		super(plugin);
	}

	@Override
	public void handleChatMessage(WWPlayer player, String message)
	{
		if (player.isModerator())
		{
			broadcastMessage(player.getPlayerIfOnline().getDisplayName() + ChatColor.WHITE + ":" + message);
			return ;
		}
		if (WWClass.NIGHT_ORDER[currentevent] == WWClass.WEREWOLF && player.getPlayedClass() == WWClass.WEREWOLF)
		{
			Set<WWPlayer> receivers = this.getPlayersByClass(WWClass.WEREWOLF);
			String msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + player.getPlayerIfOnline().getDisplayName() + ChatColor.WHITE + ": " + message;
			for (WWPlayer wwp : receivers)
				wwp.getPlayerIfOnline().sendMessage(msg);
			msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + "Loup" + ChatColor.WHITE + ": " + message;
			receivers = this.getPlayersByClass(WWClass.LITTLE_GIRL);
			for (WWPlayer wwp : receivers)
				wwp.getPlayerIfOnline().sendMessage(msg);
			plugin.getServer().getConsoleSender().sendMessage(msg);
			return ;
		}
		player.getPlayerIfOnline().sendMessage(ChatColor.RED + "Le chat est désactivé en mode vocal. Merci de vous exprimer sur TeamSpeak.");
	}
	
	@Override
	public void startGame()
	{
		if (this.isGameStarted())
			return ;
		List<String> list = new ArrayList<String>();
		for (WWPlayer player : this.getInGamePlayers().values())
		{
			if (player.isModerator() || player.isSpectator() || !player.isOnline())
				continue ;
			list.add(player.getOfflinePlayer().getName());
		}
		String[] names = new String[list.size()];
		int i = 0;
		for (String n : list)
		{
			names[i] = n;
			i++;
		}
		this.broadcastMessage(this.coherenceMachine.getGameTag() + ChatColor.YELLOW + " Création d'un channel sur TeamSpeak ...");
		this.broadcastMessage(ChatColor.RED + " /!\\ Si vous n'êtes pas sur le TeamSpeak (ts.samagames.net), vous serez ejecté de la partie.");
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			String[] players = SamaBOTConnector.createChannel(plugin.getApi().getServerName().split("_")[1].substring(0, 8).toUpperCase(), names);
			if (players == null)
			{
				for (WWPlayer player : this.getInGamePlayers().values())
				{
					if (!player.isOnline())
						continue ;
					plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getApi().getGameManager().kickPlayer(player.getPlayerIfOnline(), "Impossible de créer le channel. Contactez un administrateur."));
				}
				plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().shutdown());
			}
			else
			{
				for (WWPlayer player : this.getInGamePlayers().values())
				{
					if (player.isModerator() || player.isSpectator() || !player.isOnline())
						continue ;
					boolean kick = true;
					for (int j = 0; j < players.length; j++)
						if (players[j].equalsIgnoreCase(player.getOfflinePlayer().getName()))
							kick = false;
					if (kick)
						plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getApi().getGameManager().kickPlayer(player.getPlayerIfOnline(), "Vous n'êtes pas sur TeamSpeak (ts.samagames.net) ou alors vous n'avez pas le même pseudo."));
				}
				plugin.getServer().getScheduler().runTask(plugin, () -> super.startGame());
			}
		});
	}
}
